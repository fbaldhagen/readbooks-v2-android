package com.fbaldhagen.readbooks.domain.model

object AchievementCatalog {

    val all: List<AchievementDefinition> = buildList {
        // Streaks
        add(AchievementDefinition(
            "streak_1",
            "First Spark",
            "Read for 1 day in a row",
            AchievementTier.BRONZE,
            AchievementCategory.STREAK,
            1)
        )
        add(AchievementDefinition(
            "streak_7",
            "On a Roll",
            "Read for 7 days in a row",
            AchievementTier.SILVER,
            AchievementCategory.STREAK,
            7)
        )
        add(AchievementDefinition(
            "streak_30",
            "Dedicated Reader",
            "Read for 30 days in a row",
            AchievementTier.GOLD,
            AchievementCategory.STREAK,
            30)
        )
        add(AchievementDefinition(
            "streak_100",
            "Unstoppable",
            "Read for 100 days in a row",
            AchievementTier.PLATINUM,
            AchievementCategory.STREAK,
            100)
        )

        // Books finished
        add(AchievementDefinition(
            "books_1",
            "First Chapter",
            "Finish your first book",
            AchievementTier.BRONZE,
            AchievementCategory.BOOKS_FINISHED,
            1)
        )
        add(AchievementDefinition(
            "books_5",
            "Bookworm",
            "Finish 5 books",
            AchievementTier.SILVER,
            AchievementCategory.BOOKS_FINISHED,
            5)
        )
        add(AchievementDefinition(
            "books_10",
            "Avid Reader",
            "Finish 10 books",
            AchievementTier.SILVER,
            AchievementCategory.BOOKS_FINISHED,
            10)
        )
        add(AchievementDefinition(
            "books_25",
            "Library Card",
            "Finish 25 books",
            AchievementTier.GOLD,
            AchievementCategory.BOOKS_FINISHED,
            25)
        )
        add(AchievementDefinition(
            "books_50",
            "Scholar",
            "Finish 50 books",
            AchievementTier.PLATINUM,
            AchievementCategory.BOOKS_FINISHED,
            50)
        )

        // Reading time
        add(AchievementDefinition(
            "time_60",
            "First Hour",
            "Read for 1 hour total",
            AchievementTier.BRONZE,
            AchievementCategory.READING_TIME,
            60)
        )
        add(AchievementDefinition(
            "time_600",
            "Deep Reader",
            "Read for 10 hours total",
            AchievementTier.SILVER,
            AchievementCategory.READING_TIME,
            600)
        )
        add(AchievementDefinition("time_6000",
            "Marathoner",
            "Read for 100 hours total",
            AchievementTier.GOLD,
            AchievementCategory.READING_TIME,
            6000)
        )

        // Daily goal
        add(AchievementDefinition(
            "goal_7",
            "On Target",
            "Hit your daily goal 7 days in a row",
            AchievementTier.SILVER,
            AchievementCategory.DAILY_GOAL,
            7)
        )
        add(AchievementDefinition(
            "goal_30",
            "Consistent",
            "Hit your daily goal 30 days in a row",
            AchievementTier.GOLD,
            AchievementCategory.DAILY_GOAL,
            30)
        )

        // Variety
        add(AchievementDefinition(
            "authors_5",
            "Worldly Reader",
            "Finish books by 5 different authors",
            AchievementTier.SILVER,
            AchievementCategory.VARIETY,
            5
        ))
        add(AchievementDefinition(
            "ratings_10",
            "Critic",
            "Rate 10 books",
            AchievementTier.BRONZE,
            AchievementCategory.VARIETY,
            10)
        )

        // Unique
        add(AchievementDefinition(
            "time_of_day_night",
            "Night Owl",
            "Read past midnight",
            AchievementTier.BRONZE,
            AchievementCategory.UNIQUE,
            1)
        )
        add(AchievementDefinition(
            "time_of_day_early",
            "Early Bird",
            "Read before 6am",
            AchievementTier.BRONZE,
            AchievementCategory.UNIQUE,
            1)
        )

        // Month badges
        val months = listOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        months.forEachIndexed { index, month ->
            add(AchievementDefinition(
                id = "month_${month.lowercase()}",
                name = "$month Reader",
                description = "Read every day in $month",
                tier = AchievementTier.GOLD,
                category = AchievementCategory.MONTH_BADGE,
                targetProgress = getDaysInMonth(index + 1)
            ))
        }
    }

    private fun getDaysInMonth(month: Int): Int = when (month) {
        2 -> 28
        4, 6, 9, 11 -> 30
        else -> 31
    }

    fun getById(id: String): AchievementDefinition? = all.find { it.id == id }
}

data class AchievementDefinition(
    val id: String,
    val name: String,
    val description: String,
    val tier: AchievementTier,
    val category: AchievementCategory,
    val targetProgress: Int
)

enum class AchievementCategory {
    STREAK,
    BOOKS_FINISHED,
    READING_TIME,
    DAILY_GOAL,
    VARIETY,
    UNIQUE,
    MONTH_BADGE
}