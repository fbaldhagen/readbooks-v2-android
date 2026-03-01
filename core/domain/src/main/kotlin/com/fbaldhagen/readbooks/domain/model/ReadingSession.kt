package com.fbaldhagen.readbooks.domain.model

data class ReadingSession(
    val id: Long = 0,
    val bookId: Long?,
    val gutenbergId: Int?,
    val startTime: Long,
    val endTime: Long? = null,
    val durationMinutes: Int = 0,
    val pagesRead: Int = 0
)