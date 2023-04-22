package com.numq.common.upload

sealed class UploadException private constructor(override val message: String) : Exception(message) {
    object InvalidFileSize : UploadException("The file size must not exceed 100 megabytes.")
}