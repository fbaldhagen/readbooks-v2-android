package com.fbaldhagen.readbooks.ui.discover

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.fbaldhagen.readbooks.ui.discover.components.DiscoverContent
import com.fbaldhagen.readbooks.ui.discover.components.DiscoverSections
import com.fbaldhagen.readbooks.ui.discover.components.DiscoverTopBar

@Composable
fun DiscoverScreen(
    onNavigateToBookDetails: (Int) -> Unit,
    onNavigateToTopic: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val books = viewModel.books.collectAsLazyPagingItems()
    val shelves by viewModel.shelves.collectAsStateWithLifecycle()
    val libraryGutenbergIds by viewModel.libraryGutenbergIds.collectAsStateWithLifecycle()
    val archivedGutenbergIds by viewModel.archivedGutenbergIds.collectAsStateWithLifecycle()

    BackHandler(enabled = state.isSearchActive) {
        viewModel.onToggleSearch()
    }

    Column(modifier = modifier.fillMaxSize()) {
        DiscoverTopBar(
            isSearchActive = state.isSearchActive,
            searchQuery = state.searchQuery,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onSearchSubmit = viewModel::onSearchSubmit,
            onToggleSearch = viewModel::onToggleSearch,
            onClearSearch = viewModel::onClearSearch
        )

        if (state.isSearchActive) {
            DiscoverContent(
                books = books,
                onBookClick = onNavigateToBookDetails,
                libraryGutenbergIds = libraryGutenbergIds,
                archivedGutenbergIds = archivedGutenbergIds
            )
        } else {
            DiscoverSections(
                topics = viewModel.topics,
                shelves = shelves,
                libraryGutenbergIds = libraryGutenbergIds,
                archivedGutenbergIds = archivedGutenbergIds,
                onBookClick = onNavigateToBookDetails,
                onSeeAll = onNavigateToTopic,
                onLoad = viewModel::loadShelf,
                onRetry = viewModel::retryShelf
            )
        }
    }
}