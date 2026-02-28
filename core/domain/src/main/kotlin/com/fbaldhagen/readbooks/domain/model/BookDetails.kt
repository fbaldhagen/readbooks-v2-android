package com.fbaldhagen.readbooks.domain.model

data class BookDetails(
    val gutenbergId: Int?,
    val title: String,
    val authors: List<String>,
    val coverUrl: String?,
    val description: String?,
    val subjects: List<String>,
    val state: BookDetailsState
)

sealed interface BookDetailsState {

    data class NotInLibrary(
        val gutenbergId: Int,
        val downloadUrl: String?
    ) : BookDetailsState

    data object Downloading : BookDetailsState

    data class InLibrary(
        val bookId: Long,
        val filePath: String?,
        val readingStatus: ReadingStatus,
        val progress: Float,
        val rating: Int?,
        val currentLocator: String?,
        val collections: List<Collection>,
        val totalReadingMinutes: Int
    ) : BookDetailsState
}