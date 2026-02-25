package com.fbaldhagen.readbooks.ui.profile

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fbaldhagen.readbooks.ui.utils.createTempImageUri
import com.fbaldhagen.readbooks.ui.profile.components.AchievementsCard
import com.fbaldhagen.readbooks.ui.profile.components.AvatarOptionsSheet
import com.fbaldhagen.readbooks.ui.profile.components.BookClubsCard
import com.fbaldhagen.readbooks.ui.profile.components.EditProfileSheet
import com.fbaldhagen.readbooks.ui.profile.components.ProfileHeaderCard
import com.fbaldhagen.readbooks.ui.profile.components.QuickActionsCard
import com.fbaldhagen.readbooks.ui.profile.components.ReadingGoalCard
import com.fbaldhagen.readbooks.ui.profile.components.SettingsOverlay

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.onUpdateAvatarUri(it.toString()) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) viewModel.onUpdateAvatarUri(cameraImageUri?.toString())
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) cameraLauncher.launch(cameraImageUri!!)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.clickable { viewModel.onToggleSettings() }
                )
            }

            ProfileHeaderCard(
                userName = state.preferences.userName,
                email = state.preferences.email,
                avatarUri = state.preferences.avatarUri,
                bio = state.preferences.bio,
                totalBooks = state.analytics.totalBooks,
                totalBooksFinished = state.analytics.totalBooksFinished,
                currentStreak = state.analytics.currentStreak.currentDays,
                onAvatarClick = { viewModel.onToggleAvatarOptions() },
                onEditClick = { viewModel.onToggleEditProfile() }
            )

            ReadingGoalCard(
                booksFinished = state.analytics.totalBooksFinished,
                yearlyGoal = state.preferences.yearlyBooksGoal
            )

            AchievementsCard()
            BookClubsCard()
            QuickActionsCard()
        }

        if (state.showSettings) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { viewModel.onToggleSettings() }
            )
            SettingsOverlay(
                onClose = { viewModel.onToggleSettings() },
                onLogout = {
                    viewModel.onLogout()
                    onLogout()
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
            )
        }

        if (state.showAvatarOptions) {
            AvatarOptionsSheet(
                onDismiss = { viewModel.onToggleAvatarOptions() },
                onPickFromGallery = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onTakePhoto = {
                    val uri = context.createTempImageUri()
                    cameraImageUri = uri
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onRemovePhoto = { viewModel.onUpdateAvatarUri(null) },
                hasCurrentAvatar = state.preferences.avatarUri != null
            )
        }

        if (state.showEditProfile) {
            EditProfileSheet(
                currentName = state.preferences.userName,
                currentBio = state.preferences.bio,
                currentYearlyGoal = state.preferences.yearlyBooksGoal,
                onDismiss = { viewModel.onToggleEditProfile() },
                onSave = { name, bio, goal ->
                    viewModel.onSaveProfile(name, bio, goal)
                }
            )
        }
    }
}