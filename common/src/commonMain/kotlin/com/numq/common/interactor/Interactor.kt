package com.numq.common.interactor

import kotlinx.coroutines.*

abstract class Interactor<in T, out R> {

    private var job: Job? = null

    abstract suspend fun execute(arg: T): R

    operator fun invoke(
        arg: T,
        error: (Exception) -> Unit,
        success: (R) -> Unit,
    ) {
        job = GlobalScope.launch {
            withContext(Dispatchers.Main) {
                runCatching { execute(arg) }.onFailure { error(Exception(it)) }.onSuccess(success)
            }
        }
    }

    fun cancel() {
        job?.cancel()
    }
}