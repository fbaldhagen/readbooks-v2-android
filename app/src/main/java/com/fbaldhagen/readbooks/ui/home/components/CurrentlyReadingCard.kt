package com.fbaldhagen.readbooks.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.ui.components.BookCoverImage

@Composable
fun CurrentlyReadingCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BookCoverImage(
                coverUrl = book.coverUri,
                contentDescription = book.title,
                modifier = Modifier
                    .width(110.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = book.authors.firstOrNull() ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                book.progress.takeIf { it > 0f }?.let { progress ->
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(MaterialTheme.shapes.small)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${(progress * 100).toInt()}% complete",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

fun Modifier.glowEffect(
    color: Color,
    glowRadius: Dp = 12.dp,
    alpha: Float = 0.2f
): Modifier = this.drawBehind {
    val radiusPx = glowRadius.toPx()
    val glowPaint = Paint().apply {
        asFrameworkPaint().apply {
            isAntiAlias = true
            this.color = android.graphics.Color.TRANSPARENT
            setShadowLayer(
                radiusPx,
                0f,
                0f,
                color.copy(alpha = alpha).toArgb()
            )
        }
    }
    drawIntoCanvas { canvas ->
        canvas.drawRoundRect(
            left = -radiusPx / 4,
            top = -radiusPx / 4,
            right = size.width + radiusPx / 4,
            bottom = size.height + radiusPx / 4,
            radiusX = radiusPx / 2,
            radiusY = radiusPx / 2,
            paint = glowPaint
        )
    }
}