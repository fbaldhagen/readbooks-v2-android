package com.fbaldhagen.readbooks.ui.bookdetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.BookDetailsState
import com.fbaldhagen.readbooks.domain.model.ReadingStatus

@Composable
fun LibrarySection(
    state: BookDetailsState.InLibrary,
    onRatingChanged: (Int?) -> Unit,
    onStatusChanged: (ReadingStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Your Progress",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Status chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReadingStatus.entries.forEach { status ->
                OutlinedButton(
                    onClick = { onStatusChanged(status) },
                    border = if (state.readingStatus == status) {
                        androidx.compose.foundation.BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.primary
                        )
                    } else {
                        androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        )
                    }
                ) {
                    Text(
                        text = when (status) {
                            ReadingStatus.NOT_STARTED -> "Not Started"
                            ReadingStatus.READING -> "Reading"
                            ReadingStatus.FINISHED -> "Finished"
                        },
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Rating
        Text(
            text = "Rating",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(
            rating = state.rating ?: 0,
            onRatingChanged = { rating ->
                onRatingChanged(if (rating == state.rating) null else rating)
            }
        )

        if (state.totalReadingMinutes > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Time spent reading: ${formatMinutes(state.totalReadingMinutes)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (state.collections.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "In collections: ${state.collections.joinToString(", ") { it.name }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatMinutes(minutes: Int): String = when {
    minutes < 60 -> "$minutes min"
    else -> "${minutes / 60}h ${minutes % 60}m"
}