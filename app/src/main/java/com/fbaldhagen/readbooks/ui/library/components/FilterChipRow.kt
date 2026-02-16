package com.fbaldhagen.readbooks.ui.library.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.ui.library.displayName

@Composable
fun FilterChipRow(
    selectedStatus: ReadingStatus?,
    onStatusSelected: (ReadingStatus?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedStatus == null,
            onClick = { onStatusSelected(null) },
            label = { Text("All") }
        )
        ReadingStatus.entries.forEach { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = {
                    onStatusSelected(if (selectedStatus == status) null else status)
                },
                label = { Text(status.displayName()) }
            )
        }
    }
}