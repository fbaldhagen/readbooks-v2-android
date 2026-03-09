package com.fbaldhagen.readbooks.ui.reader.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.fbaldhagen.readbooks.R
import com.fbaldhagen.readbooks.domain.model.TtsPlaybackState
import com.fbaldhagen.readbooks.ui.components.MarqueeText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TtsMiniPlayer(
    ttsState: TtsPlaybackState,
    bookTitle: String?,
    bookAuthor: String?,
    coverUri: String?,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {},
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpand() },
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 8.dp,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = coverUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop,
                    fallback = painterResource(R.drawable.ic_notification)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    MarqueeText(
                        text = bookTitle ?: "Reading…",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (!bookAuthor.isNullOrBlank()) {
                        Text(
                            text = bookAuthor,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (ttsState is TtsPlaybackState.Playing)
                            Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (ttsState is TtsPlaybackState.Playing)
                            "Pause" else "Play"
                    )
                }

                IconButton(onClick = onStop) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Stop"
                    )
                }
            }
        }
    }
}