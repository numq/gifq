package com.numq.common.format

object SizeFormatter {
    fun format(size: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var fileSize = size.toDouble()
        var unitIndex = 0
        while (fileSize > 1024 && unitIndex < units.size - 1) {
            fileSize /= 1024
            unitIndex++
        }
        return String.format("%.2f %s", fileSize, units[unitIndex])
    }
}