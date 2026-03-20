package com.fixmybill.app.presentation.screens.complaint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.model.BillType
import com.fixmybill.app.domain.model.Complaint
import com.fixmybill.app.domain.model.ComplaintType
import com.fixmybill.app.domain.model.OverchargeReport
import com.fixmybill.app.domain.repository.BillRepository
import com.fixmybill.app.domain.usecase.GenerateComplaintUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

sealed class ComplaintState {
    data object Idle : ComplaintState()
    data object Loading : ComplaintState()
    data class Generated(val complaint: Complaint) : ComplaintState()
    data class Sent(val complaint: Complaint) : ComplaintState()
    data class Error(val message: String) : ComplaintState()
}

data class ComplaintUiState(
    val complaintState: ComplaintState = ComplaintState.Idle,
    val bill: Bill? = null,
    val overchargeReport: OverchargeReport? = null,
    val selectedComplaintType: ComplaintType = ComplaintType.UTILITY_COMPANY,
    val availableTypes: List<ComplaintType> = ComplaintType.entries
)

@HiltViewModel
class ComplaintViewModel @Inject constructor(
    private val generateComplaintUseCase: GenerateComplaintUseCase,
    private val billRepository: BillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComplaintUiState())
    val uiState: StateFlow<ComplaintUiState> = _uiState.asStateFlow()

    fun loadBill(billId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(complaintState = ComplaintState.Loading) }
            try {
                val bill = billRepository.getBillById(billId)
                if (bill != null) {
                    _uiState.update {
                        it.copy(
                            complaintState = ComplaintState.Idle,
                            bill = bill
                        )
                    }
                } else {
                    // Use mock bill for demonstration
                    val mockBill = generateMockBill(billId)
                    _uiState.update {
                        it.copy(
                            complaintState = ComplaintState.Idle,
                            bill = mockBill
                        )
                    }
                }
            } catch (e: Exception) {
                // Fallback to mock bill
                val mockBill = generateMockBill(billId)
                _uiState.update {
                    it.copy(
                        complaintState = ComplaintState.Idle,
                        bill = mockBill
                    )
                }
            }
        }
    }

    fun selectComplaintType(type: ComplaintType) {
        _uiState.update { it.copy(selectedComplaintType = type) }
    }

    fun generateComplaint() {
        val bill = _uiState.value.bill ?: return
        val complaintType = _uiState.value.selectedComplaintType

        viewModelScope.launch {
            _uiState.update { it.copy(complaintState = ComplaintState.Loading) }
            try {
                val complaint = generateComplaintUseCase(
                    bill = bill,
                    complaintType = complaintType,
                    overchargeReport = _uiState.value.overchargeReport
                )
                _uiState.update {
                    it.copy(complaintState = ComplaintState.Generated(complaint))
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        complaintState = ComplaintState.Error(
                            message = e.message ?: "Failed to generate complaint"
                        )
                    )
                }
            }
        }
    }

    fun markAsSent() {
        val currentState = _uiState.value.complaintState
        if (currentState is ComplaintState.Generated) {
            _uiState.update {
                it.copy(complaintState = ComplaintState.Sent(currentState.complaint))
            }
        }
    }

    fun resetState() {
        _uiState.update {
            it.copy(complaintState = ComplaintState.Idle)
        }
    }

    private fun generateMockBill(billId: Long): Bill {
        return Bill(
            id = billId,
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
        )
    }
}
