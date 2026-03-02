package com.fbaldhagen.readbooks.domain.model

data class AuthorDetails(
    val name: String,
    val bio: String?,
    val birthYear: Int?,
    val deathYear: Int?,
    val photoUrl: String?,
    val books: List<DiscoverBook>
)