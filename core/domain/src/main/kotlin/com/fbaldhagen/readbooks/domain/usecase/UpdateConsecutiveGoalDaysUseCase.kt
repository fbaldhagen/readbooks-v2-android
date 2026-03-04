package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.getOrNull
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject

class UpdateConsecutiveGoalDaysUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val userPreferencesUseCases: UserPreferencesUseCases
) {
    suspend operator fun invoke() {
        val prefs = userPreferencesUseCases.observe().first()
        val goalMinutes = prefs.dailyReadingGoalMinutes
        val todayMinutes = sessionRepository.getTodayMinutes().getOrNull() ?: 0
        val today = clearTime(System.currentTimeMillis())
        val lastGoalMetDate = prefs.lastGoalMetDate?.let { clearTime(it) }

        if (todayMinutes >= goalMinutes) {
            if (lastGoalMetDate == today) return
            val yesterday = today - DAY_MILLIS
            val newConsecutiveDays = if (lastGoalMetDate == yesterday) {
                prefs.consecutiveGoalDays + 1
            } else {
                1
            }
            userPreferencesUseCases.setConsecutiveGoalDays(newConsecutiveDays)
            userPreferencesUseCases.setLastGoalMetDate(System.currentTimeMillis())
        } else {
            if (lastGoalMetDate != null && lastGoalMetDate < today) {
                userPreferencesUseCases.setConsecutiveGoalDays(0)
            }
        }
    }

    private fun clearTime(timestamp: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    companion object {
        private const val DAY_MILLIS = 24 * 60 * 60 * 1000L
    }
}