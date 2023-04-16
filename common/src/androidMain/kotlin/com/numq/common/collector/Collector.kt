package com.numq.common.collector

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

actual object Collector {
    @Composable
    actual fun <T> collect(flow: StateFlow<T>): T = flow.collectAsStateWithLifecycle().value

    @Composable
    actual fun <T> collect(flow: Flow<T>, initial: T?): T? = flow.collectAsStateWithLifecycle(initial).value
}