package com.numq.common.settings

data class Settings(
    val fileUrl: String,
    val fileInitialName: String,
    val fileChangedName: String?,
    val filePath: String,
    val width: Int,
    val height: Int,
    val fps: Double = 1.0,
    val lengthInMillis: Long,
    val qualityLevel: Float = 100f,
    val repeat: Boolean = true,
) {
    companion object {
        const val minWidth = 100
        const val maxWidth = 4096
        const val minHeight = 100
        const val maxHeight = 2160
        const val minFPS = 1.0
        const val maxFPS = 60.0
        const val minQuality = 1f
        const val midQuality = 50f
        const val maxQuality = 100f
    }
}