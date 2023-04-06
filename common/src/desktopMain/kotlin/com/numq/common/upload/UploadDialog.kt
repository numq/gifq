package com.numq.common.upload

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.ComposeWindow
import java.awt.FileDialog

actual class UploadDialog {
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
                        status(UploadStatus.Uploaded(UploadedFile(
                            path,
                            nameWithoutExtension,
                            path.split(nameWithoutExtension).first(),
                            length()
                        )))
                    } ?: status(UploadStatus.Closed)
                } catch (e: Exception) {
                    println("Upload dialog exception: ${e.localizedMessage}")
                } finally {
                    setVisible(false)
                }
            }
        }
    }
}