package com.fbaldhagen.readbooks.data.mapper

import com.fbaldhagen.readbooks.data.local.db.entity.UserAchievementEntity
import com.fbaldhagen.readbooks.domain.model.Achievement
import com.fbaldhagen.readbooks.domain.model.AchievementCategory
import com.fbaldhagen.readbooks.domain.model.AchievementDefinition
import com.fbaldhagen.readbooks.domain.model.AchievementTier

fun UserAchievementEntity.toDomain(): Achievement = Achievement(
    id = id,
    name = name,
    description = description,
    tier = try {
        AchievementTier.valueOf(tier)
    } catch (_: IllegalArgumentException) {
        AchievementTier.BRONZE
    },
    category = try {
        AchievementCategory.valueOf(category)
    } catch (_: IllegalArgumentException) {
        AchievementCategory.UNIQUE
    },
    currentProgress = currentProgress,
    targetProgress = targetProgress,
    unlockedAt = unlockedAt
)

fun Achievement.toEntity(): UserAchievementEntity = UserAchievementEntity(
    id = id,
    name = name,
    description = description,
    tier = tier.name,
    currentProgress = currentProgress,
    targetProgress = targetProgress,
    unlockedAt = unlockedAt
)

fun AchievementDefinition.toEntity(): UserAchievementEntity = UserAchievementEntity(
    id = id,
    name = name,
    description = description,
    tier = tier.name,
    category = category.name,
    currentProgress = 0,
    targetProgress = targetProgress
)