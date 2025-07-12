package io.github.numq.di

import io.github.numq.comparison.ComparisonService
import io.github.numq.comparison.tf.TFComparisonService
import io.github.numq.navigation.NavigationFeature
import io.github.numq.navigation.NavigationReducer
import kotlinx.coroutines.runBlocking
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

private val recognition = module {
    single { TFComparisonService() } bind ComparisonService::class onClose {
        runBlocking {
            it?.close()?.getOrThrow()
        }
    }
}

private val navigation = module {
    single { NavigationReducer() }

    single { NavigationFeature(get()) } onClose { it?.close() }
}

internal val appModule = listOf(recognition, navigation)