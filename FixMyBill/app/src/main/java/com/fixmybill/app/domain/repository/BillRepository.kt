package com.fixmybill.app.domain.repository

import com.fixmybill.app.domain.model.Bill
import kotlinx.coroutines.flow.Flow

interface BillRepository {
    suspend fun insertBill(bill: Bill): Long
    suspend fun updateBill(bill: Bill)
    suspend fun deleteBill(bill: Bill)
    fun getAllBills(): Flow<List<Bill>>
    suspend fun getBillById(id: Long): Bill?
    suspend fun getRecentBills(limit: Int): List<Bill>
    suspend fun getTotalSavings(): Double
}
