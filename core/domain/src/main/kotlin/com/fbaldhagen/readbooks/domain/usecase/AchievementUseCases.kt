package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.Achievement
import com.fbaldhagen.readbooks.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AchievementUseCases @Inject constructor(
    private val achievementRepository: AchievementRepository
) {
    fun observeAll(): Flow<List<Achievement>> =
        achievementRepository.observeAll()

    fun observeRecent(limit: Int = 5): Flow<List<Achievement>> =
        achievementRepository.observeRecent(limit)

    suspend fun getById(achievementId: String): Result<Achievement> =
        achievementRepository.getById(achievementId)

    suspend fun updateProgress(achievementId: String, progress: Int): Result<Unit> =
        achievementRepository.updateProgress(achievementId, progress)

    suspend fun unlock(achievementId: String): Result<Unit> =
        achievementRepository.unlock(achievementId)

    suspend fun resetAll(): Result<Unit> =
        achievementRepository.resetAll()
}