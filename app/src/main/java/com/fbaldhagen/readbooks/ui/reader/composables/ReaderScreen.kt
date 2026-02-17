package com.fbaldhagen.readbooks.ui.reader.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.Bookmark
import com.fbaldhagen.readbooks.ui.components.LoadingIndicator
import com.fbaldhagen.readbooks.ui.reader.presentation.ReaderPreferences
import com.fbaldhagen.readbooks.ui.reader.presentation.ReaderState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    state: ReaderState,
    fragmentContainerId: Int,
    onBack: () -> Unit,
    onAddBookmark: () -> Unit,
    onUpdatePreferences: (ReaderPreferences) -> Unit,
    onDeleteBookmark: (Bookmark) -> Unit
)
 {
    var showSettings by remember { mutableStateOf(false) }
    var showBookmarks by remember { mutableStateOf(false) }

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
            Text(state.error)
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ReaderFragmentContainer(
            fragmentContainerId = fragmentContainerId,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp)
        )

        AnimatedVisibility(
            visible = state.barsVisible,
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            ReaderTopAppBar(
                bookTitle = state.bookTitle,
                chapterTitle = state.currentChapterTitle,
                onBack = onBack,
                onAddBookmark = onAddBookmark,
                onOpenSettings = { showSettings = true }
            )
        }
    }

    if (showSettings) {
        ReaderSettingsSheet(
            preferences = state.preferences,
            onPreferencesChanged = onUpdatePreferences,
            onDismiss = { showSettings = false }
        )
    }

    if (showBookmarks) {
        BookmarksSheet(
            bookmarks = state.bookmarks,
            onBookmarkDelete = onDeleteBookmark,
            onDismiss = { showBookmarks = false }
        )
    }
}