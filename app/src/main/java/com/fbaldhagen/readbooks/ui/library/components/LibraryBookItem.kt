package com.fbaldhagen.readbooks.ui.library.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.ui.components.BookCoverImage

@Composable
fun LibraryBookItem(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        BookCoverImage(
            coverUrl = book.coverUri,
            contentDescription = book.title,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (book.progress > 0f) {
            LinearProgressIndicator(
                progress = { book.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
        }
        Text(
            text = book.title,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = book.authors.firstOrNull() ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}