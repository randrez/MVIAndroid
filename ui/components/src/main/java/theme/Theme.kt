package theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun SCGTSTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColors,
        typography = SCGTSTypography,
        shapes = SCGTSShapes,
        content = content
    )
}

private val LightColors = lightColors(
    primary = Blue500,
    primaryVariant = Blue500,
    onPrimary = Color.White,
    secondary = Color.Transparent,
    secondaryVariant = Color.Transparent,
    onSecondary = N500,
    error = DarkRed,
    onError = Color.White,
    background = Color.White,
    onBackground = N900,
    surface = Color.White,
    onSurface = N900,
)