package com.fbaldhagen.readbooks.ui.tts

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.common.result.onSuccess
import com.fbaldhagen.readbooks.data.tts.TtsController
import com.fbaldhagen.readbooks.domain.model.DomainLocator
import com.fbaldhagen.readbooks.domain.model.TtsPlaybackState
import com.fbaldhagen.readbooks.domain.model.TtsSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TtsViewModel @Inject constructor(
    private val ttsController: TtsController,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    val ttsState: StateFlow<TtsPlaybackState> = ttsController.ttsState
    val bookTitle: StateFlow<String?> = ttsController.bookTitle
    val bookAuthor: StateFlow<String?> = ttsController.bookAuthor
    val coverUri: StateFlow<String?> = ttsController.coverUri

    fun onStartTts(
        bookId: Long,
        bookTitle: String,
        bookAuthor: String?,
        coverUri: String?,
        filePath: String? = null,
        startLocator: DomainLocator? = null
    ) {
        viewModelScope.launch {
            ttsController.startPlayer(bookId, bookTitle, bookAuthor, coverUri, filePath, startLocator)
                .onSuccess {
                    context.startForegroundService(Intent(context, TtsService::class.java))
                }
        }
    }

    fun onStop() {
        viewModelScope.launch {
            ttsController.stop()
            context.stopService(Intent(context, TtsService::class.java))
        }
    }

    fun onPlayPause() { viewModelScope.launch { ttsController.playPause() } }
    fun onSkipNext() = ttsController.skipNext()
    fun onSkipPrevious() = ttsController.skipPrevious()
    fun onUpdateSettings(settings: TtsSettings) {
        viewModelScope.launch { ttsController.updateSettings(settings) }
    }
}