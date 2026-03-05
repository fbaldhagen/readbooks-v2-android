package com.fbaldhagen.readbooks.ui.reader.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.common.result.onError
import com.fbaldhagen.readbooks.common.result.onSuccess
import com.fbaldhagen.readbooks.domain.model.DomainLocator
import com.fbaldhagen.readbooks.domain.model.TtsPlaybackState
import com.fbaldhagen.readbooks.domain.model.TtsSettings
import com.fbaldhagen.readbooks.domain.repository.TtsPlayer
import com.fbaldhagen.readbooks.domain.usecase.TtsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TtsViewModel @Inject constructor(
    private val ttsUseCases: TtsUseCases
) : ViewModel() {

    private val _player = MutableStateFlow<TtsPlayer?>(null)

    val ttsState: StateFlow<TtsPlaybackState> = _player
        .flatMapLatest { it?.state ?: flowOf(TtsPlaybackState.Idle) }
        .let { flow ->
            val stateFlow = MutableStateFlow<TtsPlaybackState>(TtsPlaybackState.Idle)
            viewModelScope.launch { flow.collect { stateFlow.value = it } }
            stateFlow.asStateFlow()
        }

    fun onStartTts(bookId: Long, startLocator: DomainLocator? = null) {
        viewModelScope.launch {
            ttsUseCases.createPlayer(bookId, startLocator)
                .onSuccess { player ->
                    _player.value?.close()
                    _player.value = player
                    player.play()
                }
                .onError { error ->
                    // TODO: expose error to UI
                }
        }
    }

    fun onPlayPause() {
        viewModelScope.launch {
            val player = _player.value ?: return@launch
            when (ttsState.value) {
                is TtsPlaybackState.Playing -> player.pause()
                is TtsPlaybackState.Paused -> player.play()
                else -> player.play()
            }
        }
    }

    fun onSkipNext() {
        viewModelScope.launch { _player.value?.skipNext() }
    }

    fun onSkipPrevious() {
        viewModelScope.launch { _player.value?.skipPrevious() }
    }

    fun onStop() {
        viewModelScope.launch {
            _player.value?.stop()
            _player.value = null
        }
    }

    fun onUpdateSettings(settings: TtsSettings) {
        viewModelScope.launch { _player.value?.updateSettings(settings) }
    }

    override fun onCleared() {
        super.onCleared()
        _player.value?.close()
        _player.value = null
    }
}