package com.fbaldhagen.readbooks.data.worker

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fbaldhagen.readbooks.common.result.getOrNull
import com.fbaldhagen.readbooks.data.notification.NotificationHelper
import com.fbaldhagen.readbooks.domain.model.ReadingSession
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import com.fbaldhagen.readbooks.domain.repository.UserPreferencesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val sessionRepository: SessionRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val prefs = userPreferencesRepository.observe().first()
        if (!prefs.notificationsEnabled) return Result.success()

        val todayMinutes = sessionRepository.getTodayMinutes().getOrNull() ?: 0
        val goalMinutes = prefs.dailyReadingGoalMinutes

        // Daily goal reminder
        if (todayMinutes < goalMinutes) {
            notificationHelper.postDailyReminderNotification(todayMinutes, goalMinutes)
        }

        // Streak at risk - has a streak but hasn't read today
        if (todayMinutes == 0) {
            val sessions = sessionRepository.getAllSessions().getOrNull() ?: emptyList()
            val streak = calculateCurrentStreak(sessions)
            if (streak > 0) {
                notificationHelper.postStreakReminderNotification(streak)
            }
        }

        return Result.success()
    }

    private fun calculateCurrentStreak(sessions: List<ReadingSession>): Int {
        if (sessions.isEmpty()) return 0
        val today = clearTime(System.currentTimeMillis())
        val daysWithReading = sessions
            .map { clearTime(it.startTime) }
            .distinct()
            .sorted()

        if (daysWithReading.last() != today - DAY_MILLIS) return 0

        var streak = 0
        var checkDay = today - DAY_MILLIS
        for (day in daysWithReading.reversed()) {
            if (day == checkDay) {
                streak++
                checkDay -= DAY_MILLIS
            } else break
        }
        return streak
    }

    private fun clearTime(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    companion object {
        private const val DAY_MILLIS = 24 * 60 * 60 * 1000L
    }
}