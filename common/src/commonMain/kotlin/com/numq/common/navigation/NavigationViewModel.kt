package com.numq.common.navigation

import com.numq.common.viewmodel.SharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationViewModel : SharedViewModel() {

    private val _destination = MutableStateFlow<Destination>(Destination.Setup)
    val destination = _destination.asStateFlow()

    fun navigateTo(destination: Destination) {
        _destination.value = destination
    }
}