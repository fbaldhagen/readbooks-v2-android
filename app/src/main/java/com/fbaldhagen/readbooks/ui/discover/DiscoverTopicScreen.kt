package com.fbaldhagen.readbooks.ui.discover

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.fbaldhagen.readbooks.ui.discover.components.DiscoverContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverTopicScreen(
    topic: String,
    onNavigateBack: () -> Unit,
    onNavigateToBookDetails: (Int) -> Unit,
    viewModel: DiscoverTopicViewModel = hiltViewModel()
) {
    val books = viewModel.books.collectAsLazyPagingItems()
    val libraryGutenbergIds by viewModel.libraryGutenbergIds.collectAsStateWithLifecycle()
    val archivedGutenbergIds by viewModel.archivedGutenbergIds.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topic) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        DiscoverContent(
            books = books,
            onBookClick = onNavigateToBookDetails,
            libraryGutenbergIds = libraryGutenbergIds,
            archivedGutenbergIds = archivedGutenbergIds,
            modifier = Modifier.padding(innerPadding)
        )
    }
}