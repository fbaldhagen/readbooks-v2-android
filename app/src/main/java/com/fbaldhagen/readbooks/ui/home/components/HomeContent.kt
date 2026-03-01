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
import java.util.Calendar

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
            .padding(top = 24.dp)
            .padding(bottom = 16.dp)
    ) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 4 -> "Late night reading?"
            hour < 12 -> "Good morning"
            hour < 18 -> "Good afternoon"
            else      -> "Good evening"
        }

        Text(
            text = greeting,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = "ReadBooks",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        state.readingGoalProgress?.let { progress ->
            ReadingGoalCard(progress = progress)
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.currentlyReading.isNotEmpty()) {
            Text(
                text = "Continue Reading",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            CurrentlyReadingCard(
                book = state.currentlyReading.first(),
                onClick = { onBookClick(state.currentlyReading.first().id) },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .glowEffect(color = MaterialTheme.colorScheme.primary)
            )

            if (state.currentlyReading.size > 1) {
                Spacer(modifier = Modifier.height(24.dp))
                HomeBookRow(
                    title = "Other books you've started",
                    books = state.currentlyReading.drop(n = 1),
                    onBookClick = onBookClick
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
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