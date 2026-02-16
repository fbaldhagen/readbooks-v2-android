package com.fbaldhagen.readbooks.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GutendexBookDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "authors") val authors: List<AuthorDto>,
    @Json(name = "summaries") val summaries: List<String>?,
    @Json(name = "subjects") val subjects: List<String>,
    @Json(name = "bookshelves") val bookshelves: List<String>,
    @Json(name = "languages") val languages: List<String>,
    @Json(name = "formats") val formats: Map<String, String>,
    @Json(name = "download_count") val downloadCount: Int,
    @Json(name = "media_type") val mediaType: String?
)