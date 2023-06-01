package com.numq.common.converter

import com.numq.common.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.FFmpegLogCallback
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

interface ConverterService {

    suspend fun calculateFileSize(settings: Settings): Result<Long>
    suspend fun convertMovieToGif(settings: Settings): Flow<ConversionStatus>

    class Implementation : ConverterService {

        init {
            FFmpegLogCallback.set()
        }

        override suspend fun calculateFileSize(settings: Settings) = settings.runCatching {
            withContext(Dispatchers.IO) {
                FFmpegFrameGrabber(settings.fileUrl).apply {
                    frameRate = fps
                    imageWidth = width
                    imageHeight = height
                    pixelFormat = avutil.AV_PIX_FMT_RGB24
                }.use { grabber ->
                    grabber.start()
                    val finalSize = ByteArrayOutputStream().use { os ->
                        FFmpegFrameRecorder(os, width, height).apply {
                            format = "gif"
                            frameRate = fps
                            pixelFormat = avutil.AV_PIX_FMT_RGB8
                            videoCodec = avcodec.AV_CODEC_ID_GIF
                            setOption("loop_output", if (repeat) "0" else "1")
                            setOption("delay", "${1000L / fps}")
                        }.use { recorder ->
                            recorder.start()
                            grabber.grabImage()?.let(recorder::record)
                            recorder.stop()
                        }
                        (os.size() * grabber.lengthInVideoFrames).toLong()
                    }
                    grabber.stop()
                    finalSize
                }
            }
        }

        override suspend fun convertMovieToGif(settings: Settings) = channelFlow {
            settings.runCatching {
                "$filePath/$fileName.gif".let { outputName ->
                    var success = false
                    try {
                        FFmpegFrameGrabber(fileUrl).apply {
                            frameRate = fps
                            imageWidth = width
                            imageHeight = height
                            pixelFormat = avutil.AV_PIX_FMT_RGB24
                        }.use { grabber ->
                            grabber.start()
                            val file = File(outputName)
                            FFmpegFrameRecorder(file, width, height).apply {
                                format = "gif"
                                frameRate = fps
                                pixelFormat = avutil.AV_PIX_FMT_RGB8
                                videoCodec = avcodec.AV_CODEC_ID_GIF
                                setOption("loop_output", if (repeat) "0" else "1")
                                setOption("delay", "${1000L / fps}")
                            }.use { recorder ->
                                recorder.start()
                                var frameNumber = 0
                                while (isActive) {
                                    grabber.grabImage()?.let(recorder::record) ?: break
                                    send(ConversionStatus.Progress(((frameNumber) * 100f) / grabber.lengthInVideoFrames))
                                    frameNumber += 1
                                    println("Frame number: $frameNumber")
                                }
                                recorder.stop()
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
            }
        }.flowOn(Dispatchers.IO)
    }
}