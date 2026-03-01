package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.domain.model.FilterState
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    fun observeAll(): Flow<List<Book>>

    fun observeFiltered(filterState: FilterState): Flow<List<Book>>

    fun observeById(bookId: Long): Flow<Book?>

    fun observeByStatus(status: ReadingStatus): Flow<List<Book>>

    fun observeRecent(limit: Int): Flow<List<Book>>

    fun observeTotalCount(): Flow<Int>

    fun observeFinishedCount(): Flow<Int>

    suspend fun getById(bookId: Long): Result<Book>

    suspend fun getByGutenbergId(gutenbergId: Int): Book?

    suspend fun insert(book: Book): Result<Long>

    suspend fun update(book: Book): Result<Unit>

    suspend fun delete(bookId: Long): Result<Unit>

    suspend fun updateReadingStatus(bookId: Long, status: ReadingStatus): Result<Unit>

    suspend fun updateProgress(bookId: Long, locator: String, progress: Float): Result<Unit>

    suspend fun updateLastReadAt(bookId: Long, timestamp: Long): Result<Unit>

    suspend fun updateRating(bookId: Long, rating: Int?): Result<Unit>

    suspend fun archiveBook(bookId: Long): Result<Unit>

    suspend fun resetProgress(bookId: Long): Result<Unit>
}