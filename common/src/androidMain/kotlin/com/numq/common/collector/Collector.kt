package com.numq.common.collector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

actual object Collector {
    @Composable
    actual fun <T> collectLatest(flow: Flow<T>): T? {
        val lifecycleOwner = LocalLifecycleOwner.current
        val state = remember { mutableStateOf<T?>(null) }
        LaunchedEffect(lifecycleOwner) {
            flow.collectLatest { value ->
                state.value = value
            }
        }
        return state.value
    }

    @Composable
    actual fun <T> collect(flow: StateFlow<T>) = collect(flow, flow.value) ?: flow.value

    @Composable
    actual fun <T> collect(flow: Flow<T>, initialValue: T?): T? {
        val lifecycleOwner = LocalLifecycleOwner.current
        val state = remember { mutableStateOf(initialValue) }
        LaunchedEffect(lifecycleOwner) {
            flow.collect { value ->
                state.value = value
            }
        }
        return state.value
    }
}