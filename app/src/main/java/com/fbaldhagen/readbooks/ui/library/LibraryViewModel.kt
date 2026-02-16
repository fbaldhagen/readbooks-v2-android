package com.fbaldhagen.readbooks.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.domain.model.FilterState
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.model.SortType
import com.fbaldhagen.readbooks.domain.usecase.LibraryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val libraryUseCases: LibraryUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryState())
    val state: StateFlow<LibraryState> = _state.asStateFlow()

    private val filterState = MutableStateFlow(FilterState())

    init {
        observeBooks()
    }

    private fun observeBooks() {
        viewModelScope.launch {
            filterState
                .flatMapLatest { filter ->
                    libraryUseCases.observeFiltered(filter)
                        .onStart {
                            _state.update { it.copy(isLoading = true) }
                        }
                }
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load library"
                        )
                    }
                }
                .collect { books ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            books = books,
                            filterState = filterState.value,
                            error = null
                        )
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        filterState.update { it.copy(searchQuery = query) }
    }

    fun onSortTypeChanged(sortType: SortType) {
        filterState.update { it.copy(sortType = sortType) }
    }

    fun onReadingStatusFilterChanged(status: ReadingStatus?) {
        filterState.update { it.copy(readingStatus = status) }
    }

    fun onToggleShowArchived() {
        filterState.update { it.copy(showArchived = !it.showArchived) }
    }

    fun onToggleSearch() {
        _state.update { it.copy(isSearchActive = !it.isSearchActive) }
        if (!_state.value.isSearchActive) {
            onSearchQueryChanged("")
        }
    }
}