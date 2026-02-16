package com.fbaldhagen.readbooks.domain.model

data class ReadingAnalytics(
    val totalReadingMinutes: Int,
    val totalBooksFinished: Int,
    val averageMinutesPerDay: Float,
    val currentStreak: ReadingStreak,
    val weeklyMinutes: List<DailyReading>
)

data class ReadingStreak(
    val currentDays: Int,
    val longestDays: Int
)

data class DailyReading(
    val dayOfWeek: Int,
    val minutes: Int
)