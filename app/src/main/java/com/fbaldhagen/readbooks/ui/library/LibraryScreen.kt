package com.fbaldhagen.readbooks.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fbaldhagen.readbooks.ui.components.ErrorMessage
import com.fbaldhagen.readbooks.ui.components.LoadingIndicator
import com.fbaldhagen.readbooks.ui.library.components.EmptyLibraryMessage
import com.fbaldhagen.readbooks.ui.library.components.FilterChipRow
import com.fbaldhagen.readbooks.ui.library.components.LibraryGrid
import com.fbaldhagen.readbooks.ui.library.components.LibraryTopBar

@Composable
fun LibraryScreen(
    onNavigateToBookDetails: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        LibraryTopBar(
            isSearchActive = state.isSearchActive,
            searchQuery = searchQuery,
            onSearchQueryChanged = { query ->
                searchQuery = query
                viewModel.onSearchQueryChanged(query)
            },
            onToggleSearch = {
                viewModel.onToggleSearch()
                if (state.isSearchActive) searchQuery = ""
            },
            currentSort = state.filterState.sortType,
            onSortChanged = viewModel::onSortTypeChanged
        )

        FilterChipRow(
            selectedStatus = state.filterState.readingStatus,
            onStatusSelected = viewModel::onReadingStatusFilterChanged,
            showArchived = state.filterState.showArchived,
            onToggleArchived = viewModel::onToggleShowArchived
        )

        when {
            state.isLoading -> LoadingIndicator()
            state.error != null -> ErrorMessage(message = state.error!!)
            state.books.isEmpty() -> EmptyLibraryMessage()
            else -> LibraryGrid(
                books = state.books,
                onBookClick = onNavigateToBookDetails
            )
        }
    }
}