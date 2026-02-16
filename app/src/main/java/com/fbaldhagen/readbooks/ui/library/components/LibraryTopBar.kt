package com.fbaldhagen.readbooks.ui.library.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.SortType

@Composable
fun LibraryTopBar(
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onToggleSearch: () -> Unit,
    currentSort: SortType,
    onSortChanged: (SortType) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Library",
                style = MaterialTheme.typography.headlineMedium
            )
            Row {
                IconButton(onClick = onToggleSearch) {
                    Icon(
                        imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (isSearchActive) "Close search" else "Search"
                    )
                }
                IconButton(onClick = { showSortMenu = true }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "Sort"
                    )
                }
                SortDropdownMenu(
                    expanded = showSortMenu,
                    currentSort = currentSort,
                    onDismiss = { showSortMenu = false },
                    onSortSelected = {
                        onSortChanged(it)
                        showSortMenu = false
                    }
                )
            }
        }

        AnimatedVisibility(visible = isSearchActive) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = { Text("Search books...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium
            )
        }
    }
}