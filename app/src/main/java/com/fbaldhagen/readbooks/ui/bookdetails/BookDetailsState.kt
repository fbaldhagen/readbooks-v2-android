package com.fbaldhagen.readbooks.ui.bookdetails

import com.fbaldhagen.readbooks.domain.model.BookDetails

sealed interface BookDetailsUiState {
    data object Loading : BookDetailsUiState
    data class Success(val details: BookDetails) : BookDetailsUiState
    data class Error(val message: String) : BookDetailsUiState
}