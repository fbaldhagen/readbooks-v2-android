package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.common.result.suspendRunCatching
import com.fbaldhagen.readbooks.data.local.db.dao.BookDao
import com.fbaldhagen.readbooks.data.local.file.BookFileManager
import com.fbaldhagen.readbooks.data.mapper.toDomain
import com.fbaldhagen.readbooks.data.mapper.toEntity
import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.domain.model.FilterState
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val bookFileManager: BookFileManager
) : BookRepository {

    override fun observeAll(): Flow<List<Book>> =
        bookDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeFiltered(filterState: FilterState): Flow<List<Book>> =
        bookDao.observeFiltered(
            searchQuery = filterState.searchQuery,
            readingStatus = filterState.readingStatus?.name,
            sortType = filterState.sortType.name,
            showArchived = filterState.showArchived
        ).map { entities -> entities.map { it.toDomain() } }

    override fun observeById(bookId: Long): Flow<Book?> =
        bookDao.observeById(bookId).map { it?.toDomain() }

    override fun observeByStatus(status: ReadingStatus): Flow<List<Book>> =
        bookDao.observeByStatus(status.name).map { entities -> entities.map { it.toDomain() } }

    override fun observeRecent(limit: Int): Flow<List<Book>> =
        bookDao.observeRecent(limit).map { entities -> entities.map { it.toDomain() } }

    override fun observeTotalCount(): Flow<Int> =
        bookDao.observeTotalCount()

    override fun observeFinishedCount(): Flow<Int> =
        bookDao.observeFinishedCount()

    override suspend fun getById(bookId: Long): Result<Book> = suspendRunCatching {
        bookDao.getById(bookId)?.toDomain()
            ?: throw NoSuchElementException("Book $bookId not found")
    }

    override suspend fun getByGutenbergId(gutenbergId: Int): Book? =
        bookDao.getByGutenbergId(gutenbergId)?.toDomain()

    override suspend fun insert(book: Book): Result<Long> = suspendRunCatching {
        bookDao.insert(book.toEntity())
    }

    override suspend fun update(book: Book): Result<Unit> = suspendRunCatching {
        bookDao.update(book.toEntity())
    }

    override suspend fun delete(bookId: Long): Result<Unit> = suspendRunCatching {
        val entity = bookDao.getById(bookId) ?: return@suspendRunCatching
        entity.gutenbergId?.let { bookFileManager.deleteBookFiles(it) }
        bookDao.delete(bookId)
    }

    override suspend fun updateReadingStatus(bookId: Long, status: ReadingStatus): Result<Unit> =
        suspendRunCatching {
            bookDao.updateReadingStatus(bookId, status.name)
        }

    override suspend fun updateProgress(
        bookId: Long,
        locator: String,
        progress: Float
    ): Result<Unit> = suspendRunCatching {
        bookDao.updateProgress(bookId, locator, progress, System.currentTimeMillis())
    }

    override suspend fun updateLastReadAt(bookId: Long, timestamp: Long): Result<Unit> =
        suspendRunCatching {
            bookDao.updateLastReadAt(bookId, timestamp)
        }

    override suspend fun updateRating(bookId: Long, rating: Int?): Result<Unit> =
        suspendRunCatching {
            bookDao.updateRating(bookId, rating)
        }

    override suspend fun archiveBook(bookId: Long): Result<Unit> = suspendRunCatching {
        val entity = bookDao.getById(bookId) ?: return@suspendRunCatching
        entity.gutenbergId?.let { bookFileManager.archiveBook(it) }
        bookDao.archiveBook(bookId)
    }

    override suspend fun resetProgress(bookId: Long): Result<Unit> = suspendRunCatching {
        bookDao.resetProgress(bookId)
    }
}