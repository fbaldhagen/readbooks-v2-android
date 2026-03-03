package com.fbaldhagen.readbooks.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.ShelfState
import com.fbaldhagen.readbooks.ui.components.BookItem
import com.fbaldhagen.readbooks.ui.components.ErrorMessage
import com.fbaldhagen.readbooks.ui.components.ShimmerBookItem

@Composable
fun DiscoverShelf(
    title: String,
    shelfState: ShelfState,
    onBookClick: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        when (shelfState) {
            is ShelfState.Loading -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(6) { ShimmerBookItem() }
                }
            }
            is ShelfState.Error -> {
                ErrorMessage(
                    message = shelfState.message,
                    onRetry = onRetry,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            is ShelfState.Success -> {
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
                            showAuthor = false,
                            rating = book.averageRating,
                            modifier = Modifier.width(90.dp)
                        )
                    }
                }
            }
        }
    }
}