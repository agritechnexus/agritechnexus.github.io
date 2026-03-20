package com.fixmybill.app.presentation.screens.history

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
import javax.inject.Inject

enum class BillFilter {
    ALL, ELECTRICITY, WATER, GAS, OVERCHARGED
}

data class HistoryUiState(
    val isLoading: Boolean = true,
    val bills: List<Bill> = emptyList(),
    val filteredBills: List<Bill> = emptyList(),
    val selectedFilter: BillFilter = BillFilter.ALL,
    val chartData: List<Pair<String, Double>> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getBillHistoryUseCase: GetBillHistoryUseCase
) : ViewModel() {

    private val _historyState = MutableStateFlow(HistoryUiState())
    val historyState: StateFlow<HistoryUiState> = _historyState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _historyState.update { it.copy(isLoading = true, error = null) }
            try {
                val bills = getBillHistoryUseCase.getAll()
                val chartData = bills
                    .groupBy { it.periodStart.month.name.take(3) }
                    .map { (month, monthBills) ->
                        month to monthBills.sumOf { it.totalAmount }
                    }

                _historyState.update {
                    HistoryUiState(
                        isLoading = false,
                        bills = bills,
                        filteredBills = bills,
                        chartData = chartData
                    )
                }
            } catch (e: Exception) {
                _historyState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load bill history"
                    )
                }
            }
        }
    }

    fun setFilter(filter: BillFilter) {
        _historyState.update { state ->
            val filtered = when (filter) {
                BillFilter.ALL -> state.bills
                BillFilter.ELECTRICITY -> state.bills.filter { it.billType == BillType.ELECTRICITY }
                BillFilter.WATER -> state.bills.filter { it.billType == BillType.WATER }
                BillFilter.GAS -> state.bills.filter { it.billType == BillType.GAS }
                BillFilter.OVERCHARGED -> state.bills.filter { it.isOvercharged }
            }
            state.copy(selectedFilter = filter, filteredBills = filtered)
        }
    }
}
