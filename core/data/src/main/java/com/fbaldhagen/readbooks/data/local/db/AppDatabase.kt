package com.fbaldhagen.readbooks.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fbaldhagen.readbooks.data.local.db.dao.BookDao
import com.fbaldhagen.readbooks.data.local.db.dao.BookmarkDao
import com.fbaldhagen.readbooks.data.local.db.dao.CollectionDao
import com.fbaldhagen.readbooks.data.local.db.dao.ReadingSessionDao
import com.fbaldhagen.readbooks.data.local.db.dao.UserAchievementDao
import com.fbaldhagen.readbooks.data.local.db.entity.BookCollectionCrossRef
import com.fbaldhagen.readbooks.data.local.db.entity.BookEntity
import com.fbaldhagen.readbooks.data.local.db.entity.BookmarkEntity
import com.fbaldhagen.readbooks.data.local.db.entity.CollectionEntity
import com.fbaldhagen.readbooks.data.local.db.entity.ReadingSessionEntity
import com.fbaldhagen.readbooks.data.local.db.entity.UserAchievementEntity

@Database(
    entities = [
        BookEntity::class,
        BookmarkEntity::class,
        CollectionEntity::class,
        BookCollectionCrossRef::class,
        ReadingSessionEntity::class,
        UserAchievementEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun collectionDao(): CollectionDao
    abstract fun readingSessionDao(): ReadingSessionDao
    abstract fun userAchievementDao(): UserAchievementDao
}