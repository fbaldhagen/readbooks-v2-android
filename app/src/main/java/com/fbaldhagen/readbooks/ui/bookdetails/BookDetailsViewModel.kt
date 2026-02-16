package com.fbaldhagen.readbooks.ui.bookdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.fbaldhagen.readbooks.common.result.onError
import com.fbaldhagen.readbooks.common.result.onSuccess
import com.fbaldhagen.readbooks.domain.model.BookDetailsState
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.usecase.DiscoverUseCases
import com.fbaldhagen.readbooks.domain.usecase.GetBookDetailsUseCase
import com.fbaldhagen.readbooks.domain.usecase.LibraryUseCases
import com.fbaldhagen.readbooks.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBookDetails: GetBookDetailsUseCase,
    private val libraryUseCases: LibraryUseCases,
    private val discoverUseCases: DiscoverUseCases
) : ViewModel() {

    private val _state = MutableStateFlow<BookDetailsUiState>(BookDetailsUiState.Loading)
    val state: StateFlow<BookDetailsUiState> = _state.asStateFlow()

    // Determine entry point from saved state
    private val libraryBookId: Long? = try {
        savedStateHandle.toRoute<Route.BookDetails>().bookId
    } catch (_: Exception) {
        null
    }

    private val discoverGutenbergId: Int? = try {
        savedStateHandle.toRoute<Route.DiscoverBookDetails>().gutenbergId
    } catch (_: Exception) {
        null
    }

    init {
        when {
            libraryBookId != null -> loadFromLibrary(libraryBookId)
            discoverGutenbergId != null -> loadFromDiscover(discoverGutenbergId)
            else -> _state.value = BookDetailsUiState.Error("Invalid navigation")
        }
    }

    private fun loadFromLibrary(bookId: Long) {
        viewModelScope.launch {
            getBookDetails.fromLibrary(bookId)
                .catch { e ->
                    _state.value = BookDetailsUiState.Error(
                        e.message ?: "Failed to load book details"
                    )
                }
                .collect { details ->
                    _state.value = BookDetailsUiState.Success(details)
                }
        }
    }

    private fun loadFromDiscover(gutenbergId: Int) {
        viewModelScope.launch {
            _state.value = BookDetailsUiState.Loading
            discoverUseCases.getBookById(gutenbergId)
                .onSuccess { discoverBook ->
                    val details = getBookDetails.fromDiscover(discoverBook)
                    _state.value = BookDetailsUiState.Success(details)
                }
                .onError { error ->
                    _state.value = BookDetailsUiState.Error(error.message)
                }
        }
    }

    fun onUpdateRating(rating: Int?) {
        val currentState = _state.value as? BookDetailsUiState.Success ?: return
        val libraryState = currentState.details.state as? BookDetailsState.InLibrary ?: return
        viewModelScope.launch {
            libraryUseCases.updateRating(libraryState.bookId, rating)
        }
    }

    fun onUpdateReadingStatus(status: ReadingStatus) {
        val currentState = _state.value as? BookDetailsUiState.Success ?: return
        val libraryState = currentState.details.state as? BookDetailsState.InLibrary ?: return
        viewModelScope.launch {
            libraryUseCases.updateReadingStatus(libraryState.bookId, status)
        }
    }

    fun onDownloadBook() {
        // TODO: Implement download with WorkManager
    }
}