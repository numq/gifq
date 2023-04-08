package com.numq.common.converter

import com.numq.common.interactor.Interactor
import com.numq.common.settings.Settings

class CalculateSize constructor(
    private val converter: Converter,
) : Interactor<Settings, Long>() {
    override suspend fun execute(arg: Settings) = converter.calculateFileSize(arg).getOrThrow()
}