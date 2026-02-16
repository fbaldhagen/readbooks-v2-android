package com.fbaldhagen.readbooks.domain.model

data class FilterState(
    val searchQuery: String = "",
    val readingStatus: ReadingStatus? = null,
    val sortType: SortType = SortType.RECENTLY_ADDED,
    val showArchived: Boolean = false
)