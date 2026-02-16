package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.common.result.suspendRunCatching
import com.fbaldhagen.readbooks.data.local.db.dao.UserAchievementDao
import com.fbaldhagen.readbooks.data.mapper.toDomain
import com.fbaldhagen.readbooks.domain.model.Achievement
import com.fbaldhagen.readbooks.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: UserAchievementDao
) : AchievementRepository {

    override fun observeAll(): Flow<List<Achievement>> =
        achievementDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeRecent(limit: Int): Flow<List<Achievement>> =
        achievementDao.observeRecent(limit).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(achievementId: String): Result<Achievement> =
        suspendRunCatching {
            achievementDao.getById(achievementId)?.toDomain()
                ?: throw NoSuchElementException("Achievement $achievementId not found")
        }

    override suspend fun updateProgress(achievementId: String, progress: Int): Result<Unit> =
        suspendRunCatching {
            achievementDao.updateProgress(achievementId, progress)
        }

    override suspend fun unlock(achievementId: String, timestamp: Long): Result<Unit> =
        suspendRunCatching {
            achievementDao.unlock(achievementId, timestamp)
        }

    override suspend fun resetAll(): Result<Unit> = suspendRunCatching {
        achievementDao.resetAll()
    }
}