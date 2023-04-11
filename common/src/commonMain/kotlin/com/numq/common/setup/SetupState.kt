package com.numq.common.setup

import com.numq.common.mvi.State
import com.numq.common.settings.Settings

sealed class SetupState private constructor() : State {
    object Empty : SetupState()
    data class Uploaded(val settings: Settings, val size: Long? = null, val exception: Exception? = null) : SetupState()
    data class Error(val exception: Exception) : SetupState()
}