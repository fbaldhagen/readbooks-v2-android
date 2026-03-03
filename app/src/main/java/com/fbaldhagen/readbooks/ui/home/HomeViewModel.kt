package com.fbaldhagen.readbooks.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.common.result.onError
import com.fbaldhagen.readbooks.common.result.onSuccess
import com.fbaldhagen.readbooks.domain.model.ShelfState
import com.fbaldhagen.readbooks.domain.usecase.DiscoverUseCases
import com.fbaldhagen.readbooks.domain.usecase.GetHomeContentUseCase
import com.fbaldhagen.readbooks.domain.usecase.LibraryUseCases
import com.fbaldhagen.readbooks.domain.usecase.RatingUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeContent: GetHomeContentUseCase,
    private val discoverUseCases: DiscoverUseCases,
    private val libraryUseCases: LibraryUseCases,
    private val ratingUseCases: RatingUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        observeHomeContent()
        observeLibraryAndLoadShelves()
    }

    private fun observeHomeContent() {
        viewModelScope.launch {
            getHomeContent()
                .onStart { _state.update { it.copy(isLoading = true) } }
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load home content"
                        )
                    }
                }
                .collect { content ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            currentlyReading = content.currentlyReading,
                            recentBooks = content.recentBooks,
                            readingGoalProgress = content.readingGoalProgress,
                            recentAchievements = content.recentAchievements
                        )
                    }
                }
        }
    }

    private fun observeLibraryAndLoadShelves() {
        viewModelScope.launch {
            libraryUseCases.observeAll()
                .collect { books ->
                    val ids = books.mapNotNull { it.gutenbergId }.toSet()
                    _state.update { it.copy(libraryGutenbergIds = ids) }
                    loadPopular(ids)
                    loadTopRated(ids)
                }
        }
    }

    private fun loadPopular(excludeIds: Set<Int>) {
        viewModelScope.launch {
            _state.update { it.copy(popularBooks = ShelfState.Loading) }
            discoverUseCases.getPopularPreview(12)
                .onSuccess { books ->
                    val filtered = books.filter { it.gutenbergId !in excludeIds }
                    _state.update { it.copy(popularBooks = ShelfState.Success(filtered)) }
                }
                .onError { error ->
                    _state.update { it.copy(popularBooks = ShelfState.Error(error.message)) }
                }
        }
    }

    private fun loadTopRated(excludeIds: Set<Int>) {
        viewModelScope.launch {
            _state.update { it.copy(topRatedBooks = ShelfState.Loading) }
            ratingUseCases.getTopRated(10, excludeIds.toList())
                .onSuccess { topRated ->
                    if (topRated.isEmpty()) {
                        _state.update { it.copy(topRatedBooks = ShelfState.Success(emptyList())) }
                        return@onSuccess
                    }
                    val ids = topRated.joinToString(",") { it.gutenbergId.toString() }
                    discoverUseCases.getBooksByIds(ids)
                        .onSuccess { books ->
                            val ratingMap = topRated.associate {
                                it.gutenbergId to it.averageRating
                            }
                            val ranked = books
                                .map { it.copy(averageRating = ratingMap[it.gutenbergId]) }
                                .sortedByDescending { it.averageRating }
                            _state.update {
                                it.copy(topRatedBooks = ShelfState.Success(ranked))
                            }
                        }
                        .onError { error ->
                            _state.update {
                                it.copy(topRatedBooks = ShelfState.Error(error.message))
                            }
                        }
                }
                .onError { error ->
                    _state.update { it.copy(topRatedBooks = ShelfState.Error(error.message)) }
                }
        }
    }

    fun retryPopular() {
        loadPopular(_state.value.libraryGutenbergIds)
    }

    fun retryTopRated() {
        loadTopRated(_state.value.libraryGutenbergIds)
    }

    fun dismissError() {
        _state.update { it.copy(error = null) }
    }
}