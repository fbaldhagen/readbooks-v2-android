package com.fbaldhagen.readbooks.ui.bookdetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.model.BookDetailsState
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.model.RemoteRating

@Composable
fun BookDetailsContent(
    details: BookDetails,
    remoteRating: RemoteRating?,
    onOpenReader: (Long) -> Unit,
    onDownload: () -> Unit,
    onRatingChanged: (Int?) -> Unit,
    onStatusChanged: (ReadingStatus) -> Unit,
    modifier: Modifier = Modifier,
    authorBooks: List<DiscoverBook> = emptyList(),
    onAuthorBookClick: (Int) -> Unit,
    onAuthorClick: (String) -> Unit,
    onStartTts: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        BookHeader(
            details = details,
            onRatingChanged = onRatingChanged,
            onAuthorClick = onAuthorClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        ActionSection(
            state = details.state,
            onOpenReader = onOpenReader,
            onDownload = onDownload,
            onStartTts = onStartTts
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (details.state is BookDetailsState.InLibrary) {
            LibrarySection(
                state = details.state as BookDetailsState.InLibrary,
                onStatusChanged = onStatusChanged
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        details.description?.takeIf { it.isNotBlank() }?.let { description ->
            DescriptionSection(description = description)
            Spacer(modifier = Modifier.height(16.dp))
        }

        RatingSection(
            remoteRating = remoteRating
        )

        if (details.subjects.isNotEmpty()) {
            SubjectsSection(subjects = details.subjects)
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (authorBooks.isNotEmpty()) {
            MoreByAuthorSection(
                authorName = details.authors.first(),
                books = authorBooks,
                onBookClick = onAuthorBookClick
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}