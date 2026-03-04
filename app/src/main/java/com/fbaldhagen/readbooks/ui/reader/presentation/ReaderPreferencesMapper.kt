package com.fbaldhagen.readbooks.ui.reader.presentation

import com.fbaldhagen.readbooks.domain.model.ReaderFontFamily
import com.fbaldhagen.readbooks.domain.model.ReaderPreferences
import com.fbaldhagen.readbooks.domain.model.ReaderTheme
import com.fbaldhagen.readbooks.domain.model.ThemeMode
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.preferences.FontFamily
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.navigator.preferences.Theme as ReadiumTheme

@OptIn(ExperimentalReadiumApi::class)
fun ReaderPreferences.toEpubPreferences(): EpubPreferences = EpubPreferences(
    fontSize = fontSize,
    fontFamily = fontFamily.toReadiumFontFamily(),
    theme = theme.toReadiumTheme(),
    pageMargins = pageMargins,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
    publisherStyles = false
)

fun ReaderFontFamily.toReadiumFontFamily(): FontFamily? = when (this) {
    ReaderFontFamily.DEFAULT -> null
    ReaderFontFamily.SERIF -> FontFamily("serif")
    ReaderFontFamily.SANS_SERIF -> FontFamily("sans-serif")
    ReaderFontFamily.DYSLEXIC -> FontFamily("OpenDyslexic")
    ReaderFontFamily.MONOSPACE -> FontFamily("monospace")
}

fun ReaderTheme.toReadiumTheme(): ReadiumTheme = when (this) {
    ReaderTheme.LIGHT -> ReadiumTheme.LIGHT
    ReaderTheme.DARK -> ReadiumTheme.DARK
    ReaderTheme.SEPIA -> ReadiumTheme.SEPIA
}

fun ThemeMode.toReaderTheme(): ReaderTheme = when (this) {
    ThemeMode.DARK -> ReaderTheme.DARK
    ThemeMode.SEPIA -> ReaderTheme.SEPIA
    ThemeMode.LIGHT, ThemeMode.SYSTEM -> ReaderTheme.LIGHT
}