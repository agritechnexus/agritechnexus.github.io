package com.fixmybill.app.util

import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.model.Discrepancy
import com.fixmybill.app.domain.model.OverchargeReport
import com.fixmybill.app.domain.model.TariffSlab
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calculates expected bill amounts based on tariff slabs and detects overcharges.
 * Supports telescopic (incremental slab) and non-telescopic (flat slab) billing methods.
 */
@Singleton
class TariffCalculator @Inject constructor() {

    data class CalculationResult(
        val totalEnergyCharge: Double,
        val totalFixedCharge: Double,
        val slabBreakdown: List<SlabCharge>,
        val totalAmount: Double
    )

    data class SlabCharge(
        val slabStart: Int,
        val slabEnd: Int,
        val units: Int,
        val ratePerUnit: Double,
        val charge: Double
    )

    /**
     * Calculates the expected bill amount using telescopic (incremental slab) billing.
     * Each slab rate applies only to the units within that slab range.
     */
    fun calculateTelescopicBill(unitsConsumed: Int, tariffSlabs: List<TariffSlab>): CalculationResult {
        val sortedSlabs = tariffSlabs.sortedBy { it.slabStart }
        val slabCharges = mutableListOf<SlabCharge>()
        var totalEnergyCharge = 0.0
        var totalFixedCharge = 0.0

        for (slab in sortedSlabs) {
            val units = slab.unitsInSlab(unitsConsumed)
            if (units > 0) {
                val charge = slab.chargeForUnits(unitsConsumed)
                slabCharges.add(
                    SlabCharge(
                        slabStart = slab.slabStart,
                        slabEnd = slab.slabEnd,
                        units = units,
                        ratePerUnit = slab.ratePerUnit,
                        charge = charge
                    )
                )
                totalEnergyCharge += charge
                totalFixedCharge += slab.fixedCharge
            }
        }

        return CalculationResult(
            totalEnergyCharge = totalEnergyCharge,
            totalFixedCharge = totalFixedCharge,
            slabBreakdown = slabCharges,
            totalAmount = totalEnergyCharge + totalFixedCharge
        )
    }

    /**
     * Calculates the expected bill amount using non-telescopic (flat slab) billing.
     * The rate of the highest applicable slab applies to all units.
     */
    fun calculateNonTelescopicBill(unitsConsumed: Int, tariffSlabs: List<TariffSlab>): CalculationResult {
        val sortedSlabs = tariffSlabs.sortedBy { it.slabStart }

        val applicableSlab = sortedSlabs.lastOrNull { unitsConsumed > it.slabStart }
            ?: sortedSlabs.firstOrNull()
            ?: return CalculationResult(0.0, 0.0, emptyList(), 0.0)

        val charge = unitsConsumed * applicableSlab.ratePerUnit
        val slabCharge = SlabCharge(
            slabStart = 0,
            slabEnd = unitsConsumed,
            units = unitsConsumed,
            ratePerUnit = applicableSlab.ratePerUnit,
            charge = charge
        )

        return CalculationResult(
            totalEnergyCharge = charge,
            totalFixedCharge = applicableSlab.fixedCharge,
            slabBreakdown = listOf(slabCharge),
            totalAmount = charge + applicableSlab.fixedCharge
        )
    }

    /**
     * Compares the actual bill amount with the expected calculated amount
     * and generates an overcharge report.
     */
    fun detectOvercharge(
        bill: Bill,
        tariffSlabs: List<TariffSlab>,
        useTelescopicBilling: Boolean = true
    ): OverchargeReport {
        val calculationResult = if (useTelescopicBilling) {
            calculateTelescopicBill(bill.unitsConsumed, tariffSlabs)
        } else {
            calculateNonTelescopicBill(bill.unitsConsumed, tariffSlabs)
        }

        val expectedTotal = calculationResult.totalAmount
        val actualTotal = bill.totalAmount
        val difference = actualTotal - expectedTotal
        val isOvercharged = difference > OVERCHARGE_THRESHOLD

        val discrepancies = mutableListOf<Discrepancy>()

        if (isOvercharged) {
            discrepancies.add(
                Discrepancy(
                    component = "Energy Charges",
                    expected = calculationResult.totalEnergyCharge,
                    actual = actualTotal - calculationResult.totalFixedCharge,
                    difference = actualTotal - calculationResult.totalFixedCharge - calculationResult.totalEnergyCharge
                )
            )
        }

        // Calculate confidence based on how much data we have
        val confidence = calculateConfidence(bill, tariffSlabs)

        val suggestions = buildOverchargeSuggestions(isOvercharged, difference, bill)

        return OverchargeReport(
            isOvercharged = isOvercharged,
            overchargeAmount = if (isOvercharged) difference else 0.0,
            discrepancies = discrepancies,
            confidence = confidence,
            suggestions = suggestions
        )
    }

    private fun calculateConfidence(bill: Bill, tariffSlabs: List<TariffSlab>): Float {
        var confidence = 0.5f

        // Higher confidence if we have matching tariff slabs
        if (tariffSlabs.isNotEmpty()) confidence += 0.2f

        // Higher confidence if units consumed matches meter readings
        val expectedUnits = bill.currentReading - bill.previousReading
        if (expectedUnits.toInt() == bill.unitsConsumed) confidence += 0.2f

        // Higher confidence if we have complete bill data
        if (bill.consumerNumber.isNotBlank()) confidence += 0.1f

        return confidence.coerceIn(0f, 1f)
    }

    private fun buildOverchargeSuggestions(
        isOvercharged: Boolean,
        difference: Double,
        bill: Bill
    ): List<String> = buildList {
        if (isOvercharged) {
            add("Your bill appears to be overcharged by ${difference.toCurrencyString()}")
            add("File a complaint with ${bill.utilityProvider} referencing consumer number ${bill.consumerNumber}")
            add("You can also approach the Consumer Grievance Redressal Forum (CGRF)")
            if (difference > 1000) {
                add("Consider filing an RTI to get the detailed tariff calculation from your utility provider")
            }
        } else {
            add("Your bill amount appears to be correct based on current tariff rates")
            add("Check for energy-saving tips to reduce your future bills")
        }
    }

    companion object {
        /** Minimum amount difference (in Rupees) to flag as an overcharge */
        const val OVERCHARGE_THRESHOLD = 10.0
    }
}
