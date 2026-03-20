package com.fixmybill.app.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixmybill.app.data.local.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    companion object {
        const val TOTAL_PAGES = 3
    }

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _onboardingComplete = MutableStateFlow(false)
    val onboardingComplete: StateFlow<Boolean> = _onboardingComplete.asStateFlow()

    fun onNextPage() {
        val next = _currentPage.value + 1
        if (next < TOTAL_PAGES) {
            _currentPage.value = next
        } else {
            onComplete()
        }
    }

    fun onPreviousPage() {
        val prev = _currentPage.value - 1
        if (prev >= 0) {
            _currentPage.value = prev
        }
    }

    fun onComplete() {
        viewModelScope.launch {
            userPreferences.setOnboardingCompleted(true)
            _onboardingComplete.value = true
        }
    }
}
