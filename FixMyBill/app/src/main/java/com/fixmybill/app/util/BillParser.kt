package com.fixmybill.app.util

import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.model.BillType
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Parses OCR-extracted text from utility bill images into structured Bill objects.
 * Supports electricity, water, and gas bills from various Indian utility providers.
 */
@Singleton
class BillParser @Inject constructor() {

    data class ParsedBillData(
        val consumerNumber: String? = null,
        val billingPeriodStart: LocalDate? = null,
        val billingPeriodEnd: LocalDate? = null,
        val previousReading: Long? = null,
        val currentReading: Long? = null,
        val unitsConsumed: Int? = null,
        val totalAmount: Double? = null,
        val billType: BillType? = null,
        val utilityProvider: String? = null,
        val state: String? = null
    ) {
        val isComplete: Boolean
            get() = consumerNumber != null &&
                    billingPeriodStart != null &&
                    billingPeriodEnd != null &&
                    previousReading != null &&
                    currentReading != null &&
                    unitsConsumed != null &&
                    totalAmount != null

        val missingFields: List<String>
            get() = buildList {
                if (consumerNumber == null) add("Consumer Number")
                if (billingPeriodStart == null) add("Billing Period Start")
                if (billingPeriodEnd == null) add("Billing Period End")
                if (previousReading == null) add("Previous Reading")
                if (currentReading == null) add("Current Reading")
                if (unitsConsumed == null) add("Units Consumed")
                if (totalAmount == null) add("Total Amount")
            }
    }

    /**
     * Parses raw OCR text into a structured ParsedBillData object.
     * Extracts key fields using regex patterns common to Indian utility bills.
     */
    fun parseOcrText(ocrText: String): ParsedBillData {
        val normalizedText = ocrText.lowercase().trim()

        return ParsedBillData(
            consumerNumber = extractConsumerNumber(normalizedText),
            billingPeriodStart = extractBillingPeriodStart(normalizedText),
            billingPeriodEnd = extractBillingPeriodEnd(normalizedText),
            previousReading = extractPreviousReading(normalizedText),
            currentReading = extractCurrentReading(normalizedText),
            unitsConsumed = extractUnitsConsumed(normalizedText),
            totalAmount = extractTotalAmount(normalizedText),
            billType = detectBillType(normalizedText),
            utilityProvider = extractUtilityProvider(ocrText),
            state = detectState(ocrText)
        )
    }

    /**
     * Converts parsed bill data into a domain Bill object.
     * Returns null if required fields are missing.
     */
    fun toBill(parsedData: ParsedBillData): Bill? {
        if (!parsedData.isComplete) return null

        return Bill(
            consumerNumber = parsedData.consumerNumber!!,
            billingPeriod = Pair(parsedData.billingPeriodStart!!, parsedData.billingPeriodEnd!!),
            meterReadings = Pair(parsedData.previousReading!!, parsedData.currentReading!!),
            unitsConsumed = parsedData.unitsConsumed!!,
            totalAmount = parsedData.totalAmount!!,
            billType = parsedData.billType ?: BillType.ELECTRICITY,
            utilityProvider = parsedData.utilityProvider ?: "Unknown",
            state = parsedData.state ?: "Unknown",
            createdAt = LocalDateTime.now()
        )
    }

    // --- Extraction helpers (to be implemented with detailed regex patterns) ---

    private fun extractConsumerNumber(text: String): String? {
        // Common patterns: "consumer no", "consumer number", "ca number", "account no"
        val patterns = listOf(
            """(?:consumer\s*(?:no|number|id)|ca\s*(?:no|number)|account\s*(?:no|number))\s*[:\-]?\s*(\w+[\w\-/]*)""".toRegex(),
            """(?:k\s*no|service\s*no)\s*[:\-]?\s*(\w+[\w\-/]*)""".toRegex()
        )
        for (pattern in patterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.let { return it.uppercase() }
        }
        return null
    }

    private fun extractBillingPeriodStart(text: String): LocalDate? {
        // TODO: Implement date extraction from billing period text
        return null
    }

    private fun extractBillingPeriodEnd(text: String): LocalDate? {
        // TODO: Implement date extraction from billing period text
        return null
    }

    private fun extractPreviousReading(text: String): Long? {
        val pattern = """(?:previous|prev|initial|opening)\s*(?:reading|rdg)\s*[:\-]?\s*(\d+)""".toRegex()
        return pattern.find(text)?.groupValues?.getOrNull(1)?.toLongOrNull()
    }

    private fun extractCurrentReading(text: String): Long? {
        val pattern = """(?:current|present|final|closing)\s*(?:reading|rdg)\s*[:\-]?\s*(\d+)""".toRegex()
        return pattern.find(text)?.groupValues?.getOrNull(1)?.toLongOrNull()
    }

    private fun extractUnitsConsumed(text: String): Int? {
        val pattern = """(?:units?\s*consumed|consumption|total\s*units?)\s*[:\-]?\s*(\d+)""".toRegex()
        return pattern.find(text)?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun extractTotalAmount(text: String): Double? {
        val patterns = listOf(
            """(?:total\s*amount|net\s*(?:amount|payable)|amount\s*payable|grand\s*total)\s*[:\-]?\s*(?:rs\.?|₹)?\s*([\d,]+\.?\d*)""".toRegex(),
            """(?:rs\.?|₹)\s*([\d,]+\.\d{2})""".toRegex()
        )
        for (pattern in patterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.let { match ->
                return match.replace(",", "").toDoubleOrNull()
            }
        }
        return null
    }

    private fun detectBillType(text: String): BillType {
        return when {
            text.contains("electric") || text.contains("kwh") || text.contains("discom") -> BillType.ELECTRICITY
            text.contains("water") || text.contains("jal") || text.contains("kl") -> BillType.WATER
            text.contains("gas") || text.contains("scm") || text.contains("png") -> BillType.GAS
            else -> BillType.ELECTRICITY
        }
    }

    private fun extractUtilityProvider(text: String): String? {
        // TODO: Match against known Indian utility providers
        return null
    }

    private fun detectState(text: String): String? {
        for (state in Constants.SUPPORTED_STATES) {
            if (text.contains(state, ignoreCase = true)) {
                return state
            }
        }
        return null
    }
}
