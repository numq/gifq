package com.numq.common.viewmodel

import kotlinx.coroutines.CoroutineScope

expect abstract class SharedViewModel constructor() {
    val coroutineScope: CoroutineScope
}