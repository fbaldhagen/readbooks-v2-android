package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.Achievement
import com.fbaldhagen.readbooks.domain.model.AchievementCategory
import com.fbaldhagen.readbooks.domain.model.ReadingSession
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

data class AchievementCheckInput(
    val allSessions: List<ReadingSession>,
    val totalBooksFinished: Int,
    val currentStreak: Int,
    val consecutiveGoalDays: Int,
    val totalRatings: Int,
    val authorCount: Int
)

data class AchievementUpdate(
    val achievementId: String,
    val newProgress: Int,
    val shouldUnlock: Boolean
)

@Singleton
class AchievementChecker @Inject constructor() {

    fun check(
        input: AchievementCheckInput,
        existing: List<Achievement>
    ): List<AchievementUpdate> {
        val updates = mutableListOf<AchievementUpdate>()
        val existingMap = existing.associateBy { it.id }

        existing.filter { !it.isUnlocked }.forEach { achievement ->
            val update = when (achievement.category) {
                AchievementCategory.STREAK ->
                    checkProgress(
                        achievement,
                        input.currentStreak,
                        existingMap
                    )
                AchievementCategory.BOOKS_FINISHED ->
                    checkProgress(
                        achievement,
                        input.totalBooksFinished,
                        existingMap
                    )
                AchievementCategory.READING_TIME ->
                    checkProgress(
                        achievement,
                        input.allSessions.sumOf { it.durationMinutes },
                        existingMap
                    )
                AchievementCategory.DAILY_GOAL ->
                    checkProgress(
                        achievement,
                        input.consecutiveGoalDays,
                        existingMap
                    )
                AchievementCategory.VARIETY -> when (achievement.id) {
                    "authors_5" ->
                        checkProgress(
                            achievement,
                            input.authorCount,
                            existingMap
                        )

                    "ratings_10" ->
                        checkProgress(
                            achievement,
                            input.totalRatings,
                            existingMap
                        )

                    else -> null
                }
                AchievementCategory.UNIQUE -> checkUnique(achievement, input.allSessions)
                AchievementCategory.MONTH_BADGE -> checkMonthBadge(achievement, input.allSessions)
            }
            update?.let { updates.add(it) }
        }
        return updates
    }

    private fun checkProgress(
        achievement: Achievement,
        currentValue: Int,
        existingMap: Map<String, Achievement>
    ): AchievementUpdate? {
        val existing = existingMap[achievement.id] ?: return null
        if (existing.currentProgress == currentValue) return null
        return AchievementUpdate(
            achievementId = achievement.id,
            newProgress = currentValue,
            shouldUnlock = currentValue >= achievement.targetProgress
        )
    }

    private fun checkUnique(
        achievement: Achievement,
        sessions: List<ReadingSession>
    ): AchievementUpdate? {
        val unlocked = when (achievement.id) {
            "time_of_day_night" -> sessions.any { session ->
                val hour = Calendar.getInstance().apply {
                    timeInMillis = session.startTime
                }.get(Calendar.HOUR_OF_DAY)
                hour < 4
            }
            "time_of_day_early" -> sessions.any { session ->
                val hour = Calendar.getInstance().apply {
                    timeInMillis = session.startTime
                }.get(Calendar.HOUR_OF_DAY)
                hour < 6
            }
            else -> false
        }
        return if (unlocked) AchievementUpdate(achievement.id, 1, true) else null
    }

    private fun checkMonthBadge(
        achievement: Achievement,
        sessions: List<ReadingSession>
    ): AchievementUpdate? {
        val monthIndex = listOf(
            "january", "february", "march", "april", "may", "june",
            "july", "august", "september", "october", "november", "december"
        ).indexOf(achievement.id.removePrefix("month_"))
        if (monthIndex == -1) return null

        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)

        val daysReadInMonth = sessions
            .filter { session ->
                val sessionCal = Calendar.getInstance().apply { timeInMillis = session.startTime }
                sessionCal.get(Calendar.MONTH) == monthIndex &&
                        sessionCal.get(Calendar.YEAR) == currentYear
            }
            .map { session ->
                Calendar.getInstance().apply { timeInMillis = session.startTime }
                    .get(Calendar.DAY_OF_MONTH)
            }
            .distinct()
            .size

        val existing = achievement.currentProgress
        if (daysReadInMonth == existing) return null
        return AchievementUpdate(
            achievementId = achievement.id,
            newProgress = daysReadInMonth,
            shouldUnlock = daysReadInMonth >= achievement.targetProgress
        )
    }
}