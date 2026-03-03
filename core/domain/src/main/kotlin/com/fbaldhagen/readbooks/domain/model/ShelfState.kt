package com.fbaldhagen.readbooks.domain.model

sealed interface ShelfState {
    data object Loading : ShelfState
    data class Success(val books: List<DiscoverBook>) : ShelfState
    data class Error(val message: String) : ShelfState
}