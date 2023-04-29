package com.numq.common.collector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

actual object Collector {

    // TODO: 4/27/2023 provide custom collectWithLifecycle implementation
    
    @Composable
    actual fun <T> collect(flow: StateFlow<T>): T = flow.collectAsState().value
//    actual fun <T> collect(flow: StateFlow<T>): T = flow.collectAsStateWithLifecycle().value

    @Composable
    actual fun <T> collect(flow: Flow<T>, initial: T?): T? = flow.collectAsState(initial).value
//    actual fun <T> collect(flow: Flow<T>, initial: T?): T? = flow.collectAsStateWithLifecycle(initial).value
}