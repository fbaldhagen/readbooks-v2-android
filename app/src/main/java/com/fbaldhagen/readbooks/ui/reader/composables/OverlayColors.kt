package com.fbaldhagen.readbooks.ui.reader.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.fbaldhagen.readbooks.domain.model.ReaderTheme
import com.fbaldhagen.readbooks.ui.reader.presentation.toReadiumTheme

data class OverlayColors(
    val scrim: Color,
    val content: Color,
    val contentSubtle: Color
)

@Composable
fun overlayColorsFor(theme: ReaderTheme): OverlayColors {
    val bg = Color(theme.toReadiumTheme().backgroundColor)
    val fg = Color(theme.toReadiumTheme().contentColor)
    return OverlayColors(
        scrim = bg,
        content = fg.copy(alpha = 0.85f),
        contentSubtle = fg.copy(alpha = 0.5f)
    )
}