package com.numq.common.extension

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File

fun Uri.asFile(context: Context): File? {
    val documentFile = DocumentFile.fromSingleUri(context, this) ?: return null
    val file = File(context.getExternalFilesDir(null), documentFile.name ?: return null)
    context.contentResolver.openInputStream(this)?.use { inputStream ->
        file.outputStream().use { outputStream ->
            val bufferSize = 1024 * 4
            val buffer = ByteArray(bufferSize)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
        }
    }
    return file
}

