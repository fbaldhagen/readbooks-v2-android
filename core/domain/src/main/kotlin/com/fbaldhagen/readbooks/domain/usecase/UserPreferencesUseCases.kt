package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.ThemeMode
import com.fbaldhagen.readbooks.domain.model.UserPreferences
import com.fbaldhagen.readbooks.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPreferencesUseCases @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) {
    fun observe(): Flow<UserPreferences> =
        preferencesRepository.observe()

    suspend fun updateUserName(name: String) =
        preferencesRepository.updateUserName(name)

    suspend fun updateAvatarUri(uri: String?) =
        preferencesRepository.updateAvatarUri(uri)

    suspend fun setDailyReadingGoal(minutes: Int) =
        preferencesRepository.setDailyReadingGoal(minutes)

    suspend fun setThemeMode(mode: ThemeMode) =
        preferencesRepository.setThemeMode(mode)

    suspend fun updateBio(bio: String?) =
        preferencesRepository.updateBio(bio)

    suspend fun setYearlyBooksGoal(goal: Int) =
        preferencesRepository.setYearlyBooksGoal(goal)

    suspend fun setGuestMode(isGuest: Boolean) =
        preferencesRepository.setGuestMode(isGuest)

    suspend fun setNotificationsEnabled(enabled: Boolean) =
        preferencesRepository.setNotificationsEnabled(enabled)
}