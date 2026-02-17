package com.fbaldhagen.readbooks.ui.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
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
    private val fragmentContainerId = android.view.View.generateViewId()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ReadBooksTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()

                ReaderScreen(
                    state = state,
                    fragmentContainerId = fragmentContainerId,
                    onBack = { finish() },
                    onAddBookmark = viewModel::addBookmark,
                    onUpdatePreferences = viewModel::onUpdatePreferences,
                    onDeleteBookmark = viewModel::deleteBookmark
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
            insetsController.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            insetsController.systemBarsBehavior =
                androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            insetsController.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun showReader() {
        supportFragmentManager.beginTransaction()
            .replace(fragmentContainerId, ReaderFragment())
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            viewModel.endSession()
        }
    }

    companion object {
        const val EXTRA_BOOK_ID = "book_id"

        fun createIntent(context: Context, bookId: Long): Intent =
            Intent(context, ReaderActivity::class.java).apply {
                putExtra(EXTRA_BOOK_ID, bookId)
            }
    }
}