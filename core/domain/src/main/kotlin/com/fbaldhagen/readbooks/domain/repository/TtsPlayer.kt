package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.domain.model.TtsPlaybackState
import com.fbaldhagen.readbooks.domain.model.TtsSettings
import com.fbaldhagen.readbooks.domain.model.TtsVoice
import kotlinx.coroutines.flow.StateFlow

interface TtsPlayer {
    val state: StateFlow<TtsPlaybackState>
    val availableVoices: StateFlow<List<TtsVoice>>
    suspend fun play()
    suspend fun pause()
    suspend fun stop()
    suspend fun skipNext()
    suspend fun skipPrevious()
    suspend fun updateSettings(settings: TtsSettings)
    fun close()
}