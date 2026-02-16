package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.common.result.Result
import com.fbaldhagen.readbooks.domain.model.Achievement
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {

    fun observeAll(): Flow<List<Achievement>>

    fun observeRecent(limit: Int): Flow<List<Achievement>>

    suspend fun getById(achievementId: String): Result<Achievement>

    suspend fun updateProgress(achievementId: String, progress: Int): Result<Unit>

    suspend fun unlock(achievementId: String, timestamp: Long = System.currentTimeMillis()): Result<Unit>

    suspend fun resetAll(): Result<Unit>
}