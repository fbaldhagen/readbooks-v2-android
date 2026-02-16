package com.fbaldhagen.readbooks.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.ui.home.HomeState

@Composable
fun HomeContent(
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
            HomeBookRow(
                title = "Continue Reading",
                books = state.currentlyReading,
                onBookClick = onBookClick
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.recentBooks.isNotEmpty()) {
            HomeBookRow(
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