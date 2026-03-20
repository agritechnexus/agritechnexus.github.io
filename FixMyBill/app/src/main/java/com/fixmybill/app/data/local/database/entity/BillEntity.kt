package com.fixmybill.app.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BillType {
    ELECTRICITY,
    WATER,
    GAS
}

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "consumer_number")
    val consumerNumber: String,

    @ColumnInfo(name = "billing_period_start")
    val billingPeriodStart: Long,

    @ColumnInfo(name = "billing_period_end")
    val billingPeriodEnd: Long,

    @ColumnInfo(name = "previous_reading")
    val previousReading: Double,

    @ColumnInfo(name = "current_reading")
    val currentReading: Double,

    @ColumnInfo(name = "units_consumed")
    val unitsConsumed: Double,

    @ColumnInfo(name = "total_amount")
    val totalAmount: Double,

    @ColumnInfo(name = "bill_type")
    val billType: BillType,

    @ColumnInfo(name = "utility_provider")
    val utilityProvider: String,

    @ColumnInfo(name = "state")
    val state: String,

    @ColumnInfo(name = "image_path")
    val imagePath: String? = null,

    @ColumnInfo(name = "ocr_text")
    val ocrText: String? = null,

    @ColumnInfo(name = "is_overcharged")
    val isOvercharged: Boolean = false,

    @ColumnInfo(name = "overcharge_amount")
    val overchargeAmount: Double = 0.0,

    @ColumnInfo(name = "confidence")
    val confidence: Float = 0f,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "synced_to_cloud")
    val syncedToCloud: Boolean = false
)
