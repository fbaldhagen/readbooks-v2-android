package com.fbaldhagen.readbooks.ui.reader.presentation

import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.preferences.FontFamily
import org.readium.r2.navigator.preferences.Theme as ReadiumTheme
import org.readium.r2.shared.ExperimentalReadiumApi

data class ReaderPreferences(
    val fontSize: Double = 1.0,
    val fontFamily: ReaderFontFamily = ReaderFontFamily.DEFAULT,
    val theme: ReaderTheme = ReaderTheme.LIGHT,
    val pageMargins: Double = 1.5,
    val lineHeight: Double = 1.5,
    val letterSpacing: Double = 0.0
) {
    @OptIn(ExperimentalReadiumApi::class)
    fun toEpubPreferences(): EpubPreferences = EpubPreferences(
        fontSize = fontSize,
        fontFamily = fontFamily.toReadiumFontFamily(),
        theme = theme.toReadiumTheme(),
        pageMargins = pageMargins,
        lineHeight = lineHeight,
        letterSpacing = letterSpacing,
        publisherStyles = false
    )
}

enum class ReaderFontFamily(val displayName: String) {
    DEFAULT("Default"),
    SERIF("Serif"),
    SANS_SERIF("Sans Serif"),
    DYSLEXIC("OpenDyslexic"),
    MONOSPACE("Monospace");

    fun toReadiumFontFamily(): FontFamily? = when (this) {
        DEFAULT -> null
        SERIF -> FontFamily("serif")
        SANS_SERIF -> FontFamily("sans-serif")
        DYSLEXIC -> FontFamily("OpenDyslexic")
        MONOSPACE -> FontFamily("monospace")
    }
}

enum class ReaderTheme(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SEPIA("Sepia");

    fun toReadiumTheme(): ReadiumTheme = when (this) {
        LIGHT -> ReadiumTheme.LIGHT
        DARK -> ReadiumTheme.DARK
        SEPIA -> ReadiumTheme.SEPIA
    }
}