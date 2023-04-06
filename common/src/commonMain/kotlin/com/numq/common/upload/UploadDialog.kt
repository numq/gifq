package com.numq.common.upload

expect class UploadDialog constructor() {
    fun show(status: (UploadStatus) -> Unit)
}