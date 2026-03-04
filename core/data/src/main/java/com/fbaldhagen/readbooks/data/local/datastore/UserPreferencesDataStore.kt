package com.fbaldhagen.readbooks.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fbaldhagen.readbooks.domain.model.ThemeMode
import com.fbaldhagen.readbooks.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

class UserPreferencesDataStore @Inject constructor(
    private val context: Context
) {
    private object Keys {
        val IS_GUEST = booleanPreferencesKey("is_guest")
        val USER_NAME = stringPreferencesKey("user_name")
        val AVATAR_URI = stringPreferencesKey("avatar_uri")
        val DAILY_READING_GOAL = intPreferencesKey("daily_reading_goal_minutes")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ID = longPreferencesKey("user_id")
        val BIO = stringPreferencesKey("bio")
        val YEARLY_BOOKS_GOAL = intPreferencesKey("yearly_books_goal")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val USE_PUBLIC_GUTENBERG = booleanPreferencesKey("use_public_gutenberg")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            isGuest = prefs[Keys.IS_GUEST] ?: false,
            userName = prefs[Keys.USER_NAME] ?: "",
            avatarUri = prefs[Keys.AVATAR_URI],
            dailyReadingGoalMinutes = prefs[Keys.DAILY_READING_GOAL] ?: 30,
            themeMode = prefs[Keys.THEME_MODE]?.let {
                try {
                    ThemeMode.valueOf(it)
                } catch (_: IllegalArgumentException) {
                    ThemeMode.SYSTEM
                }
            } ?: ThemeMode.SYSTEM,
            authToken = prefs[Keys.AUTH_TOKEN],
            email = prefs[Keys.USER_EMAIL],
            userId = prefs[Keys.USER_ID],
            bio = prefs[Keys.BIO],
            yearlyBooksGoal = prefs[Keys.YEARLY_BOOKS_GOAL] ?: 12,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: false,
            usePublicGutenberg = prefs[Keys.USE_PUBLIC_GUTENBERG] ?: false
        )
    }

    suspend fun setGuestMode(isGuest: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_GUEST] = isGuest
        }
    }

    suspend fun updateUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = name
        }
    }

    suspend fun updateAvatarUri(uri: String?) {
        context.dataStore.edit { prefs ->
            if (uri != null) {
                prefs[Keys.AVATAR_URI] = uri
            } else {
                prefs.remove(Keys.AVATAR_URI)
            }
        }
    }

    suspend fun setDailyReadingGoal(minutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DAILY_READING_GOAL] = minutes
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.AUTH_TOKEN] = token
        }
    }

    suspend fun getAuthToken(): String? {
        return context.dataStore.data.map { prefs ->
            prefs[Keys.AUTH_TOKEN]
        }.first()
    }

    suspend fun saveUserInfo(email: String, userId: Long) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_EMAIL] = email
            prefs[Keys.USER_ID] = userId
        }
    }

    suspend fun clearAuthData() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.AUTH_TOKEN)
            prefs.remove(Keys.USER_EMAIL)
            prefs.remove(Keys.USER_ID)
        }
    }

    suspend fun updateBio(bio: String?) {
        context.dataStore.edit { prefs ->
            if (bio != null) prefs[Keys.BIO] = bio
            else prefs.remove(Keys.BIO)
        }
    }

    suspend fun setYearlyBooksGoal(goal: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.YEARLY_BOOKS_GOAL] = goal
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setUsePublicGutenberg(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USE_PUBLIC_GUTENBERG] = enabled
        }
    }
}