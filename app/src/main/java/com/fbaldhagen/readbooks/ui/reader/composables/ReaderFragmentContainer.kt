package com.fbaldhagen.readbooks.ui.reader.composables

import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ReaderFragmentContainer(
    fragmentContainerId: Int,
    modifier: Modifier = Modifier
) {
    @Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
    AndroidView(
        factory = { context ->
            FrameLayout(context).apply {
                id = fragmentContainerId
            }
        },
        modifier = modifier
    )
}