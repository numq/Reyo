package io.github.numq.di

import io.github.numq.navigation.NavigationFeature
import io.github.numq.navigation.NavigationReducer
import io.github.numq.recognition.TextRecognitionService
import kotlinx.coroutines.runBlocking
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

private val recognition = module {
    single { TextRecognitionService.Implementation() } bind TextRecognitionService::class onClose {
        runBlocking {
            it?.close()?.getOrNull()
        }
    }
}

private val navigation = module {
    single { NavigationReducer() }

    single { NavigationFeature(get()) } onClose { it?.close() }
}

internal val appModule = listOf(recognition, navigation)