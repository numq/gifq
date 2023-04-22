package com.numq.common.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColors(
    primary = Color(0xFF3F51B5),
    primaryVariant = Color(0xFF303F9F),
    secondary = Color(0xFF008577),
    secondaryVariant = Color(0xFF00574B),
    onPrimary = Color.White,
    onSecondary = Color.White,
    surface = Color.White,
    onSurface = Color.Black
)

private val DarkColors = darkColors(
    primary = Color(0xFF7986CB),
    primaryVariant = Color(0xFF5C6BC0),
    secondary = Color(0xFF26A69A),
    secondaryVariant = Color(0xFF00796B),
    onPrimary = Color.White,
    onSecondary = Color.White,
    surface = Color(0xFF121212),
    onSurface = Color.White
)

@Composable
fun GifqTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content,
        colors = if (darkTheme) DarkColors else LightColors
    )
}