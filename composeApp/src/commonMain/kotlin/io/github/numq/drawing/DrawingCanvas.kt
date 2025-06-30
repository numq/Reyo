package io.github.numq.drawing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import io.github.numq.recognition.TextRecognitionService
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawingCanvas(initialMode: DrawingMode = DrawingMode.DRAW, recognitionService: TextRecognitionService) {
    val coroutineScope = rememberCoroutineScope()

    var recognizedText by remember { mutableStateOf("Nothing recognized") }

    var mode by remember { mutableStateOf(initialMode) }

    val paths = remember { mutableStateListOf<Path>() }

    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }

    val strokeColor = if (mode == DrawingMode.DRAW) Color.Black else Color.White

    val strokeWidth = if (mode == DrawingMode.DRAW) 8f else 40f

    var canvasSize by remember { mutableStateOf<Size>(Size.Zero) }

    Column(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(Modifier.fillMaxWidth().fillMaxHeight(.9f).onSizeChanged { size ->
            canvasSize = Size(width = size.width.toFloat(), height = size.height.toFloat())
        }.pointerInput(mode, strokeColor, strokeWidth) {
            detectDragGestures(
                onDragStart = { offset ->
                    currentStroke = listOf(offset)
                },
                onDrag = { change, _ ->
                    currentStroke = currentStroke + change.position

                    change.consume()
                },
                onDragEnd = {
                    if (currentStroke.size > 1) {
                        paths.add(buildPathFromPoints(currentStroke))
                    }

                    currentStroke = emptyList()

                    val bitmap = captureCanvasBitmap(canvasSize, paths, strokeColor, strokeWidth)

                    val skiaBitmap = bitmap.asSkiaBitmap()

                    val bytes = skiaBitmap.readPixels()

                    if (bytes != null) {
                        val width = skiaBitmap.width

                        val height = skiaBitmap.height

                        val bytesPerPixel = skiaBitmap.bytesPerPixel

                        val bytesPerLine = skiaBitmap.rowBytes

                        coroutineScope.launch {
                            recognizedText = recognitionService.recognize(
                                bytes = bytes,
                                width = width,
                                height = height,
                                bytesPerPixel = bytesPerPixel,
                                bytesPerLine = bytesPerLine
                            )
                                .fold(onSuccess = { text ->
                                    text
                                }, onFailure = { throwable ->
                                    throwable.message ?: "Unknown error"
                                })
                        }
                    }
                }
            )
        }) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(color = Color.White, size = size)

                paths.forEach { path ->
                    drawPath(
                        path = path,
                        color = strokeColor,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }

                if (currentStroke.size > 1) {
                    drawPath(
                        path = buildPathFromPoints(currentStroke),
                        color = strokeColor,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }
        }

        Text(recognizedText, modifier = Modifier.weight(1f))
    }
}

private fun buildPathFromPoints(points: List<Offset>) = Path().apply {
    if (points.isNotEmpty()) {
        moveTo(points.first().x, points.first().y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
    }
}

private fun captureCanvasBitmap(
    size: Size,
    paths: List<Path>,
    strokeColor: Color,
    strokeWidth: Float
) = ImageBitmap(size.width.toInt(), size.height.toInt()).let { imageBitmap ->
    Canvas(imageBitmap).apply {
        drawRect(0f, 0f, size.width, size.height, Paint().apply { color = Color.White })

        paths.forEach { path ->
            drawPath(
                path = path,
                paint = Paint().apply {
                    color = strokeColor
                    this.strokeWidth = strokeWidth
                    strokeCap = StrokeCap.Round
                    strokeJoin = StrokeJoin.Round
                }
            )
        }
    }

    imageBitmap
}