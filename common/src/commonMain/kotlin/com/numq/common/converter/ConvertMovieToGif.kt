package com.numq.common.converter

import com.numq.common.interactor.Interactor
import com.numq.common.settings.Settings
import kotlinx.coroutines.flow.Flow

class ConvertMovieToGif(
    private val converterService: ConverterService,
) : Interactor<Settings, Flow<ConversionStatus>>() {
    override suspend fun execute(arg: Settings) = converterService.convertMovieToGif(arg)
}