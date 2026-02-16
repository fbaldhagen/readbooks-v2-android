package com.fbaldhagen.readbooks.ui.discover.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.ui.components.ErrorMessage
@Composable
fun DiscoverContent(
    books: LazyPagingItems<DiscoverBook>,
    onBookClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    when (val refreshState = books.loadState.refresh) {
        is LoadState.Loading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
        is LoadState.Error -> {
            ErrorMessage(
                message = refreshState.error.localizedMessage ?: "Failed to load books",
                onRetry = { books.retry() },
                modifier = modifier
            )
        }
        is LoadState.NotLoading -> {
            if (books.itemCount == 0) {
                EmptyDiscoverMessage(modifier = modifier)
            } else {
                DiscoverGrid(
                    books = books,
                    onBookClick = onBookClick,
                    modifier = modifier
                )
            }
        }
    }
}