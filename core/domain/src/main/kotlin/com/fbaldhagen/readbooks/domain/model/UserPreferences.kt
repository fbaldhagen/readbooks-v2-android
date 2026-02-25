package com.fbaldhagen.readbooks.domain.model

data class UserPreferences(
    val userName: String = "",
    val avatarUri: String? = null,
    val dailyReadingGoalMinutes: Int = 30,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val authToken: String? = null,
    val email: String? = null
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}