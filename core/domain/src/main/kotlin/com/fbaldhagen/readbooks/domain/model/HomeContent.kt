package com.fbaldhagen.readbooks.domain.model

data class HomeContent(
    val currentlyReading: List<Book>,
    val recentBooks: List<Book>,
    val readingGoalProgress: ReadingGoalProgress?,
    val recentAchievements: List<Achievement>
)

data class ReadingGoalProgress(
    val todayMinutes: Int,
    val goalMinutes: Int
) {
    val progressFraction: Float get() = if (goalMinutes > 0) {
        (todayMinutes.toFloat() / goalMinutes).coerceIn(0f, 1f)
    } else 0f

    val isGoalMet: Boolean get() = todayMinutes >= goalMinutes
}