package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.common.result.suspendRunCatching
import com.fbaldhagen.readbooks.domain.model.DailyReading
import com.fbaldhagen.readbooks.domain.model.ReadingAnalytics
import com.fbaldhagen.readbooks.domain.model.ReadingStreak
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

class GetReadingAnalyticsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val bookRepository: BookRepository
) {
    operator fun invoke(): Flow<Result<ReadingAnalytics>> = combine(
        sessionRepository.observeAll(),
        bookRepository.observeFinishedCount(),
        bookRepository.observeTotalCount()
    ) { sessions, finishedCount, totalCount ->
        suspendRunCatching {
            val totalMinutes = sessions.sumOf { it.durationMinutes }

            val calendar = Calendar.getInstance()
            val today = clearTime(calendar.timeInMillis)
            val weekAgo = today - 7 * DAY_MILLIS

            val weekSessions = sessions.filter { it.startTime >= weekAgo }
            val weeklyMinutes = (0 until 7).map { daysAgo ->
                val dayStart = today - daysAgo * DAY_MILLIS
                val dayEnd = dayStart + DAY_MILLIS
                val minutes = weekSessions
                    .filter { it.startTime in dayStart until dayEnd }
                    .sumOf { it.durationMinutes }

                DailyReading(
                    dayOfWeek = getDayOfWeek(dayStart),
                    minutes = minutes
                )
            }.reversed()

            val daysWithReading = sessions
                .map { clearTime(it.startTime) }
                .distinct()
                .sorted()

            val streak = calculateStreak(daysWithReading, today)

            val activeDays = daysWithReading.size.coerceAtLeast(1)

            ReadingAnalytics(
                totalBooks = totalCount,
                totalReadingMinutes = totalMinutes,
                totalBooksFinished = finishedCount,
                averageMinutesPerDay = totalMinutes.toFloat() / activeDays,
                currentStreak = streak,
                weeklyMinutes = weeklyMinutes
            )
        }
    }

    private fun calculateStreak(sortedDays: List<Long>, today: Long): ReadingStreak {
        if (sortedDays.isEmpty()) return ReadingStreak(0, 0)

        var current = 0
        var longest = 0
        var checkDay = today

        if (sortedDays.last() == checkDay || sortedDays.last() == checkDay - DAY_MILLIS) {
            for (i in sortedDays.indices.reversed()) {
                if (sortedDays[i] == checkDay || sortedDays[i] == checkDay - DAY_MILLIS) {
                    current++
                    checkDay = sortedDays[i] - DAY_MILLIS
                } else {
                    break
                }
            }
        }

        var tempStreak = 1
        for (i in 1 until sortedDays.size) {
            if (sortedDays[i] - sortedDays[i - 1] == DAY_MILLIS) {
                tempStreak++
            } else {
                longest = maxOf(longest, tempStreak)
                tempStreak = 1
            }
        }
        longest = maxOf(longest, tempStreak, current)

        return ReadingStreak(currentDays = current, longestDays = longest)
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

    private fun getDayOfWeek(timestamp: Long): Int {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    companion object {
        private const val DAY_MILLIS = 24 * 60 * 60 * 1000L
    }
}