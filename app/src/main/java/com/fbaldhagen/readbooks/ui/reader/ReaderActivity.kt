package com.fbaldhagen.readbooks.ui.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.luminance
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.fbaldhagen.readbooks.R
import com.fbaldhagen.readbooks.domain.model.ReaderTheme
import com.fbaldhagen.readbooks.ui.reader.composables.ReaderScreen
import com.fbaldhagen.readbooks.ui.reader.presentation.ReaderViewModel
import com.fbaldhagen.readbooks.ui.theme.ReadBooksTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReaderActivity : AppCompatActivity() {

    private val viewModel: ReaderViewModel by viewModels()
    private val fragmentContainerId = R.id.reader_fragment_container

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ReadBooksTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                val surfaceColor = MaterialTheme.colorScheme.surface

                LaunchedEffect(state.barsVisible) {
                    setImmersiveMode(!state.barsVisible)
                }

                LaunchedEffect(state.preferences.theme, state.barsVisible) {
                    val controller = WindowCompat.getInsetsController(window, window.decorView)
                    controller.isAppearanceLightStatusBars = if (state.barsVisible) {
                        surfaceColor.luminance() > 0.5f
                    } else {
                        when (state.preferences.theme) {
                            ReaderTheme.LIGHT, ReaderTheme.SEPIA -> true
                            ReaderTheme.DARK -> false
                        }
                    }
                }

                ReaderScreen(
                    state = state,
                    fragmentContainerId = fragmentContainerId,
                    onToggleBookmark = viewModel::toggleBookmark,
                    onUpdatePreferences = viewModel::onUpdatePreferences,
                    onDeleteBookmark = viewModel::deleteBookmark,
                    onNavigateToTocEntry = viewModel::navigateToTocEntry,
                    onNavigateToBookmark = viewModel::navigateToBookmark,
                    onUpdateBookmarkNote = viewModel::updateBookmarkNote
                )
            }
        }


        if (savedInstanceState == null) {
            lifecycleScope.launch {
                val state = viewModel.state
                    .filterNotNull()
                    .first { !it.isLoading }

                if (state.publication != null) {
                    showReader()
                }
            }
        }
    }

    private fun setImmersiveMode(immersive: Boolean) {
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (immersive) {
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun showReader() {
        supportFragmentManager.beginTransaction()
            .replace(fragmentContainerId, ReaderFragment())
            .commit()
    }

    override fun onStart() {
        super.onStart()
        viewModel.resumeSessionIfNeeded()
    }

    override fun onStop() {
        super.onStop()
        viewModel.endSession()
    }

    companion object {
        const val EXTRA_BOOK_ID = "book_id"

        fun createIntent(context: Context, bookId: Long): Intent =
            Intent(context, ReaderActivity::class.java).apply {
                putExtra(EXTRA_BOOK_ID, bookId)
            }
    }
}