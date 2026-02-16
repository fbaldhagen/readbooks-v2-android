package com.fbaldhagen.readbooks.ui.library

import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.domain.model.FilterState

data class LibraryState(
    val isLoading: Boolean = true,
    val books: List<Book> = emptyList(),
    val filterState: FilterState = FilterState(),
    val isSearchActive: Boolean = false,
    val error: String? = null
)