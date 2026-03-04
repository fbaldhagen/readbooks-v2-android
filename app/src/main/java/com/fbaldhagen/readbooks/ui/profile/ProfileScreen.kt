package com.fbaldhagen.readbooks.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
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
import com.fbaldhagen.readbooks.ui.profile.components.AchievementsCard
import com.fbaldhagen.readbooks.ui.profile.components.AvatarOptionsSheet
import com.fbaldhagen.readbooks.ui.profile.components.BookClubsCard
import com.fbaldhagen.readbooks.ui.profile.components.EditProfileSheet
import com.fbaldhagen.readbooks.ui.profile.components.GuestProfileCard
import com.fbaldhagen.readbooks.ui.profile.components.LockedFeatureOverlay
import com.fbaldhagen.readbooks.ui.profile.components.ProfileHeaderCard
import com.fbaldhagen.readbooks.ui.profile.components.QuickActionsCard
import com.fbaldhagen.readbooks.ui.profile.components.ReadingGoalCard
import com.fbaldhagen.readbooks.ui.profile.components.SettingsOverlay
import com.fbaldhagen.readbooks.ui.utils.createTempImageUri
import com.yalantis.ucrop.UCrop
import java.io.File
import androidx.core.graphics.toColorInt
import com.fbaldhagen.readbooks.ui.profile.components.AchievementsOverlay

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToCreateAccount: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val isBackendReachable by viewModel.isBackendReachable.collectAsStateWithLifecycle()

    BackHandler(enabled = state.showSettings) {
        viewModel.onSettingsClosed()
        viewModel.onToggleSettings()
    }

    val uCropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val croppedUri = UCrop.getOutput(result.data!!)
            croppedUri?.let { viewModel.onUpdateAvatarUri(it.toString()) }
        }
    }

    fun launchCrop(sourceUri: Uri, context: Context) {
        val destinationUri = Uri.fromFile(
            File(context.cacheDir, "cropped_avatar_${System.currentTimeMillis()}.jpg")
        )
        val options = UCrop.Options().apply {
            setCircleDimmedLayer(true)
            setShowCropGrid(false)
            setShowCropFrame(false)
            setHideBottomControls(false)
            withAspectRatio(1f, 1f)
            setCompressionQuality(90)
            setStatusBarColor("#1C1917".toColorInt())
            setToolbarColor(android.graphics.Color.BLACK)
            setToolbarWidgetColor(android.graphics.Color.WHITE)
        }
        val intent = UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .withMaxResultSize(512, 512)
            .getIntent(context)
        uCropLauncher.launch(intent)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { launchCrop(it, context) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) cameraImageUri?.let { launchCrop(it, context) }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) cameraLauncher.launch(cameraImageUri!!)
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.onNotificationsToggled(true)
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
                    modifier = Modifier.clickable {
                        if (!state.showSettings) viewModel.onSettingsOpened()
                        else viewModel.onSettingsClosed()
                        viewModel.onToggleSettings()
                    }
                )
            }

            if (state.preferences.isGuest) {
                GuestProfileCard(
                    onCreateAccount = {
                        viewModel.onNavigateToCreateAccount()
                        onNavigateToCreateAccount()
                    }
                )
            } else {
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
            }

            if (state.preferences.isGuest) {
                LockedFeatureOverlay(
                    message = "Create an account to unlock achievements"
                )
            } else {
                AchievementsCard(
                    achievements = state.achievements,
                    onSeeAll = { viewModel.onToggleAchievements() }
                )
            }

            if (state.preferences.isGuest) {
                LockedFeatureOverlay(
                    message = "Create an account to join book clubs"
                )
            } else {
                BookClubsCard()
            }

            if (state.preferences.isGuest) {
                LockedFeatureOverlay(
                    message = "Create an account to get access to more features!"
                )
            } else {
                QuickActionsCard()
            }
        }

        if (state.showSettings) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { viewModel.onToggleSettings() }
            )
            SettingsOverlay(
                onClose = {
                    viewModel.onSettingsClosed()
                    viewModel.onToggleSettings()
                },
                onLogout = {
                    viewModel.onLogout()
                    onLogout()
                },
                currentTheme = state.preferences.themeMode,
                onThemeChanged = viewModel::onThemeModeChanged,
                notificationsEnabled = state.preferences.notificationsEnabled,
                onNotificationsToggled = { enabled ->
                    if (enabled) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        } else {
                            viewModel.onNotificationsToggled(true)
                        }
                    } else {
                        viewModel.onNotificationsToggled(false)
                    }
                },
                usePublicGutenberg = state.preferences.usePublicGutenberg,
                onUsePublicGutenbergToggled = viewModel::onUsePublicGutenbergToggled,
                isBackendReachable = isBackendReachable,
                syncReaderTheme = state.preferences.syncReaderTheme,
                onSyncReaderThemeChanged = viewModel::onSyncReaderThemeChanged,
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
                hasCurrentAvatar = state.preferences.avatarUri != null,
                avatarUri = state.preferences.avatarUri
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

        if (state.showAchievements) {
            BackHandler { viewModel.onToggleAchievements() }
            AchievementsOverlay(
                achievements = state.achievements,
                onDismiss = { viewModel.onToggleAchievements() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}