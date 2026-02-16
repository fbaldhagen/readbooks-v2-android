package com.fbaldhagen.readbooks.ui.bookdetails

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fbaldhagen.readbooks.ui.bookdetails.components.BookDetailsContent
import com.fbaldhagen.readbooks.ui.components.ErrorMessage
import com.fbaldhagen.readbooks.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    onNavigateBack: () -> Unit,
    onOpenReader: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when (val current = state) {
            is BookDetailsUiState.Loading -> LoadingIndicator(
                modifier = Modifier.padding(innerPadding)
            )
            is BookDetailsUiState.Error -> ErrorMessage(
                message = current.message,
                modifier = Modifier.padding(innerPadding)
            )
            is BookDetailsUiState.Success -> BookDetailsContent(
                details = current.details,
                onOpenReader = onOpenReader,
                onDownload = viewModel::onDownloadBook,
                onRatingChanged = viewModel::onUpdateRating,
                onStatusChanged = viewModel::onUpdateReadingStatus,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}