package com.fixmybill.app.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ComplaintStatus {
    DRAFT,
    SENT,
    RESOLVED
}

@Entity(
    tableName = "complaints",
    foreignKeys = [
        ForeignKey(
            entity = BillEntity::class,
            parentColumns = ["id"],
            childColumns = ["bill_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["bill_id"])]
)
data class ComplaintEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "bill_id")
    val billId: Long,

    @ColumnInfo(name = "complaint_type")
    val complaintType: String,

    @ColumnInfo(name = "complaint_text")
    val complaintText: String,

    @ColumnInfo(name = "recipient_address")
    val recipientAddress: String,

    @ColumnInfo(name = "status")
    val status: ComplaintStatus = ComplaintStatus.DRAFT,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
