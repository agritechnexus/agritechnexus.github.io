package com.fixmybill.app.util

import java.util.Calendar
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.sqrt

class BillPredictorEngine @Inject constructor() {

    data class BillHistoryPoint(
        val month: Int,
        val year: Int,
        val units: Int,
        val amount: Double
    )

    data class BillPrediction(
        val predictedUnits: Int,
        val predictedAmount: Double,
        val confidenceRange: Pair<Double, Double>,
        val trend: PredictionTrend,
        val seasonalFactor: String
    )

    enum class PredictionTrend { INCREASING, DECREASING, STABLE }

    fun predictNextBill(history: List<BillHistoryPoint>): BillPrediction {
        if (history.isEmpty()) {
            return BillPrediction(
                predictedUnits = 0,
                predictedAmount = 0.0,
                confidenceRange = 0.0 to 0.0,
                trend = PredictionTrend.STABLE,
                seasonalFactor = "Insufficient data"
            )
        }

        if (history.size < 3) {
            val avgUnits = history.map { it.units }.average().toInt()
            val avgAmount = history.map { it.amount }.average()
            return BillPrediction(
                predictedUnits = avgUnits,
                predictedAmount = avgAmount,
                confidenceRange = avgAmount * 0.8 to avgAmount * 1.2,
                trend = PredictionTrend.STABLE,
                seasonalFactor = "Limited data - using average"
            )
        }

        // Linear regression on amounts
        val amounts = history.map { it.amount }
        val units = history.map { it.units.toDouble() }
        val (slopeAmount, interceptAmount) = linearRegression(amounts)
        val (slopeUnits, interceptUnits) = linearRegression(units)

        val nextIndex = history.size.toDouble()
        var predictedAmount = slopeAmount * nextIndex + interceptAmount
        var predictedUnits = (slopeUnits * nextIndex + interceptUnits).toInt()

        // Determine target month
        val lastEntry = history.last()
        val targetMonth = if (lastEntry.month == 12) 1 else lastEntry.month + 1

        // Apply seasonal adjustment
        val seasonalMultiplier = getSeasonalMultiplier(targetMonth)
        predictedAmount *= seasonalMultiplier
        predictedUnits = (predictedUnits * seasonalMultiplier).toInt()

        // Ensure non-negative
        predictedAmount = predictedAmount.coerceAtLeast(0.0)
        predictedUnits = predictedUnits.coerceAtLeast(0)

        // Confidence range based on standard deviation
        val stdDev = standardDeviation(amounts)
        val confidenceLow = (predictedAmount - 1.5 * stdDev).coerceAtLeast(0.0)
        val confidenceHigh = predictedAmount + 1.5 * stdDev

        // Determine trend
        val trend = when {
            slopeAmount > 50.0 -> PredictionTrend.INCREASING
            slopeAmount < -50.0 -> PredictionTrend.DECREASING
            else -> PredictionTrend.STABLE
        }

        val seasonalFactor = getSeasonalDescription(targetMonth)

        return BillPrediction(
            predictedUnits = predictedUnits,
            predictedAmount = Math.round(predictedAmount * 100.0) / 100.0,
            confidenceRange = Math.round(confidenceLow * 100.0) / 100.0 to Math.round(confidenceHigh * 100.0) / 100.0,
            trend = trend,
            seasonalFactor = seasonalFactor
        )
    }

    private fun linearRegression(data: List<Double>): Pair<Double, Double> {
        val n = data.size
        if (n <= 1) return 0.0 to (data.firstOrNull() ?: 0.0)

        val indices = (0 until n).map { it.toDouble() }
        val sumX = indices.sum()
        val sumY = data.sum()
        val sumXY = indices.zip(data).sumOf { it.first * it.second }
        val sumX2 = indices.sumOf { it * it }

        val denominator = n * sumX2 - sumX * sumX
        if (abs(denominator) < 0.0001) return 0.0 to (sumY / n)

        val slope = (n * sumXY - sumX * sumY) / denominator
        val intercept = (sumY - slope * sumX) / n

        return slope to intercept
    }

    private fun standardDeviation(data: List<Double>): Double {
        if (data.size <= 1) return data.firstOrNull()?.times(0.1) ?: 0.0
        val mean = data.average()
        val variance = data.sumOf { (it - mean) * (it - mean) } / (data.size - 1)
        return sqrt(variance)
    }

    private fun getSeasonalMultiplier(month: Int): Double {
        // Indian seasonal patterns for electricity usage
        return when (month) {
            // Summer months (Apr-Jun) - higher AC usage
            4 -> 1.25
            5 -> 1.40
            6 -> 1.35
            // Monsoon (Jul-Sep) - moderate
            7 -> 1.15
            8 -> 1.10
            9 -> 1.05
            // Post-monsoon / autumn (Oct-Nov) - lower
            10 -> 0.95
            11 -> 0.85
            // Winter (Dec-Feb) - lower usage in most states, heater in north
            12 -> 0.85
            1 -> 0.80
            2 -> 0.85
            // Spring (Mar) - moderate
            3 -> 1.00
            else -> 1.0
        }
    }

    private fun getSeasonalDescription(month: Int): String {
        return when (month) {
            4, 5, 6 -> "Summer peak - expect higher usage due to AC/cooler"
            7, 8, 9 -> "Monsoon - moderate usage expected"
            10, 11 -> "Post-monsoon - typically lower bills"
            12, 1, 2 -> "Winter - lower usage (unless using heaters)"
            3 -> "Spring - moderate, pre-summer usage"
            else -> "Seasonal adjustment applied"
        }
    }
}
