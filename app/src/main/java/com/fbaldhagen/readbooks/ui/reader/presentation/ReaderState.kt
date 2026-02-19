package com.fbaldhagen.readbooks.ui.reader.presentation

import com.fbaldhagen.readbooks.domain.model.Bookmark
import com.fbaldhagen.readbooks.domain.model.TocEntry
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication

data class ReaderState(
    val publication: Publication? = null,
    val initialLocator: Locator? = null,
    val bookId: Long = 0,
    val bookTitle: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val bookmarks: List<Bookmark> = emptyList(),
    val currentPageBookmark: Bookmark? = null,
    val currentLocator: Locator? = null,
    val preferences: ReaderPreferences = ReaderPreferences(),
    val currentChapterTitle: String? = null,
    val totalProgression: Float = 0f,
    val barsVisible: Boolean = true,
    val tableOfContents: List<TocEntry> = emptyList()
)