package com.fbaldhagen.readbooks.ui.profile

import com.fbaldhagen.readbooks.domain.model.UserPreferences
import com.fbaldhagen.readbooks.domain.model.UserProfileStats

data class ProfileState(
    val isLoading: Boolean = true,
    val preferences: UserPreferences = UserPreferences(),
    val stats: UserProfileStats = UserProfileStats(
        totalBooks = 0,
        finishedBooks = 0,
        totalReadingMinutes = 0,
        currentStreak = 0
    ),
    val error: String? = null
)