package io.github.numq.reference

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp

@Composable
fun TextReference(text: String) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier.fillMaxSize().alpha(.5f)) {
        drawRect(color = Color.White, size = size)

        val textLayoutResult = textMeasurer.measure(
            text = text,
            style = TextStyle(
                color = Color.Black,
                fontSize = minOf(size.width, size.height).sp / 2,
            )
        )

        val textSize = textLayoutResult.size

        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                x = size.width / 2 - textSize.width / 2f,
                y = size.height / 2 - textSize.height / 2f
            )
        )
    }
}