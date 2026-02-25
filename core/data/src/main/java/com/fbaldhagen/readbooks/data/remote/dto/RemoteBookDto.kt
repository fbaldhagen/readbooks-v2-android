package com.fbaldhagen.readbooks.data.remote.dto

data class RemoteBookRequest(
    val gutenbergId: Int? = null,
    val title: String,
    val authors: String,
    val description: String? = null,
    val coverUri: String? = null,
    val language: String? = null,
    val subjects: String = "[]",
    val readingStatus: String = "NOT_STARTED",
    val currentLocator: String? = null,
    val progress: Float = 0f,
    val rating: Int? = null,
    val isArchived: Boolean = false
)

data class RemoteBookResponse(
    val id: Long,
    val gutenbergId: Int? = null,
    val title: String,
    val authors: String,
    val description: String? = null,
    val coverUri: String? = null,
    val language: String? = null,
    val subjects: String,
    val readingStatus: String,
    val currentLocator: String? = null,
    val progress: Float,
    val rating: Int? = null,
    val isArchived: Boolean
)