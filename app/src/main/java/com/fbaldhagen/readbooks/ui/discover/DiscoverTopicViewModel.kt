package com.fbaldhagen.readbooks.ui.discover

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.usecase.DiscoverUseCases
import com.fbaldhagen.readbooks.domain.usecase.LibraryUseCases
import com.fbaldhagen.readbooks.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DiscoverTopicViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    discoverUseCases: DiscoverUseCases,
    libraryUseCases: LibraryUseCases
) : ViewModel() {

    private val topic: String = savedStateHandle.toRoute<Route.DiscoverTopic>().topic

    val books: Flow<PagingData<DiscoverBook>> = if (topic == "Popular") {
        discoverUseCases.getPopular()
    } else {
        discoverUseCases.getByTopic(topic)
    }.cachedIn(viewModelScope)

    private val libraryBooks: StateFlow<List<Book>> = libraryUseCases.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val libraryGutenbergIds: StateFlow<Set<Int>> = libraryBooks
        .map { books -> books.filter { !it.isArchived }.mapNotNull { it.gutenbergId }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val archivedGutenbergIds: StateFlow<Set<Int>> = libraryBooks
        .map { books -> books.filter { it.isArchived }.mapNotNull { it.gutenbergId }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )
}