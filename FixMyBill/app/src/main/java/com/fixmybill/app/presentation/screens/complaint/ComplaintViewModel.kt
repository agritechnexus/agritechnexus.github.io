package com.fixmybill.app.presentation.screens.complaint

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixmybill.app.domain.model.ComplaintType
import com.fixmybill.app.domain.repository.BillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ComplaintUiState(
    val isLoading: Boolean = true,
    val selectedType: ComplaintType = ComplaintType.UTILITY_COMPANY,
    val complaintText: String = "",
    val error: String? = null
)

@HiltViewModel
class ComplaintViewModel @Inject constructor(
    private val billRepository: BillRepository
) : ViewModel() {

    private val _complaintState = MutableStateFlow(ComplaintUiState())
    val complaintState: StateFlow<ComplaintUiState> = _complaintState.asStateFlow()

    private var billId: Long = 0

    fun loadComplaint(billId: Long) {
        this.billId = billId
        viewModelScope.launch {
            _complaintState.update { it.copy(isLoading = true, error = null) }
            try {
                val bill = billRepository.getBillById(billId)
                if (bill == null) {
                    _complaintState.update {
                        it.copy(isLoading = false, error = "Bill not found")
                    }
                    return@launch
                }

                val text = generateComplaintText(
                    _complaintState.value.selectedType,
                    bill.utilityProvider,
                    bill.consumerNumber,
                    bill.totalAmount,
                    bill.overchargeAmount,
                    bill.state
                )

                _complaintState.update {
                    it.copy(isLoading = false, complaintText = text)
                }
            } catch (e: Exception) {
                _complaintState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to generate complaint")
                }
            }
        }
    }

    fun setComplaintType(type: ComplaintType) {
        _complaintState.update { it.copy(selectedType = type) }
        loadComplaint(billId)
    }

    fun downloadAsPdf(context: Context) {
        // PDF generation would be implemented here
    }

    private fun generateComplaintText(
        type: ComplaintType,
        provider: String,
        consumerNumber: String,
        totalAmount: Double,
        overchargeAmount: Double,
        state: String
    ): String {
        return when (type) {
            ComplaintType.UTILITY_COMPANY -> """
                |To,
                |The Billing Department
                |$provider
                |$state
                |
                |Subject: Complaint Regarding Overbilling - Consumer No. $consumerNumber
                |
                |Dear Sir/Madam,
                |
                |I am writing to bring to your attention a billing discrepancy in my recent utility bill. Upon careful analysis of my bill, I have identified that I have been overcharged by an amount of Rs. ${String.format("%,.2f", overchargeAmount)}.
                |
                |Details:
                |Consumer Number: $consumerNumber
                |Total Amount Billed: Rs. ${String.format("%,.2f", totalAmount)}
                |Estimated Overcharge: Rs. ${String.format("%,.2f", overchargeAmount)}
                |
                |I request you to kindly review my bill, rectify the error, and issue a corrected bill at the earliest. If the overcharge is confirmed, I request a refund or adjustment in my next billing cycle.
                |
                |I look forward to a prompt resolution of this matter.
                |
                |Thanking you,
                |[Your Name]
                |[Your Address]
                |[Your Phone Number]
            """.trimMargin()

            ComplaintType.CONSUMER_FORUM -> """
                |BEFORE THE DISTRICT CONSUMER DISPUTES REDRESSAL FORUM
                |$state
                |
                |CONSUMER COMPLAINT
                |
                |Complainant: [Your Name]
                |Address: [Your Address]
                |
                |Opposite Party: $provider
                |Address: [Provider Address]
                |
                |Subject: Deficiency in Service - Overbilling
                |Consumer Number: $consumerNumber
                |
                |FACTS OF THE CASE:
                |
                |1. The Complainant is a consumer of the Opposite Party's utility services bearing Consumer No. $consumerNumber.
                |
                |2. The Complainant received a bill for Rs. ${String.format("%,.2f", totalAmount)} which upon analysis was found to contain an overcharge of Rs. ${String.format("%,.2f", overchargeAmount)}.
                |
                |3. Despite bringing this to the attention of the Opposite Party, no corrective action has been taken, which amounts to deficiency in service and unfair trade practice.
                |
                |PRAYER:
                |
                |The Complainant prays that this Forum may:
                |a) Direct the Opposite Party to refund the overcharged amount of Rs. ${String.format("%,.2f", overchargeAmount)}.
                |b) Award compensation for mental agony and harassment.
                |c) Award costs of this complaint.
                |
                |Date: [Date]
                |Place: $state
                |
                |[Your Name]
                |Complainant
            """.trimMargin()

            ComplaintType.RTI -> """
                |To,
                |The Public Information Officer
                |$provider
                |$state
                |
                |Subject: Application under Right to Information Act, 2005
                |
                |Sir/Madam,
                |
                |I, [Your Name], resident of [Your Address], hereby seek the following information under the Right to Information Act, 2005:
                |
                |1. The applicable tariff slab rates for domestic consumers in $state for the current financial year.
                |
                |2. The detailed calculation sheet for bill generated against Consumer Number $consumerNumber showing the total amount of Rs. ${String.format("%,.2f", totalAmount)}.
                |
                |3. The meter reading records for the said consumer number for the last 12 months.
                |
                |4. Any circulars or orders regarding revision of tariff rates in the last 6 months.
                |
                |5. The grievance redressal mechanism available for consumers who suspect billing errors.
                |
                |I am enclosing the prescribed fee of Rs. 10/- via [payment method].
                |
                |Date: [Date]
                |Place: $state
                |
                |[Your Name]
                |[Your Address]
                |[Your Phone Number]
            """.trimMargin()
        }
    }
}
