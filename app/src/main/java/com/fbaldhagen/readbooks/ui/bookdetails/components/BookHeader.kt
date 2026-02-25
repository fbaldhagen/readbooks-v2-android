package com.fbaldhagen.readbooks.ui.bookdetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.model.BookDetailsState
import com.fbaldhagen.readbooks.ui.components.BookCoverImage

@Composable
fun BookHeader(
    details: BookDetails,
    onRatingChanged: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        BookCoverImage(
            coverUrl = details.coverUrl,
            contentDescription = details.title,
            modifier = Modifier.width(120.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = details.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = details.authors.joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (details.state is BookDetailsState.InLibrary) {
                Spacer(modifier = Modifier.height(12.dp))
                RatingBar(
                    rating = (details.state as BookDetailsState.InLibrary).rating ?: 0,
                    onRatingChanged = { rating ->
                        onRatingChanged(if (rating == (details.state as BookDetailsState.InLibrary).rating) null else rating)
                    },
                    starSize = 28.dp
                )
            }
        }
    }
}