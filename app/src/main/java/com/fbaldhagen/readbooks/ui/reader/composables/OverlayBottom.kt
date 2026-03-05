package com.fbaldhagen.readbooks.ui.reader.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.ReaderTheme
import com.fbaldhagen.readbooks.domain.model.TtsPlaybackState
import com.fbaldhagen.readbooks.domain.model.isActive

@Composable
fun OverlayBottom(
    bookProgress: Float,
    theme: ReaderTheme,
    isBarsVisible: Boolean,
    ttsState: TtsPlaybackState,
    onTtsPlayPause: () -> Unit,
    onTtsSkipNext: () -> Unit,
    onTtsSkipPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = overlayColorsFor(theme)
    val isTtsActive = ttsState.isActive

    val textAlpha by animateFloatAsState(
        targetValue = if (isBarsVisible) 0.7f else 0.4f,
        animationSpec = tween(250),
        label = "textAlpha"
    )
    val progressAlpha by animateFloatAsState(
        targetValue = if (isBarsVisible && !isTtsActive) 1f else 0f,
        animationSpec = tween(250),
        label = "progressAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = if (!isBarsVisible) 32.dp else 58.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isTtsActive) {
            Text(
                text = "${(bookProgress * 100).toInt()}%",
                style = if (isBarsVisible)
                    MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                else
                    MaterialTheme.typography.labelMedium,
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

        AnimatedVisibility(
            visible = isTtsActive,
            enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 },
            exit = fadeOut(tween(300)) + slideOutVertically(tween(300)) { it / 2 }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onTtsSkipPrevious) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous utterance",
                        tint = colors.contentSubtle,
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = onTtsPlayPause) {
                    Icon(
                        imageVector = if (ttsState is TtsPlaybackState.Playing)
                            Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                        contentDescription = if (ttsState is TtsPlaybackState.Playing) "Pause" else "Play",
                        tint = colors.content,
                        modifier = Modifier.size(40.dp)
                    )
                }
                IconButton(onClick = onTtsSkipNext) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next utterance",
                        tint = colors.contentSubtle,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}