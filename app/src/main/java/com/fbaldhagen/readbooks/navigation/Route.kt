package com.fbaldhagen.readbooks.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Auth : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Library : Route

    @Serializable
    data object Discover : Route

    @Serializable
    data object Profile : Route

    @Serializable
    data class BookDetails(val bookId: Long) : Route

    @Serializable
    data class DiscoverBookDetails(val gutenbergId: Int) : Route

    @Serializable
    data class DiscoverTopic(val topic: String) : Route

    @Serializable
    data class Author(val authorName: String, val excludeGutenbergId: Int?) : Route
}