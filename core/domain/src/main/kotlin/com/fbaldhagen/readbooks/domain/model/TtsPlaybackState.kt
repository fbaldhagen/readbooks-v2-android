package com.fbaldhagen.readbooks.domain.model

sealed interface TtsPlaybackState {
    data object Idle : TtsPlaybackState
    data class Playing(
        val utterance: TtsUtterance,
        val settings: TtsSettings
    ) : TtsPlaybackState
    data class Paused(
        val utterance: TtsUtterance,
        val settings: TtsSettings
    ) : TtsPlaybackState
    data class Error(
        val message: String
    ) : TtsPlaybackState
}

val TtsPlaybackState.isActive: Boolean
    get() = this is TtsPlaybackState.Playing || this is TtsPlaybackState.Paused

val TtsPlaybackState.currentSettings: TtsSettings?
    get() = when (this) {
        is TtsPlaybackState.Playing -> settings
        is TtsPlaybackState.Paused -> settings
        else -> null
    }

val TtsPlaybackState.currentUtterance: TtsUtterance?
    get() = when (this) {
        is TtsPlaybackState.Playing -> utterance
        is TtsPlaybackState.Paused -> utterance
        else -> null
    }