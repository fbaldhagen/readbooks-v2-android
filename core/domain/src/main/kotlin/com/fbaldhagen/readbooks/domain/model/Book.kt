package com.fbaldhagen.readbooks.domain.model

data class Book(
    val id: Long = 0,
    val title: String,
    val authors: List<String>,
    val coverUri: String? = null,
    val filePath: String? = null,
    val gutenbergId: Int? = null,
    val language: String? = null,
    val subjects: List<String> = emptyList(),
    val addedAt: Long = System.currentTimeMillis(),
    val lastReadAt: Long? = null,
    val readingStatus: ReadingStatus = ReadingStatus.NOT_STARTED,
    val currentLocator: String? = null,
    val progress: Float = 0f,
    val rating: Int? = null,
    val isArchived: Boolean = false
)