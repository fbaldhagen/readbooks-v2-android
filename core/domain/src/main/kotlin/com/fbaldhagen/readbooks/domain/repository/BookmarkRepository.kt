package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {

    fun observeForBook(bookId: Long): Flow<List<Bookmark>>

    suspend fun add(bookmark: Bookmark): Result<Long>

    suspend fun delete(bookmarkId: Long): Result<Unit>

    suspend fun update(bookmark: Bookmark): Result<Unit>
}