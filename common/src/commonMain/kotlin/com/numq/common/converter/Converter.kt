package com.numq.common.converter

import com.madgag.gif.fmsware.AnimatedGifEncoder
import com.numq.common.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import kotlin.math.abs


class Converter {
    fun calculateFileSize(settings: Settings) = settings.runCatching {
        FFmpegFrameGrabber(fileUrl).use { grabber ->
            grabber.apply {
                imageWidth = width
                imageHeight = height
                frameRate = fps
            }
            grabber.start()
            val numFrames = grabber.lengthInVideoFrames.toLong()
            val compressedFrameSize = ByteArrayOutputStream().use { baos ->
                AnimatedGifEncoder().apply {
                    setSize(grabber.imageWidth, grabber.imageHeight)
                    setDelay((1000 / grabber.videoFrameRate).toInt())
                    setQuality(abs(quality).toInt())
                    setRepeat(if (repeat) 0 else 1)
                    start(baos)
                    Java2DFrameConverter().use { converter ->
                        val frame = converter.convert(grabber.grabImage())
                        addFrame(frame)
                    }
                    finish()
                }
                baos.size()
            }
            grabber.stop()
            numFrames * compressedFrameSize
        }
    }

    suspend fun convertMovieToGif(settings: Settings) = settings.runCatching {
        "$filePath/$fileName.gif".let { outputName ->
            var success = false
            channelFlow {
                withContext(Dispatchers.IO) {
                    try {
                        FileOutputStream(outputName).use { fos ->
                            FFmpegFrameGrabber(fileUrl).use { grabber ->
                                grabber.apply {
                                    imageWidth = width
                                    imageHeight = height
                                    frameRate = fps
                                }
                                val encoder = AnimatedGifEncoder().apply {
                                    setSize(grabber.imageWidth, grabber.imageHeight)
                                    setDelay((1000 / grabber.videoFrameRate).toInt())
                                    setQuality(abs(quality).toInt())
                                    setRepeat(if (repeat) 0 else 1)
                                }
                                grabber.start()
                                encoder.start(fos)
                                Java2DFrameConverter().use { converter ->
                                    var cancelled = false
                                    var frameNumber = 0
                                    while (frameNumber < grabber.lengthInVideoFrames) {
                                        if (!isActive) {
                                            cancelled = true
                                            break
                                        }
                                        grabber.grabImage()?.let(converter::convert)?.let(encoder::addFrame)
                                        send(ConversionStatus.Progress((frameNumber + 1) * 100f / grabber.lengthInVideoFrames))
                                        frameNumber += 1
                                    }
                                    encoder.finish()
                                    grabber.stop()
                                    File(outputName).runCatching {
                                        if (!cancelled && exists()) {
                                            send(ConversionStatus.Result(path))
                                            success = true
                                        } else throw FileNotFoundException()
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.cause?.let { send(ConversionStatus.Error(Exception(it))) }
                    } finally {
                        if (!success) File(outputName).delete()
                    }
                }
            }
        }
    }
}