package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.Bookmark
import com.fbaldhagen.readbooks.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookmarkUseCases @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    fun observeForBook(bookId: Long): Flow<List<Bookmark>> =
        bookmarkRepository.observeForBook(bookId)

    suspend fun add(bookmark: Bookmark): Result<Long> =
        bookmarkRepository.add(bookmark)

    suspend fun delete(bookmarkId: Long): Result<Unit> =
        bookmarkRepository.delete(bookmarkId)
}