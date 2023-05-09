package com.numq.common.collector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

actual object Collector {
    @Composable
    actual fun <T> collect(flow: StateFlow<T>): T = flow.collectAsState().value

    @Composable
    actual fun <T> collect(flow: Flow<T>, initialValue: T?): T? = flow.collectAsState(initialValue).value
}