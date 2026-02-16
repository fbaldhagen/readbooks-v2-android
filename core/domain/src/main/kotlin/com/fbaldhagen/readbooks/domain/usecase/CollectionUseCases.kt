package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.Collection
import com.fbaldhagen.readbooks.domain.model.CollectionWithBooks
import com.fbaldhagen.readbooks.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CollectionUseCases @Inject constructor(
    private val collectionRepository: CollectionRepository
) {
    fun observeAll(): Flow<List<Collection>> =
        collectionRepository.observeAll()

    fun observeAllWithBooks(): Flow<List<CollectionWithBooks>> =
        collectionRepository.observeAllWithBooks()

    fun observeWithBooks(collectionId: Long): Flow<CollectionWithBooks?> =
        collectionRepository.observeWithBooks(collectionId)

    fun observeCollectionsForBook(bookId: Long): Flow<List<Collection>> =
        collectionRepository.observeCollectionsForBook(bookId)

    suspend fun create(name: String): Result<Long> =
        collectionRepository.create(name)

    suspend fun rename(collectionId: Long, name: String): Result<Unit> =
        collectionRepository.rename(collectionId, name)

    suspend fun delete(collectionId: Long): Result<Unit> =
        collectionRepository.delete(collectionId)

    suspend fun addBook(collectionId: Long, bookId: Long): Result<Unit> =
        collectionRepository.addBook(collectionId, bookId)

    suspend fun removeBook(collectionId: Long, bookId: Long): Result<Unit> =
        collectionRepository.removeBook(collectionId, bookId)
}