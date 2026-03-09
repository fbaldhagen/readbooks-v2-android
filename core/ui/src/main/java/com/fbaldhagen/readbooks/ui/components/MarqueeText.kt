package com.fbaldhagen.readbooks.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null
) {
    Text(
        text = text,
        modifier = modifier.basicMarquee(
            iterations = Int.MAX_VALUE,
            repeatDelayMillis = 2000,
            initialDelayMillis = 1500,
            velocity = 40.dp
        ),
        style = style,
        color = color,
        fontWeight = fontWeight,
        maxLines = 1,
        overflow = TextOverflow.Clip
    )
}