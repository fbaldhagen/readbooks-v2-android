package com.fbaldhagen.readbooks.ui.reader.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.TocEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TocSheet(
    entries: List<TocEntry>,
    currentChapterTitle: String?,
    onEntryClick: (TocEntry) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Table of Contents",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )
            HorizontalDivider()
            LazyColumn {
                flattenedTocItems(entries, depth = 0).forEach { (entry, depth) ->
                    item(key = entry.href) {
                        TocItem(
                            entry = entry,
                            depth = depth,
                            isCurrent = entry.title == currentChapterTitle,
                            onClick = {
                                onEntryClick(entry)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TocItem(
    entry: TocEntry,
    depth: Int,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = entry.title,
        style = if (depth == 0) {
            MaterialTheme.typography.bodyLarge
        } else {
            MaterialTheme.typography.bodyMedium
        },
        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
        color = if (isCurrent) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                start = (24 + depth * 16).dp,
                end = 24.dp,
                top = 12.dp,
                bottom = 12.dp
            )
    )
}

private fun flattenedTocItems(
    entries: List<TocEntry>,
    depth: Int
): List<Pair<TocEntry, Int>> = buildList {
    entries.forEach { entry ->
        add(entry to depth)
        addAll(flattenedTocItems(entry.children, depth + 1))
    }
}