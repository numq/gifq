package com.numq.common.converter

sealed class ConversionStatus private constructor() {
    data class Progress(val progress: Float = 0f) : ConversionStatus()
    data class Result(val path: String) : ConversionStatus()
    data class Error(val exception: Exception) : ConversionStatus()
}