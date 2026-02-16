package com.fbaldhagen.readbooks.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fbaldhagen.readbooks.ui.bookdetails.BookDetailsScreen
import com.fbaldhagen.readbooks.ui.discover.DiscoverScreen
import com.fbaldhagen.readbooks.ui.home.HomeScreen
import com.fbaldhagen.readbooks.ui.library.LibraryScreen
import com.fbaldhagen.readbooks.ui.profile.ProfileScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen(
                onNavigateToBookDetails = { bookId ->
                    navController.navigate(Route.BookDetails(bookId))
                }
            )
        }

        composable<Route.Library> {
            LibraryScreen(
                onNavigateToBookDetails = { bookId ->
                    navController.navigate(Route.BookDetails(bookId))
                }
            )
        }

        composable<Route.Discover> {
            DiscoverScreen(
                onNavigateToBookDetails = { gutenbergId ->
                    navController.navigate(Route.DiscoverBookDetails(gutenbergId))
                }
            )
        }

        composable<Route.Profile> {
            ProfileScreen(
                onNavigateToSettings = {
                    navController.navigate(Route.Settings)
                },
                onNavigateToProgress = {
                    navController.navigate(Route.Progress)
                }
            )
        }

        composable<Route.BookDetails> {
            BookDetailsScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenReader = { bookId ->
                    // TODO: Launch ReaderActivity
                }
            )
        }

        composable<Route.DiscoverBookDetails> {
            BookDetailsScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenReader = { bookId ->
                    // TODO: Launch ReaderActivity
                }
            )
        }
    }
}