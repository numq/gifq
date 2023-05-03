package com.numq.common.collector

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

expect object Collector {
    @Composable
    fun <T> collect(flow: StateFlow<T>): T

    @Composable
    fun <T> collect(flow: Flow<T>, initialValue: T? = null): T?
}