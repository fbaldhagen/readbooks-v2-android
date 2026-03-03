package com.fbaldhagen.readbooks.navigation

import android.graphics.Rect
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.fbaldhagen.readbooks.domain.usecase.AuthStatus
import com.fbaldhagen.readbooks.ui.auth.AuthScreen
import com.fbaldhagen.readbooks.ui.author.AuthorScreen
import com.fbaldhagen.readbooks.ui.bookdetails.BookDetailsScreen
import com.fbaldhagen.readbooks.ui.discover.DiscoverScreen
import com.fbaldhagen.readbooks.ui.discover.DiscoverTopicScreen
import com.fbaldhagen.readbooks.ui.home.HomeScreen
import com.fbaldhagen.readbooks.ui.library.LibraryScreen
import com.fbaldhagen.readbooks.ui.profile.ProfileScreen
import com.fbaldhagen.readbooks.ui.reader.ReaderActivity

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    authStatus: AuthStatus,
    modifier: Modifier = Modifier,
    onLogoPositioned: ((Rect) -> Unit)? = null
) {
    NavHost(
        navController = navController,
        startDestination = if (authStatus != AuthStatus.UNAUTHENTICATED) Route.Home else Route.Auth,
        modifier = modifier
    ) {
        composable<Route.Auth> {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Auth) { inclusive = true }
                    }
                },
                onLogoPositioned = onLogoPositioned
            )
        }

        composable<Route.Home> {
            HomeScreen(
                modifier = Modifier.padding(innerPadding),
                onNavigateToBookDetails = { bookId ->
                    navController.navigate(Route.BookDetails(bookId))
                },
                onNavigateToDiscoverBook = { gutenbergId ->
                    navController.navigate(Route.DiscoverBookDetails(gutenbergId))
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
                },
                onNavigateToTopic = { topic ->
                    navController.navigate(Route.DiscoverTopic(topic))
                }
            )
        }

        composable<Route.DiscoverTopic> { backStackEntry ->
            val topic = backStackEntry.toRoute<Route.DiscoverTopic>().topic
            DiscoverTopicScreen(
                topic = topic,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBookDetails = { gutenbergId ->
                    navController.navigate(Route.DiscoverBookDetails(gutenbergId))
                }
            )
        }

        composable<Route.Profile> {
            ProfileScreen(
                modifier = Modifier.padding(innerPadding),
                onLogout = {
                    navController.navigate(Route.Auth) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToCreateAccount = {
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
                },
                onAuthorBookClick = { gutenbergId ->
                    navController.navigate(Route.DiscoverBookDetails(gutenbergId))
                },
                onNavigateToAuthor = { authorName, gutenbergId ->
                    navController.navigate(Route.Author(authorName, gutenbergId))
                }
            )
        }

        composable<Route.DiscoverBookDetails> {
            BookDetailsScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenReader = { bookId ->
                    val intent = ReaderActivity.createIntent(navController.context, bookId)
                    navController.context.startActivity(intent)
                },
                onAuthorBookClick = { gutenbergId ->
                    navController.navigate(Route.DiscoverBookDetails(gutenbergId))
                },
                onNavigateToAuthor = { authorName, gutenbergId ->
                    navController.navigate(Route.Author(authorName, gutenbergId))
                }
            )
        }

        composable<Route.Author> {
            AuthorScreen(
                onNavigateBack = { navController.popBackStack() },
                onBookClick = { gutenbergId ->
                    navController.navigate(Route.DiscoverBookDetails(gutenbergId))
                }
            )
        }
    }
}