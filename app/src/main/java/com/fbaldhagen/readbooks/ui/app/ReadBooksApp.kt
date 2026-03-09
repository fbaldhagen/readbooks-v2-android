package com.fbaldhagen.readbooks.ui.app

import android.content.Intent
import android.graphics.Rect
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fbaldhagen.readbooks.domain.model.isActive
import com.fbaldhagen.readbooks.domain.usecase.AuthStatus
import com.fbaldhagen.readbooks.navigation.AppNavHost
import com.fbaldhagen.readbooks.navigation.TopLevelDestination
import com.fbaldhagen.readbooks.ui.auth.AuthViewModel
import com.fbaldhagen.readbooks.ui.tts.TtsMiniPlayer
import com.fbaldhagen.readbooks.ui.tts.TtsPlayerSheet
import com.fbaldhagen.readbooks.ui.tts.TtsViewModel

@Composable
fun ReadBooksApp(
    intent: Intent? = null,
    onLogoPositioned: ((Rect) -> Unit)? = null
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val achievementViewModel: AchievementViewModel = hiltViewModel()
    val ttsViewModel: TtsViewModel = hiltViewModel()
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val ttsState by ttsViewModel.ttsState.collectAsStateWithLifecycle()
    val ttsBookTitle by ttsViewModel.bookTitle.collectAsStateWithLifecycle()
    val ttsBookAuthor by ttsViewModel.bookAuthor.collectAsStateWithLifecycle()
    val ttsCoverUri by ttsViewModel.coverUri.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showTtsPlayer by remember { mutableStateOf(false) }

    if (authState.authStatus == AuthStatus.LOADING) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    LaunchedEffect(Unit) {
        achievementViewModel.newlyUnlocked.collect { achievements ->
            if (achievements.isNotEmpty()) {
                achievements.forEach { achievement ->
                    snackbarHostState.showSnackbar(
                        message = "🏆 Achievement unlocked: ${achievement.name}",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    LaunchedEffect(intent) {
        intent?.data?.let { uri ->
            val token = uri.getQueryParameter("token")
            if (token != null) {
                authViewModel.verifyEmail(token)
            }
        }
    }

    // Dismiss sheet when TTS becomes idle
    LaunchedEffect(ttsState) {
        if (!ttsState.isActive) showTtsPlayer = false
    }

    val showBottomBar = TopLevelDestination.entries.any { destination ->
        currentDestination?.hasRoute(destination.route::class) == true
    }

    val isTtsActive = ttsState.isActive

    @Suppress("AssignedValueIsNeverRead")
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Column(
                modifier = if (!showBottomBar) Modifier.navigationBarsPadding() else Modifier
            ) {
                AnimatedVisibility(
                    visible = isTtsActive,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it },
                    exit = fadeOut(tween(300)) + slideOutVertically(tween(300)) { it }
                ) {
                    TtsMiniPlayer(
                        ttsState = ttsState,
                        bookTitle = ttsBookTitle,
                        bookAuthor = ttsBookAuthor,
                        coverUri = ttsCoverUri,
                        onPlayPause = ttsViewModel::onPlayPause,
                        onStop = ttsViewModel::onStop,
                        onExpand = { showTtsPlayer = true }
                    )
                }
                if (showBottomBar) {
                    ReadBooksBottomBar(
                        currentDestination = currentDestination,
                        onNavigate = { destination ->
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            innerPadding = innerPadding,
            authStatus = authState.authStatus,
            onLogoPositioned = onLogoPositioned,
            onStartTts = { bookId, title, author, coverUri, filePath, locator ->
                ttsViewModel.onStartTts(
                    bookId = bookId,
                    bookTitle = title,
                    bookAuthor = author,
                    coverUri = coverUri,
                    filePath = filePath,
                    startLocator = locator
                )
            }
        )

        if (showTtsPlayer) {
            TtsPlayerSheet(
                ttsState = ttsState,
                bookTitle = ttsBookTitle,
                bookAuthor = ttsBookAuthor,
                coverUri = ttsCoverUri,
                onPlayPause = ttsViewModel::onPlayPause,
                onSkipNext = ttsViewModel::onSkipNext,
                onSkipPrevious = ttsViewModel::onSkipPrevious,
                onStop = {
                    ttsViewModel.onStop()
                    showTtsPlayer = false
                },
                onDismiss = { showTtsPlayer = false }
            )
        }
    }
}

@Composable
private fun ReadBooksBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (TopLevelDestination) -> Unit
) {
    NavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            val selected = currentDestination?.hierarchy?.any {
                it.hasRoute(destination.route::class)
            } == true

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = if (selected) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        },
                        contentDescription = destination.label
                    )
                },
                label = { Text(destination.label) }
            )
        }
    }
}