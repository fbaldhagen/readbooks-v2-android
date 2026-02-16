package com.fbaldhagen.readbooks.domain.model

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val tier: AchievementTier,
    val currentProgress: Int,
    val targetProgress: Int,
    val unlockedAt: Long? = null
) {
    val isUnlocked: Boolean get() = unlockedAt != null
    val progressFraction: Float get() = if (targetProgress > 0) {
        (currentProgress.toFloat() / targetProgress).coerceIn(0f, 1f)
    } else 0f
}

enum class AchievementTier {
    BRONZE,
    SILVER,
    GOLD,
    PLATINUM
}