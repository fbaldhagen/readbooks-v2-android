package com.fbaldhagen.readbooks.domain.model

data class RemoteRating(
    val averageRating: Double?,
    val ratingCount: Int,
    val userRating: Int?,
    val distribution: Map<Int, Int>
)