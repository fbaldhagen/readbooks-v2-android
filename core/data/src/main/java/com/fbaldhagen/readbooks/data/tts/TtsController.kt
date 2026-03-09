package com.fbaldhagen.readbooks.data.tts

import androidx.media3.common.Player
import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.common.result.getOrThrow
import com.fbaldhagen.readbooks.common.result.suspendRunCatching
import com.fbaldhagen.readbooks.domain.model.DomainLocator
import com.fbaldhagen.readbooks.domain.model.TtsPlaybackState
import com.fbaldhagen.readbooks.domain.model.TtsSettings
import com.fbaldhagen.readbooks.domain.repository.TtsPlayer
import com.fbaldhagen.readbooks.domain.repository.TtsPlayerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class TtsController @Inject constructor(
    private val factory: TtsPlayerFactory
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _player = MutableStateFlow<TtsPlayer?>(null)
    private val _bookId = MutableStateFlow<Long?>(null)
    private val _bookTitle = MutableStateFlow<String?>(null)
    private val _bookAuthor = MutableStateFlow<String?>(null)
    private val _coverUri = MutableStateFlow<String?>(null)

    val ttsState: StateFlow<TtsPlaybackState> = _player
        .flatMapLatest { it?.state ?: flowOf(TtsPlaybackState.Idle) }
        .stateIn(scope, SharingStarted.Eagerly, TtsPlaybackState.Idle)

    val bookId: StateFlow<Long?> = _bookId.asStateFlow()
    val bookTitle: StateFlow<String?> = _bookTitle.asStateFlow()
    val bookAuthor: StateFlow<String?> = _bookAuthor.asStateFlow()
    val coverUri: StateFlow<String?> = _coverUri.asStateFlow()

    fun getMedia3Player(): Player? {
        val player = _player.value as? ReadiumTtsPlayer ?: return null
        val rawPlayer = player.asMedia3Player()
        return TtsForwardingPlayer(
            player = rawPlayer,
            onSkipToNext = ::skipNext,
            onSkipToPrevious = ::skipPrevious
        )
    }

    fun skipNext() {
        scope.launch { _player.value?.skipNext() }
    }

    fun skipPrevious() {
        scope.launch { _player.value?.skipPrevious() }
    }

    suspend fun startPlayer(
        bookId: Long,
        bookTitle: String,
        bookAuthor: String?,
        coverUri: String?,
        startLocator: DomainLocator? = null
    ): Result<Unit> = suspendRunCatching {
        _player.value?.close()
        val player = factory.create(bookId, startLocator).getOrThrow()
        _player.value = player
        _bookId.value = bookId
        _bookTitle.value = bookTitle
        _bookAuthor.value = bookAuthor
        _coverUri.value = coverUri
        player.play()
    }

    suspend fun playPause() {
        val player = _player.value ?: return
        when (ttsState.value) {
            is TtsPlaybackState.Playing -> player.pause()
            is TtsPlaybackState.Paused -> player.play()
            else -> player.play()
        }
    }

    suspend fun stop() {
        _player.value?.stop()
        _player.value = null
        _bookId.value = null
        _bookTitle.value = null
        _bookAuthor.value = null
        _coverUri.value = null
    }

    suspend fun updateSettings(settings: TtsSettings) {
        _player.value?.updateSettings(settings)
    }
}