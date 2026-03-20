package com.fixmybill.app.data.repository

import com.fixmybill.app.data.local.database.dao.ComplaintDao
import com.fixmybill.app.data.local.database.entity.ComplaintEntity
import com.fixmybill.app.data.local.database.entity.ComplaintStatus as EntityComplaintStatus
import com.fixmybill.app.domain.model.Complaint
import com.fixmybill.app.domain.model.ComplaintStatus
import com.fixmybill.app.domain.model.ComplaintType
import com.fixmybill.app.domain.repository.ComplaintRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComplaintRepositoryImpl @Inject constructor(
    private val complaintDao: ComplaintDao
) : ComplaintRepository {

    override suspend fun insertComplaint(complaint: Complaint): Long {
        return complaintDao.insert(complaint.toEntity())
    }

    override suspend fun updateComplaint(complaint: Complaint) {
        complaintDao.update(complaint.toEntity())
    }

    override suspend fun getComplaintsByBillId(billId: Long): List<Complaint> {
        return complaintDao.getComplaintsByBillId(billId).first().map { it.toDomain() }
    }

    override fun getAllComplaints(): Flow<List<Complaint>> {
        return complaintDao.getAllComplaints().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun Complaint.toEntity(): ComplaintEntity {
        return ComplaintEntity(
            id = id,
            billId = billId,
            complaintType = complaintType.name,
            complaintText = complaintText,
            recipientAddress = recipientAddress,
            status = when (status) {
                ComplaintStatus.DRAFT -> EntityComplaintStatus.DRAFT
                ComplaintStatus.SENT -> EntityComplaintStatus.SENT
                ComplaintStatus.RESOLVED -> EntityComplaintStatus.RESOLVED
            },
            createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }

    private fun ComplaintEntity.toDomain(): Complaint {
        return Complaint(
            id = id,
            billId = billId,
            complaintType = try {
                ComplaintType.valueOf(complaintType)
            } catch (e: IllegalArgumentException) {
                ComplaintType.UTILITY_COMPANY
            },
            complaintText = complaintText,
            recipientAddress = recipientAddress,
            status = when (status) {
                EntityComplaintStatus.DRAFT -> ComplaintStatus.DRAFT
                EntityComplaintStatus.SENT -> ComplaintStatus.SENT
                EntityComplaintStatus.RESOLVED -> ComplaintStatus.RESOLVED
            },
            createdAt = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()).toLocalDateTime()
        )
    }
}
