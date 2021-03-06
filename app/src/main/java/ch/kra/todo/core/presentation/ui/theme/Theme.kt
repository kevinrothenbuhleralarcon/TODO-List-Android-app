package ch.kra.todo.core.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = MainRed,
    primaryVariant = MainRed,
    onPrimary = Color.White,
    secondary = MainRed,
    secondaryVariant = MainRed,
    onSecondary = Color.White,
    background = Color.White,
    surface = CardColor,
    error = TextErrorColor,
    onBackground = Color.Black,
    onSurface = Color.Black

)

private val LightColorPalette = lightColors(
    primary = MainRed,
    primaryVariant = MainRed,
    onPrimary = Color.White,
    secondary = MainRed,
    secondaryVariant = MainRed,
    onSecondary = Color.White,
    background = Color.White,
    surface = CardColor,
    error = TextErrorColor,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun TodoTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}