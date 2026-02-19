package com.fbaldhagen.readbooks.ui.reader.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.ui.reader.presentation.ReaderTheme

@Composable
fun ReaderOverlay(
    theme: ReaderTheme,
    barsVisible: Boolean,
    chapterTitle: String?,
    bookProgress: Float,
    isCurrentPageBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenToc: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = barsVisible,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(250)),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            TopOverlay(
                theme = theme,
                chapterTitle = chapterTitle,
                isCurrentPageBookmarked = isCurrentPageBookmarked,
                onToggleBookmark = onToggleBookmark,
                onOpenBookmarks = onOpenBookmarks,
                onOpenToc = onOpenToc,
                onOpenSettings = onOpenSettings
            )
        }

        BottomOverlay(
            bookProgress = bookProgress,
            theme = theme,
            isBarsVisible = barsVisible,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun overlayColorsFor(theme: ReaderTheme): OverlayColors {
    val bg = Color(theme.toReadiumTheme().backgroundColor)
    val fg = Color(theme.toReadiumTheme().contentColor)
    return OverlayColors(
        scrim = bg,
        content = fg.copy(alpha = 0.85f),
        contentSubtle = fg.copy(alpha = 0.5f)
    )
}

private data class OverlayColors(
    val scrim: Color,
    val content: Color,
    val contentSubtle: Color
)

@Composable
private fun TopOverlay(
    theme: ReaderTheme,
    chapterTitle: String?,
    isCurrentPageBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenToc: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var showOverflowMenu by remember { mutableStateOf(false) }
    val colors = overlayColorsFor(theme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colors.scrim.copy(alpha = 0.8f),
                        Color.Transparent
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!chapterTitle.isNullOrBlank()) {
            Text(
                text = chapterTitle,
                style = MaterialTheme.typography.labelSmall,
                color = colors.contentSubtle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                IconButton(onClick = onToggleBookmark) {
                    Icon(
                        imageVector = if (isCurrentPageBookmarked) Icons.Default.Bookmark
                        else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isCurrentPageBookmarked) "Remove bookmark"
                        else "Add bookmark",
                        tint = colors.content
                    )
                }
                IconButton(onClick = onOpenToc) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.List,
                        contentDescription = "Table of Contents",
                        tint = colors.content
                    )
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = colors.content
                    )
                }
            }

            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                IconButton(onClick = { showOverflowMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = colors.content
                    )
                }
                DropdownMenu(
                    expanded = showOverflowMenu,
                    onDismissRequest = { showOverflowMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Bookmarks") },
                        onClick = {
                            showOverflowMenu = false
                            onOpenBookmarks()
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.Bookmarks, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Text to Speech") },
                        onClick = { showOverflowMenu = false },
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Share") },
                        onClick = { showOverflowMenu = false },
                        leadingIcon = {
                            Icon(Icons.Outlined.Share, contentDescription = null)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomOverlay(
    bookProgress: Float,
    theme: ReaderTheme,
    isBarsVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = overlayColorsFor(theme)

    val textAlpha by animateFloatAsState(
        targetValue = if (isBarsVisible) 0.7f else 0.4f,
        animationSpec = tween(250),
        label = "textAlpha"
    )

    val progressAlpha by animateFloatAsState(
        targetValue = if (isBarsVisible) 1f else 0f,
        animationSpec = tween(250),
        label = "progressAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 75.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${(bookProgress * 100).toInt()}%",
            style = if (isBarsVisible) {
                MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            } else {
                MaterialTheme.typography.labelMedium
            },
            color = colors.contentSubtle.copy(alpha = textAlpha)
        )

        LinearProgressIndicator(
            progress = { bookProgress },
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .padding(top = 4.dp)
                .height(1.5.dp)
                .clip(RoundedCornerShape(50))
                .alpha(progressAlpha),
            color = colors.contentSubtle.copy(alpha = 0.4f),
            trackColor = colors.contentSubtle.copy(alpha = 0.1f)
        )
    }
}