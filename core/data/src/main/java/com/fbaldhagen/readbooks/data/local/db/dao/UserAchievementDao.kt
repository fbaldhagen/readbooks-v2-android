package com.fbaldhagen.readbooks.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fbaldhagen.readbooks.data.local.db.entity.UserAchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAchievementDao {

    @Query("SELECT * FROM user_achievements ORDER BY unlocked_at DESC")
    fun observeAll(): Flow<List<UserAchievementEntity>>

    @Query(
        "SELECT * FROM user_achievements WHERE unlocked_at IS NOT NULL " +
                "ORDER BY unlocked_at DESC LIMIT :limit"
    )
    fun observeRecent(limit: Int): Flow<List<UserAchievementEntity>>

    @Query("SELECT * FROM user_achievements WHERE id = :achievementId")
    suspend fun getById(achievementId: String): UserAchievementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<UserAchievementEntity>)

    @Query("UPDATE user_achievements SET current_progress = :progress WHERE id = :achievementId")
    suspend fun updateProgress(achievementId: String, progress: Int)

    @Query("UPDATE user_achievements SET unlocked_at = :timestamp WHERE id = :achievementId")
    suspend fun unlock(achievementId: String, timestamp: Long)

    @Query("UPDATE user_achievements SET current_progress = 0, unlocked_at = NULL")
    suspend fun resetAll()

    @Query("SELECT id FROM user_achievements")
    suspend fun getAllIds(): List<String>
}