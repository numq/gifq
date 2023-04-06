package com.numq.common.viewmodel

import kotlinx.coroutines.*
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

actual abstract class SharedViewModel {
    private class CloseableCoroutineScope(
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
    ) : CoroutineScope, Closeable {
        override val coroutineContext: CoroutineContext = dispatcher + SupervisorJob()
        override fun close() = coroutineContext.cancel()
    }

    actual val coroutineScope: CoroutineScope = CloseableCoroutineScope()
}