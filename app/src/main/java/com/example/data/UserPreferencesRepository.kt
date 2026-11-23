package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "battery_settings")

data class UserPreferences(
    val isLockscreenNotificationRequested: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val LOCK_SCREEN_NOTIFICATION_REQUESTED = booleanPreferencesKey("lock_screen_notification_requested")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val requested = preferences[PreferencesKeys.LOCK_SCREEN_NOTIFICATION_REQUESTED] ?: false
            val themeStr = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            val themeMode = try {
                ThemeMode.valueOf(themeStr)
            } catch (e: Exception) {
                ThemeMode.SYSTEM
            }
            UserPreferences(
                isLockscreenNotificationRequested = requested,
                themeMode = themeMode
            )
        }

    suspend fun setLockscreenNotificationRequested(requested: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LOCK_SCREEN_NOTIFICATION_REQUESTED] = requested
        }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }
}
