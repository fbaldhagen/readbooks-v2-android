package com.fbaldhagen.readbooks.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fbaldhagen.readbooks.common.result.onError
import com.fbaldhagen.readbooks.common.result.onSuccess
import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.model.ShelfState
import com.fbaldhagen.readbooks.domain.usecase.DiscoverUseCases
import com.fbaldhagen.readbooks.domain.usecase.LibraryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val discoverUseCases: DiscoverUseCases,
    libraryUseCases: LibraryUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(DiscoverState())
    val state: StateFlow<DiscoverState> = _state.asStateFlow()

    private val _searchTrigger = MutableStateFlow<SearchTrigger>(SearchTrigger.Popular)

    val books: Flow<PagingData<DiscoverBook>> = _searchTrigger
        .flatMapLatest { trigger ->
            when (trigger) {
                is SearchTrigger.Popular -> discoverUseCases.getPopular()
                is SearchTrigger.Search -> discoverUseCases.search(trigger.query)
            }
        }
        .cachedIn(viewModelScope)

    private val _shelves = MutableStateFlow<Map<String, ShelfState>>(emptyMap())
    val shelves: StateFlow<Map<String, ShelfState>> = _shelves.asStateFlow()

    val topics = listOf(
        "Popular",
        "Philosophy", "Mystery", "Romance", "History",
        "Science Fiction", "Adventure", "Poetry", "Drama", "Children"
    )

    private val libraryBooks: StateFlow<List<Book>> = libraryUseCases.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val libraryGutenbergIds: StateFlow<Set<Int>> = libraryBooks
        .map { books -> books.filter { !it.isArchived }.mapNotNull { it.gutenbergId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val archivedGutenbergIds: StateFlow<Set<Int>> = libraryBooks
        .map { books -> books.filter { it.isArchived }.mapNotNull { it.gutenbergId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun loadShelf(topic: String) {
        if (_shelves.value[topic] is ShelfState.Success) return
        viewModelScope.launch {
            _shelves.update { it + (topic to ShelfState.Loading) }
            val result = if (topic == "Popular") {
                discoverUseCases.getPopularPreview(12)
            } else {
                discoverUseCases.getByTopicPreview(topic, 12)
            }
            result
                .onSuccess { books ->
                    _shelves.update { it + (topic to ShelfState.Success(books)) }
                }
                .onError { error ->
                    _shelves.update { it + (topic to ShelfState.Error(error.message)) }
                }
        }
    }

    fun retryShelf(topic: String) {
        _shelves.update { it + (topic to ShelfState.Loading) }
        loadShelf(topic)
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onSearchSubmit() {
        val query = _state.value.searchQuery.trim()
        if (query.isNotEmpty()) {
            _searchTrigger.value = SearchTrigger.Search(query)
        }
    }

    fun onToggleSearch() {
        _state.update { it.copy(isSearchActive = !it.isSearchActive) }
        if (!_state.value.isSearchActive) {
            _state.update { it.copy(searchQuery = "") }
            _searchTrigger.value = SearchTrigger.Popular
        }
    }

    fun onClearSearch() {
        _state.update { it.copy(searchQuery = "") }
        _searchTrigger.value = SearchTrigger.Popular
    }

    private sealed interface SearchTrigger {
        data object Popular : SearchTrigger
        data class Search(val query: String) : SearchTrigger
    }
}