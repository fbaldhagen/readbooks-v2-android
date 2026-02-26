package com.fbaldhagen.readbooks

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fbaldhagen.readbooks.ui.app.ReadBooksApp
import com.fbaldhagen.readbooks.ui.theme.ReadBooksTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReadBooksTheme {
                ReadBooksApp(intent = intent)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        setContent {
            ReadBooksTheme {
                ReadBooksApp(intent = intent)
            }
        }
    }
}