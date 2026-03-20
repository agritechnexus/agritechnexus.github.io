package com.fixmybill.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.model.BillType
import com.fixmybill.app.domain.usecase.GetBillHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val totalSavings: Double = 0.0,
    val recentBills: List<Bill> = emptyList(),
    val billCount: Int = 0,
    val monthlyAverage: Double = 0.0,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBillHistoryUseCase: GetBillHistoryUseCase
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _homeState.update { it.copy(isLoading = true, error = null) }
            try {
                val recentBills = getBillHistoryUseCase.getRecent(5)
                val totalSavings = getBillHistoryUseCase.getTotalSavings()

                if (recentBills.isEmpty()) {
                    // Use mock data for demonstration when no bills exist
                    val mockBills = generateMockBills()
                    _homeState.update {
                        HomeUiState(
                            isLoading = false,
                            totalSavings = 2450.0,
                            recentBills = mockBills,
                            billCount = mockBills.size,
                            monthlyAverage = 1850.0
                        )
                    }
                } else {
                    val average = if (recentBills.isNotEmpty()) {
                        recentBills.sumOf { it.totalAmount } / recentBills.size
                    } else 0.0

                    _homeState.update {
                        HomeUiState(
                            isLoading = false,
                            totalSavings = totalSavings,
                            recentBills = recentBills,
                            billCount = recentBills.size,
                            monthlyAverage = average
                        )
                    }
                }
            } catch (e: Exception) {
                _homeState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load dashboard"
                    )
                }
            }
        }
    }

    private fun generateMockBills(): List<Bill> {
        return listOf(
            Bill(
                id = 1,
                consumerNumber = "MH-1234567890",
                billingPeriod = Pair(
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 1, 31)
                ),
                meterReadings = Pair(45000L, 45350L),
                unitsConsumed = 350,
                totalAmount = 2150.0,
                billType = BillType.ELECTRICITY,
                utilityProvider = "MSEDCL",
                state = "Maharashtra",
                isOvercharged = true,
                overchargeAmount = 320.0,
                confidence = 0.85f,
                createdAt = LocalDateTime.of(2026, 2, 5, 10, 30)
            ),
            Bill(
                id = 2,
                consumerNumber = "DL-9876543210",
                billingPeriod = Pair(
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 1, 31)
                ),
                meterReadings = Pair(1200L, 1240L),
                unitsConsumed = 40,
                totalAmount = 680.0,
                billType = BillType.WATER,
                utilityProvider = "Delhi Jal Board",
                state = "Delhi",
                isOvercharged = false,
                overchargeAmount = 0.0,
                confidence = 0.75f,
                createdAt = LocalDateTime.of(2026, 2, 10, 14, 0)
            ),
            Bill(
                id = 3,
                consumerNumber = "KA-5555555555",
                billingPeriod = Pair(
                    LocalDate.of(2026, 2, 1),
                    LocalDate.of(2026, 2, 28)
                ),
                meterReadings = Pair(8900L, 9120L),
                unitsConsumed = 220,
                totalAmount = 1520.0,
                billType = BillType.ELECTRICITY,
                utilityProvider = "BESCOM",
                state = "Karnataka",
                isOvercharged = true,
                overchargeAmount = 180.0,
                confidence = 0.78f,
                createdAt = LocalDateTime.of(2026, 3, 1, 9, 15)
            )
        )
    }
}
