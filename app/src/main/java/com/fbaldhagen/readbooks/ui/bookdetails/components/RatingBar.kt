package com.fbaldhagen.readbooks.ui.bookdetails.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    starSize: Dp = 20.dp
) {
    Row(modifier = modifier
    ) {
        (1..5).forEach { star ->
            IconButton(
                onClick = { onRatingChanged(star) },
                modifier = Modifier.size(starSize),
            ) {
                Icon(
                    imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Rate $star stars",
                    modifier = Modifier.size(starSize),
                    tint = if (star <= rating) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            }
        }
    }
}