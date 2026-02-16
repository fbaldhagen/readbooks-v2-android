package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.getOrNull
import com.fbaldhagen.readbooks.domain.model.ReadingGoalProgress
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import com.fbaldhagen.readbooks.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetReadingGoalProgressUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val preferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<ReadingGoalProgress> =
        preferencesRepository.observe().map { prefs ->
            val todayMinutes = sessionRepository.getTodayMinutes().getOrNull() ?: 0
            ReadingGoalProgress(
                todayMinutes = todayMinutes,
                goalMinutes = prefs.dailyReadingGoalMinutes
            )
        }
}