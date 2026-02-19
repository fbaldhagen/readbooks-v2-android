package com.fbaldhagen.readbooks.ui.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.ReadingAnalytics

@Composable
fun StatsCard(
    analytics: ReadingAnalytics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = analytics.totalBooks.toString(), label = "Books")
            StatItem(value = analytics.totalBooksFinished.toString(), label = "Finished")
            StatItem(value = formatReadingTime(analytics.totalReadingMinutes), label = "Read")
            StatItem(value = "${analytics.currentStreak.currentDays}d", label = "Streak")
        }
    }
}

private fun formatReadingTime(minutes: Int): String = when {
    minutes < 60 -> "${minutes}m"
    minutes < 1440 -> "${minutes / 60}h"
    else -> "${minutes / 1440}d"
}