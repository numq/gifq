package com.numq.common.encoder

sealed class EncoderException private constructor(override val message: String) : Exception(message) {
    object Default : EncoderException("An error occurred during encoding")
}