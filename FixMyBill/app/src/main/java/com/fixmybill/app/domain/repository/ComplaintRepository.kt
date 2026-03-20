package com.fixmybill.app.domain.repository

import com.fixmybill.app.domain.model.Complaint
import kotlinx.coroutines.flow.Flow

interface ComplaintRepository {
    suspend fun insertComplaint(complaint: Complaint): Long
    suspend fun updateComplaint(complaint: Complaint)
    suspend fun getComplaintsByBillId(billId: Long): List<Complaint>
    fun getAllComplaints(): Flow<List<Complaint>>
}
