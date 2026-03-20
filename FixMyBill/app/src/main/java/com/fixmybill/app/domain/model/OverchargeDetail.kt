package com.fixmybill.app.domain.model

data class OverchargeReport(
    val isOvercharged: Boolean,
    val overchargeAmount: Double,
    val discrepancies: List<Discrepancy>,
    val confidence: Float,
    val suggestions: List<String>
)

data class Discrepancy(
    val component: String,
    val expected: Double,
    val actual: Double,
    val difference: Double
)
