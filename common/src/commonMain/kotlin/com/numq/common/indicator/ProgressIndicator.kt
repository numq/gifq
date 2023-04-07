package com.numq.common.indicator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProgressIndicator(modifier: Modifier = Modifier.fillMaxWidth().height(4.dp), percentage: Float) {
    Canvas(modifier) {
        drawRect(
            color = Color.LightGray,
            size = Size(size.width, 4.dp.toPx())
        )
        drawRect(
            color = Color.Green,
            size = Size(percentage / 100 * size.width, 4.dp.toPx())
        )
    }
}