package com.fbaldhagen.readbooks.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
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
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val discoverUseCases: DiscoverUseCases,
    private val libraryUseCases: LibraryUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(DiscoverState())
    val state: StateFlow<DiscoverState> = _state.asStateFlow()

    private val _searchTrigger = MutableStateFlow<SearchTrigger>(SearchTrigger.Popular)

    val books: Flow<PagingData<DiscoverBook>> = _searchTrigger
        .flatMapLatest { trigger ->
            when (trigger) {
                is SearchTrigger.Popular -> discoverUseCases.getPopular()
                is SearchTrigger.Search -> discoverUseCases.search(trigger.query)
                is SearchTrigger.Topic -> discoverUseCases.getByTopic(trigger.topic)
            }
        }
        .cachedIn(viewModelScope)

    val libraryGutenbergIds: StateFlow<Set<Int>> = libraryUseCases.observeAll()
        .map { books -> books.mapNotNull { it.gutenbergId }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }


    fun onSearchSubmit() {
        val query = _state.value.searchQuery.trim()
        if (query.isNotEmpty()) {
            _state.update { it.copy(selectedTopic = null) }
            _searchTrigger.value = SearchTrigger.Search(query)
        }
    }

    fun onTopicSelected(topic: String) {
        _state.update {
            it.copy(
                selectedTopic = if (it.selectedTopic == topic) null else topic,
                searchQuery = ""
            )
        }
        _searchTrigger.value = if (_state.value.selectedTopic != null) {
            SearchTrigger.Topic(topic)
        } else {
            SearchTrigger.Popular
        }
    }

    fun onToggleSearch() {
        _state.update { it.copy(isSearchActive = !it.isSearchActive) }
        if (!_state.value.isSearchActive) {
            _state.update { it.copy(searchQuery = "") }
            if (_state.value.selectedTopic == null) {
                _searchTrigger.value = SearchTrigger.Popular
            }
        }
    }

    fun onClearSearch() {
        _state.update { it.copy(searchQuery = "", selectedTopic = null) }
        _searchTrigger.value = SearchTrigger.Popular
    }

    private sealed interface SearchTrigger {
        data object Popular : SearchTrigger
        data class Search(val query: String) : SearchTrigger
        data class Topic(val topic: String) : SearchTrigger
    }
}