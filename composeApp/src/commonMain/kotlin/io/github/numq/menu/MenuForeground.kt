package io.github.numq.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuForeground() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth().background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "REYO", fontSize = 64.sp)
        }
        Column(
            modifier = Modifier.weight(1f).padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(shape = CircleShape.copy(CornerSize(8.dp)), onClick = {

            }) {
                Text(text = "")
            }
            Button(shape = CircleShape.copy(CornerSize(8.dp)), onClick = {

            }) {
                Text(text = "")
            }
            Button(shape = CircleShape.copy(CornerSize(8.dp)), onClick = {

            }) {
                Text(text = "")
            }
        }
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth().background(Color.White),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("")
            }
        }
    }
}