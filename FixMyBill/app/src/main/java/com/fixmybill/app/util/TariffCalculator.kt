package com.fixmybill.app.util

import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.model.Discrepancy
import com.fixmybill.app.domain.model.OverchargeReport
import com.fixmybill.app.domain.model.TariffSlab
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToLong

/**
 * Recalculates electricity bills from scratch using the [TariffDatabase] and
 * compares them against billed amounts. Supports both telescopic (incremental
 * slab) and non-telescopic (flat slab) billing methods used across Indian
 * electricity boards.
 */
@Singleton
class TariffCalculator @Inject constructor() {

    // ======================== Result data classes ========================

    /**
     * Full recalculated bill with every charge component.
     */
    data class CalculatedBill(
        val energyCharges: Double,
        val fixedCharges: Double,
        val fuelSurcharge: Double,
        val electricityDuty: Double,
        val totalAmount: Double,
        val slabBreakdown: List<SlabCalculation>
    )

    /**
     * One line item in the slab-wise breakdown.
     */
    data class SlabCalculation(
        val slabDescription: String,
        val unitsInSlab: Int,
        val ratePerUnit: Double,
        val amount: Double
    )

    /**
     * Legacy result type kept for backward compatibility with existing code
     * that uses [calculateTelescopicBill] / [calculateNonTelescopicBill].
     */
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

    // ======================== Primary API ========================

    /**
     * Calculates a complete bill for the given units and utility board using
     * the official tariff slabs from [TariffDatabase].
     *
     * Automatically selects telescopic vs non-telescopic billing based on the
     * board's configuration. Includes energy charges, fixed charges, fuel
     * surcharge, and electricity duty.
     */
    fun calculateBill(
        units: Int,
        utilityBoard: String,
        category: String = "DOMESTIC"
    ): CalculatedBill {
        val slabs = TariffDatabase.getTariffSlabs(utilityBoard, category)
        if (slabs.isEmpty()) {
            return CalculatedBill(
                energyCharges = 0.0,
                fixedCharges = 0.0,
                fuelSurcharge = 0.0,
                electricityDuty = 0.0,
                totalAmount = 0.0,
                slabBreakdown = emptyList()
            )
        }

        val isTelescopic = TariffDatabase.isTelescopicBilling(utilityBoard)

        // --- Energy charges (slab-wise) ---
        val slabBreakdown = if (isTelescopic) {
            calculateSlabWise(units, slabs)
        } else {
            calculateNonTelescopicSlabs(units, slabs)
        }

        val energyCharges = slabBreakdown.sumOf { it.amount }

        // --- Fixed charges (based on total units consumed) ---
        val fixedCharges = TariffDatabase.getFixedCharges(utilityBoard, units)

        // --- Fuel surcharge: rate (Rs/unit) * units ---
        val fuelSurchargeRate = TariffDatabase.getFuelSurchargeRate(utilityBoard)
        val fuelSurcharge = roundToTwoDecimals(units * fuelSurchargeRate)

        // --- Electricity duty: percentage of energy charges ---
        val dutyRate = TariffDatabase.getElectricityDutyRate(utilityBoard)
        val electricityDuty = roundToTwoDecimals(energyCharges * dutyRate)

        // --- Minimum charges guard ---
        val minimumCharges = TariffDatabase.getMinimumCharges(utilityBoard)
        val subtotal = energyCharges + fixedCharges
        val effectiveSubtotal = if (subtotal < minimumCharges) minimumCharges else subtotal

        val totalAmount = roundToTwoDecimals(
            effectiveSubtotal + fuelSurcharge + electricityDuty
        )

        return CalculatedBill(
            energyCharges = roundToTwoDecimals(energyCharges),
            fixedCharges = fixedCharges,
            fuelSurcharge = fuelSurcharge,
            electricityDuty = electricityDuty,
            totalAmount = totalAmount,
            slabBreakdown = slabBreakdown
        )
    }

    /**
     * Calculates energy charges using telescopic (incremental slab) billing.
     * Each slab rate applies only to the units that fall within that slab's range.
     *
     * Example for TSSPDCL with 250 units:
     *   Slab 1:  50 units  x 1.45 = 72.50
     *   Slab 2:  50 units  x 2.60 = 130.00
     *   Slab 3: 100 units  x 3.60 = 360.00
     *   Slab 4:  50 units  x 5.50 = 275.00
     *   Total energy = 837.50
     */
    fun calculateSlabWise(
        units: Int,
        slabs: List<TariffDatabase.TariffSlabInfo>
    ): List<SlabCalculation> {
        if (units <= 0) return emptyList()

        val sortedSlabs = slabs.sortedBy { it.slabStart }
        val result = mutableListOf<SlabCalculation>()
        var remaining = units

        for (slab in sortedSlabs) {
            if (remaining <= 0) break

            val slabWidth = if (slab.slabEnd == Int.MAX_VALUE) {
                remaining
            } else {
                slab.slabEnd - slab.slabStart + (if (slab.slabStart == 0) 1 else 0)
                // number of units this slab can hold
            }

            // For the standard representation 0-50, 51-100, etc.
            val slabCapacity = when {
                slab.slabEnd == Int.MAX_VALUE -> remaining
                slab.slabStart == 0 -> slab.slabEnd
                else -> slab.slabEnd - slab.slabStart
            }

            val unitsInThisSlab = minOf(remaining, slabCapacity.coerceAtLeast(1))
            val amount = roundToTwoDecimals(unitsInThisSlab * slab.ratePerUnit)

            if (unitsInThisSlab > 0) {
                result.add(
                    SlabCalculation(
                        slabDescription = slab.description,
                        unitsInSlab = unitsInThisSlab,
                        ratePerUnit = slab.ratePerUnit,
                        amount = amount
                    )
                )
            }

            remaining -= unitsInThisSlab
        }

        return result
    }

    // ======================== Legacy API ========================

    /**
     * Legacy telescopic calculation using domain [TariffSlab] objects.
     */
    fun calculateTelescopicBill(
        unitsConsumed: Int,
        tariffSlabs: List<TariffSlab>
    ): CalculationResult {
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
     * Legacy non-telescopic calculation using domain [TariffSlab] objects.
     * The rate of the highest applicable slab applies to ALL units.
     */
    fun calculateNonTelescopicBill(
        unitsConsumed: Int,
        tariffSlabs: List<TariffSlab>
    ): CalculationResult {
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
     * Legacy overcharge detection that works with domain [Bill] and [TariffSlab].
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

        val confidence = calculateLegacyConfidence(bill, tariffSlabs)
        val suggestions = buildOverchargeSuggestions(isOvercharged, difference, bill)

        return OverchargeReport(
            isOvercharged = isOvercharged,
            overchargeAmount = if (isOvercharged) difference else 0.0,
            discrepancies = discrepancies,
            confidence = confidence,
            suggestions = suggestions
        )
    }

    // ======================== Private helpers ========================

    /**
     * Non-telescopic slab calculation using [TariffDatabase.TariffSlabInfo].
     * All units are charged at the rate of the highest applicable slab.
     */
    private fun calculateNonTelescopicSlabs(
        units: Int,
        slabs: List<TariffDatabase.TariffSlabInfo>
    ): List<SlabCalculation> {
        if (units <= 0) return emptyList()

        val sortedSlabs = slabs.sortedBy { it.slabStart }
        val applicableSlab = sortedSlabs.lastOrNull { units >= it.slabStart }
            ?: sortedSlabs.first()

        val amount = roundToTwoDecimals(units * applicableSlab.ratePerUnit)
        return listOf(
            SlabCalculation(
                slabDescription = "All ${units} units @ Rs.${applicableSlab.ratePerUnit}/unit (non-telescopic)",
                unitsInSlab = units,
                ratePerUnit = applicableSlab.ratePerUnit,
                amount = amount
            )
        )
    }

    private fun calculateLegacyConfidence(bill: Bill, tariffSlabs: List<TariffSlab>): Float {
        var confidence = 0.5f
        if (tariffSlabs.isNotEmpty()) confidence += 0.2f
        val expectedUnits = bill.currentReading - bill.previousReading
        if (expectedUnits.toInt() == bill.unitsConsumed) confidence += 0.2f
        if (bill.consumerNumber.isNotBlank()) confidence += 0.1f
        return confidence.coerceIn(0f, 1f)
    }

    private fun buildOverchargeSuggestions(
        isOvercharged: Boolean,
        difference: Double,
        bill: Bill
    ): List<String> = buildList {
        if (isOvercharged) {
            add("Your bill appears to be overcharged by Rs.${String.format("%.2f", difference)}")
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

    private fun roundToTwoDecimals(value: Double): Double {
        return (value * 100.0).roundToLong() / 100.0
    }

    companion object {
        /** Minimum amount difference (in Rupees) to flag as an overcharge */
        const val OVERCHARGE_THRESHOLD = 10.0
    }
}
