package io.github.numq.menu

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*

@Composable
fun MenuBackground() {
    val coroutineScope = rememberCoroutineScope()

    val letters = remember { "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ" }

    val animatedLetters = remember { mutableStateListOf<Pair<String, Animatable<Offset, AnimationVector2D>>>() }

    var animationJob by remember { mutableStateOf<Job?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            coroutineScope.cancel()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray), contentAlignment = Alignment.Center) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().fillMaxHeight(.5f)) {
            DisposableEffect(maxWidth, maxHeight) {
                animationJob = coroutineScope.launch {
                    while (isActive) {
                        val letter = letters.random().toString()

                        val initialOffset = Offset(
                            x = (0..(maxWidth.value - 24f).toInt()).random().toFloat(), y = -48f
                        )

                        val targetOffset = Offset(x = initialOffset.x, y = maxHeight.value + 48f)

                        val animatable = Animatable(initialOffset, Offset.VectorConverter)

                        animatedLetters.add(letter to animatable)

                        launch {
                            animatable.animateTo(
                                targetValue = targetOffset, animationSpec = tween(
                                    durationMillis = 5000, easing = LinearEasing
                                )
                            )

                            animatedLetters.remove(letter to animatable)
                        }

                        delay(500L)
                    }
                }

                onDispose {
                    animationJob?.cancel()

                    animationJob = null

                    animatedLetters.clear()
                }
            }

            animatedLetters.forEach { (letter, animatable) ->
                val offset = animatable.value

                Text(text = letter, modifier = Modifier.graphicsLayer {
                    translationX = offset.x
                    translationY = offset.y
                }.clickable(interactionSource = null, indication = null) {
                    coroutineScope.launch {
                        animatable.snapTo(animatable.targetValue)
                    }
                }, fontSize = 24.sp)
            }
        }
    }
}