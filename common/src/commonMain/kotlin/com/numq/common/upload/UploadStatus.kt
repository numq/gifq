package com.numq.common.upload

sealed class UploadStatus private constructor() {
    object Opened : UploadStatus()
    data class Uploaded(val file: UploadedFile) : UploadStatus()
    data class Error(val exception: Exception) : UploadStatus()
    object Closed : UploadStatus()
}