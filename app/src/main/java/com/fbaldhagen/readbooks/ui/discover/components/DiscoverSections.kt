package com.fbaldhagen.readbooks.ui.discover.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.ui.discover.DiscoverViewModel

@Composable
fun DiscoverSections(
    topics: List<String>,
    shelves: Map<String, DiscoverViewModel.ShelfState>,
    libraryGutenbergIds: Set<Int>,
    archivedGutenbergIds: Set<Int>,
    onBookClick: (Int) -> Unit,
    onSeeAll: (String) -> Unit,
    onLoad: (String) -> Unit,
    onRetry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(topics) { topic ->
            BookShelf(
                topic = topic,
                shelfState = shelves[topic],
                libraryGutenbergIds = libraryGutenbergIds,
                archivedGutenbergIds = archivedGutenbergIds,
                onBookClick = onBookClick,
                onSeeAll = onSeeAll,
                onLoad = onLoad,
                onRetry = onRetry
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}