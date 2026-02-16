package com.fbaldhagen.readbooks.domain.model

data class UserProfileStats(
    val totalBooks: Int,
    val finishedBooks: Int,
    val totalReadingMinutes: Int,
    val currentStreak: Int
)