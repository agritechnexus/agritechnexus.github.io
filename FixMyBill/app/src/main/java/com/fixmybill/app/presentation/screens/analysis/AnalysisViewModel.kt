package com.fixmybill.app.presentation.screens.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.model.BillAnalysis
import com.fixmybill.app.domain.model.OverchargeReport
import com.fixmybill.app.domain.repository.BillRepository
import com.fixmybill.app.domain.usecase.AnalyzeBillUseCase
import com.fixmybill.app.domain.usecase.DetectOverchargeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalysisUiState(
    val isLoading: Boolean = true,
    val bill: Bill? = null,
    val overchargeReport: OverchargeReport? = null,
    val aiExplanation: String = "",
    val suggestions: List<String> = emptyList(),
    val optimizationTips: List<String> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val analyzeBillUseCase: AnalyzeBillUseCase,
    private val detectOverchargeUseCase: DetectOverchargeUseCase,
    private val billRepository: BillRepository
) : ViewModel() {

    private val _analysisState = MutableStateFlow(AnalysisUiState())
    val analysisState: StateFlow<AnalysisUiState> = _analysisState.asStateFlow()

    fun loadAnalysis(billId: Long) {
        viewModelScope.launch {
            _analysisState.update { it.copy(isLoading = true, error = null) }
            try {
                val bill = billRepository.getBillById(billId)
                if (bill == null) {
                    _analysisState.update {
                        it.copy(
                            isLoading = false,
                            error = "Bill not found with ID: $billId"
                        )
                    }
                    return@launch
                }

                val analysis: BillAnalysis = analyzeBillUseCase(bill)

                _analysisState.update {
                    AnalysisUiState(
                        isLoading = false,
                        bill = analysis.bill,
                        overchargeReport = analysis.overchargeDetails,
                        aiExplanation = analysis.explanation,
                        suggestions = analysis.suggestions,
                        optimizationTips = analysis.optimizationTips
                    )
                }
            } catch (e: Exception) {
                _analysisState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to analyze bill"
                    )
                }
            }
        }
    }
}
