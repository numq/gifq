package com.numq.common.viewmodel

import kotlinx.coroutines.CoroutineScope

expect abstract class SharedViewModel() {
    val coroutineScope: CoroutineScope
}