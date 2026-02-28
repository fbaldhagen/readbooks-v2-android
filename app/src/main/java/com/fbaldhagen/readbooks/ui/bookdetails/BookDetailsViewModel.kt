package com.fbaldhagen.readbooks.ui.bookdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.fbaldhagen.readbooks.common.result.onError
import com.fbaldhagen.readbooks.common.result.onSuccess
import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.model.BookDetailsState
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.usecase.DiscoverUseCases
import com.fbaldhagen.readbooks.domain.usecase.DownloadBookUseCase
import com.fbaldhagen.readbooks.domain.usecase.DownloadState
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
    private val discoverUseCases: DiscoverUseCases,
    private val downloadBookUseCase: DownloadBookUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<BookDetailsUiState>(BookDetailsUiState.Loading)
    val state: StateFlow<BookDetailsUiState> = _state.asStateFlow()

    private val _authorBooks = MutableStateFlow<List<DiscoverBook>>(emptyList())
    val authorBooks: StateFlow<List<DiscoverBook>> = _authorBooks.asStateFlow()

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
                    if (_authorBooks.value.isEmpty()) loadAuthorBooks(details)
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
                    loadAuthorBooks(details)
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
        val currentState = _state.value as? BookDetailsUiState.Success ?: return
        val notInLibrary = currentState.details.state as? BookDetailsState.NotInLibrary ?: return

        downloadBookUseCase.execute(notInLibrary.gutenbergId)
        observeDownload(notInLibrary.gutenbergId)
    }

    private fun observeDownload(gutenbergId: Int) {
        viewModelScope.launch {
            downloadBookUseCase.observeDownloadState(gutenbergId)
                .collect { downloadState ->
                    val currentState = _state.value as? BookDetailsUiState.Success ?: return@collect
                    when (downloadState) {
                        is DownloadState.Running, is DownloadState.Enqueued -> {
                            _state.value = BookDetailsUiState.Success(
                                currentState.details.copy(state = BookDetailsState.Downloading)
                            )
                        }
                        is DownloadState.Succeeded -> {
                            // Switch to library flow
                            loadFromLibrary(downloadState.bookId)
                        }
                        is DownloadState.Failed -> {
                            _state.value = BookDetailsUiState.Error(downloadState.message)
                        }
                        is DownloadState.Idle -> { /* no-op */ }
                    }
                }
        }
    }

    private fun loadAuthorBooks(details: BookDetails) {
        val authorName = details.authors.firstOrNull() ?: return
        val excludeId = details.gutenbergId ?: return
        viewModelScope.launch {
            discoverUseCases.getBooksByAuthor(authorName, excludeId)
                .onSuccess { _authorBooks.value = it }
        }
    }
}