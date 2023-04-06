package com.numq.common.upload

import androidx.compose.runtime.Composable

actual class UploadDialog {
    @Composable
    actual fun show(status: (UploadStatus) -> Unit) {
    }
}