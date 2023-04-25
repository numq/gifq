package com.numq.common.settings

sealed class SettingsException private constructor(override val message: String) : Exception(message) {
    override fun toString() = message

    object InvalidFormat : SettingsException("Invalid file format.")
}