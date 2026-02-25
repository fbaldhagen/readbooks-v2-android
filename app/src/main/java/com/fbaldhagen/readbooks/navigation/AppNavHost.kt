package com.fbaldhagen.readbooks.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fbaldhagen.readbooks.ui.auth.AuthScreen
import com.fbaldhagen.readbooks.ui.bookdetails.BookDetailsScreen
import com.fbaldhagen.readbooks.ui.discover.DiscoverScreen
import com.fbaldhagen.readbooks.ui.home.HomeScreen
import com.fbaldhagen.readbooks.ui.library.LibraryScreen
import com.fbaldhagen.readbooks.ui.profile.ProfileScreen
import com.fbaldhagen.readbooks.ui.reader.ReaderActivity

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Route.Home else Route.Auth,
        modifier = modifier
    ) {
        composable<Route.Auth> {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Auth) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Home> {
            HomeScreen(
                modifier = Modifier.padding(innerPadding),
                onNavigateToBookDetails = { bookId ->
                    navController.navigate(Route.BookDetails(bookId))
                }
            )
        }

        composable<Route.Library> {
            LibraryScreen(
                modifier = Modifier.padding(innerPadding),
                onNavigateToBookDetails = { bookId ->
                    navController.navigate(Route.BookDetails(bookId))
                }
            )
        }

        composable<Route.Discover> {
            DiscoverScreen(
                modifier = Modifier.padding(innerPadding),
                onNavigateToBookDetails = { gutenbergId ->
                    navController.navigate(Route.DiscoverBookDetails(gutenbergId))
                }
            )
        }

        composable<Route.Profile> {
            ProfileScreen(
                modifier = Modifier.padding(innerPadding),
                onNavigateToSettings = {
                    navController.navigate(Route.Settings)
                },
                onNavigateToProgress = {
                    navController.navigate(Route.Progress)
                },
                onLogout = {
                    navController.navigate(Route.Auth) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.BookDetails> {
            BookDetailsScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenReader = { bookId ->
                    val intent = ReaderActivity.createIntent(navController.context, bookId)
                    navController.context.startActivity(intent)
                }
            )
        }

        composable<Route.DiscoverBookDetails> {
            BookDetailsScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenReader = { bookId ->
                    val intent = ReaderActivity.createIntent(navController.context, bookId)
                    navController.context.startActivity(intent)
                }
            )
        }
    }
}