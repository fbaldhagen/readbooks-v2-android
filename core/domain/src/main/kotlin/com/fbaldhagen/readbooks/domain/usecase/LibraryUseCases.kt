package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.domain.model.FilterState
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LibraryUseCases @Inject constructor(
    private val bookRepository: BookRepository
) {
    fun observeAll(): Flow<List<Book>> =
        bookRepository.observeAll()

    fun observeFiltered(filterState: FilterState): Flow<List<Book>> =
        bookRepository.observeFiltered(filterState)

    fun observeByStatus(status: ReadingStatus): Flow<List<Book>> =
        bookRepository.observeByStatus(status)

    fun observeRecent(limit: Int = 10): Flow<List<Book>> =
        bookRepository.observeRecent(limit)

    fun observeTotalCount(): Flow<Int> =
        bookRepository.observeTotalCount()

    fun observeFinishedCount(): Flow<Int> =
        bookRepository.observeFinishedCount()

    suspend fun getById(bookId: Long): Result<Book> =
        bookRepository.getById(bookId)

    suspend fun addBook(book: Book): Result<Long> =
        bookRepository.insert(book)

    suspend fun deleteBook(bookId: Long): Result<Unit> =
        bookRepository.delete(bookId)

    suspend fun updateReadingStatus(bookId: Long, status: ReadingStatus): Result<Unit> =
        bookRepository.updateReadingStatus(bookId, status)

    suspend fun updateProgress(bookId: Long, locator: String, progress: Float): Result<Unit> =
        bookRepository.updateProgress(bookId, locator, progress)

    suspend fun updateRating(bookId: Long, rating: Int?): Result<Unit> =
        bookRepository.updateRating(bookId, rating)

    suspend fun archiveBook(bookId: Long): Result<Unit> =
        bookRepository.archiveBook(bookId)

    suspend fun resetProgress(bookId: Long): Result<Unit> =
        bookRepository.resetProgress(bookId)
}