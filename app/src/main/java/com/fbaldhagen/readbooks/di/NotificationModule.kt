package com.fbaldhagen.readbooks.di

import com.fbaldhagen.readbooks.R
import com.fbaldhagen.readbooks.data.di.NotificationIcon
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @NotificationIcon
    fun provideNotificationIcon(): Int = R.drawable.ic_notification
}