package com.fbaldhagen.readbooks.ui.discover

data class DiscoverState(
    val searchQuery: String = "",
    val selectedTopic: String? = null,
    val isSearchActive: Boolean = false
)