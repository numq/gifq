package com.numq.common.collector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.EmptyCoroutineContext

actual object Collector {
    @Composable
    actual fun <T> collectLatest(flow: Flow<T>): T? = produceState<T?>(
        null,
        this,
        EmptyCoroutineContext
    ) {
        flow.collectLatest { value = it }
    }.value

    @Composable
    actual fun <T> collect(flow: StateFlow<T>): T = flow.collectAsState().value

    @Composable
    actual fun <T> collect(flow: Flow<T>, initialValue: T?): T? = flow.collectAsState(initialValue).value
}