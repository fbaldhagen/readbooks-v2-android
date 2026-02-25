package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.domain.model.ThemeMode
import com.fbaldhagen.readbooks.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun observe(): Flow<UserPreferences>
    suspend fun updateUserName(name: String)
    suspend fun updateAvatarUri(uri: String?)
    suspend fun setDailyReadingGoal(minutes: Int)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun saveAuthToken(token: String)
    suspend fun getAuthToken(): String?
    suspend fun saveUserInfo(email: String, userId: Long)
    suspend fun clearAuthData()
}