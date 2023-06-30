package com.numq.common.processing

import com.numq.common.converter.ConvertVideoToGif
import com.numq.common.mvi.Feature

class ProcessingFeature(
    private val convertVideoToGif: ConvertVideoToGif,
) : Feature<ProcessingState, ProcessingIntent, ProcessingEffect>(ProcessingState.Loading) {

    private var fileConversion: ConvertVideoToGif? = null

    override fun reduce(
        state: ProcessingState,
        intent: ProcessingIntent,
        updateState: (ProcessingState) -> Unit,
        emitEffect: (ProcessingEffect) -> Unit,
    ) = when (intent) {
        is ProcessingIntent.Start -> {
            fileConversion?.cancel()
            fileConversion = convertVideoToGif.apply {
                invoke(coroutineScope, intent.settings, error = {
                    updateState(ProcessingState.Error(it))
                }, success = {
                    updateState(ProcessingState.Active(it))
                })
            }
        }
        is ProcessingIntent.Complete -> updateState(ProcessingState.Result(intent.path))
        is ProcessingIntent.Error -> {
            clearConversion()
            updateState(ProcessingState.Error(intent.exception))
        }
        is ProcessingIntent.Close -> {
            clearConversion()
            emitEffect(ProcessingEffect.Close)
        }
    }

    private fun clearConversion() {
        fileConversion?.cancel()
        fileConversion = null
    }
}