package io.github.numq.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.numq.comparison.ComparisonService
import io.github.numq.drawing.DrawingCanvas
import io.github.numq.reference.TextReference
import org.koin.compose.koinInject

@Composable
fun NavigationView(feature: NavigationFeature, comparisonService: ComparisonService = koinInject()) {
    val coroutineScope = rememberCoroutineScope()

    val letters = remember { "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ" }

    val referenceText = remember { letters.split("").random() }

    val width = 512

    val height = 512

    val (src, setSrc) = remember { mutableStateOf<IntArray?>(null) }

    val (dst, setDst) = remember { mutableStateOf<IntArray?>(null) }

    LaunchedEffect(dst) {
        if (src != null && dst != null) {
            comparisonService.compare(src = src, dst = dst, width = width, height = height).getOrThrow()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            BoxWithConstraints(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier.size(width.dp, height.dp).aspectRatio(1f), contentAlignment = Alignment.Center
                ) {
                    TextReference(modifier = Modifier.fillMaxSize(), text = referenceText, onContentChange = setSrc)
                    DrawingCanvas(modifier = Modifier.fillMaxSize(), onContentChange = setDst)
                }
            }
        }
    }
}