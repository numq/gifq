package com.numq.common.processing

import com.numq.common.converter.ConvertMovieToGif
import com.numq.common.mvi.Feature

class ProcessingFeature constructor(
    private val convertMovieToGif: ConvertMovieToGif,
) : Feature<ProcessingState, ProcessingIntent, ProcessingEffect>(ProcessingState.Loading) {

    private var fileConversion: ConvertMovieToGif? = null

    override fun reduce(
        state: ProcessingState,
        intent: ProcessingIntent,
        updateState: (ProcessingState) -> Unit,
        emitEffect: (ProcessingEffect) -> Unit,
    ) = when (intent) {
        is ProcessingIntent.Start -> {
            fileConversion = convertMovieToGif.apply {
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