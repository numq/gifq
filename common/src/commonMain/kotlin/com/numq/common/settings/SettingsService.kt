package com.numq.common.settings

import com.numq.common.upload.UploadedFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bytedeco.javacv.FFmpegFrameGrabber
import kotlin.math.floor

interface SettingsService {

    suspend fun getInfo(file: UploadedFile): Result<Settings>

    class Implementation : SettingsService {
        private val extensions = arrayOf("mp4", "mov", "avi", "flv", "wmv", "mkv", "m4v", "mpg", "mpeg", "webm")

        private fun isVideoFile(extensions: Array<String>, file: UploadedFile): Boolean {
            return extensions.any { file.url.endsWith(".$it", true) }
        }

        override suspend fun getInfo(file: UploadedFile) = file.runCatching {
            if (!isVideoFile(extensions, this)) throw SettingsException.InvalidFormat
            withContext(Dispatchers.IO) {
                FFmpegFrameGrabber(url).use {
                    it.start()
                    val settings = Settings(
                        fileUrl = url,
                        fileInitialName = name,
                        fileChangedName = null,
                        filePath = path,
                        width = it.imageWidth,
                        height = it.imageHeight,
                        fps = floor(it.videoFrameRate),
                        lengthInMillis = it.lengthInTime / 1000L
                    )
                    it.stop()
                    settings
                }
            }
        }
    }
}