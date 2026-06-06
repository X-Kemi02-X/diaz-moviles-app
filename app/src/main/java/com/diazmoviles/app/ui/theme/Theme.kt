package com.diazmoviles.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Black,
    onPrimary = White,
    primaryContainer = Gray90,
    onPrimaryContainer = Gray10,
    secondary = Gray40,
    onSecondary = White,
    secondaryContainer = Gray95,
    onSecondaryContainer = Gray10,
    tertiary = AccentGold,
    onTertiary = White,
    background = White,
    onBackground = Gray10,
    surface = White,
    onSurface = Gray10,
    surfaceVariant = Gray95,
    onSurfaceVariant = Gray40,
    error = Color(0xFFD32F2F),
    onError = White,
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFB71C1C),
    outline = Gray70,
    outlineVariant = Gray90
)

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,
    primaryContainer = Gray30,
    onPrimaryContainer = Gray90,
    secondary = Gray60,
    onSecondary = Black,
    secondaryContainer = Gray30,
    onSecondaryContainer = Gray90,
    tertiary = AccentGold,
    onTertiary = Black,
    background = Gray10,
    onBackground = Gray90,
    surface = Gray10,
    onSurface = Gray90,
    surfaceVariant = Gray20,
    onSurfaceVariant = Gray70,
    error = Color(0xFFEF9A9A),
    onError = Black,
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color(0xFFFFCDD2),
    outline = Gray50,
    outlineVariant = Gray30
)

@Composable
fun DiazmovilesproyectoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
