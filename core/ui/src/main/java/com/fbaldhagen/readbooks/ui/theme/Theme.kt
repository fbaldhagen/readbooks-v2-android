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
    primary = Amber500,
    onPrimary = WarmDark900,
    primaryContainer = AmberContainer,
    onPrimaryContainer = OnAmberContainerDark,
    secondary = Orange400,
    onSecondary = WarmDark900,
    secondaryContainer = WarmDark700,
    onSecondaryContainer = WarmGray300,
    tertiary = Amber300,
    onTertiary = WarmDark900,
    background = WarmDark900,
    onBackground = WarmWhite,
    surface = WarmDark800,
    onSurface = WarmWhite,
    surfaceVariant = AmberContainerDark,
    onSurfaceVariant = WarmGray300,
    outline = WarmDark600,
    outlineVariant = WarmDark700,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainer,
    onErrorContainer = ErrorDark
)

private val LightColorScheme = lightColorScheme(
    primary = Amber500,
    onPrimary = WarmWhite,
    primaryContainer = AmberContainerLight,
    onPrimaryContainer = OnAmberContainerLight,
    secondary = Orange400,
    onSecondary = WarmWhite,
    secondaryContainer = WarmCreamVariant,
    onSecondaryContainer = WarmGray600,
    tertiary = Amber400,
    onTertiary = WarmWhite,
    background = WarmCream,
    onBackground = WarmDark800,
    surface = WarmWhite,
    onSurface = WarmDark800,
    surfaceVariant = WarmCreamVariant,
    onSurfaceVariant = WarmGray500,
    outline = WarmGray300,
    outlineVariant = WarmGray100,
    error = Error,
    onError = OnError,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
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