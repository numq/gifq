package com.numq.common.processing

import com.numq.common.mvi.Effect

sealed class ProcessingEffect private constructor() : Effect {
    object Close : ProcessingEffect()
}