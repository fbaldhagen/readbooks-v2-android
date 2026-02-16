package com.fbaldhagen.readbooks.domain.model

data class Collection(
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class CollectionWithBooks(
    val collection: Collection,
    val books: List<Book>
)