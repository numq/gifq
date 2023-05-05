package com.numq.common.upload

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.numq.common.extension.asFile

actual class UploadDialog {
    private val maxFileSize = 100 * 1024 * 1024

    @Composable
    actual fun show(status: (UploadStatus) -> Unit) {
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
            onResult = { uri ->
                runCatching {
                    uri?.asFile(context)?.run {
                        if (length() > maxFileSize) status(UploadStatus.Error(UploadException.InvalidFileSize))
                        else status(UploadStatus.Uploaded(UploadedFile(
                            path,
                            nameWithoutExtension,
                            path.split(nameWithoutExtension).first()
                        )))
                    } ?: status(UploadStatus.Closed)
                }.onFailure { status(UploadStatus.Error(Exception(it))) }
            }
        )
        LaunchedEffect(Unit) {
            launcher.launch(arrayOf("*/*"))
        }
    }
}