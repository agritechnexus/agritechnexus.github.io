package com.fixmybill.app.domain.usecase

import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.model.BillAnalysis
import com.fixmybill.app.domain.model.OverchargeReport
import com.fixmybill.app.domain.repository.TariffRepository
import javax.inject.Inject

class AnalyzeBillUseCase @Inject constructor(
    private val tariffRepository: TariffRepository,
    private val detectOverchargeUseCase: DetectOverchargeUseCase
) {

    suspend operator fun invoke(bill: Bill): BillAnalysis {
        val tariffs = tariffRepository.getTariffsByBoard(bill.utilityProvider, bill.state)
        val overchargeReport = detectOverchargeUseCase(bill, tariffs)

        val explanation = buildExplanation(bill, overchargeReport)
        val suggestions = buildSuggestions(bill, overchargeReport)
        val optimizationTips = buildOptimizationTips(bill)

        return BillAnalysis(
            bill = bill,
            explanation = explanation,
            overchargeDetails = overchargeReport,
            suggestions = suggestions,
            optimizationTips = optimizationTips
        )
    }

    private fun buildExplanation(bill: Bill, report: OverchargeReport): String {
        val sb = StringBuilder()
        sb.appendLine("Bill Analysis for ${bill.billType.name} bill")
        sb.appendLine("Provider: ${bill.utilityProvider}")
        sb.appendLine("Billing Period: ${bill.periodStart} to ${bill.periodEnd}")
        sb.appendLine("Units Consumed: ${bill.unitsConsumed}")
        sb.appendLine("Total Amount Billed: ₹${"%.2f".format(bill.totalAmount)}")
        sb.appendLine()

        if (report.isOvercharged) {
            sb.appendLine("⚠ Potential overcharge detected: ₹${"%.2f".format(report.overchargeAmount)}")
            sb.appendLine("Confidence: ${"%.1f".format(report.confidence * 100)}%")
            sb.appendLine()
            sb.appendLine("Discrepancies found:")
            report.discrepancies.forEach { d ->
                sb.appendLine("  - ${d.component}: Expected ₹${"%.2f".format(d.expected)}, " +
                        "Billed ₹${"%.2f".format(d.actual)}, " +
                        "Difference ₹${"%.2f".format(d.difference)}")
            }
        } else {
            sb.appendLine("✓ No overcharge detected. Your bill appears to be correctly calculated.")
        }

        return sb.toString()
    }

    private fun buildSuggestions(bill: Bill, report: OverchargeReport): List<String> {
        val suggestions = mutableListOf<String>()

        if (report.isOvercharged) {
            suggestions.add("File a complaint with ${bill.utilityProvider} regarding the overcharge of ₹${"%.2f".format(report.overchargeAmount)}.")
            suggestions.add("Keep a copy of this bill and the analysis as evidence.")
            if (report.overchargeAmount > 500) {
                suggestions.add("Consider filing a complaint with the Consumer Forum for significant overcharge.")
            }
            if (report.overchargeAmount > 1000) {
                suggestions.add("You may also file an RTI application to get detailed tariff calculation records.")
            }
        }

        suggestions.addAll(report.suggestions)
        return suggestions
    }

    private fun buildOptimizationTips(bill: Bill): List<String> {
        val tips = mutableListOf<String>()

        when (bill.billType) {
            com.fixmybill.app.domain.model.BillType.ELECTRICITY -> {
                if (bill.unitsConsumed > 500) {
                    tips.add("Your consumption is high. Consider switching to energy-efficient appliances.")
                    tips.add("Use LED lighting to reduce electricity consumption by up to 75%.")
                }
                if (bill.unitsConsumed > 300) {
                    tips.add("Set AC temperature to 24°C for optimal energy savings.")
                    tips.add("Use a smart power strip to eliminate phantom loads from standby devices.")
                }
                tips.add("Consider installing a solar rooftop system for long-term savings.")
                tips.add("Shift heavy appliance usage to off-peak hours if your provider offers time-of-day tariffs.")
            }
            com.fixmybill.app.domain.model.BillType.WATER -> {
                if (bill.unitsConsumed > 30) {
                    tips.add("Your water consumption is above average. Check for leaks in your plumbing.")
                }
                tips.add("Install low-flow faucets and showerheads to reduce water usage.")
                tips.add("Collect rainwater for gardening and non-potable uses.")
            }
            com.fixmybill.app.domain.model.BillType.GAS -> {
                tips.add("Ensure your gas appliances are serviced regularly for optimal efficiency.")
                tips.add("Use pressure cookers to reduce gas consumption by up to 70%.")
                tips.add("Keep burner flames blue — yellow flames indicate incomplete combustion and wasted gas.")
            }
        }

        return tips
    }
}
