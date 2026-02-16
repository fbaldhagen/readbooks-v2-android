package com.fbaldhagen.readbooks.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.domain.model.ReadingGoalProgress
import com.fbaldhagen.readbooks.ui.components.BookCoverImage
import com.fbaldhagen.readbooks.ui.components.ErrorMessage
import com.fbaldhagen.readbooks.ui.components.LoadingIndicator

@Composable
fun HomeScreen(
    onNavigateToBookDetails: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when {
        state.isLoading -> LoadingIndicator(modifier = modifier)
        state.error != null -> ErrorMessage(
            message = state.error!!,
            onRetry = { viewModel.dismissError() },
            modifier = modifier
        )
        else -> HomeContent(
            state = state,
            onBookClick = onNavigateToBookDetails,
            modifier = modifier
        )
    }
}

@Composable
private fun HomeContent(
    state: HomeState,
    onBookClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp)
    ) {
        Text(
            text = "ReadBooks",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        state.readingGoalProgress?.let { progress ->
            ReadingGoalCard(progress = progress)
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.currentlyReading.isNotEmpty()) {
            BookRow(
                title = "Continue Reading",
                books = state.currentlyReading,
                onBookClick = onBookClick
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.recentBooks.isNotEmpty()) {
            BookRow(
                title = "Recently Added",
                books = state.recentBooks,
                onBookClick = onBookClick
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.currentlyReading.isEmpty() && state.recentBooks.isEmpty()) {
            EmptyHomeMessage()
        }
    }
}

@Composable
private fun ReadingGoalCard(
    progress: ReadingGoalProgress,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Today's Reading Goal",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress.progressFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${progress.todayMinutes} / ${progress.goalMinutes} min",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BookRow(
    title: String,
    books: List<Book>,
    onBookClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(books, key = { it.id }) { book ->
                BookItem(
                    book = book,
                    onClick = { onBookClick(book.id) }
                )
            }
        }
    }
}

@Composable
private fun BookItem(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(100.dp)
            .clickable(onClick = onClick)
    ) {
        BookCoverImage(
            coverUrl = book.coverUri,
            contentDescription = book.title,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = book.title,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = book.authors.firstOrNull() ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EmptyHomeMessage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Your library is empty",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Head to Discover to find your first book",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}