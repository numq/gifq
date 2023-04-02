package com.numq.common.upload

expect class UploadDialog constructor() {
    fun show(onUpload: (String) -> Unit, onClose: () -> Unit)
}