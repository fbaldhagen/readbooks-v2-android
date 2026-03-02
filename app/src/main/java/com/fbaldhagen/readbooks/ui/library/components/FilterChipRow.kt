package com.fbaldhagen.readbooks.ui.library.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.ReadingStatus

@Composable
fun FilterChipRow(
    selectedStatus: ReadingStatus?,
    onStatusSelected: (ReadingStatus?) -> Unit,
    showArchived: Boolean,
    onToggleArchived: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedStatus == null && !showArchived,
                onClick = {
                    onStatusSelected(null)
                    if (showArchived) onToggleArchived()
                },
                label = { Text("All") }
            )
        }
        items(ReadingStatus.entries) { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = {
                    onStatusSelected(if (selectedStatus == status) null else status)
                },
                label = { Text(status.displayName()) }
            )
        }
        item {
            FilterChip(
                selected = showArchived,
                onClick = {
                    onToggleArchived()
                    if (!showArchived) onStatusSelected(null)
                },
                label = { Text("Archived") }
            )
        }
    }
}

private fun ReadingStatus.displayName(): String = when (this) {
    ReadingStatus.NOT_STARTED -> "New"
    ReadingStatus.READING -> "Started"
    ReadingStatus.FINISHED -> "Done"
}