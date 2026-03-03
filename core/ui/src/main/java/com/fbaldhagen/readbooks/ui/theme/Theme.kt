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
import com.fbaldhagen.readbooks.domain.model.ThemeMode

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

private val SepiaColorScheme = lightColorScheme(
    primary = Color(0xFF8B5E3C),
    onPrimary = Color(0xFFF5ECD7),
    primaryContainer = Color(0xFFD4A574),
    onPrimaryContainer = Color(0xFF3E2008),
    secondary = Color(0xFF9C6B3C),
    onSecondary = Color(0xFFF5ECD7),
    secondaryContainer = Color(0xFFE8D5B7),
    onSecondaryContainer = Color(0xFF3E2008),
    tertiary = Color(0xFF7A5230),
    onTertiary = Color(0xFFF5ECD7),
    background = Color(0xFFF5ECD7),
    onBackground = Color(0xFF2C1810),
    surface = Color(0xFFFAF3E8),
    onSurface = Color(0xFF2C1810),
    surfaceVariant = Color(0xFFEDE0CC),
    onSurfaceVariant = Color(0xFF5C4033),
    outline = Color(0xFFB8977A),
    outlineVariant = Color(0xFFD4C4A8),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFF5ECD7),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun ReadBooksTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.SEPIA -> SepiaColorScheme
        ThemeMode.SYSTEM -> if (systemDark) DarkColorScheme else LightColorScheme
    }

    val darkTheme = themeMode == ThemeMode.DARK ||
            (themeMode == ThemeMode.SYSTEM && systemDark)

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