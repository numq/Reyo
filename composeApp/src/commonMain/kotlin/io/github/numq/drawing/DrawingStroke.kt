package io.github.numq.drawing

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class DrawingStroke(
    val path: Path,
    val color: Color,
    val width: Float
)