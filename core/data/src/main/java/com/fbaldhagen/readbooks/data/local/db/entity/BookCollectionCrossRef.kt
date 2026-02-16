package com.fbaldhagen.readbooks.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "book_collection_cross_ref",
    primaryKeys = ["book_id", "collection_id"],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collection_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("book_id"),
        Index("collection_id")
    ]
)
data class BookCollectionCrossRef(
    @ColumnInfo(name = "book_id")
    val bookId: Long,
    @ColumnInfo(name = "collection_id")
    val collectionId: Long
)