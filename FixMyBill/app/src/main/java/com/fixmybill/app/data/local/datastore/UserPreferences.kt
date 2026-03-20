package com.fixmybill.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferencesData(
    val hasCompletedOnboarding: Boolean = false,
    val selectedState: String = "",
    val selectedCity: String = "",
    val isPremiumUser: Boolean = false,
    val darkModePreference: String = "system",
    val notificationsEnabled: Boolean = true,
    val language: String = "en"
)

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferencesKeys {
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val SELECTED_STATE = stringPreferencesKey("selected_state")
        val SELECTED_CITY = stringPreferencesKey("selected_city")
        val IS_PREMIUM_USER = booleanPreferencesKey("is_premium_user")
        val DARK_MODE_PREFERENCE = stringPreferencesKey("dark_mode_preference")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val userPreferencesFlow: Flow<UserPreferencesData> = context.dataStore.data.map { preferences ->
        UserPreferencesData(
            hasCompletedOnboarding = preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false,
            selectedState = preferences[PreferencesKeys.SELECTED_STATE] ?: "",
            selectedCity = preferences[PreferencesKeys.SELECTED_CITY] ?: "",
            isPremiumUser = preferences[PreferencesKeys.IS_PREMIUM_USER] ?: false,
            darkModePreference = preferences[PreferencesKeys.DARK_MODE_PREFERENCE] ?: "system",
            notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            language = preferences[PreferencesKeys.LANGUAGE] ?: "en"
        )
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = completed
        }
    }

    suspend fun setSelectedState(state: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_STATE] = state
        }
    }

    suspend fun setSelectedCity(city: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_CITY] = city
        }
    }

    suspend fun setPremiumUser(isPremium: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_PREMIUM_USER] = isPremium
        }
    }

    suspend fun setDarkModePreference(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE_PREFERENCE] = mode
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language
        }
    }
}
