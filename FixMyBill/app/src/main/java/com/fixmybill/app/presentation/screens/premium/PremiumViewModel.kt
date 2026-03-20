package com.fixmybill.app.presentation.screens.premium

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class PremiumUiState(
    val isPremium: Boolean = false,
    val currentPlan: String = "free",
    val isLoading: Boolean = false
)

@HiltViewModel
class PremiumViewModel @Inject constructor() : ViewModel() {

    private val _premiumState = MutableStateFlow(PremiumUiState())
    val premiumState: StateFlow<PremiumUiState> = _premiumState.asStateFlow()

    fun subscribe(plan: String) {
        // Google Play billing integration would go here
    }
}
