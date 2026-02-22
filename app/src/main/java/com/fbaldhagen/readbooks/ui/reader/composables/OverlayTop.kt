package com.fbaldhagen.readbooks.ui.reader.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.ui.reader.presentation.ReaderTheme

private val AppBarHeight = 48.dp

@Composable
fun OverlayTop(
    barsVisible: Boolean,
    theme: ReaderTheme,
    chapterTitle: String?,
    isCurrentPageBookmarked: Boolean,
    statusBarHeight: Dp,
    onToggleBookmark: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenToc: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showOverflowMenu by remember { mutableStateOf(false) }
    val colors = overlayColorsFor(theme)
    val totalBarHeight = statusBarHeight + AppBarHeight

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalBarHeight + 32.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colors.scrim.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )

        if (!chapterTitle.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = statusBarHeight)
                    .height(AppBarHeight)
                    .padding(horizontal = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = chapterTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.contentSubtle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }


        AnimatedVisibility(
            visible = barsVisible,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                tonalElevation = 2.dp
            ) {
                Column {
                    Spacer(modifier = Modifier.height(statusBarHeight))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppBarHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { /* TODO */ },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(0.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onToggleBookmark) {
                                Icon(
                                    imageVector = if (isCurrentPageBookmarked) Icons.Default.Bookmark
                                    else Icons.Outlined.BookmarkBorder,
                                    contentDescription = if (isCurrentPageBookmarked) "Remove bookmark"
                                    else "Add bookmark",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(onClick = onOpenToc) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.List,
                                    contentDescription = "Table of Contents",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(onClick = onOpenSettings) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                            IconButton(onClick = { showOverflowMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More options",
                                    tint = MaterialTheme.colorScheme.onSurface
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
                                        Icon(
                                            Icons.AutoMirrored.Outlined.VolumeUp,
                                            contentDescription = null
                                        )
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
        }
    }
}