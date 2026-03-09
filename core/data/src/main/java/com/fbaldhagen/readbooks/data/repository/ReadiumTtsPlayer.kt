package com.fbaldhagen.readbooks.data.repository

import androidx.media3.common.Player
import com.fbaldhagen.readbooks.data.mapper.toDomain
import com.fbaldhagen.readbooks.domain.model.TtsPlaybackState
import com.fbaldhagen.readbooks.domain.model.TtsSettings
import com.fbaldhagen.readbooks.domain.model.TtsVoice
import com.fbaldhagen.readbooks.domain.repository.TtsPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.readium.navigator.media.tts.TtsNavigator
import org.readium.navigator.media.tts.android.AndroidTtsEngine
import org.readium.navigator.media.tts.android.AndroidTtsPreferences
import org.readium.navigator.media.tts.android.AndroidTtsSettings
import org.readium.r2.shared.ExperimentalReadiumApi

@OptIn(ExperimentalReadiumApi::class)
class ReadiumTtsPlayer(
    private val navigator: TtsNavigator<AndroidTtsSettings, AndroidTtsPreferences, AndroidTtsEngine.Error, AndroidTtsEngine.Voice>,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : TtsPlayer {

    fun asMedia3Player(): Player = navigator.asMedia3Player()
    private val _state = MutableStateFlow<TtsPlaybackState>(TtsPlaybackState.Idle)
    override val state: StateFlow<TtsPlaybackState> = _state.asStateFlow()

    private val _availableVoices = MutableStateFlow<List<TtsVoice>>(emptyList())
    override val availableVoices: StateFlow<List<TtsVoice>> = _availableVoices.asStateFlow()

    private var currentSettings = TtsSettings()

    init {
        scope.launch {
            navigator.playback.collect { playback ->
                _state.value = playback.toDomain(currentSettings)
            }
        }
        _availableVoices.value = navigator.voices.map { voice ->
            TtsVoice(
                id = voice.id.value,
                name = voice.id.value,
                language = voice.language.locale.displayLanguage
            )
        }
    }

    override suspend fun play() { navigator.play() }
    override suspend fun pause() { navigator.pause() }
    override suspend fun stop() {
        navigator.close()
        _state.value = TtsPlaybackState.Idle
    }
    override suspend fun skipNext() { navigator.skipToNextUtterance() }
    override suspend fun skipPrevious() { navigator.skipToPreviousUtterance() }

    override suspend fun updateSettings(settings: TtsSettings) {
        currentSettings = settings
        navigator.submitPreferences(
            AndroidTtsPreferences(
                speed = settings.speed.toDouble(),
                pitch = settings.pitch.toDouble()
            )
        )
    }

    override fun close() {
        navigator.close()
        _state.value = TtsPlaybackState.Idle
    }
}