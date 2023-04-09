package com.numq.common.processing

import com.numq.common.converter.ConversionStatus
import com.numq.common.mvi.State
import kotlinx.coroutines.flow.Flow

sealed class ProcessingState private constructor() : State {
    object Loading : ProcessingState()
    data class Active(val status: Flow<ConversionStatus>) : ProcessingState()
    data class Result(val path: String) : ProcessingState()
    data class Error(val exception: Exception) : ProcessingState()
}