package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.common.result.suspendRunCatching
import com.fbaldhagen.readbooks.domain.model.DailyReading
import com.fbaldhagen.readbooks.domain.model.ReadingAnalytics
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

class GetReadingAnalyticsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val bookRepository: BookRepository,
    private val streakCalculator: StreakCalculator
) {
    operator fun invoke(): Flow<Result<ReadingAnalytics>> = combine(
        sessionRepository.observeAll(),
        bookRepository.observeFinishedCount(),
        bookRepository.observeTotalCount()
    ) { sessions, finishedCount, totalCount ->
        suspendRunCatching {
            val totalMinutes = sessions.sumOf { it.durationMinutes }
            val today = streakCalculator.clearTime(System.currentTimeMillis())
            val weekAgo = today - StreakCalculator.DAY_MILLIS * 7

            val weekSessions = sessions.filter { it.startTime >= weekAgo }
            val weeklyMinutes = (0 until 7).map { daysAgo ->
                val dayStart = today - daysAgo * StreakCalculator.DAY_MILLIS
                val dayEnd = dayStart + StreakCalculator.DAY_MILLIS
                val minutes = weekSessions
                    .filter { it.startTime in dayStart until dayEnd }
                    .sumOf { it.durationMinutes }
                DailyReading(
                    dayOfWeek = getDayOfWeek(dayStart),
                    minutes = minutes
                )
            }.reversed()

            val streak = streakCalculator.calculate(sessions)
            val activeDays = sessions
                .map { streakCalculator.clearTime(it.startTime) }
                .distinct()
                .size
                .coerceAtLeast(1)

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

    private fun getDayOfWeek(timestamp: Long): Int =
        Calendar.getInstance().apply { timeInMillis = timestamp }
            .get(Calendar.DAY_OF_WEEK)
}