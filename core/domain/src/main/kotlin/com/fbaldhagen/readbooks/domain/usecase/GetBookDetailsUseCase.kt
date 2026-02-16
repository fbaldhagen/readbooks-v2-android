package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.model.BookDetailsState
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import com.fbaldhagen.readbooks.domain.repository.CollectionRepository
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetBookDetailsUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val collectionRepository: CollectionRepository,
    private val sessionRepository: SessionRepository
) {
    /**
     * Load details for a book already in the library.
     * Opened from Home or Library screen.
     */
    fun fromLibrary(bookId: Long): Flow<BookDetails> = combine(
        bookRepository.observeById(bookId),
        collectionRepository.observeCollectionsForBook(bookId),
        sessionRepository.observeForBook(bookId)
    ) { book, collections, sessions ->
        book?.let {
            BookDetails(
                title = it.title,
                authors = it.authors,
                coverUrl = it.coverUri,
                description = it.description,
                subjects = it.subjects,
                state = BookDetailsState.InLibrary(
                    bookId = it.id,
                    filePath = it.filePath,
                    readingStatus = it.readingStatus,
                    progress = it.progress,
                    rating = it.rating,
                    currentLocator = it.currentLocator,
                    collections = collections,
                    totalReadingMinutes = sessions.sumOf { s -> s.durationMinutes }
                )
            )
        } ?: throw IllegalStateException("Book $bookId not found")
    }

    /**
     * Load details for a discover book.
     * Checks if already in library to determine state.
     */
    suspend fun fromDiscover(discoverBook: DiscoverBook): BookDetails {
        val existingBook = bookRepository.getByGutenbergId(discoverBook.gutenbergId)

        return if (existingBook != null) {
            BookDetails(
                title = existingBook.title,
                authors = existingBook.authors,
                coverUrl = existingBook.coverUri ?: discoverBook.coverUrl,
                description = existingBook.description ?: discoverBook.summary,
                subjects = existingBook.subjects,
                state = BookDetailsState.InLibrary(
                    bookId = existingBook.id,
                    filePath = existingBook.filePath,
                    readingStatus = existingBook.readingStatus,
                    progress = existingBook.progress,
                    rating = existingBook.rating,
                    currentLocator = existingBook.currentLocator,
                    collections = emptyList(),
                    totalReadingMinutes = 0
                )
            )
        } else {
            BookDetails(
                title = discoverBook.title,
                authors = discoverBook.authors,
                coverUrl = discoverBook.coverUrl,
                description = discoverBook.summary,
                subjects = discoverBook.subjects,
                state = BookDetailsState.NotInLibrary(
                    gutenbergId = discoverBook.gutenbergId,
                    downloadUrl = discoverBook.downloadUrl
                )
            )
        }
    }
}