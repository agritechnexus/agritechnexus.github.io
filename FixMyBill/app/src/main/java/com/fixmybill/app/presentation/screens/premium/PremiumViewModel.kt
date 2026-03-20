package com.fixmybill.app.presentation.screens.premium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixmybill.app.data.local.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PremiumPlan(
    val id: String,
    val name: String,
    val price: Double,
    val billingPeriod: String,
    val features: List<String>,
    val isPopular: Boolean = false
)

sealed class PurchaseState {
    data object Idle : PurchaseState()
    data object Processing : PurchaseState()
    data class Success(val planId: String) : PurchaseState()
    data class Error(val message: String) : PurchaseState()
}

data class PremiumUiState(
    val isLoading: Boolean = true,
    val isPremiumUser: Boolean = false,
    val plans: List<PremiumPlan> = emptyList(),
    val selectedPlanId: String? = null,
    val purchaseState: PurchaseState = PurchaseState.Idle
)

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _premiumState = MutableStateFlow(PremiumUiState())
    val premiumState: StateFlow<PremiumUiState> = _premiumState.asStateFlow()

    init {
        loadPlans()
    }

    private fun loadPlans() {
        viewModelScope.launch {
            userPreferences.userPreferencesFlow.collect { prefs ->
                _premiumState.update {
                    PremiumUiState(
                        isLoading = false,
                        isPremiumUser = prefs.isPremiumUser,
                        plans = getAvailablePlans(),
                        selectedPlanId = null,
                        purchaseState = PurchaseState.Idle
                    )
                }
            }
        }
    }

    fun selectPlan(planId: String) {
        _premiumState.update { it.copy(selectedPlanId = planId) }
    }

    fun purchasePlan() {
        val planId = _premiumState.value.selectedPlanId ?: return

        viewModelScope.launch {
            _premiumState.update { it.copy(purchaseState = PurchaseState.Processing) }
            try {
                // Mock implementation - simulate purchase flow
                delay(2000)
                // In production, this would integrate with Google Play Billing
                userPreferences.setPremiumUser(true)
                _premiumState.update {
                    it.copy(
                        isPremiumUser = true,
                        purchaseState = PurchaseState.Success(planId)
                    )
                }
            } catch (e: Exception) {
                _premiumState.update {
                    it.copy(
                        purchaseState = PurchaseState.Error(
                            message = e.message ?: "Purchase failed. Please try again."
                        )
                    )
                }
            }
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            _premiumState.update { it.copy(purchaseState = PurchaseState.Processing) }
            try {
                // Mock implementation - simulate restore
                delay(1500)
                // In production, this would query Google Play Billing for existing purchases
                _premiumState.update {
                    it.copy(purchaseState = PurchaseState.Idle)
                }
            } catch (e: Exception) {
                _premiumState.update {
                    it.copy(
                        purchaseState = PurchaseState.Error(
                            message = e.message ?: "Failed to restore purchases"
                        )
                    )
                }
            }
        }
    }

    fun resetPurchaseState() {
        _premiumState.update { it.copy(purchaseState = PurchaseState.Idle) }
    }

    private fun getAvailablePlans(): List<PremiumPlan> {
        return listOf(
            PremiumPlan(
                id = "monthly",
                name = "Monthly",
                price = 99.0,
                billingPeriod = "per month",
                features = listOf(
                    "Unlimited bill scans",
                    "AI-powered analysis",
                    "Complaint letter generation",
                    "Email support"
                )
            ),
            PremiumPlan(
                id = "yearly",
                name = "Yearly",
                price = 799.0,
                billingPeriod = "per year",
                features = listOf(
                    "Unlimited bill scans",
                    "AI-powered analysis",
                    "Complaint letter generation",
                    "Priority support",
                    "Bill prediction",
                    "Export reports"
                ),
                isPopular = true
            ),
            PremiumPlan(
                id = "lifetime",
                name = "Lifetime",
                price = 1999.0,
                billingPeriod = "one-time",
                features = listOf(
                    "All Yearly features",
                    "Lifetime access",
                    "Early access to new features",
                    "Dedicated support",
                    "Family sharing (up to 5 members)"
                )
            )
        )
    }
}
