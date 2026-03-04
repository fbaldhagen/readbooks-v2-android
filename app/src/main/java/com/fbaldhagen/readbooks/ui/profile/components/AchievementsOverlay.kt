package com.fbaldhagen.readbooks.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.Achievement
import com.fbaldhagen.readbooks.domain.model.AchievementCategory
import com.fbaldhagen.readbooks.domain.model.AchievementTier

@Composable
fun AchievementsOverlay(
    achievements: List<Achievement>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val grouped = achievements.groupBy { it.category }
    val categoryOrder = listOf(
        AchievementCategory.STREAK,
        AchievementCategory.BOOKS_FINISHED,
        AchievementCategory.READING_TIME,
        AchievementCategory.DAILY_GOAL,
        AchievementCategory.VARIETY,
        AchievementCategory.UNIQUE,
        AchievementCategory.MONTH_BADGE
    )
    val categoryLabels = mapOf(
        AchievementCategory.STREAK to "Streaks",
        AchievementCategory.BOOKS_FINISHED to "Books Finished",
        AchievementCategory.READING_TIME to "Reading Time",
        AchievementCategory.DAILY_GOAL to "Daily Goal",
        AchievementCategory.VARIETY to "Variety",
        AchievementCategory.UNIQUE to "Unique",
        AchievementCategory.MONTH_BADGE to "Month Badges"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Achievements",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            Text(
                text = "${achievements.count { it.isUnlocked }} of ${achievements.size} unlocked",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                categoryOrder.forEach { category ->
                    val items = grouped[category] ?: return@forEach
                    item {
                        AchievementCategorySection(
                            label = categoryLabels[category] ?: category.name,
                            achievements = items
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementCategorySection(
    label: String,
    achievements: List<Achievement>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        achievements.forEach { achievement ->
            AchievementRow(achievement = achievement)
        }
    }
}

@Composable
fun AchievementRow(achievement: Achievement) {
    val isUnlocked = achievement.isUnlocked

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isUnlocked) tierColor(achievement.tier).copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (isUnlocked) tierColor(achievement.tier)
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = achievement.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isUnlocked) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!isUnlocked) {
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { achievement.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = tierColor(achievement.tier),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "${achievement.currentProgress} / ${achievement.targetProgress}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (isUnlocked) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Unlocked",
                tint = tierColor(achievement.tier),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun tierColor(tier: AchievementTier): Color = when (tier) {
    AchievementTier.BRONZE -> Color(0xFFCD7F32)
    AchievementTier.SILVER -> Color(0xFF9E9E9E)
    AchievementTier.GOLD -> Color(0xFFF59E0B)
    AchievementTier.PLATINUM -> Color(0xFF7C9FC9)
}