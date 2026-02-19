package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.common.result.suspendRunCatching
import com.fbaldhagen.readbooks.data.local.db.dao.BookmarkDao
import com.fbaldhagen.readbooks.data.mapper.toDomain
import com.fbaldhagen.readbooks.data.mapper.toEntity
import com.fbaldhagen.readbooks.domain.model.Bookmark
import com.fbaldhagen.readbooks.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {

    override fun observeForBook(bookId: Long): Flow<List<Bookmark>> =
        bookmarkDao.observeForBook(bookId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun add(bookmark: Bookmark): Result<Long> = suspendRunCatching {
        bookmarkDao.insert(bookmark.toEntity())
    }

    override suspend fun delete(bookmarkId: Long): Result<Unit> = suspendRunCatching {
        bookmarkDao.delete(bookmarkId)
    }

    override suspend fun update(bookmark: Bookmark): Result<Unit> = suspendRunCatching {
        bookmarkDao.update(bookmark.toEntity())
    }
}