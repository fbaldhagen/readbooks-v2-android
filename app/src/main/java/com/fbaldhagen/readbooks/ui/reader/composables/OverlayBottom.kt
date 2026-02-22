package com.fbaldhagen.readbooks.ui.reader.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.fbaldhagen.readbooks.ui.reader.presentation.ReaderTheme

@Composable
fun OverlayBottom(
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