package com.fbaldhagen.readbooks.ui.discover.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.ui.components.BookItem
import com.fbaldhagen.readbooks.ui.components.ErrorMessage
import com.fbaldhagen.readbooks.ui.components.ShimmerBookItem
import com.fbaldhagen.readbooks.ui.discover.DiscoverViewModel

@Composable
fun BookShelf(
    topic: String,
    shelfState: DiscoverViewModel.ShelfState?,
    libraryGutenbergIds: Set<Int>,
    archivedGutenbergIds: Set<Int>,
    onBookClick: (Int) -> Unit,
    onSeeAll: (String) -> Unit,
    onLoad: (String) -> Unit,
    onRetry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(topic) {
        onLoad(topic)
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = topic,
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = { onSeeAll(topic) }) {
                Text("See all")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (shelfState) {
            null, is DiscoverViewModel.ShelfState.Loading -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(6) {
                        ShimmerBookItem()
                    }
                }
            }
            is DiscoverViewModel.ShelfState.Error -> {
                ErrorMessage(
                    message = shelfState.message,
                    onRetry = { onRetry(topic) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            is DiscoverViewModel.ShelfState.Success -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(shelfState.books) { book ->
                        BookItem(
                            title = book.title,
                            authors = book.authors,
                            coverUrl = book.coverUrl,
                            onClick = { onBookClick(book.gutenbergId) },
                            showInLibraryBadge = book.gutenbergId in libraryGutenbergIds,
                            isArchived = book.gutenbergId in archivedGutenbergIds,
                            modifier = Modifier.width(90.dp)
                        )
                    }
                }
            }
        }
    }
}