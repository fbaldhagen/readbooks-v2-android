package com.fbaldhagen.readbooks.ui.bookdetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RatingSection(
    modifier: Modifier = Modifier,
    rating: Int = 0
) {
    Column {
        Text(
            text = "Rating",
            style = MaterialTheme.typography.titleMedium
        )
        Row(modifier.fillMaxWidth()) {
            Column(modifier = Modifier
                .align(Alignment.CenterVertically)) {
                Text(
                    text = rating.toFloat().toString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                Row {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Rate $star stars",
                            modifier = Modifier.size(12.dp),
                            tint = if (star <= rating) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            Column {
                (1..5).forEach { row ->
                    Row {
                        Text(
                            text = row.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rated $row stars",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.CenterVertically)
                        )

                        Spacer(Modifier.width(4.dp))

                        LinearProgressIndicator(
                            progress = { if (row == rating) 1f else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically)
                        )
                    }

                }
            }
        }
    }
}