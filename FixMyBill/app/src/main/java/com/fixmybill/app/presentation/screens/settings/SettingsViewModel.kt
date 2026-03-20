package com.fixmybill.app.presentation.screens.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SettingsUiState(
    val userName: String = "",
    val userPhone: String = "",
    val selectedState: String = "Maharashtra",
    val selectedCity: String = "Mumbai",
    val isDarkMode: Boolean = false,
    val isNotificationsEnabled: Boolean = true,
    val isPremium: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsUiState())
    val settingsState: StateFlow<SettingsUiState> = _settingsState.asStateFlow()

    fun setState(state: String) {
        _settingsState.update { it.copy(selectedState = state) }
    }

    fun setCity(city: String) {
        _settingsState.update { it.copy(selectedCity = city) }
    }

    fun toggleDarkMode() {
        _settingsState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun toggleNotifications() {
        _settingsState.update { it.copy(isNotificationsEnabled = !it.isNotificationsEnabled) }
    }

    fun signOut() {
        // Sign out logic
    }
}
