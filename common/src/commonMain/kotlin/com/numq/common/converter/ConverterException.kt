package com.numq.common.converter

sealed class ConverterException private constructor(override val message: String) : Exception(message) {
    override fun toString() = message

    companion object {
        const val DEFAULT_MESSAGE = "Try again with other settings or file."
    }

    object UnableToProcessFrame :
        ConverterException("Unable to process frame. $DEFAULT_MESSAGE")

    object UnableToComplete :
        ConverterException("Unable to complete processing. $DEFAULT_MESSAGE")
}