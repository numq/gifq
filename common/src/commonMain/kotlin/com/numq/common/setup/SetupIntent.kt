package com.numq.common.setup

import com.numq.common.mvi.Intent
import com.numq.common.settings.Settings
import com.numq.common.upload.UploadedFile

sealed class SetupIntent private constructor() : Intent {
    data class UploadFile(val file: UploadedFile) : SetupIntent()
    data class UploadError(val exception: Exception) : SetupIntent()
    object CancelUploading : SetupIntent()
    data class UpdateSettings(val settings: Settings) : SetupIntent()
    data class StartProcessing(val settings: Settings) : SetupIntent()
    data class Error(val exception: Exception) : SetupIntent()
    object Reset : SetupIntent()
}