package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.getOrNull
import com.fbaldhagen.readbooks.domain.model.HomeContent
import com.fbaldhagen.readbooks.domain.model.ReadingGoalProgress
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.repository.AchievementRepository
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import com.fbaldhagen.readbooks.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetHomeContentUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val sessionRepository: SessionRepository,
    private val achievementRepository: AchievementRepository,
    private val preferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<HomeContent> = combine(
        bookRepository.observeByStatus(ReadingStatus.READING),
        bookRepository.observeRecent(limit = 6),
        achievementRepository.observeRecent(limit = 3),
        preferencesRepository.observe()
    ) { reading, recent, achievements, prefs ->
        val todayMinutes = sessionRepository.getTodayMinutes().getOrNull() ?: 0

        HomeContent(
            currentlyReading = reading,
            recentBooks = recent,
            readingGoalProgress = ReadingGoalProgress(
                todayMinutes = todayMinutes,
                goalMinutes = prefs.dailyReadingGoalMinutes
            ),
            recentAchievements = achievements
        )
    }
}