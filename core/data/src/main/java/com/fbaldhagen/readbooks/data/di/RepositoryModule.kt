package com.fbaldhagen.readbooks.data.di

import com.fbaldhagen.readbooks.data.repository.AchievementRepositoryImpl
import com.fbaldhagen.readbooks.data.repository.BookRepositoryImpl
import com.fbaldhagen.readbooks.data.repository.BookmarkRepositoryImpl
import com.fbaldhagen.readbooks.data.repository.CollectionRepositoryImpl
import com.fbaldhagen.readbooks.data.repository.DiscoverRepositoryImpl
import com.fbaldhagen.readbooks.data.repository.SessionRepositoryImpl
import com.fbaldhagen.readbooks.data.repository.UserPreferencesRepositoryImpl
import com.fbaldhagen.readbooks.domain.repository.AchievementRepository
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import com.fbaldhagen.readbooks.domain.repository.BookmarkRepository
import com.fbaldhagen.readbooks.domain.repository.CollectionRepository
import com.fbaldhagen.readbooks.domain.repository.DiscoverRepository
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import com.fbaldhagen.readbooks.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBookRepository(impl: BookRepositoryImpl): BookRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository

    @Binds
    @Singleton
    abstract fun bindCollectionRepository(impl: CollectionRepositoryImpl): CollectionRepository

    @Binds
    @Singleton
    abstract fun bindDiscoverRepository(impl: DiscoverRepositoryImpl): DiscoverRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    @Singleton
    abstract fun bindAchievementRepository(impl: AchievementRepositoryImpl): AchievementRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}