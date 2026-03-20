package com.fixmybill.app.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

enum class BillType {
    ELECTRICITY,
    WATER,
    GAS
}

data class Bill(
    val id: Long = 0,
    val consumerNumber: String,
    val billingPeriod: Pair<LocalDate, LocalDate>,
    val meterReadings: Pair<Long, Long>,
    val unitsConsumed: Int,
    val totalAmount: Double,
    val billType: BillType,
    val utilityProvider: String,
    val state: String,
    val imagePath: String? = null,
    val isOvercharged: Boolean = false,
    val overchargeAmount: Double = 0.0,
    val confidence: Float = 0f,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    val previousReading: Long get() = meterReadings.first
    val currentReading: Long get() = meterReadings.second
    val periodStart: LocalDate get() = billingPeriod.first
    val periodEnd: LocalDate get() = billingPeriod.second
}
