package com.fbaldhagen.readbooks.ui.discover

data class DiscoverState(
    val searchQuery: String = "",
    val isSearchActive: Boolean = false
)