package com.numq.common.setup

import com.numq.common.mvi.Effect
import com.numq.common.settings.Settings

sealed class SetupEffect private constructor() : Effect {
    data class StartProcessing(val settings: Settings) : SetupEffect()
}