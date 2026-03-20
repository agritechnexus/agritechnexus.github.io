package com.fixmybill.app.domain.usecase

import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.model.Discrepancy
import com.fixmybill.app.domain.model.OverchargeReport
import com.fixmybill.app.domain.model.TariffSlab
import javax.inject.Inject
import kotlin.math.abs

class DetectOverchargeUseCase @Inject constructor() {

    operator fun invoke(bill: Bill, tariffSlabs: List<TariffSlab>): OverchargeReport {
        if (tariffSlabs.isEmpty()) {
            return OverchargeReport(
                isOvercharged = false,
                overchargeAmount = 0.0,
                discrepancies = emptyList(),
                confidence = 0f,
                suggestions = listOf("No tariff data available for ${bill.utilityProvider} in ${bill.state}. Please update tariff information.")
            )
        }

        val sortedSlabs = tariffSlabs.sortedBy { it.slabStart }
        val discrepancies = mutableListOf<Discrepancy>()
        val suggestions = mutableListOf<String>()

        // Recalculate energy/unit charges based on tariff slabs
        var expectedUnitCharge = 0.0
        for (slab in sortedSlabs) {
            expectedUnitCharge += slab.chargeForUnits(bill.unitsConsumed)
        }

        // Calculate expected fixed charge (from the applicable slab)
        val applicableSlab = sortedSlabs.lastOrNull { bill.unitsConsumed > it.slabStart }
        val expectedFixedCharge = applicableSlab?.fixedCharge ?: 0.0

        val expectedTotal = expectedUnitCharge + expectedFixedCharge
        val actualTotal = bill.totalAmount
        val difference = actualTotal - expectedTotal

        // Check unit charge discrepancy
        val unitChargeFromBill = actualTotal - expectedFixedCharge
        if (abs(unitChargeFromBill - expectedUnitCharge) > 1.0) {
            discrepancies.add(
                Discrepancy(
                    component = "Energy/Unit Charges",
                    expected = expectedUnitCharge,
                    actual = unitChargeFromBill,
                    difference = unitChargeFromBill - expectedUnitCharge
                )
            )
        }

        // Check meter reading consistency
        val calculatedUnits = (bill.currentReading - bill.previousReading).toInt()
        if (calculatedUnits != bill.unitsConsumed) {
            val unitDiff = bill.unitsConsumed - calculatedUnits
            discrepancies.add(
                Discrepancy(
                    component = "Units Consumed (Meter Reading Mismatch)",
                    expected = calculatedUnits.toDouble(),
                    actual = bill.unitsConsumed.toDouble(),
                    difference = unitDiff.toDouble()
                )
            )
            suggestions.add("Meter readings indicate $calculatedUnits units but bill shows ${bill.unitsConsumed} units. Verify your meter readings.")
        }

        // Check total amount discrepancy
        val isOvercharged = difference > 1.0
        val overchargeAmount = if (isOvercharged) difference else 0.0

        if (isOvercharged) {
            discrepancies.add(
                Discrepancy(
                    component = "Total Bill Amount",
                    expected = expectedTotal,
                    actual = actualTotal,
                    difference = difference
                )
            )
            suggestions.add("Your bill appears to be overcharged by ₹${"%.2f".format(overchargeAmount)}.")
            suggestions.add("Contact ${bill.utilityProvider} and request a detailed bill breakdown.")
        }

        // Calculate confidence based on tariff data completeness and discrepancy magnitude
        val confidence = calculateConfidence(sortedSlabs, bill, difference)

        return OverchargeReport(
            isOvercharged = isOvercharged,
            overchargeAmount = overchargeAmount,
            discrepancies = discrepancies,
            confidence = confidence,
            suggestions = suggestions
        )
    }

    private fun calculateConfidence(
        slabs: List<TariffSlab>,
        bill: Bill,
        difference: Double
    ): Float {
        var confidence = 0.5f

        // Higher confidence if we have good slab coverage
        val maxSlabEnd = slabs.maxOfOrNull { it.slabEnd } ?: 0
        if (maxSlabEnd >= bill.unitsConsumed || slabs.any { it.slabEnd == Int.MAX_VALUE }) {
            confidence += 0.2f
        }

        // Higher confidence if meter readings are consistent
        val calculatedUnits = (bill.currentReading - bill.previousReading).toInt()
        if (calculatedUnits == bill.unitsConsumed) {
            confidence += 0.15f
        }

        // Higher confidence for larger discrepancies (more clearly wrong)
        if (abs(difference) > bill.totalAmount * 0.1) {
            confidence += 0.1f
        }

        // Cap at reasonable bounds
        if (slabs.size < 2) {
            confidence -= 0.15f
        }

        return confidence.coerceIn(0.1f, 0.95f)
    }
}
