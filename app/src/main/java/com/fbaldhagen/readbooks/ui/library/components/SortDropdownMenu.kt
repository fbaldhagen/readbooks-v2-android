package com.fbaldhagen.readbooks.ui.library.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.model.SortType

@Composable
fun SortDropdownMenu(
    expanded: Boolean,
    currentSort: SortType,
    onDismiss: () -> Unit,
    onSortSelected: (SortType) -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        SortType.entries.forEach { sortType ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = sortType.displayName(),
                        color = if (sortType == currentSort) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                },
                onClick = { onSortSelected(sortType) }
            )
        }
    }
}

private fun SortType.displayName(): String = when (this) {
    SortType.TITLE -> "Title"
    SortType.AUTHOR -> "Author"
    SortType.RECENTLY_ADDED -> "Recently Added"
    SortType.LAST_READ -> "Last Read"
}

private fun ReadingStatus.displayName(): String = when (this) {
    ReadingStatus.NOT_STARTED -> "Not Started"
    ReadingStatus.READING -> "Reading"
    ReadingStatus.FINISHED -> "Finished"
}