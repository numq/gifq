package com.numq.common.mvi

import com.numq.common.viewmodel.SharedViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class Feature<S : State, I : Intent, E : Effect>(defaultState: S) : SharedViewModel() {

    abstract fun reduce(state: S, intent: I, updateState: (S) -> Unit, emitEffect: (E) -> Unit)

    private val _state = MutableStateFlow(defaultState)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<E>()
    val effect = _effect.asSharedFlow()

    fun dispatch(intent: I) {
        reduce(_state.value, intent, updateState = {
            _state.value = it
        }, emitEffect = {
            coroutineScope.launch { _effect.emit(it) }
        })
    }
}