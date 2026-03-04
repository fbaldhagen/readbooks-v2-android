package com.fbaldhagen.readbooks.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fbaldhagen.readbooks.domain.model.ReaderFontFamily
import com.fbaldhagen.readbooks.domain.model.ReaderPreferences
import com.fbaldhagen.readbooks.domain.model.ReaderTheme
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
        val SYNC_READER_THEME = booleanPreferencesKey("sync_reader_theme")
        val READER_FONT_SIZE = floatPreferencesKey("reader_font_size")
        val READER_FONT_FAMILY = stringPreferencesKey("reader_font_family")
        val READER_THEME = stringPreferencesKey("reader_theme")
        val READER_PAGE_MARGINS = floatPreferencesKey("reader_page_margins")
        val READER_LINE_HEIGHT = floatPreferencesKey("reader_line_height")
        val READER_LETTER_SPACING = floatPreferencesKey("reader_letter_spacing")
        val CONSECUTIVE_GOAL_DAYS = intPreferencesKey("consecutive_goal_days")
        val LAST_GOAL_MET_DATE = longPreferencesKey("last_goal_met_date")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            isGuest = prefs[Keys.IS_GUEST] ?: false,
            userName = prefs[Keys.USER_NAME] ?: "",
            avatarUri = prefs[Keys.AVATAR_URI],
            dailyReadingGoalMinutes = prefs[Keys.DAILY_READING_GOAL] ?: 30,
            consecutiveGoalDays = prefs[Keys.CONSECUTIVE_GOAL_DAYS] ?: 0,
            lastGoalMetDate = prefs[Keys.LAST_GOAL_MET_DATE],
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
            usePublicGutenberg = prefs[Keys.USE_PUBLIC_GUTENBERG] ?: false,
            syncReaderTheme = prefs[Keys.SYNC_READER_THEME] ?: false,
            readerPreferences = ReaderPreferences(
                fontSize = (prefs[Keys.READER_FONT_SIZE] ?: 1.0f).toDouble(),
                fontFamily = prefs[Keys.READER_FONT_FAMILY]?.let {
                    try {
                        ReaderFontFamily.valueOf(it)
                    } catch (_: Exception) {
                        ReaderFontFamily.DEFAULT
                    }
                } ?: ReaderFontFamily.DEFAULT,
                theme = prefs[Keys.READER_THEME]?.let {
                    try {
                        ReaderTheme.valueOf(it)
                    } catch (_: Exception) {
                        ReaderTheme.LIGHT
                    }
                } ?: ReaderTheme.LIGHT,
                pageMargins = (prefs[Keys.READER_PAGE_MARGINS] ?: 1.5f).toDouble(),
                lineHeight = (prefs[Keys.READER_LINE_HEIGHT] ?: 1.5f).toDouble(),
                letterSpacing = (prefs[Keys.READER_LETTER_SPACING] ?: 0.0f).toDouble()
            )
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

    suspend fun setSyncReaderTheme(sync: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SYNC_READER_THEME] = sync
        }
    }

    suspend fun saveReaderPreferences(preferences: ReaderPreferences) {
        context.dataStore.edit { prefs ->
            prefs[Keys.READER_FONT_SIZE] = preferences.fontSize.toFloat()
            prefs[Keys.READER_FONT_FAMILY] = preferences.fontFamily.name
            prefs[Keys.READER_THEME] = preferences.theme.name
            prefs[Keys.READER_PAGE_MARGINS] = preferences.pageMargins.toFloat()
            prefs[Keys.READER_LINE_HEIGHT] = preferences.lineHeight.toFloat()
            prefs[Keys.READER_LETTER_SPACING] = preferences.letterSpacing.toFloat()
        }
    }

    suspend fun setConsecutiveGoalDays(days: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CONSECUTIVE_GOAL_DAYS] = days
        }
    }

    suspend fun setLastGoalMetDate(date: Long?) {
        context.dataStore.edit { prefs ->
            if (date != null) prefs[Keys.LAST_GOAL_MET_DATE] = date
            else prefs.remove(Keys.LAST_GOAL_MET_DATE)
        }
    }
}