package com.fbaldhagen.readbooks.domain.model

data class UserPreferences(
    val isGuest: Boolean = false,
    val userName: String = "",
    val avatarUri: String? = null,
    val dailyReadingGoalMinutes: Int = 30,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val authToken: String? = null,
    val email: String? = null,
    val userId: Long? = null,
    val bio: String? = null,
    val yearlyBooksGoal: Int = 12,
    val notificationsEnabled: Boolean = false,
    val usePublicGutenberg: Boolean = false,
    val syncReaderTheme: Boolean = false,
    val readerPreferences: ReaderPreferences = ReaderPreferences()
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SEPIA,
    SYSTEM
}