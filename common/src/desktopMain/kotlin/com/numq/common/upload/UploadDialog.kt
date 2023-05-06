package com.numq.common.upload

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.ComposeWindow
import java.awt.FileDialog

actual class UploadDialog {
    private val maxFileSize = 100 * 1024 * 1024

    @Composable
    actual fun show(status: (UploadStatus) -> Unit) {
        val (visible, setVisible) = remember { mutableStateOf(true) }
        LaunchedEffect(Unit) {
            status(UploadStatus.Opened)
            with(
                FileDialog(
                    ComposeWindow(),
                    "Upload files",
                    FileDialog.LOAD
                ).apply {
                    isAlwaysOnTop = true
                    isVisible = visible
                }
            ) {
                try {
                    files?.firstOrNull()?.run {
                        if (length() > maxFileSize) status(UploadStatus.Error(UploadException.InvalidFileSize))
                        else status(UploadStatus.Uploaded(UploadedFile(
                            path,
                            nameWithoutExtension,
                            path.split(nameWithoutExtension).first()
                        )))
                    } ?: status(UploadStatus.Closed)
                } catch (e: Exception) {
                    status(UploadStatus.Error(e))
                } finally {
                    setVisible(false)
                }
            }
        }
    }
}