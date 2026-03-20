package com.fixmybill.app.util

import com.fixmybill.app.domain.model.OverchargeDetail.Discrepancy
import com.fixmybill.app.domain.model.OverchargeDetail.OverchargeReport
import javax.inject.Inject
import kotlin.math.abs

class OverchargeDetector @Inject constructor(
    private val tariffCalculator: TariffCalculator
) {
    fun detectOvercharge(
        parsedBill: BillParsingEngine.ParsedBillData,
        utilityBoard: String
    ): OverchargeReport {
        val units = parsedBill.unitsConsumed ?: return createLowConfidenceReport(
            "Could not determine units consumed from the bill"
        )

        val billedTotal = parsedBill.totalAmount ?: return createLowConfidenceReport(
            "Could not determine total amount from the bill"
        )

        // Recalculate from scratch
        val calculated = tariffCalculator.calculateBill(units, utilityBoard)

        val discrepancies = mutableListOf<Discrepancy>()

        // Compare energy charges
        val billedEnergy = parsedBill.chargeBreakdown["Energy Charges"]
            ?: parsedBill.chargeBreakdown["Current Charges"]
            ?: parsedBill.chargeBreakdown["Consumption Charges"]

        if (billedEnergy != null) {
            val energyDiff = billedEnergy - calculated.energyCharges
            if (abs(energyDiff) > 1.0) {
                discrepancies.add(
                    Discrepancy(
                        component = "Energy Charges",
                        expected = calculated.energyCharges,
                        actual = billedEnergy,
                        difference = energyDiff
                    )
                )
            }
        }

        // Compare fixed charges
        val billedFixed = parsedBill.chargeBreakdown["Fixed Charges"]
            ?: parsedBill.chargeBreakdown["Customer Charges"]
            ?: parsedBill.chargeBreakdown["Demand Charges"]

        if (billedFixed != null) {
            val fixedDiff = billedFixed - calculated.fixedCharges
            if (abs(fixedDiff) > 1.0) {
                discrepancies.add(
                    Discrepancy(
                        component = "Fixed Charges",
                        expected = calculated.fixedCharges,
                        actual = billedFixed,
                        difference = fixedDiff
                    )
                )
            }
        }

        // Compare fuel surcharge
        val billedFuel = parsedBill.chargeBreakdown["Fuel Surcharge"]
            ?: parsedBill.chargeBreakdown["FAC"]
            ?: parsedBill.chargeBreakdown["Fuel Adjustment Charge"]

        if (billedFuel != null) {
            val fuelDiff = billedFuel - calculated.fuelSurcharge
            if (abs(fuelDiff) > 1.0) {
                discrepancies.add(
                    Discrepancy(
                        component = "Fuel Surcharge",
                        expected = calculated.fuelSurcharge,
                        actual = billedFuel,
                        difference = fuelDiff
                    )
                )
            }
        }

        // Compare electricity duty
        val billedDuty = parsedBill.chargeBreakdown["Electricity Duty"]
            ?: parsedBill.chargeBreakdown["ED"]
            ?: parsedBill.chargeBreakdown["Tax"]

        if (billedDuty != null) {
            val dutyDiff = billedDuty - calculated.electricityDuty
            if (abs(dutyDiff) > 1.0) {
                discrepancies.add(
                    Discrepancy(
                        component = "Electricity Duty",
                        expected = calculated.electricityDuty,
                        actual = billedDuty,
                        difference = dutyDiff
                    )
                )
            }
        }

        // Overall total comparison
        val totalDifference = billedTotal - calculated.totalAmount
        val totalThreshold = calculated.totalAmount * 0.02 // 2% tolerance

        if (abs(totalDifference) > totalThreshold && discrepancies.isEmpty()) {
            discrepancies.add(
                Discrepancy(
                    component = "Total Amount",
                    expected = calculated.totalAmount,
                    actual = billedTotal,
                    difference = totalDifference
                )
            )
        }

        val isOvercharged = totalDifference > totalThreshold
        val overchargeAmount = if (isOvercharged) totalDifference else 0.0

        // Calculate confidence
        val confidence = calculateConfidence(parsedBill, discrepancies)

        // Generate suggestions
        val suggestions = generateSuggestions(discrepancies, utilityBoard, isOvercharged)

        return OverchargeReport(
            isOvercharged = isOvercharged,
            overchargeAmount = overchargeAmount,
            discrepancies = discrepancies,
            confidence = confidence,
            suggestions = suggestions
        )
    }

    private fun calculateConfidence(
        parsedBill: BillParsingEngine.ParsedBillData,
        discrepancies: List<Discrepancy>
    ): Float {
        var confidence = parsedBill.overallConfidence

        // Higher confidence if we have more data points to compare
        val availableComponents = parsedBill.chargeBreakdown.size
        if (availableComponents >= 4) confidence += 0.1f
        if (availableComponents >= 6) confidence += 0.05f

        // Lower confidence if many discrepancies (might indicate OCR issues)
        if (discrepancies.size > 3) confidence -= 0.15f

        // Lower confidence if discrepancy is very large (might be OCR error)
        discrepancies.forEach { d ->
            if (d.expected > 0 && abs(d.difference / d.expected) > 0.5) {
                confidence -= 0.1f
            }
        }

        return confidence.coerceIn(0.1f, 0.99f)
    }

    private fun generateSuggestions(
        discrepancies: List<Discrepancy>,
        utilityBoard: String,
        isOvercharged: Boolean
    ): List<String> {
        val suggestions = mutableListOf<String>()

        if (!isOvercharged) {
            suggestions.add("Your bill appears to be calculated correctly based on the applicable tariff rates.")
            return suggestions
        }

        discrepancies.forEach { d ->
            when {
                d.component.contains("Energy", ignoreCase = true) -> {
                    suggestions.add("Energy charges appear incorrect. Your utility may have applied the wrong tariff slab or category.")
                    suggestions.add("Verify that your connection is classified as 'Domestic' and not 'Commercial'.")
                }
                d.component.contains("Fixed", ignoreCase = true) -> {
                    suggestions.add("Fixed charges don't match the applicable slab. Check if your sanctioned load is correctly recorded.")
                }
                d.component.contains("Fuel", ignoreCase = true) -> {
                    suggestions.add("Fuel surcharge seems higher than the approved rate. Cross-check with the latest SERC order.")
                }
                d.component.contains("Duty", ignoreCase = true) -> {
                    suggestions.add("Electricity duty percentage appears incorrect. The applicable rate should be based on your state's regulation.")
                }
                d.component.contains("Total", ignoreCase = true) -> {
                    suggestions.add("Total amount doesn't match the sum of individual components. There may be hidden charges or calculation errors.")
                }
            }
        }

        suggestions.add("File a complaint with $utilityBoard citing Electricity Act 2003, Section 56.")
        suggestions.add("If unresolved within 15 days, escalate to the Consumer Grievance Redressal Forum.")

        return suggestions
    }

    private fun createLowConfidenceReport(reason: String): OverchargeReport {
        return OverchargeReport(
            isOvercharged = false,
            overchargeAmount = 0.0,
            discrepancies = emptyList(),
            confidence = 0.2f,
            suggestions = listOf(
                reason,
                "Try scanning the bill again with better lighting and alignment."
            )
        )
    }
}
