package com.numq.common.interactor

import kotlinx.coroutines.*

abstract class Interactor<in T, out R> {

    private var job: Job? = null

    abstract suspend fun execute(arg: T): R

    operator fun invoke(
        coroutineScope: CoroutineScope,
        arg: T,
        error: (Exception) -> Unit,
        success: (R) -> Unit,
    ) {
        job = coroutineScope.launch {
            runCatching { execute(arg) }.onFailure {
                withContext(Dispatchers.Default) {
                    error(Exception(it))
                }
            }.onSuccess {
                withContext(Dispatchers.Default) {
                    success(it)
                }
            }
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
    }
}