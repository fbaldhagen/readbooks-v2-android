package com.fbaldhagen.readbooks.domain.model

data class ReaderPreferences(
    val fontSize: Double = 1.0,
    val fontFamily: ReaderFontFamily = ReaderFontFamily.DEFAULT,
    val theme: ReaderTheme = ReaderTheme.LIGHT,
    val pageMargins: Double = 1.5,
    val lineHeight: Double = 1.5,
    val letterSpacing: Double = 0.0
)

enum class ReaderFontFamily(val displayName: String) {
    DEFAULT("Default"),
    SERIF("Serif"),
    SANS_SERIF("Sans Serif"),
    DYSLEXIC("OpenDyslexic"),
    MONOSPACE("Monospace")
}

enum class ReaderTheme(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SEPIA("Sepia")
}