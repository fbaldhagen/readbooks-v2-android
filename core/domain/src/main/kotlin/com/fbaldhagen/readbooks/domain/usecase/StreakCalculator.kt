package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.ReadingSession
import com.fbaldhagen.readbooks.domain.model.ReadingStreak
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakCalculator @Inject constructor() {

    fun calculate(sessions: List<ReadingSession>): ReadingStreak {
        val today = clearTime(System.currentTimeMillis())
        val daysWithReading = sessions
            .map { clearTime(it.startTime) }
            .distinct()
            .sorted()
        return calculateStreak(daysWithReading, today)
    }

    fun getCurrentStreak(sessions: List<ReadingSession>): Int =
        calculate(sessions).currentDays

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
                } else break
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

    fun clearTime(timestamp: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    companion object {
        const val DAY_MILLIS = 24 * 60 * 60 * 1000L
    }
}