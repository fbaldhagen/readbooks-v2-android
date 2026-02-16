package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.common.result.suspendRunCatching
import com.fbaldhagen.readbooks.data.local.db.dao.CollectionDao
import com.fbaldhagen.readbooks.data.local.db.entity.BookCollectionCrossRef
import com.fbaldhagen.readbooks.data.local.db.entity.CollectionEntity
import com.fbaldhagen.readbooks.data.mapper.toDomain
import com.fbaldhagen.readbooks.domain.model.Collection
import com.fbaldhagen.readbooks.domain.model.CollectionWithBooks
import com.fbaldhagen.readbooks.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CollectionRepositoryImpl @Inject constructor(
    private val collectionDao: CollectionDao
) : CollectionRepository {

    override fun observeAll(): Flow<List<Collection>> =
        collectionDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeAllWithBooks(): Flow<List<CollectionWithBooks>> =
        collectionDao.observeAllWithBooks().map { relations -> relations.map { it.toDomain() } }

    override fun observeWithBooks(collectionId: Long): Flow<CollectionWithBooks?> =
        collectionDao.observeWithBooks(collectionId).map { it?.toDomain() }

    override fun observeCollectionsForBook(bookId: Long): Flow<List<Collection>> =
        collectionDao.observeCollectionsForBook(bookId)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun create(name: String): Result<Long> = suspendRunCatching {
        collectionDao.insert(CollectionEntity(name = name))
    }

    override suspend fun rename(collectionId: Long, name: String): Result<Unit> =
        suspendRunCatching {
            collectionDao.rename(collectionId, name)
        }

    override suspend fun delete(collectionId: Long): Result<Unit> = suspendRunCatching {
        collectionDao.delete(collectionId)
    }

    override suspend fun addBook(collectionId: Long, bookId: Long): Result<Unit> =
        suspendRunCatching {
            collectionDao.addBookToCollection(
                BookCollectionCrossRef(bookId = bookId, collectionId = collectionId)
            )
        }

    override suspend fun removeBook(collectionId: Long, bookId: Long): Result<Unit> =
        suspendRunCatching {
            collectionDao.removeBookFromCollection(collectionId, bookId)
        }
}