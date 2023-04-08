package com.numq.common.converter

import com.numq.common.interactor.Interactor
import com.numq.common.settings.Settings
import kotlinx.coroutines.flow.Flow

class ConvertMovieToGif constructor(
    private val converter: Converter,
) : Interactor<Settings, Flow<ConversionStatus>>() {
    override suspend fun execute(arg: Settings) = converter.convertMovieToGif(arg).getOrThrow()
}