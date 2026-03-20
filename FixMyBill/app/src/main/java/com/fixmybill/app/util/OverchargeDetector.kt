package com.fixmybill.app.util

import com.fixmybill.app.domain.model.Discrepancy
import com.fixmybill.app.domain.model.OverchargeReport
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Compares an OCR-parsed bill against the expected amounts calculated from
 * official tariff rates, and produces a detailed overcharge report.
 *
 * The detector compares every available charge component individually (energy,
 * fixed, fuel surcharge, electricity duty) as well as the overall total, so
 * that the user gets a precise breakdown of where their bill deviates.
 */
@Singleton
class OverchargeDetector @Inject constructor(
    private val tariffCalculator: TariffCalculator
) {

    companion object {
        /** Absolute tolerance (Rs) for individual component comparison. */
        private const val COMPONENT_TOLERANCE = 1.0

        /** Relative tolerance (fraction) for total amount comparison. */
        private const val TOTAL_RELATIVE_TOLERANCE = 0.02  // 2%
    }

    // ======================== Public API ========================

    /**
     * Detects overcharges by recalculating the bill from scratch and comparing
     * each component against the values parsed from the OCR text.
     *
     * @param parsedBill The structured data extracted by [BillParsingEngine].
     * @param utilityBoard The electricity board code (e.g. "TSSPDCL", "BESCOM").
     * @return An [OverchargeReport] with discrepancies, confidence, and suggestions.
     */
    fun detectOvercharge(
        parsedBill: BillParsingEngine.ParsedBillData,
        utilityBoard: String
    ): OverchargeReport {
        // Guard: need units to recalculate
        val units = parsedBill.unitsConsumed ?: return createLowConfidenceReport(
            "Could not determine units consumed from the bill"
        )

        // Guard: need total amount to compare
        val billedTotal = parsedBill.totalAmount ?: return createLowConfidenceReport(
            "Could not determine total amount from the bill"
        )

        // Recalculate expected bill from tariff database
        val calculated = tariffCalculator.calculateBill(units, utilityBoard)

        val discrepancies = mutableListOf<Discrepancy>()

        // --- Compare energy charges ---
        compareComponent(
            componentName = "Energy Charges",
            billedValue = findBilledComponent(
                parsedBill.chargeBreakdown,
                "Energy Charges", "Current Charges", "Consumption Charges", "Slab Charges"
            ),
            expectedValue = calculated.energyCharges,
            discrepancies = discrepancies
        )

        // --- Compare fixed charges ---
        compareComponent(
            componentName = "Fixed Charges",
            billedValue = findBilledComponent(
                parsedBill.chargeBreakdown,
                "Fixed Charges", "Customer Charges", "Demand Charges", "Monthly Minimum Charges"
            ),
            expectedValue = calculated.fixedCharges,
            discrepancies = discrepancies
        )

        // --- Compare fuel surcharge ---
        compareComponent(
            componentName = "Fuel Surcharge",
            billedValue = findBilledComponent(
                parsedBill.chargeBreakdown,
                "Fuel Surcharge", "FAC", "Fuel Adjustment Charge", "FSA", "FPPCA"
            ),
            expectedValue = calculated.fuelSurcharge,
            discrepancies = discrepancies
        )

        // --- Compare electricity duty ---
        compareComponent(
            componentName = "Electricity Duty",
            billedValue = findBilledComponent(
                parsedBill.chargeBreakdown,
                "Electricity Duty", "ED", "Tax", "State Duty"
            ),
            expectedValue = calculated.electricityDuty,
            discrepancies = discrepancies
        )

        // --- Overall total comparison ---
        val totalDifference = billedTotal - calculated.totalAmount
        val totalThreshold = (calculated.totalAmount * TOTAL_RELATIVE_TOLERANCE)
            .coerceAtLeast(COMPONENT_TOLERANCE)

        if (abs(totalDifference) > totalThreshold && discrepancies.isEmpty()) {
            // No individual component mismatch was found, but the total still differs.
            // This can happen when the breakdown is not available from OCR.
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

        val confidence = calculateConfidence(parsedBill, discrepancies, calculated, billedTotal)
        val suggestions = generateSuggestions(discrepancies, utilityBoard, isOvercharged, overchargeAmount)

        return OverchargeReport(
            isOvercharged = isOvercharged,
            overchargeAmount = overchargeAmount,
            discrepancies = discrepancies,
            confidence = confidence,
            suggestions = suggestions
        )
    }

    // ======================== Private helpers ========================

    /**
     * Looks up the billed value from the OCR charge breakdown using multiple
     * possible label variants (different boards use different labels for the
     * same component).
     */
    private fun findBilledComponent(
        breakdown: Map<String, Double>,
        vararg keys: String
    ): Double? {
        for (key in keys) {
            breakdown[key]?.let { return it }
        }
        // Case-insensitive fallback
        for (key in keys) {
            val lowerKey = key.lowercase()
            for ((k, v) in breakdown) {
                if (k.lowercase() == lowerKey) return v
            }
        }
        return null
    }

    /**
     * Compares a single charge component. Adds a [Discrepancy] to the list
     * only when the absolute difference exceeds [COMPONENT_TOLERANCE].
     */
    private fun compareComponent(
        componentName: String,
        billedValue: Double?,
        expectedValue: Double,
        discrepancies: MutableList<Discrepancy>
    ) {
        if (billedValue == null) return
        val diff = billedValue - expectedValue
        if (abs(diff) > COMPONENT_TOLERANCE) {
            discrepancies.add(
                Discrepancy(
                    component = componentName,
                    expected = expectedValue,
                    actual = billedValue,
                    difference = diff
                )
            )
        }
    }

    /**
     * Calculates the confidence score of the overcharge analysis.
     *
     * Higher confidence when:
     * - OCR confidence was already high
     * - More charge components were available for cross-checking
     * - Discrepancies are proportionally small (large discrepancies may
     *   indicate OCR misreading rather than actual overcharging)
     */
    private fun calculateConfidence(
        parsedBill: BillParsingEngine.ParsedBillData,
        discrepancies: List<Discrepancy>,
        calculated: TariffCalculator.CalculatedBill,
        billedTotal: Double
    ): Float {
        var confidence = parsedBill.overallConfidence

        // More breakdown components -> higher confidence
        val availableComponents = parsedBill.chargeBreakdown.size
        if (availableComponents >= 4) confidence += 0.10f
        else if (availableComponents >= 2) confidence += 0.05f

        // Cross-validation: breakdown sum close to total
        if (parsedBill.chargeBreakdown.isNotEmpty()) {
            val breakdownSum = parsedBill.chargeBreakdown.values.sum()
            val ratio = if (billedTotal > 0) breakdownSum / billedTotal else 0.0
            if (ratio in 0.85..1.15) confidence += 0.05f
        }

        // Meter readings match units consumed
        if (parsedBill.previousReading != null && parsedBill.currentReading != null) {
            val derivedUnits = (parsedBill.currentReading - parsedBill.previousReading).toInt()
            if (derivedUnits == (parsedBill.unitsConsumed ?: -1)) confidence += 0.05f
        }

        // Too many or very large discrepancies lower confidence (possible OCR error)
        if (discrepancies.size > 3) confidence -= 0.15f
        discrepancies.forEach { d ->
            if (d.expected > 0 && abs(d.difference / d.expected) > 0.50) {
                confidence -= 0.10f
            }
        }

        return confidence.coerceIn(0.1f, 0.99f)
    }

    /**
     * Generates human-readable suggestions based on the discrepancies found.
     */
    private fun generateSuggestions(
        discrepancies: List<Discrepancy>,
        utilityBoard: String,
        isOvercharged: Boolean,
        overchargeAmount: Double
    ): List<String> {
        val suggestions = mutableListOf<String>()

        if (!isOvercharged) {
            suggestions.add(
                "Your bill appears to be calculated correctly based on the applicable tariff rates."
            )
            suggestions.add(
                "You can still check for energy-saving tips to reduce future bills."
            )
            return suggestions
        }

        // Per-component suggestions
        discrepancies.forEach { d ->
            when {
                d.component.contains("Energy", ignoreCase = true) -> {
                    suggestions.add(
                        "Energy charges appear incorrect (billed Rs.${fmtAmt(d.actual)} " +
                        "vs expected Rs.${fmtAmt(d.expected)}). " +
                        "Your utility may have applied the wrong tariff slab or category."
                    )
                    suggestions.add(
                        "Verify that your connection is classified as 'Domestic' " +
                        "and not 'Commercial' or 'Non-Domestic'."
                    )
                }
                d.component.contains("Fixed", ignoreCase = true) -> {
                    suggestions.add(
                        "Fixed charges do not match the applicable slab " +
                        "(billed Rs.${fmtAmt(d.actual)} vs expected Rs.${fmtAmt(d.expected)}). " +
                        "Check if your sanctioned load is correctly recorded."
                    )
                }
                d.component.contains("Fuel", ignoreCase = true) -> {
                    suggestions.add(
                        "Fuel surcharge appears higher than the approved rate " +
                        "(billed Rs.${fmtAmt(d.actual)} vs expected Rs.${fmtAmt(d.expected)}). " +
                        "Cross-check with the latest SERC tariff order."
                    )
                }
                d.component.contains("Duty", ignoreCase = true) ||
                d.component.contains("Tax", ignoreCase = true) -> {
                    suggestions.add(
                        "Electricity duty / tax appears incorrect " +
                        "(billed Rs.${fmtAmt(d.actual)} vs expected Rs.${fmtAmt(d.expected)}). " +
                        "The applicable rate should match your state's regulation."
                    )
                }
                d.component.contains("Total", ignoreCase = true) -> {
                    suggestions.add(
                        "Total amount does not match the sum of expected components " +
                        "(billed Rs.${fmtAmt(d.actual)} vs expected Rs.${fmtAmt(d.expected)}). " +
                        "There may be hidden charges or a calculation error."
                    )
                }
            }
        }

        // General legal / escalation advice
        suggestions.add(
            "Estimated overcharge: Rs.${fmtAmt(overchargeAmount)}. " +
            "File a complaint with $utilityBoard citing Electricity Act 2003, Section 56."
        )
        suggestions.add(
            "If unresolved within 15 days, escalate to the Consumer Grievance Redressal Forum (CGRF)."
        )
        if (overchargeAmount > 2000) {
            suggestions.add(
                "For significant overcharges, consider filing an RTI application " +
                "to obtain the detailed tariff calculation worksheet from your utility."
            )
        }

        return suggestions
    }

    /**
     * Creates a low-confidence report when essential bill data is missing,
     * prompting the user to re-scan.
     */
    private fun createLowConfidenceReport(reason: String): OverchargeReport {
        return OverchargeReport(
            isOvercharged = false,
            overchargeAmount = 0.0,
            discrepancies = emptyList(),
            confidence = 0.2f,
            suggestions = listOf(
                reason,
                "Try scanning the bill again with better lighting and alignment.",
                "Ensure the entire bill is visible within the camera frame."
            )
        )
    }

    private fun fmtAmt(value: Double): String = String.format("%.2f", value)
}
