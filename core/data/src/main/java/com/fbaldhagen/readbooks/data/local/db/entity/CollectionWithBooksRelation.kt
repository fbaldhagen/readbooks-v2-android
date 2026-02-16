package com.fbaldhagen.readbooks.data.local.db.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class CollectionWithBooksRelation(
    @Embedded
    val collection: CollectionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = BookCollectionCrossRef::class,
            parentColumn = "collection_id",
            entityColumn = "book_id"
        )
    )
    val books: List<BookEntity>
)