package com.numq.common.format

import kotlin.math.ceil

object SizeFormatter {
    fun format(sizeInBytes: Long): String {
        val kb = ceil(sizeInBytes.toDouble() / 1024) to "KB"
        val mb = ceil(sizeInBytes.toDouble() / (1024 * 1024)) to "MB"
        return arrayOf(kb, mb).maxByOrNull { it.second }?.run {
            String.format("%.2f %s", first, second)
        } ?: "Unknown"
    }
}