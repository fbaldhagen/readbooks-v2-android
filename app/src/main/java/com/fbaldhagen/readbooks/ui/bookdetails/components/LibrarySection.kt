package com.fbaldhagen.readbooks.ui.bookdetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.BookDetailsState
import com.fbaldhagen.readbooks.domain.model.ReadingStatus

@Composable
fun LibrarySection(
    state: BookDetailsState.InLibrary,
    onStatusChanged: (ReadingStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Your Progress",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Status chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReadingStatus.entries.forEach { status ->
                FilterChip(
                    selected = state.readingStatus == status,
                    onClick = { onStatusChanged(status) },
                    label = {
                        Text(
                            text = when (status) {
                                ReadingStatus.NOT_STARTED -> "Not Started"
                                ReadingStatus.READING -> "Reading"
                                ReadingStatus.FINISHED -> "Finished"
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    leadingIcon = if (state.readingStatus == status) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null
                )
            }
        }

        if (state.totalReadingMinutes > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Time spent reading: ${formatMinutes(state.totalReadingMinutes)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (state.collections.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "In collections: ${state.collections.joinToString(", ") { it.name }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

private fun formatMinutes(minutes: Int): String = when {
    minutes < 60 -> "$minutes min"
    else -> "${minutes / 60}h ${minutes % 60}m"
}