package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.common.result.suspendRunCatching
import com.fbaldhagen.readbooks.data.remote.api.GutendexApiService
import com.fbaldhagen.readbooks.data.remote.api.OpenLibraryApiService
import com.fbaldhagen.readbooks.data.remote.dto.toDiscoverBook
import com.fbaldhagen.readbooks.domain.model.AuthorDetails
import com.fbaldhagen.readbooks.domain.repository.AuthorRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class AuthorRepositoryImpl @Inject constructor(
    private val openLibraryApiService: OpenLibraryApiService,
    private val gutendexApiService: GutendexApiService
) : AuthorRepository {

    override suspend fun getAuthorDetails(
        authorName: String,
        excludeGutenbergId: Int?
    ): Result<AuthorDetails> = suspendRunCatching {
        coroutineScope {
            val openLibraryDeferred = async {
                runCatching {
                    val searchResult = openLibraryApiService.searchAuthor(authorName)
                    val doc = searchResult.docs
                        .filter { it.name.isNotBlank() }
                        .maxByOrNull { it.workCount ?: 0 }
                    if (doc != null) {
                        val olid = doc.key.removePrefix("/authors/")
                        val author = openLibraryApiService.getAuthor(olid)
                        AuthorMetadata(
                            bio = author.bio,
                            birthYear = parseYear(author.birthDate ?: doc.birthDate),
                            deathYear = parseYear(author.deathDate ?: doc.deathDate),
                            photoUrl = "https://covers.openlibrary.org/a/olid/$olid-L.jpg"
                        )
                    } else null
                }.getOrNull()
            }

            val booksDeferred = async {
                runCatching {
                    gutendexApiService.getBooks(search = authorName)
                        .results
                        .filter { dto ->
                            dto.authors.any { it.name == authorName } &&
                                    dto.id != excludeGutenbergId
                        }
                        .map { it.toDiscoverBook() }
                }.getOrElse { emptyList() }
            }

            val metadata = openLibraryDeferred.await()
            val books = booksDeferred.await()

            AuthorDetails(
                name = authorName,
                bio = metadata?.bio,
                birthYear = metadata?.birthYear,
                deathYear = metadata?.deathYear,
                photoUrl = metadata?.photoUrl,
                books = books
            )
        }
    }

    private fun parseYear(dateString: String?): Int? {
        if (dateString == null) return null
        return dateString.trim().takeLast(4).toIntOrNull()
            ?: dateString.trim().take(4).toIntOrNull()
    }

    private data class AuthorMetadata(
        val bio: String?,
        val birthYear: Int?,
        val deathYear: Int?,
        val photoUrl: String?
    )
}