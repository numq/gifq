package com.numq.common.navigation

import com.numq.common.settings.Settings

sealed class Destination private constructor() {
    object Setup : Destination()
    data class Processing(val settings: Settings) : Destination()
}