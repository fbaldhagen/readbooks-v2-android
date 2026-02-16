package com.fbaldhagen.readbooks.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fbaldhagen.readbooks.domain.model.ThemeMode
import com.fbaldhagen.readbooks.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

class UserPreferencesDataStore @Inject constructor(
    private val context: Context
) {
    private object Keys {
        val USER_NAME = stringPreferencesKey("user_name")
        val AVATAR_URI = stringPreferencesKey("avatar_uri")
        val DAILY_READING_GOAL = intPreferencesKey("daily_reading_goal_minutes")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            userName = prefs[Keys.USER_NAME] ?: "",
            avatarUri = prefs[Keys.AVATAR_URI],
            dailyReadingGoalMinutes = prefs[Keys.DAILY_READING_GOAL] ?: 30,
            themeMode = prefs[Keys.THEME_MODE]?.let {
                try {
                    ThemeMode.valueOf(it)
                } catch (_: IllegalArgumentException) {
                    ThemeMode.SYSTEM
                }
            } ?: ThemeMode.SYSTEM
        )
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
}