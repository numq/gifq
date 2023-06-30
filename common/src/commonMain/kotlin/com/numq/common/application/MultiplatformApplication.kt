package com.numq.common.application

import androidx.compose.runtime.Composable
import com.numq.common.di.appModule
import com.numq.common.navigation.Navigation
import com.numq.common.theme.ApplicationTheme
import org.koin.core.context.GlobalContext.startKoin

@Composable
fun MultiplatformApplication() {
    startKoin {
        modules(appModule)
    }
    ApplicationTheme {
        Navigation()
    }
}
