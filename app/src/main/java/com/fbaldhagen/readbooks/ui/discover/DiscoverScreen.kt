package com.fbaldhagen.readbooks.ui.discover

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.fbaldhagen.readbooks.ui.discover.components.DiscoverContent
import com.fbaldhagen.readbooks.ui.discover.components.DiscoverTopBar
import com.fbaldhagen.readbooks.ui.discover.components.TopicChipRow

@Composable
fun DiscoverScreen(
    onNavigateToBookDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val books = viewModel.books.collectAsLazyPagingItems()

    Column(modifier = modifier.fillMaxSize()) {
        DiscoverTopBar(
            isSearchActive = state.isSearchActive,
            searchQuery = state.searchQuery,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onSearchSubmit = viewModel::onSearchSubmit,
            onToggleSearch = viewModel::onToggleSearch,
            onClearSearch = viewModel::onClearSearch
        )

        TopicChipRow(
            selectedTopic = state.selectedTopic,
            onTopicSelected = viewModel::onTopicSelected
        )

        DiscoverContent(
            books = books,
            onBookClick = onNavigateToBookDetails
        )
    }
}