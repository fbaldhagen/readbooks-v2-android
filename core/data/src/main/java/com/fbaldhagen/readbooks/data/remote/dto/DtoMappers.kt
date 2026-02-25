package com.fbaldhagen.readbooks.data.remote.dto

import com.fbaldhagen.readbooks.domain.model.DiscoverBook

private const val EPUB_MIME = "application/epub+zip"
private const val IMAGE_MIME = "image/jpeg"

fun GutendexBookDto.toDiscoverBook(): DiscoverBook = DiscoverBook(
    gutenbergId = id,
    title = title,
    authors = authors.map { it.name },
    coverUrl = formats[IMAGE_MIME],
    downloadUrl = formats[EPUB_MIME],
    summary = summaries?.firstOrNull(),
    subjects = bookshelves,
    downloadCount = downloadCount
)