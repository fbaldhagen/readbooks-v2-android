package com.fbaldhagen.readbooks.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerBookItem(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(90.dp)
            .aspectRatio(2f / 3f)
            .clip(MaterialTheme.shapes.small)
            .shimmerBackground()
    )
}