package com.fbaldhagen.readbooks.ui.discover.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.ui.components.BookItem
import com.fbaldhagen.readbooks.ui.components.ErrorMessage

@Composable
fun DiscoverGrid(
    books: LazyPagingItems<DiscoverBook>,
    onBookClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(count = books.itemCount) { index ->
            books[index]?.let { book ->
                BookItem(
                    title = book.title,
                    authors = book.authors,
                    coverUrl = book.coverUrl,
                    onClick = { onBookClick(book.gutenbergId) },
                    showInLibraryBadge = false,
                    modifier = modifier
                )
            }
        }

        if (books.loadState.append is LoadState.Loading) {
            item {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        if (books.loadState.append is LoadState.Error) {
            item {
                ErrorMessage(
                    message = "Failed to load more",
                    onRetry = { books.retry() }
                )
            }
        }
    }
}