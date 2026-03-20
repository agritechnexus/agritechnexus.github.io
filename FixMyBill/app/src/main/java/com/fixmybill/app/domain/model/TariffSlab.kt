package com.fixmybill.app.domain.model

data class TariffSlab(
    val id: Long = 0,
    val utilityBoard: String,
    val state: String,
    val category: String,
    val slabStart: Int,
    val slabEnd: Int,
    val ratePerUnit: Double,
    val fixedCharge: Double
) {
    fun unitsInSlab(totalUnits: Int): Int {
        if (totalUnits <= slabStart) return 0
        val effectiveEnd = if (slabEnd == Int.MAX_VALUE) totalUnits else slabEnd
        return (minOf(totalUnits, effectiveEnd) - slabStart).coerceAtLeast(0)
    }

    fun chargeForUnits(totalUnits: Int): Double {
        val units = unitsInSlab(totalUnits)
        return units * ratePerUnit
    }
}
