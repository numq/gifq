package com.numq.common.converter

import com.numq.common.interactor.Interactor
import com.numq.common.settings.Settings

class CalculateSize(
    private val converterService: ConverterService,
) : Interactor<Settings, Long>() {
    override suspend fun execute(arg: Settings) = converterService.calculateFileSize(arg).getOrThrow()
}