package com.fbaldhagen.readbooks.ui.reader.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.common.result.onError
import com.fbaldhagen.readbooks.common.result.onSuccess
import com.fbaldhagen.readbooks.domain.model.Bookmark
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.model.TocEntry
import com.fbaldhagen.readbooks.domain.usecase.BookmarkUseCases
import com.fbaldhagen.readbooks.domain.usecase.LibraryUseCases
import com.fbaldhagen.readbooks.domain.usecase.ReadingSessionUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.readium.r2.shared.publication.Link
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.services.positions
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.streamer.PublicationOpener
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val assetRetriever: AssetRetriever,
    private val publicationOpener: PublicationOpener,
    private val libraryUseCases: LibraryUseCases,
    private val bookmarkUseCases: BookmarkUseCases,
    private val readingSessionUseCases: ReadingSessionUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(ReaderState())
    val state: StateFlow<ReaderState> = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Link>(extraBufferCapacity = 1)
    val navigationEvent: SharedFlow<Link> = _navigationEvent.asSharedFlow()

    private var sessionId: Long? = null
    private var startProgression: Float = 0f
    private var estimatedPageCount: Int = 0

    val bookId: Long = savedStateHandle.get<Long>("book_id") ?: 0

    init {
        if (bookId > 0) {
            openBook(bookId)
        } else {
            _state.update { it.copy(isLoading = false, error = "Invalid book ID") }
        }
    }

    private fun openBook(bookId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, bookId = bookId) }

            libraryUseCases.getById(bookId)
                .onSuccess { book ->
                    val filePath = book.filePath
                    if (filePath == null) {
                        _state.update {
                            it.copy(isLoading = false, error = "Book file not found")
                        }
                        return@launch
                    }

                    val url = AbsoluteUrl("file://$filePath")
                    if (url == null) {
                        _state.update {
                            it.copy(isLoading = false, error = "Invalid file path")
                        }
                        return@launch
                    }

                    val asset = assetRetriever.retrieve(url)
                        .getOrElse { error ->
                            _state.update {
                                it.copy(isLoading = false, error = "Failed to retrieve asset: $error")
                            }
                            return@launch
                        }

                    val publication = publicationOpener.open(
                        asset = asset,
                        allowUserInteraction = false
                    ).getOrElse { error ->
                        _state.update {
                            it.copy(isLoading = false, error = "Failed to open book: $error")
                        }
                        return@launch
                    }

                    val initialLocator = book.currentLocator?.let {
                        parseLocator(it)
                    }

                    startProgression = book.progress
                    estimatedPageCount = publication.positions().size

                    _state.update {
                        it.copy(
                            publication = publication,
                            initialLocator = initialLocator,
                            bookTitle = book.title,
                            isLoading = false,
                            tableOfContents = publication.tableOfContents.map { link -> link.toTocEntry() }
                        )
                    }

                    libraryUseCases.updateReadingStatus(bookId, ReadingStatus.READING)
                    readingSessionUseCases.start(bookId).onSuccess { id ->
                        sessionId = id
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(isLoading = false, error = error.message)
                    }
                }
        }

        viewModelScope.launch {
            bookmarkUseCases.observeForBook(bookId)
                .catch { /* ignore */ }
                .collect { bookmarks ->
                    _state.update { it.copy(bookmarks = bookmarks) }
                }
        }
    }

    fun navigateToTocEntry(entry: TocEntry) {
        val publication = _state.value.publication ?: return
        val link = findLink(publication.tableOfContents, entry.href) ?: return
        _navigationEvent.tryEmit(link)
        _state.update { it.copy(barsVisible = false) }
    }

    private fun findLink(links: List<Link>, href: String): Link? {
        for (link in links) {
            if (link.href.toString() == href) return link
            findLink(link.children, href)?.let { return it }
        }
        return null
    }

    private fun Link.toTocEntry(): TocEntry = TocEntry(
        title = title ?: "Untitled",
        href = href.toString(),
        children = children.map { it.toTocEntry() }
    )

    fun onToggleBars() {
        _state.update { it.copy(barsVisible = !it.barsVisible) }
    }

    fun onUpdatePreferences(preferences: ReaderPreferences) {
        _state.update { it.copy(preferences = preferences) }
    }

    fun onLocatorChanged(locator: Locator) {
        _state.update {
            it.copy(
                currentLocator = locator,
                totalProgression = locator.locations.totalProgression?.toFloat() ?: it.totalProgression,
                currentChapterTitle = locator.title ?: it.currentChapterTitle
            )
        }
        viewModelScope.launch {
            libraryUseCases.updateProgress(
                bookId = bookId,
                locator = locator.toJSON().toString(),
                progress = locator.locations.totalProgression?.toFloat() ?: 0f
            )
        }
    }

    fun addBookmark() {
        val locator = _state.value.currentLocator ?: return
        viewModelScope.launch {
            bookmarkUseCases.add(
                Bookmark(
                    bookId = bookId,
                    locator = locator.toJSON().toString(),
                    title = locator.title
                )
            )
        }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarkUseCases.delete(bookmark.id)
        }
    }

    fun endSession() {
        val id = sessionId ?: return
        sessionId = null
        val currentProgression = _state.value.totalProgression
        val progressionDelta = (currentProgression - startProgression).coerceAtLeast(0f)
        val pagesRead = (progressionDelta * estimatedPageCount).toInt()
        viewModelScope.launch(NonCancellable) {
            readingSessionUseCases.end(id, pagesRead)
        }
    }

    fun resumeSessionIfNeeded() {
        if (sessionId != null || _state.value.publication == null) return
        startProgression = _state.value.totalProgression
        viewModelScope.launch {
            readingSessionUseCases.start(bookId).onSuccess { id ->
                sessionId = id
            }
        }
    }

    private fun parseLocator(json: String): Locator? =
        try {
            Locator.fromJSON(JSONObject(json))
        } catch (_: Exception) {
            null
        }

    override fun onCleared() {
        super.onCleared()
        endSession()
    }
}