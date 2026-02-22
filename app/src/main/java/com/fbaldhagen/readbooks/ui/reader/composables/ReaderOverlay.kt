package com.fbaldhagen.readbooks.ui.reader.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
    val view = LocalView.current
    val density = LocalDensity.current

    var statusBarHeight by remember {
        val insets = ViewCompat.getRootWindowInsets(view)
        val top = insets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
        mutableStateOf(with(density) { top.toDp() })
    }

    val currentTop = with(density) { WindowInsets.statusBars.getTop(density).toDp() }
    LaunchedEffect(currentTop) {
        if (currentTop > 0.dp && statusBarHeight == 0.dp) {
            statusBarHeight = currentTop
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        OverlayTop(
            barsVisible = barsVisible,
            theme = theme,
            chapterTitle = chapterTitle,
            isCurrentPageBookmarked = isCurrentPageBookmarked,
            statusBarHeight = statusBarHeight,
            onToggleBookmark = onToggleBookmark,
            onOpenBookmarks = onOpenBookmarks,
            onOpenToc = onOpenToc,
            onOpenSettings = onOpenSettings,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        OverlayBottom(
            bookProgress = bookProgress,
            theme = theme,
            isBarsVisible = barsVisible,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}