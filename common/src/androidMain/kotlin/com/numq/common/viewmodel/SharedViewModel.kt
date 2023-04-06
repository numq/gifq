package com.numq.common.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import androidx.lifecycle.ViewModel as AndroidViewModel

actual abstract class SharedViewModel : AndroidViewModel() {
    actual val coroutineScope: CoroutineScope = viewModelScope
}