package com.numq.common.upload

import androidx.compose.runtime.Composable
import java.io.File

actual class UploadDialog {
    @Composable
    actual fun show(onUpload: (File) -> Unit, onClose: () -> Unit) {
    }
}