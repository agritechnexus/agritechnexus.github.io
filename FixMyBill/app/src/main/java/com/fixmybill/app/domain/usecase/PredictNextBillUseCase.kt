package com.fixmybill.app.domain.usecase

import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.repository.BillRepository
import javax.inject.Inject

data class BillPrediction(
    val predictedAmount: Double,
    val predictedUnits: Int,
    val confidence: Float,
    val trend: String
)

class PredictNextBillUseCase @Inject constructor(
    private val billRepository: BillRepository
) {

    suspend operator fun invoke(pastBills: List<Bill>): BillPrediction {
        if (pastBills.isEmpty()) {
            return BillPrediction(
                predictedAmount = 0.0,
                predictedUnits = 0,
                confidence = 0f,
                trend = "Insufficient data for prediction."
            )
        }

        if (pastBills.size == 1) {
            val bill = pastBills.first()
            return BillPrediction(
                predictedAmount = bill.totalAmount,
                predictedUnits = bill.unitsConsumed,
                confidence = 0.2f,
                trend = "Only one bill available. Prediction is based on a single data point."
            )
        }

        val sortedBills = pastBills.sortedBy { it.billingPeriod.first }

        // Simple linear regression on amounts
        val amountRegression = linearRegression(
            sortedBills.mapIndexed { index, _ -> index.toDouble() },
            sortedBills.map { it.totalAmount }
        )

        // Simple linear regression on units
        val unitsRegression = linearRegression(
            sortedBills.mapIndexed { index, _ -> index.toDouble() },
            sortedBills.map { it.unitsConsumed.toDouble() }
        )

        val nextIndex = sortedBills.size.toDouble()
        val predictedAmount = (amountRegression.slope * nextIndex + amountRegression.intercept)
            .coerceAtLeast(0.0)
        val predictedUnits = (unitsRegression.slope * nextIndex + unitsRegression.intercept)
            .coerceAtLeast(0.0)
            .toInt()

        val trend = when {
            amountRegression.slope > 10 -> "Your bills are trending upward. Consider reviewing your usage patterns."
            amountRegression.slope < -10 -> "Your bills are trending downward. Your conservation efforts are paying off."
            else -> "Your bills have been relatively stable."
        }

        // Confidence based on R-squared and number of data points
        val confidence = calculateConfidence(amountRegression.rSquared, sortedBills.size)

        return BillPrediction(
            predictedAmount = Math.round(predictedAmount * 100.0) / 100.0,
            predictedUnits = predictedUnits,
            confidence = confidence,
            trend = trend
        )
    }

    private fun linearRegression(x: List<Double>, y: List<Double>): RegressionResult {
        val n = x.size
        val sumX = x.sum()
        val sumY = y.sum()
        val sumXY = x.zip(y).sumOf { it.first * it.second }
        val sumX2 = x.sumOf { it * it }

        val denominator = n * sumX2 - sumX * sumX
        if (denominator == 0.0) {
            return RegressionResult(slope = 0.0, intercept = sumY / n, rSquared = 0.0)
        }

        val slope = (n * sumXY - sumX * sumY) / denominator
        val intercept = (sumY - slope * sumX) / n

        // Calculate R-squared
        val meanY = sumY / n
        val ssTotal = y.sumOf { (it - meanY) * (it - meanY) }
        val ssResidual = x.zip(y).sumOf { (xi, yi) ->
            val predicted = slope * xi + intercept
            (yi - predicted) * (yi - predicted)
        }
        val rSquared = if (ssTotal > 0) 1.0 - (ssResidual / ssTotal) else 0.0

        return RegressionResult(slope = slope, intercept = intercept, rSquared = rSquared)
    }

    private fun calculateConfidence(rSquared: Double, dataPoints: Int): Float {
        val rSquaredFactor = rSquared.toFloat() * 0.5f
        val dataPointsFactor = when {
            dataPoints >= 12 -> 0.4f
            dataPoints >= 6 -> 0.3f
            dataPoints >= 3 -> 0.2f
            else -> 0.1f
        }
        return (rSquaredFactor + dataPointsFactor).coerceIn(0.1f, 0.9f)
    }

    private data class RegressionResult(
        val slope: Double,
        val intercept: Double,
        val rSquared: Double
    )
}
