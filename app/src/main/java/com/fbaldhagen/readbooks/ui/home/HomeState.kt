package com.fbaldhagen.readbooks.ui.home

import com.fbaldhagen.readbooks.domain.model.Achievement
import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.domain.model.ReadingGoalProgress

data class HomeState(
    val isLoading: Boolean = true,
    val currentlyReading: List<Book> = emptyList(),
    val recentBooks: List<Book> = emptyList(),
    val readingGoalProgress: ReadingGoalProgress? = null,
    val recentAchievements: List<Achievement> = emptyList(),
    val error: String? = null
)