package com.fbaldhagen.readbooks.ui.bookdetails

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fbaldhagen.readbooks.domain.model.BookDetailsState
import com.fbaldhagen.readbooks.ui.bookdetails.components.BookDetailsContent
import com.fbaldhagen.readbooks.ui.components.ErrorMessage
import com.fbaldhagen.readbooks.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    onNavigateBack: () -> Unit,
    onOpenReader: (Long) -> Unit,
    onAuthorBookClick: (Int) -> Unit,
    onNavigateToAuthor: (String, Int?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val authorBooks by viewModel.authorBooks.collectAsStateWithLifecycle()
    val confirmationDialog by viewModel.confirmationDialog.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val remoteRating by viewModel.remoteRating.collectAsStateWithLifecycle()

    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { onNavigateBack() }
    }

    val inLibraryState = (state as? BookDetailsUiState.Success)
        ?.details?.state as? BookDetailsState.InLibrary

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
                },
                actions = {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options"
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Go to author") },
                                onClick = {
                                    menuExpanded = false
                                    val details = (state as? BookDetailsUiState.Success)?.details
                                    val authorName = details?.authors?.firstOrNull()
                                    if (authorName != null) {
                                        onNavigateToAuthor(authorName, details.gutenbergId)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Share") },
                                onClick = { menuExpanded = false }
                            )
                            if (inLibraryState != null) {
                                if (!inLibraryState.isArchived) {
                                    DropdownMenuItem(
                                        text = { Text("Archive book") },
                                        onClick = {
                                            menuExpanded = false
                                            viewModel.onArchiveBookClick()
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Remove from library",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        viewModel.onDeleteBookClick()
                                    }
                                )
                            }
                        }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                scrollBehavior = scrollBehavior
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
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
                remoteRating = remoteRating,
                onOpenReader = onOpenReader,
                onDownload = viewModel::onDownloadBook,
                onRatingChanged = viewModel::onUpdateRating,
                onStatusChanged = viewModel::onUpdateReadingStatus,
                modifier = Modifier.padding(innerPadding),
                authorBooks = authorBooks,
                onAuthorBookClick = onAuthorBookClick,
                onAuthorClick = { onNavigateToAuthor(current.details.authors.first(), current.details.gutenbergId) }
            )
        }
    }

    when (confirmationDialog) {
        ConfirmationDialog.Archive -> AlertDialog(
            onDismissRequest = viewModel::onDismissConfirmation,
            title = { Text("Archive book?") },
            text = { Text("The EPUB file will be deleted from your device. Your reading progress, rating and notes will be kept. You can download the book again at any time.") },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmArchive) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissConfirmation) {
                    Text("Cancel")
                }
            }
        )
        ConfirmationDialog.Delete -> AlertDialog(
            onDismissRequest = viewModel::onDismissConfirmation,
            title = { Text("Remove from library?") },
            text = { Text("This will permanently delete the book and all associated data including bookmarks and reading progress. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmDelete) {
                    Text(
                        "Remove",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissConfirmation) {
                    Text("Cancel")
                }
            }
        )
        ConfirmationDialog.None -> Unit
    }
}