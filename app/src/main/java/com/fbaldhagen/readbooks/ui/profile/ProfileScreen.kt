package com.fbaldhagen.readbooks.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fbaldhagen.readbooks.ui.components.LoadingIndicator
import com.fbaldhagen.readbooks.ui.profile.components.ProfileHeader
import com.fbaldhagen.readbooks.ui.profile.components.ProfileMenuItem
import com.fbaldhagen.readbooks.ui.profile.components.StatsCard

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToProgress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        LoadingIndicator(modifier = modifier)
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ProfileHeader(
            userName = state.preferences.userName,
            avatarUri = state.preferences.avatarUri,
            onEditName = viewModel::onUpdateUserName
        )

        Spacer(modifier = Modifier.height(24.dp))

        StatsCard(analytics = state.analytics)

        Spacer(modifier = Modifier.height(24.dp))

        ProfileMenuItem(
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            title = "Reading Progress",
            subtitle = "View detailed analytics",
            onClick = onNavigateToProgress
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProfileMenuItem(
            icon = Icons.Default.Settings,
            title = "Settings",
            subtitle = "Theme, reading goals, and more",
            onClick = onNavigateToSettings
        )
    }
}