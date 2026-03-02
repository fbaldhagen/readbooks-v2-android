package com.fbaldhagen.readbooks.ui.author

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.fbaldhagen.readbooks.common.result.onError
import com.fbaldhagen.readbooks.common.result.onSuccess
import com.fbaldhagen.readbooks.domain.model.AuthorDetails
import com.fbaldhagen.readbooks.domain.usecase.GetAuthorDetailsUseCase
import com.fbaldhagen.readbooks.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAuthorDetails: GetAuthorDetailsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AuthorUiState>(AuthorUiState.Loading)
    val state: StateFlow<AuthorUiState> = _state.asStateFlow()

    private val authorName: String =
        savedStateHandle.toRoute<Route.Author>().authorName
    private val excludeGutenbergId: Int? =
        savedStateHandle.toRoute<Route.Author>().excludeGutenbergId

    init {
        loadAuthor()
    }

    private fun loadAuthor() {
        viewModelScope.launch {
            _state.value = AuthorUiState.Loading
            getAuthorDetails(authorName, excludeGutenbergId)
                .onSuccess { details ->
                    _state.value = AuthorUiState.Success(details)
                }
                .onError { error ->
                    _state.value = AuthorUiState.Error(error.message)
                }
        }
    }

    fun retry() = loadAuthor()
}

sealed interface AuthorUiState {
    data object Loading : AuthorUiState
    data class Success(val details: AuthorDetails) : AuthorUiState
    data class Error(val message: String) : AuthorUiState
}