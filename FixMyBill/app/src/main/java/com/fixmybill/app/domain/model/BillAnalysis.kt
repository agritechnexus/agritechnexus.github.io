package com.fixmybill.app.domain.model

data class BillAnalysis(
    val bill: Bill,
    val explanation: String,
    val overchargeDetails: OverchargeReport?,
    val suggestions: List<String>,
    val optimizationTips: List<String>
)
