package com.numq.common.processing

import com.numq.common.mvi.Intent
import com.numq.common.settings.Settings

sealed class ProcessingIntent : Intent {
    data class Start(val settings: Settings) : ProcessingIntent()
    data class Complete(val path: String) : ProcessingIntent()
    data class Error(val exception: Exception) : ProcessingIntent()
    object Close : ProcessingIntent()
}