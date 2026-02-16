package com.fbaldhagen.readbooks.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthorDto(
    @Json(name = "name") val name: String,
    @Json(name = "birth_year") val birthYear: Int?,
    @Json(name = "death_year") val deathYear: Int?
)