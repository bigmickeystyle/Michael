package com.mikesmith.michael.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = Purple200,
    secondary = Color.DarkGray,
    background = Color.Black,
    onSurface = Color.Red,
    surface = Color.Blue
)

private val LightColorPalette = lightColorScheme(
    primary = Purple500,
    secondary = Color.DarkGray,
    background = Color.White,
    onSurface = Color.Green,
    surface = Color.Blue
    /* Other default colors to override
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun MichaelTheme(darkTheme: Boolean = true, content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }


    MaterialTheme(
        colorScheme = colors,
        shapes = Shapes,
        content = content
    )
}