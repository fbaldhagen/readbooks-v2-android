package com.fbaldhagen.readbooks.domain.model

data class Bookmark(
    val id: Long = 0,
    val bookId: Long,
    val locator: String,
    val title: String?,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)