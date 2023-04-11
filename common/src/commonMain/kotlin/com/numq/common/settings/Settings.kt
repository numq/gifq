package com.numq.common.settings

data class Settings constructor(
    val fileUrl: String,
    val fileName: String,
    val filePath: String,
    val width: Int,
    val height: Int,
    val fps: Double = 30.0,
    val lengthInMillis: Long? = null,
    val quality: Float = 1f,
    val repeat: Boolean = true,
) {
    companion object {
        const val minWidth = 426
        const val maxWidth = 4096
        const val minHeight = 240
        const val maxHeight = 2160
        const val minFPS = 1.0
        const val maxFPS = 60.0
        const val minQuality = -20f
        const val midQuality = -10f
        const val maxQuality = 1f
    }
}