package com.fbaldhagen.readbooks.domain.model

data class TtsPlaybackState(
    val isPlaying: Boolean = false,
    val bookTitle: String? = null,
    val currentText: String? = null,
    val speed: Float = 1.0f
)