package com.fbaldhagen.readbooks.ui.reader.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fbaldhagen.readbooks.domain.model.Bookmark
import com.fbaldhagen.readbooks.domain.model.DomainLocator
import com.fbaldhagen.readbooks.domain.model.ReaderPreferences
import com.fbaldhagen.readbooks.domain.model.TocEntry
import com.fbaldhagen.readbooks.ui.components.LoadingIndicator
import com.fbaldhagen.readbooks.ui.reader.presentation.ReaderViewModel
import com.fbaldhagen.readbooks.ui.reader.presentation.TtsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel = hiltViewModel(),
    ttsViewModel: TtsViewModel = hiltViewModel(),
    fragmentContainerId: Int,
    onToggleBookmark: () -> Unit,
    onUpdatePreferences: (ReaderPreferences) -> Unit,
    onDeleteBookmark: (Bookmark) -> Unit,
    onNavigateToTocEntry: (TocEntry) -> Unit,
    onNavigateToBookmark: (Bookmark) -> Unit,
    onUpdateBookmarkNote: (Bookmark, String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val ttsState by ttsViewModel.ttsState.collectAsStateWithLifecycle()

    var showSettings by remember { mutableStateOf(false) }
    var showBookmarks by remember { mutableStateOf(false) }
    var showToc by remember { mutableStateOf(false) }

    if (state.isLoading) {
        LoadingIndicator()
        return
    }

    if (state.error != null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(state.error ?: "An error occurred.")
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ReaderFragmentContainer(
            fragmentContainerId = fragmentContainerId,
            modifier = Modifier
                .fillMaxSize()
        )

        ReaderOverlay(
            theme = state.preferences.theme,
            barsVisible = state.barsVisible,
            chapterTitle = state.currentChapterTitle,
            bookProgress = state.totalProgression,
            isCurrentPageBookmarked = state.currentPageBookmark != null,
            onToggleBookmark = onToggleBookmark,
            onOpenBookmarks = { showBookmarks = true },
            onOpenToc = { showToc = true },
            onOpenSettings = { showSettings = true },
            ttsState = ttsState,
            onTtsStart = {
                val domainLocator = state.currentLocator?.let {
                    DomainLocator(
                        href = it.href.toString(),
                        progression = it.locations.progression ?: 0.0
                    )
                }
                ttsViewModel.onStartTts(
                    bookId = state.bookId,
                    bookTitle = state.bookTitle,
                    bookAuthor = state.bookAuthor,
                    coverUri = state.coverUri,
                    startLocator = domainLocator
                )
            },
            onTtsPlayPause = ttsViewModel::onPlayPause,
            onTtsSkipNext = ttsViewModel::onSkipNext,
            onTtsSkipPrevious = ttsViewModel::onSkipPrevious
        )
    }

    if (showSettings) {
        ReaderSettingsSheet(
            preferences = state.preferences,
            onPreferencesChanged = onUpdatePreferences,
            onDismiss = @Suppress("AssignedValueIsNeverRead"){ showSettings = false },
            syncReaderTheme = state.syncReaderTheme
        )
    }

    if (showBookmarks) {
        BookmarksSheet(
            bookmarks = state.bookmarks,
            onBookmarkDelete = onDeleteBookmark,
            onDismiss = @Suppress("AssignedValueIsNeverRead"){ showBookmarks = false },
            onBookmarkClick = onNavigateToBookmark,
            onBookmarkNoteUpdate = onUpdateBookmarkNote
        )
    }

    if (showToc) {
        TocSheet(
            entries = state.tableOfContents,
            currentChapterTitle = state.currentChapterTitle,
            onEntryClick = onNavigateToTocEntry,
            onDismiss = @Suppress("AssignedValueIsNeverRead"){ showToc = false }
        )
    }
}