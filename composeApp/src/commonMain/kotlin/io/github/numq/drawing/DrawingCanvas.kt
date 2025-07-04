package io.github.numq.drawing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawingCanvas(
    modifier: Modifier,
    initialMode: DrawingMode = DrawingMode.DRAW,
    strokeWidth: Float = 8f,
    onContentChange: (IntArray) -> Unit
) {
    val canvasDrawScope = remember { CanvasDrawScope() }

    var mode by remember { mutableStateOf(initialMode) }

    val paths = remember { mutableStateListOf<Path>() }

    val points = remember { mutableStateListOf<Offset>() }

    val drawContent: DrawScope.() -> Unit = {
        paths.forEach { path ->
            drawPath(
                path = path,
                color = if (mode == DrawingMode.DRAW) Color.Black else Color.White,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        if (points.isNotEmpty()) {
            drawPath(
                path = buildPath(points),
                color = if (mode == DrawingMode.DRAW) Color.Black else Color.White,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }

    BoxWithConstraints(modifier = modifier) {
        val canvasSize = remember(maxWidth, maxHeight) {
            Size(maxWidth.value, maxHeight.value)
        }

        Box(modifier = Modifier.fillMaxSize().pointerInput(canvasSize) {
            detectDragGestures(onDragStart = { offset ->
                points.add(offset)
            }, onDrag = { change, _ ->
                points.add(change.position)

                change.consume()
            }, onDragEnd = {
                if (points.isNotEmpty()) {
                    val path = buildPath(points)

                    paths.add(path)

                    points.clear()

                    val bitmap = ImageBitmap(canvasSize.width.toInt(), canvasSize.height.toInt())

                    val canvas = Canvas(bitmap)

                    canvasDrawScope.draw(
                        density = Density(1f),
                        layoutDirection = LayoutDirection.Ltr,
                        canvas = canvas,
                        size = canvasSize,
                    ) {
                        drawRect(color = Color.White)

                        drawContent()
                    }

                    onContentChange(bitmap.toPixelMap().buffer)
                }
            })
        }) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawContent()
            }
        }
    }
}

private fun buildPath(points: List<Offset>) = Path().apply {
    points.forEachIndexed { index, (x, y) ->
        if (index == 0) moveTo(x, y) else lineTo(x, y)
    }
}