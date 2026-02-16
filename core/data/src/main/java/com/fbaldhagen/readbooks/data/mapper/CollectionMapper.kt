package com.fbaldhagen.readbooks.data.mapper

import com.fbaldhagen.readbooks.data.local.db.entity.CollectionEntity
import com.fbaldhagen.readbooks.data.local.db.entity.CollectionWithBooksRelation
import com.fbaldhagen.readbooks.domain.model.Collection
import com.fbaldhagen.readbooks.domain.model.CollectionWithBooks

fun CollectionEntity.toDomain(): Collection = Collection(
    id = id,
    name = name,
    createdAt = createdAt
)

fun Collection.toEntity(): CollectionEntity = CollectionEntity(
    id = id,
    name = name,
    createdAt = createdAt
)

fun CollectionWithBooksRelation.toDomain(): CollectionWithBooks = CollectionWithBooks(
    collection = collection.toDomain(),
    books = books.map { it.toDomain() }
)