package com.fbaldhagen.readbooks

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fbaldhagen.readbooks.domain.model.ThemeMode
import com.fbaldhagen.readbooks.domain.repository.UserPreferencesRepository
import com.fbaldhagen.readbooks.domain.usecase.AuthStatus
import com.fbaldhagen.readbooks.ui.app.ReadBooksApp
import com.fbaldhagen.readbooks.ui.auth.AuthViewModel
import com.fbaldhagen.readbooks.ui.theme.ReadBooksTheme
import com.fbaldhagen.readbooks.ui.utils.SplashAnimationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var keepSplashScreen = true
    private val splashAnimationManager = SplashAnimationManager()

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }
        splashScreen.setOnExitAnimationListener { splashAnimationManager.animateAway(it) }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by userPreferencesRepository.observe()
                .map { it.themeMode }
                .collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)

            ReadBooksTheme(themeMode = themeMode) {
                val authViewModel: AuthViewModel = hiltViewModel()
                val authStatus by authViewModel.state.collectAsStateWithLifecycle()
                keepSplashScreen = authStatus.authStatus == AuthStatus.LOADING
                ReadBooksApp(
                    intent = intent,
                    onLogoPositioned = splashAnimationManager::onLogoPositioned
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        setContent {
            val themeMode by userPreferencesRepository.observe()
                .map { it.themeMode }
                .collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)

            ReadBooksTheme(themeMode = themeMode) {
                ReadBooksApp(intent = intent)
            }
        }
    }
}