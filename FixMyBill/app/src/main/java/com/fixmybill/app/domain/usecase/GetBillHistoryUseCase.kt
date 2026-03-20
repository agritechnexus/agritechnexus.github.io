package com.fixmybill.app.domain.usecase

import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.repository.BillRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBillHistoryUseCase @Inject constructor(
    private val billRepository: BillRepository
) {

    operator fun invoke(): Flow<List<Bill>> {
        return billRepository.getAllBills()
    }

    suspend fun getRecent(limit: Int = 5): List<Bill> {
        return billRepository.getRecentBills(limit)
    }

    suspend fun getTotalSavings(): Double {
        return billRepository.getTotalSavings()
    }
}
