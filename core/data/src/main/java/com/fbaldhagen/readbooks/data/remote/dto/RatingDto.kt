package com.fbaldhagen.readbooks.data.remote.dto

import com.fbaldhagen.readbooks.domain.model.RemoteRating
import com.fbaldhagen.readbooks.domain.model.TopRatedBook
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RatingResponseDto(
    @Json(name = "averageRating") val averageRating: Double?,
    @Json(name = "ratingCount") val ratingCount: Int,
    @Json(name = "userRating") val userRating: Int?,
    @Json(name = "distribution") val distribution: Map<String, Int>
)

@JsonClass(generateAdapter = true)
data class RatingRequest(
    @Json(name = "rating") val rating: Int
)

@JsonClass(generateAdapter = true)
data class TopRatedBookDto(
    @Json(name = "gutenbergId") val gutenbergId: Int,
    @Json(name = "averageRating") val averageRating: Double,
    @Json(name = "ratingCount") val ratingCount: Int
)

fun RatingResponseDto.toRemoteRating() = RemoteRating(
    averageRating = averageRating,
    ratingCount = ratingCount,
    userRating = userRating,
    distribution = distribution.mapKeys { it.key.toIntOrNull() ?: 0 }
)

fun TopRatedBookDto.toTopRatedBook() = TopRatedBook(
    gutenbergId = gutenbergId,
    averageRating = averageRating,
    ratingCount = ratingCount
)