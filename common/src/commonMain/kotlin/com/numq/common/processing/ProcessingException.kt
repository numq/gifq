package com.numq.common.processing

sealed class ProcessingException private constructor(override val message: String) : Exception(message) {
    override fun toString() = message

    object UnableToComplete :
        ProcessingException("Unable to complete processing. Try again with other settings or file.")
}