package com.fbaldhagen.readbooks.domain.model

data class TopRatedBook(
    val gutenbergId: Int,
    val averageRating: Double,
    val ratingCount: Int
)