package com.fbaldhagen.readbooks.ui.reader.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.ui.reader.presentation.ReaderFontFamily
import com.fbaldhagen.readbooks.ui.reader.presentation.ReaderPreferences
import com.fbaldhagen.readbooks.ui.reader.presentation.ReaderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(
    preferences: ReaderPreferences,
    onPreferencesChanged: (ReaderPreferences) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Reader Settings",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Font Size
            Text("Font Size", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("A", style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = preferences.fontSize.toFloat(),
                    onValueChange = { value ->
                        onPreferencesChanged(
                            preferences.copy(fontSize = value.toDouble())
                        )
                    },
                    valueRange = 0.5f..3.0f,
                    steps = 9,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
                Text("A", style = MaterialTheme.typography.headlineSmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Font Family
            Text("Font", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReaderFontFamily.entries.forEach { font ->
                    FilterChip(
                        selected = preferences.fontFamily == font,
                        onClick = {
                            onPreferencesChanged(
                                preferences.copy(fontFamily = font)
                            )
                        },
                        label = {
                            Text(
                                font.displayName,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Theme
            Text("Theme", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReaderTheme.entries.forEach { theme ->
                    FilterChip(
                        selected = preferences.theme == theme,
                        onClick = {
                            onPreferencesChanged(
                                preferences.copy(theme = theme)
                            )
                        },
                        label = { Text(theme.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Page Margins
            Text("Page Margins", style = MaterialTheme.typography.titleSmall)
            Slider(
                value = preferences.pageMargins.toFloat(),
                onValueChange = { value ->
                    onPreferencesChanged(
                        preferences.copy(pageMargins = value.toDouble())
                    )
                },
                valueRange = 0.5f..3.0f,
                steps = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Line Height
            Text("Line Spacing", style = MaterialTheme.typography.titleSmall)
            Slider(
                value = preferences.lineHeight.toFloat(),
                onValueChange = { value ->
                    onPreferencesChanged(
                        preferences.copy(lineHeight = value.toDouble())
                    )
                },
                valueRange = 1.0f..2.5f,
                steps = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
