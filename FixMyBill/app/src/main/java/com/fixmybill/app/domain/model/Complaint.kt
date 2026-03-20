package com.fixmybill.app.domain.model

import java.time.LocalDateTime

enum class ComplaintType {
    UTILITY_COMPANY,
    CONSUMER_FORUM,
    RTI
}

enum class ComplaintStatus {
    DRAFT,
    SENT,
    RESOLVED
}

data class Complaint(
    val id: Long = 0,
    val billId: Long,
    val complaintType: ComplaintType,
    val complaintText: String,
    val recipientAddress: String,
    val status: ComplaintStatus = ComplaintStatus.DRAFT,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
