package com.numq.common.upload

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.ComposeWindow
import java.awt.FileDialog
import java.io.File

actual class UploadDialog {
    @Composable
    actual fun show(onUpload: (File) -> Unit, onClose: () -> Unit) {
        val (visible, setVisible) = remember { mutableStateOf(true) }
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
                files?.firstOrNull()?.let(onUpload)
            } catch (e: Exception) {
                println("Upload dialog exception: ${e.localizedMessage}")
            } finally {
                setVisible(false)
                onClose()
            }
        }
    }
}