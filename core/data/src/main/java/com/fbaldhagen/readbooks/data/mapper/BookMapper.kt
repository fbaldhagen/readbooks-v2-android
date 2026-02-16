package com.fbaldhagen.readbooks.data.mapper

import com.fbaldhagen.readbooks.data.local.db.entity.BookEntity
import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

private val moshi = Moshi.Builder().build()
private val stringListAdapter = moshi.adapter<List<String>>(
    Types.newParameterizedType(List::class.java, String::class.java)
)

fun BookEntity.toDomain(): Book = Book(
    id = id,
    title = title,
    authors = parseJsonStringList(authors),
    description = description,
    coverUri = coverUri,
    filePath = filePath,
    gutenbergId = gutenbergId,
    language = language,
    subjects = parseJsonStringList(subjects),
    addedAt = addedAt,
    lastReadAt = lastReadAt,
    readingStatus = try {
        ReadingStatus.valueOf(readingStatus)
    } catch (_: IllegalArgumentException) {
        ReadingStatus.NOT_STARTED
    },
    currentLocator = currentLocator,
    progress = progress,
    rating = rating,
    isArchived = isArchived
)

fun Book.toEntity(): BookEntity = BookEntity(
    id = id,
    title = title,
    authors = toJsonStringList(authors),
    description = description,
    coverUri = coverUri,
    filePath = filePath,
    gutenbergId = gutenbergId,
    language = language,
    subjects = toJsonStringList(subjects),
    addedAt = addedAt,
    lastReadAt = lastReadAt,
    readingStatus = readingStatus.name,
    currentLocator = currentLocator,
    progress = progress,
    rating = rating,
    isArchived = isArchived
)

private fun parseJsonStringList(json: String): List<String> =
    try {
        stringListAdapter.fromJson(json) ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }

private fun toJsonStringList(list: List<String>): String =
    stringListAdapter.toJson(list)