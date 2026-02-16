package com.fbaldhagen.readbooks.ui.discover.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TopicChipRow(
    selectedTopic: String?,
    onTopicSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val topics = listOf(
        "Fiction",
        "Science Fiction",
        "Mystery",
        "Romance",
        "History",
        "Philosophy",
        "Adventure",
        "Poetry",
        "Drama",
        "Children"
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        items(topics) { topic ->
            FilterChip(
                selected = selectedTopic == topic,
                onClick = { onTopicSelected(topic) },
                label = { Text(topic) }
            )
        }
    }
}