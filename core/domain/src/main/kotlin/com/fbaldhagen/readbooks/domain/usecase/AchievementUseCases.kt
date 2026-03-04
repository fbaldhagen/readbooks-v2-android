package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.common.result.getOrNull
import com.fbaldhagen.readbooks.domain.model.Achievement
import com.fbaldhagen.readbooks.domain.repository.AchievementRepository
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AchievementUseCases @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val achievementChecker: AchievementChecker,
    private val sessionRepository: SessionRepository,
    private val bookRepository: BookRepository,
    private val streakCalculator: StreakCalculator
) {
    fun observeAll(): Flow<List<Achievement>> =
        achievementRepository.observeAll()

    fun observeRecent(limit: Int = 5): Flow<List<Achievement>> =
        achievementRepository.observeRecent(limit)

    suspend fun getById(id: String): Result<Achievement> =
        achievementRepository.getById(id)

    suspend fun updateProgress(id: String, progress: Int): Result<Unit> =
        achievementRepository.updateProgress(id, progress)

    suspend fun unlock(id: String): Result<Unit> =
        achievementRepository.unlock(id)

    suspend fun resetAll(): Result<Unit> =
        achievementRepository.resetAll()

    suspend fun initializeCatalog(): Result<Unit> =
        achievementRepository.initializeCatalog()

    suspend fun checkAndUpdate(consecutiveGoalDays: Int): List<Achievement> {
        val allSessions = sessionRepository.getAllSessions().getOrNull() ?: emptyList()
        val totalBooksFinished = bookRepository.observeFinishedCount().first()
        val authorCount = bookRepository.getFinishedAuthorCount()
        val totalRatings = bookRepository.getRatedBookCount()
        val currentStreak = streakCalculator.getCurrentStreak(allSessions)
        val existing = achievementRepository.observeAll().first()

        val input = AchievementCheckInput(
            allSessions = allSessions,
            totalBooksFinished = totalBooksFinished,
            currentStreak = currentStreak,
            consecutiveGoalDays = consecutiveGoalDays,
            totalRatings = totalRatings,
            authorCount = authorCount
        )

        val updates = achievementChecker.check(input, existing)
        val newlyUnlocked = mutableListOf<Achievement>()

        updates.forEach { update ->
            achievementRepository.updateProgress(update.achievementId, update.newProgress)
            if (update.shouldUnlock) {
                achievementRepository.unlock(update.achievementId)
                achievementRepository.getById(update.achievementId)
                    .getOrNull()?.let { newlyUnlocked.add(it) }
            }
        }

        achievementRepository.emitNewlyUnlocked(newlyUnlocked)
        return newlyUnlocked
    }

    fun observeNewlyUnlocked() = achievementRepository.observeNewlyUnlocked()
}