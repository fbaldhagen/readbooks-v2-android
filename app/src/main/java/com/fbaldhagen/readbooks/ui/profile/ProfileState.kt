package com.fbaldhagen.readbooks.ui.profile

import com.fbaldhagen.readbooks.domain.model.ReadingAnalytics
import com.fbaldhagen.readbooks.domain.model.ReadingStreak
import com.fbaldhagen.readbooks.domain.model.UserPreferences

data class ProfileState(
    val isLoading: Boolean = true,
    val preferences: UserPreferences = UserPreferences(),
    val analytics: ReadingAnalytics = ReadingAnalytics(
        totalBooks = 0,
        totalBooksFinished = 0,
        totalReadingMinutes = 0,
        averageMinutesPerDay = 0f,
        currentStreak = ReadingStreak(currentDays = 0, longestDays = 0),
        weeklyMinutes = emptyList()
    ),
    val error: String? = null
)