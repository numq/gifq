package com.numq.common.di

import com.numq.common.converter.CalculateSize
import com.numq.common.converter.ConvertMovieToGif
import com.numq.common.converter.Converter
import com.numq.common.navigation.NavigationViewModel
import com.numq.common.processing.ProcessingFeature
import com.numq.common.settings.GetSettings
import com.numq.common.settings.SettingsService
import com.numq.common.setup.SetupFeature
import org.koin.dsl.bind
import org.koin.dsl.module

val converter = module {
    single { Converter() }
    factory { CalculateSize(get()) }
    factory { ConvertMovieToGif(get()) }
}

val settings = module {
    single { SettingsService.Implementation() } bind SettingsService::class
    factory { GetSettings(get()) }
}

val setup = module {
    factory { SetupFeature(get(), get()) }
}

val processing = module {
    factory { ProcessingFeature(get()) }
}

val navigation = module {
    single { NavigationViewModel() }
}

val appModule = converter + settings + setup + processing + navigation