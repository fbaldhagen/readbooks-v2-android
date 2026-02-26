package com.fbaldhagen.readbooks.data.di

import android.content.Context
import androidx.room.Room
import com.fbaldhagen.readbooks.data.local.db.AppDatabase
import com.fbaldhagen.readbooks.data.local.db.dao.BookDao
import com.fbaldhagen.readbooks.data.local.db.dao.BookmarkDao
import com.fbaldhagen.readbooks.data.local.db.dao.CollectionDao
import com.fbaldhagen.readbooks.data.local.db.dao.ReadingSessionDao
import com.fbaldhagen.readbooks.data.local.db.dao.UserAchievementDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "readbooks.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun provideBookDao(database: AppDatabase): BookDao = database.bookDao()

    @Provides
    fun provideBookmarkDao(database: AppDatabase): BookmarkDao = database.bookmarkDao()

    @Provides
    fun provideCollectionDao(database: AppDatabase): CollectionDao = database.collectionDao()

    @Provides
    fun provideReadingSessionDao(database: AppDatabase): ReadingSessionDao =
        database.readingSessionDao()

    @Provides
    fun provideUserAchievementDao(database: AppDatabase): UserAchievementDao =
        database.userAchievementDao()
}