package io.github.numq.reference

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp

@Composable
fun TextReference(modifier: Modifier, text: String, onContentChange: (IntArray) -> Unit) {
    val canvasDrawScope = remember { CanvasDrawScope() }

    val textMeasurer = rememberTextMeasurer()

    BoxWithConstraints(modifier = modifier.alpha(0.5f)) {
        val canvasSize = remember(maxWidth, maxHeight) {
            Size(maxWidth.value, maxHeight.value)
        }

        val fontSize = remember(canvasSize) {
            minOf(canvasSize.width, canvasSize.height).sp / 2
        }

        val textLayoutResult = remember(text, fontSize) {
            textMeasurer.measure(
                text = text, style = TextStyle(color = Color.Black, fontSize = fontSize)
            )
        }

        val drawContent: DrawScope.() -> Unit = {
            drawText(
                textLayoutResult = textLayoutResult, topLeft = Offset(
                    x = size.width / 2 - textLayoutResult.size.width / 2f,
                    y = size.height / 2 - textLayoutResult.size.height / 2f
                )
            )
        }

        LaunchedEffect(canvasSize, text) {
            val bitmap = ImageBitmap(canvasSize.width.toInt(), canvasSize.height.toInt())

            val canvas = androidx.compose.ui.graphics.Canvas(bitmap)

            canvasDrawScope.draw(
                density = Density(1f),
                layoutDirection = LayoutDirection.Ltr,
                canvas = canvas,
                size = canvasSize
            ) {
                drawRect(color = Color.White)

                drawContent()
            }

            onContentChange(bitmap.toPixelMap().buffer)
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawContent()
        }
    }
}
