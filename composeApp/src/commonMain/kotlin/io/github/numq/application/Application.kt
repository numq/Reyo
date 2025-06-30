package io.github.numq.application

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.numq.di.appModule
import io.github.numq.navigation.NavigationView
import org.koin.compose.koinInject
import org.koin.core.context.startKoin

@Composable
fun App() {
    startKoin { modules(appModule) }

    MaterialTheme {
        NavigationView(feature = koinInject())
    }
}