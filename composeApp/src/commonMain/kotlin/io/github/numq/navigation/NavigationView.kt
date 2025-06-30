package io.github.numq.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.numq.drawing.DrawingCanvas
import org.koin.compose.koinInject

@Composable
fun NavigationView(feature: NavigationFeature) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            BoxWithConstraints(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                DrawingCanvas(recognitionService = koinInject())
            }
        }
    }
}