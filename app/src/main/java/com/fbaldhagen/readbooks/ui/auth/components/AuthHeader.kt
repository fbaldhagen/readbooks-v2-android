package com.fbaldhagen.readbooks.ui.auth.components

import android.graphics.Rect
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.R

@Composable
fun AuthHeader(
    subtitle: String,
    onLogoPositioned: ((Rect) -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_readbooks_logo),
            contentDescription = "ReadBooks logo",
            modifier = Modifier
                .size(120.dp)
                .onGloballyPositioned { coordinates ->
                    val bounds = coordinates.boundsInWindow()
                    onLogoPositioned?.invoke(
                        Rect(
                            bounds.left.toInt(),
                            bounds.top.toInt(),
                            bounds.right.toInt(),
                            bounds.bottom.toInt()
                        )
                    )
                }
        )
        Text(
            text = "ReadBooks",
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}