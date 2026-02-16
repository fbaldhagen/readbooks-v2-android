package com.fbaldhagen.readbooks.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fbaldhagen.readbooks.ui.components.ErrorMessage
import com.fbaldhagen.readbooks.ui.components.LoadingIndicator
import com.fbaldhagen.readbooks.ui.home.components.HomeContent

@Composable
fun HomeScreen(
    onNavigateToBookDetails: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when {
        state.isLoading -> LoadingIndicator(modifier = modifier)
        state.error != null -> ErrorMessage(
            message = state.error!!,
            onRetry = { viewModel.dismissError() },
            modifier = modifier
        )
        else -> HomeContent(
            state = state,
            onBookClick = onNavigateToBookDetails,
            modifier = modifier
        )
    }
}