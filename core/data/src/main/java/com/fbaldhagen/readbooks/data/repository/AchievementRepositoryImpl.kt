package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.common.result.suspendRunCatching
import com.fbaldhagen.readbooks.data.local.db.dao.UserAchievementDao
import com.fbaldhagen.readbooks.data.mapper.toDomain
import com.fbaldhagen.readbooks.data.mapper.toEntity
import com.fbaldhagen.readbooks.domain.model.Achievement
import com.fbaldhagen.readbooks.domain.model.AchievementCatalog
import com.fbaldhagen.readbooks.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: UserAchievementDao
) : AchievementRepository {

    private val _newlyUnlocked = MutableSharedFlow<List<Achievement>>(extraBufferCapacity = 1)

    override suspend fun initializeCatalog(): Result<Unit> = suspendRunCatching {
        val existing = achievementDao.getAllIds().toSet()
        val toInsert = AchievementCatalog.all
            .filter { it.id !in existing }
            .map { it.toEntity() }
        if (toInsert.isNotEmpty()) achievementDao.insertAll(toInsert)
    }

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

    override fun observeNewlyUnlocked(): Flow<List<Achievement>> = _newlyUnlocked

    override suspend fun emitNewlyUnlocked(achievements: List<Achievement>) {
        if (achievements.isNotEmpty()) _newlyUnlocked.emit(achievements)
    }
}