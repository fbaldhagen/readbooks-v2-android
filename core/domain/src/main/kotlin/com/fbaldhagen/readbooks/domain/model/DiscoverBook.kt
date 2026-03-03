package com.fbaldhagen.readbooks.domain.model

data class DiscoverBook(
    val gutenbergId: Int,
    val title: String,
    val authors: List<String>,
    val coverUrl: String?,
    val downloadUrl: String?,
    val summary: String?,
    val subjects: List<String> = emptyList(),
    val downloadCount: Int = 0,
    val averageRating: Double? = null
)