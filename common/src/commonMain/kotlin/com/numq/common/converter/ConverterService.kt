package com.numq.common.converter

import com.numq.common.settings.Settings
import com.numq.encoder.AnimatedGifEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegLogCallback
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream


interface ConverterService {

    suspend fun calculateFileSize(settings: Settings): Result<Long>
    suspend fun convertVideoToGif(settings: Settings): Flow<ConversionStatus>

    class Implementation : ConverterService {

        init {
            FFmpegLogCallback.set()
        }

        private val targetFormat = avutil.AV_PIX_FMT_BGR24

        private fun scaleRange(value: Float, fromStart: Float, fromEnd: Float, toStart: Float, toEnd: Float): Int {
            val fromRange = fromEnd - fromStart
            val toRange = toEnd - toStart
            val scaled = (value - fromStart) / fromRange
            return (toStart + (scaled * toRange)).toInt()
        }

        private fun scaleQuality(quality: Float) = scaleRange(quality, 1f, 100f, 20f, 1f)

        override suspend fun calculateFileSize(settings: Settings) = settings.runCatching {
            withContext(Dispatchers.IO) {
                FFmpegFrameGrabber(settings.fileUrl).apply {
                    frameRate = fps
                    imageWidth = width
                    imageHeight = height
                    pixelFormat = targetFormat
                }.use { grabber ->
                    grabber.start()
                    val finalSize = ByteArrayOutputStream().use { os ->
                        AnimatedGifEncoder().apply {
                            start(os)

                            setFrameRate(fps.toFloat())
                            setSize(width, height)
                            setDelay((1000L / fps).toInt())
                            setQuality(scaleQuality(qualityLevel))
                            setRepeat(if (repeat) 0 else 1)

                            grabber.grabImage()?.let(::addFrame)?.let { success ->
                                if (!success) throw ConverterException.UnableToProcessFrame
                            }

                            finish()
                        }

                        (os.size() * grabber.lengthInVideoFrames).toLong()
                    }
                    grabber.stop()
                    finalSize
                }
            }
        }.onFailure(::println)

        override suspend fun convertVideoToGif(settings: Settings) = channelFlow {
            settings.runCatching {
                val name = fileChangedName ?: fileInitialName.takeIf {
                    File("$fileInitialName.gif").exists().not()
                } ?: System.currentTimeMillis()
                "$filePath/$name.gif".let { outputName ->
                    var success = false
                    try {
                        FFmpegFrameGrabber(fileUrl).apply {
                            frameRate = fps
                            imageWidth = width
                            imageHeight = height
                            pixelFormat = targetFormat
                        }.use { grabber ->
                            grabber.start()
                            val file = File(outputName)

                            FileOutputStream(file).use { fos ->
                                AnimatedGifEncoder().apply {
                                    start(fos)

                                    setFrameRate(fps.toFloat())
                                    setSize(width, height)
                                    setDelay((1000L / fps).toInt())
                                    setQuality(scaleQuality(qualityLevel))
                                    setRepeat(if (repeat) 0 else 1)

                                    var frameNumber = 0
                                    while (isActive) {
                                        grabber.grabImage()?.let(::addFrame)?.let { success ->
                                            if (!success) throw ConverterException.UnableToComplete
                                        } ?: break
                                        send(ConversionStatus.Progress(((frameNumber) * 100f) / grabber.lengthInVideoFrames))
                                        frameNumber += 1
                                        println("Frame number: $frameNumber")
                                    }

                                    finish()
                                }
                            }

                            grabber.stop()
                            if (isActive && file.exists()) {
                                send(ConversionStatus.Result(file.path))
                                success = true
                            } else throw FileNotFoundException()
                        }
                    } catch (e: Exception) {
                        e.cause?.let { send(ConversionStatus.Error(Exception(it))) }
                    } finally {
                        if (!success) File(outputName).delete()
                    }
                }
            }.onFailure(::println)
        }.flowOn(Dispatchers.IO)
    }
}