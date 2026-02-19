package com.fbaldhagen.readbooks.data.mapper

import com.fbaldhagen.readbooks.data.local.db.entity.BookmarkEntity
import com.fbaldhagen.readbooks.domain.model.Bookmark

fun BookmarkEntity.toDomain(): Bookmark = Bookmark(
    id = id,
    bookId = bookId,
    locator = locator,
    title = title,
    note = note,
    createdAt = createdAt
)

fun Bookmark.toEntity(): BookmarkEntity = BookmarkEntity(
    id = id,
    bookId = bookId,
    locator = locator,
    title = title,
    note = note,
    createdAt = createdAt
)