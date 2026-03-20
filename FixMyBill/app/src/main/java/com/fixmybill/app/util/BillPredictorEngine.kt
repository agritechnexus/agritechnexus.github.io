package com.fixmybill.app.util

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.math.sqrt

/**
 * Predicts the next month's electricity bill based on historical usage data.
 * Uses linear regression for trend detection and applies Indian seasonal
 * multipliers (summer peak, monsoon, winter) to account for weather-driven
 * consumption patterns.
 *
 * Provides a point estimate, a confidence range (based on historical standard
 * deviation), a qualitative trend indicator, and a seasonal factor description.
 */
@Singleton
class BillPredictorEngine @Inject constructor() {

    // ======================== Data classes ========================

    data class BillHistoryPoint(
        val month: Int,   // 1-12
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

    enum class PredictionTrend {
        INCREASING,
        DECREASING,
        STABLE
    }

    // ======================== Public API ========================

    /**
     * Predicts the next bill given a chronologically ordered list of
     * historical data points.
     *
     * - With 0 data points: returns a zeroed prediction.
     * - With 1-2 data points: returns the simple average with a wide
     *   confidence band.
     * - With 3+ data points: uses linear regression + seasonal adjustment.
     */
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
            return predictFromAverage(history)
        }

        return predictWithRegression(history)
    }

    // ======================== Prediction strategies ========================

    /**
     * Simple average-based prediction for insufficient data (< 3 points).
     * Uses +/- 20% as the confidence band.
     */
    private fun predictFromAverage(history: List<BillHistoryPoint>): BillPrediction {
        val avgUnits = history.map { it.units }.average().roundToInt()
        val avgAmount = roundTwo(history.map { it.amount }.average())

        val lastEntry = history.last()
        val targetMonth = nextMonth(lastEntry.month)
        val seasonalMultiplier = getSeasonalMultiplier(targetMonth)

        val adjustedUnits = (avgUnits * seasonalMultiplier).roundToInt().coerceAtLeast(0)
        val adjustedAmount = roundTwo((avgAmount * seasonalMultiplier).coerceAtLeast(0.0))

        return BillPrediction(
            predictedUnits = adjustedUnits,
            predictedAmount = adjustedAmount,
            confidenceRange = roundTwo(adjustedAmount * 0.80) to roundTwo(adjustedAmount * 1.20),
            trend = PredictionTrend.STABLE,
            seasonalFactor = "Limited data - using average. ${getSeasonalDescription(targetMonth)}"
        )
    }

    /**
     * Linear regression-based prediction with seasonal adjustment.
     *
     * 1. Fit a line to the historical amounts and units.
     * 2. Project to the next index.
     * 3. Multiply by the seasonal multiplier for the target month.
     * 4. Build a confidence interval from 1.5 standard deviations.
     */
    private fun predictWithRegression(history: List<BillHistoryPoint>): BillPrediction {
        val amounts = history.map { it.amount }
        val units = history.map { it.units.toDouble() }

        val (slopeAmount, interceptAmount) = linearRegression(amounts)
        val (slopeUnits, interceptUnits) = linearRegression(units)

        val nextIndex = history.size.toDouble()
        var predictedAmount = slopeAmount * nextIndex + interceptAmount
        var predictedUnits = (slopeUnits * nextIndex + interceptUnits).roundToInt()

        // Determine target month
        val lastEntry = history.last()
        val targetMonth = nextMonth(lastEntry.month)

        // Apply seasonal adjustment
        val seasonalMultiplier = getSeasonalMultiplier(targetMonth)
        predictedAmount = applySeasonalAdjustment(predictedAmount, targetMonth)
        predictedUnits = (predictedUnits * seasonalMultiplier).roundToInt()

        // Clamp negatives
        predictedAmount = predictedAmount.coerceAtLeast(0.0)
        predictedUnits = predictedUnits.coerceAtLeast(0)

        // Confidence range: mean +/- 1.5 * stddev
        val stdDev = standardDeviation(amounts)
        val confidenceLow = roundTwo((predictedAmount - 1.5 * stdDev).coerceAtLeast(0.0))
        val confidenceHigh = roundTwo(predictedAmount + 1.5 * stdDev)

        // Determine trend from amount slope
        val trend = determineTrend(slopeAmount)
        val seasonalFactor = getSeasonalDescription(targetMonth)

        return BillPrediction(
            predictedUnits = predictedUnits,
            predictedAmount = roundTwo(predictedAmount),
            confidenceRange = confidenceLow to confidenceHigh,
            trend = trend,
            seasonalFactor = seasonalFactor
        )
    }

    // ======================== Linear regression ========================

    /**
     * Simple ordinary-least-squares linear regression.
     * Returns (slope, intercept) for y = slope * x + intercept,
     * where x = 0, 1, 2, ... (index-based).
     */
    private fun linearRegression(data: List<Double>): Pair<Double, Double> {
        val n = data.size
        if (n <= 1) return 0.0 to (data.firstOrNull() ?: 0.0)

        val indices = (0 until n).map { it.toDouble() }
        val sumX = indices.sum()
        val sumY = data.sum()
        val sumXY = indices.zip(data).sumOf { (x, y) -> x * y }
        val sumX2 = indices.sumOf { it * it }

        val denominator = n * sumX2 - sumX * sumX
        if (abs(denominator) < 1e-9) return 0.0 to (sumY / n)

        val slope = (n * sumXY - sumX * sumY) / denominator
        val intercept = (sumY - slope * sumX) / n

        return slope to intercept
    }

    /**
     * Sample standard deviation (Bessel-corrected, n-1 denominator).
     */
    private fun standardDeviation(data: List<Double>): Double {
        if (data.size <= 1) return (data.firstOrNull() ?: 0.0) * 0.10
        val mean = data.average()
        val variance = data.sumOf { (it - mean) * (it - mean) } / (data.size - 1)
        return sqrt(variance)
    }

    // ======================== Seasonal adjustment ========================

    /**
     * Applies the seasonal multiplier for the target month to the base
     * predicted amount. This captures typical Indian electricity usage
     * patterns driven by weather.
     */
    private fun applySeasonalAdjustment(baseAmount: Double, targetMonth: Int): Double {
        return baseAmount * getSeasonalMultiplier(targetMonth)
    }

    /**
     * Seasonal multipliers reflecting Indian electricity consumption patterns.
     *
     * - **Summer (Apr-Jun)**: Peak AC / cooler usage. May is the hottest month
     *   across most of India, leading to the highest multiplier (1.40).
     * - **Monsoon (Jul-Sep)**: Moderate; humidity may drive AC use in some
     *   regions but rain brings relief in others.
     * - **Post-monsoon / Autumn (Oct-Nov)**: Lower consumption.
     * - **Winter (Dec-Feb)**: Lower in most states. Northern states may see
     *   heater use, but overall national average is below baseline.
     * - **Spring (Mar)**: Baseline / pre-summer moderate usage.
     */
    private fun getSeasonalMultiplier(month: Int): Double {
        return when (month) {
            1  -> 0.80   // January  - winter low
            2  -> 0.85   // February - late winter
            3  -> 1.00   // March    - spring baseline
            4  -> 1.25   // April    - early summer
            5  -> 1.40   // May      - peak summer
            6  -> 1.35   // June     - late summer / early monsoon
            7  -> 1.15   // July     - monsoon
            8  -> 1.10   // August   - monsoon
            9  -> 1.05   // September - late monsoon
            10 -> 0.95   // October  - post-monsoon
            11 -> 0.85   // November - autumn
            12 -> 0.85   // December - winter
            else -> 1.0
        }
    }

    /**
     * Human-readable description of the seasonal factor for the target month.
     */
    private fun getSeasonalDescription(month: Int): String {
        return when (month) {
            4, 5, 6   -> "Summer peak - expect higher usage due to AC/cooler"
            7, 8, 9   -> "Monsoon season - moderate usage expected"
            10, 11    -> "Post-monsoon - typically lower bills"
            12, 1, 2  -> "Winter - lower usage (unless using heaters in the north)"
            3         -> "Spring - moderate, pre-summer usage"
            else      -> "Seasonal adjustment applied"
        }
    }

    // ======================== Utilities ========================

    /**
     * Determines qualitative trend from the regression slope.
     * A slope > 50 Rs/month is INCREASING; < -50 is DECREASING.
     */
    private fun determineTrend(slope: Double): PredictionTrend {
        return when {
            slope > 50.0  -> PredictionTrend.INCREASING
            slope < -50.0 -> PredictionTrend.DECREASING
            else          -> PredictionTrend.STABLE
        }
    }

    /** Returns the next month number (wrapping Dec -> Jan). */
    private fun nextMonth(current: Int): Int = if (current == 12) 1 else current + 1

    /** Round to two decimal places. */
    private fun roundTwo(value: Double): Double = (value * 100.0).roundToLong() / 100.0
}
