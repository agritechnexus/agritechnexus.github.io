package com.fixmybill.app.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixmybill.app.data.local.datastore.UserPreferences
import com.fixmybill.app.data.local.datastore.UserPreferencesData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = true,
    val darkModePreference: String = "system",
    val notificationsEnabled: Boolean = true,
    val selectedState: String = "",
    val selectedCity: String = "",
    val isPremiumUser: Boolean = false,
    val language: String = "en",
    val appVersion: String = "1.0.0"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsUiState())
    val settingsState: StateFlow<SettingsUiState> = _settingsState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            userPreferences.userPreferencesFlow.collect { prefs: UserPreferencesData ->
                _settingsState.update {
                    SettingsUiState(
                        isLoading = false,
                        darkModePreference = prefs.darkModePreference,
                        notificationsEnabled = prefs.notificationsEnabled,
                        selectedState = prefs.selectedState,
                        selectedCity = prefs.selectedCity,
                        isPremiumUser = prefs.isPremiumUser,
                        language = prefs.language
                    )
                }
            }
        }
    }

    fun toggleDarkMode(mode: String) {
        viewModelScope.launch {
            userPreferences.setDarkModePreference(mode)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotificationsEnabled(enabled)
        }
    }

    fun updateSelectedState(state: String) {
        viewModelScope.launch {
            userPreferences.setSelectedState(state)
        }
    }

    fun updateSelectedCity(city: String) {
        viewModelScope.launch {
            userPreferences.setSelectedCity(city)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            userPreferences.setLanguage(language)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            // In production, this would call FirebaseAuth.signOut()
            // and clear user session data
            userPreferences.setOnboardingCompleted(false)
        }
    }
}
