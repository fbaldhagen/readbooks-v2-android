package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.data.local.datastore.UserPreferencesDataStore
import com.fbaldhagen.readbooks.domain.model.ThemeMode
import com.fbaldhagen.readbooks.domain.model.UserPreferences
import com.fbaldhagen.readbooks.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : UserPreferencesRepository {

    override fun observe(): Flow<UserPreferences> = dataStore.preferences

    override suspend fun updateUserName(name: String) = dataStore.updateUserName(name)

    override suspend fun updateAvatarUri(uri: String?) = dataStore.updateAvatarUri(uri)

    override suspend fun setDailyReadingGoal(minutes: Int) = dataStore.setDailyReadingGoal(minutes)

    override suspend fun setThemeMode(mode: ThemeMode) = dataStore.setThemeMode(mode)
}