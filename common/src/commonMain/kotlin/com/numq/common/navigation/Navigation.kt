package com.numq.common.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.numq.common.collector.Collector.collect
import com.numq.common.processing.ProcessingFeature
import com.numq.common.processing.ProcessingScreen
import com.numq.common.setup.SetupFeature
import com.numq.common.setup.SetupScreen
import org.koin.java.KoinJavaComponent.inject

@Composable
fun Navigation() {

    val vm: NavigationViewModel by inject(NavigationViewModel::class.java)

    Scaffold(Modifier.fillMaxSize()) { paddingValues ->
        Box(Modifier.padding(paddingValues).fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val destination = collect(vm.destination)) {
                is Destination.Setup -> {
                    val feature: SetupFeature by inject(SetupFeature::class.java)
                    SetupScreen(feature) {
                        vm.navigateTo(Destination.Processing(it))
                    }
                }
                is Destination.Processing -> {
                    val feature: ProcessingFeature by inject(ProcessingFeature::class.java)
                    ProcessingScreen(feature, destination.settings, close = {
                        vm.navigateTo(Destination.Setup)
                    })
                }
            }
        }
    }
}