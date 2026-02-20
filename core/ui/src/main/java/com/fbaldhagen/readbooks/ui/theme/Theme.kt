package com.fbaldhagen.readbooks.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE8C46A),
    onPrimary = Color(0xFF3B2A00),
    primaryContainer = Color(0xFF553E00),
    onPrimaryContainer = Color(0xFFFFDF9B),
    secondary = Color(0xFFBFAAA0),
    onSecondary = Color(0xFF322016),
    background = Color(0xFF1A1611),
    onBackground = Color(0xFFEDE0D4),
    surface = Color(0xFF1A1611),
    onSurface = Color(0xFFEDE0D4),
    surfaceVariant = Color(0xFF4A4030),
    onSurfaceVariant = Color(0xFFCDB99A),
    outline = Color(0xFF7A6A55),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6B4F00),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDF9B),
    onPrimaryContainer = Color(0xFF221600),
    secondary = Color(0xFF6B5C45),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFFFF8F0),
    onBackground = Color(0xFF1E1B16),
    surface = Color(0xFFFFF8F0),
    onSurface = Color(0xFF1E1B16),
    surfaceVariant = Color(0xFFF0E0C8),
    onSurfaceVariant = Color(0xFF4E4539),
    outline = Color(0xFF7A6A55),
)

@Composable
fun ReadBooksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}