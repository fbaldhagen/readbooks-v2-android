package com.fbaldhagen.readbooks.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fbaldhagen.readbooks.domain.model.AchievementCategory

@Entity(tableName = "user_achievements")
data class UserAchievementEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val tier: String,
    val category: String = AchievementCategory.UNIQUE.name,
    @ColumnInfo(name = "current_progress")
    val currentProgress: Int = 0,
    @ColumnInfo(name = "target_progress")
    val targetProgress: Int,
    @ColumnInfo(name = "unlocked_at")
    val unlockedAt: Long? = null
)