package com.numq.common.settings

import com.numq.common.upload.UploadedFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bytedeco.javacv.FFmpegFrameGrabber
import kotlin.math.floor

interface SettingsService {

    suspend fun getInfo(file: UploadedFile): Result<Settings>

    class Implementation : SettingsService {
        override suspend fun getInfo(file: UploadedFile) = file.runCatching {
            withContext(Dispatchers.IO) {
                FFmpegFrameGrabber(url).use {
                    it.start()
                    val settings = Settings(
                        fileUrl = url,
                        fileName = name,
                        filePath = path,
                        width = it.imageWidth,
                        height = it.imageHeight,
                        fps = floor(it.frameRate),
                        lengthInMillis = it.lengthInTime / 1000L
                    )
                    it.stop()
                    settings
                }
            }
        }
    }
}