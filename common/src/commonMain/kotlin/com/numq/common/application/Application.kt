package com.numq.common.application

import androidx.compose.runtime.Composable
import com.numq.common.di.appModule
import com.numq.common.navigation.Navigation
import com.numq.common.theme.GifqTheme
import org.koin.core.context.GlobalContext.startKoin

@Composable
fun Application() {
    startKoin {
        modules(appModule)
    }
    GifqTheme {
        Navigation()
    }
}
