package com.numq.common.upload

expect class UploadDialog() {
    fun show(status: (UploadStatus) -> Unit)
}