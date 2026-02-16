package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.Collection
import com.fbaldhagen.readbooks.domain.model.CollectionWithBooks
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {

    fun observeAll(): Flow<List<Collection>>

    fun observeAllWithBooks(): Flow<List<CollectionWithBooks>>

    fun observeWithBooks(collectionId: Long): Flow<CollectionWithBooks?>

    fun observeCollectionsForBook(bookId: Long): Flow<List<Collection>>

    suspend fun create(name: String): Result<Long>

    suspend fun rename(collectionId: Long, name: String): Result<Unit>

    suspend fun delete(collectionId: Long): Result<Unit>

    suspend fun addBook(collectionId: Long, bookId: Long): Result<Unit>

    suspend fun removeBook(collectionId: Long, bookId: Long): Result<Unit>
}