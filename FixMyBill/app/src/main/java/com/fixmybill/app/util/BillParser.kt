package com.fixmybill.app.util

import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.model.BillType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Parses OCR-extracted text from utility bill images into structured Bill objects.
 * Supports electricity, water, and gas bills from various Indian utility providers.
 *
 * Handles common OCR artefacts such as O/0, l/1 confusion, stray spaces in numbers,
 * and missing decimal points.
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

    // ---------- Date format patterns commonly found on Indian bills ----------

    private val dateFormatters = listOf(
        DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH),  // 01-Jan-2024
        DateTimeFormatter.ofPattern("dd/MMM/yyyy", Locale.ENGLISH),  // 01/Jan/2024
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),                    // 01-01-2024
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),                    // 01/01/2024
        DateTimeFormatter.ofPattern("dd.MM.yyyy"),                    // 01.01.2024
        DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH),   // 01-Jan-24
        DateTimeFormatter.ofPattern("dd/MM/yy"),                      // 01/01/24
        DateTimeFormatter.ofPattern("yyyy-MM-dd")                     // 2024-01-01
    )

    /**
     * Parses raw OCR text into a structured ParsedBillData object.
     * Extracts key fields using regex patterns common to Indian utility bills.
     */
    fun parseOcrText(ocrText: String): ParsedBillData {
        val cleanedText = cleanOcrText(ocrText)
        val normalizedText = cleanedText.lowercase(Locale.ENGLISH).trim()

        val provider = extractUtilityProvider(cleanedText)
        val billingPeriod = extractBillingPeriod(normalizedText)
        val previousReading = extractPreviousReading(normalizedText)
        val currentReading = extractCurrentReading(normalizedText)
        val directUnits = extractUnitsConsumed(normalizedText)

        // Derive units from meter readings if not found directly
        val unitsConsumed = directUnits
            ?: if (previousReading != null && currentReading != null && currentReading > previousReading) {
                (currentReading - previousReading).toInt()
            } else null

        return ParsedBillData(
            consumerNumber = extractConsumerNumber(normalizedText, provider),
            billingPeriodStart = billingPeriod?.first,
            billingPeriodEnd = billingPeriod?.second,
            previousReading = previousReading,
            currentReading = currentReading,
            unitsConsumed = unitsConsumed,
            totalAmount = extractTotalAmount(normalizedText),
            billType = detectBillType(normalizedText),
            utilityProvider = provider,
            state = detectState(cleanedText)
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

    // ==================== OCR text cleanup ====================

    /**
     * Cleans common OCR artefacts:
     * - O/0 confusion when surrounded by digits
     * - l/I/1 confusion when surrounded by digits
     * - Stray spaces inside digit sequences
     * - Normalises whitespace
     */
    private fun cleanOcrText(text: String): String {
        return text
            .replace(Regex("""(?<=\d)[Oo](?=\d)"""), "0")
            .replace(Regex("""(?<=\d)[lI](?=\d)"""), "1")
            .replace(Regex("""(\d)\s+(\d)"""), "$1$2")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    // ==================== Field extractors ====================

    private fun extractConsumerNumber(text: String, provider: String?): String? {
        // Provider-specific patterns first
        val providerPatterns: List<Regex> = when (provider?.uppercase()) {
            "TSSPDCL", "TSNPDCL" -> listOf(
                """(?:consumer\s*no|service\s*number|sc\s*no)\s*[:\-.]?\s*(\d{10,13})""".toRegex(),
                """(?:consumer|svc)\s*[:\-.]?\s*(\d{10,13})""".toRegex()
            )
            "BESCOM", "MESCOM" -> listOf(
                """(?:rr\s*number|rr\s*no|account\s*(?:no|id))\s*[:\-.]?\s*(\d{8,12})""".toRegex()
            )
            "MSEDCL" -> listOf(
                """(?:consumer\s*no|consumer\s*number)\s*[:\-.]?\s*(\d{12,16})""".toRegex()
            )
            "TANGEDCO", "TNEB" -> listOf(
                """(?:service\s*connection\s*no|sc\s*no|assessment)\s*[:\-.]?\s*(\d{8,12})""".toRegex()
            )
            "BSES", "TATA_POWER_DELHI", "TPDDL" -> listOf(
                """(?:ca\s*number|contract\s*account|ca\s*no)\s*[:\-.]?\s*(\d{10,12})""".toRegex()
            )
            "APSPDCL", "APEPDCL" -> listOf(
                """(?:service\s*number|consumer\s*no|svc\s*no)\s*[:\-.]?\s*(\d{10,13})""".toRegex()
            )
            "WBSEDCL" -> listOf(
                """(?:consumer\s*id|consumer\s*no)\s*[:\-.]?\s*(\d{10,14})""".toRegex()
            )
            else -> emptyList()
        }

        for (pattern in providerPatterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.let { return it.uppercase() }
        }

        // Generic fallback patterns
        val genericPatterns = listOf(
            """(?:consumer\s*(?:no|number|id)|account\s*(?:no|number|id)|ca\s*(?:no|number))\s*[:\-.]?\s*(\w[\w\-/]{6,15})""".toRegex(),
            """(?:k\s*no|service\s*(?:no|number))\s*[:\-.]?\s*(\d{8,16})""".toRegex()
        )
        for (pattern in genericPatterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.let { return it.uppercase() }
        }
        return null
    }

    private fun extractBillingPeriod(text: String): Pair<LocalDate, LocalDate>? {
        // Pattern: "billing period: 01-Jan-2024 to 31-Jan-2024"
        val periodPatterns = listOf(
            """(?:billing\s*period|bill\s*period|period)\s*[:\-]?\s*(\d{1,2}[\-/\.]\w{3,9}[\-/\.]\d{2,4})\s*(?:to|[-\u2013])\s*(\d{1,2}[\-/\.]\w{3,9}[\-/\.]\d{2,4})""".toRegex(RegexOption.IGNORE_CASE),
            """(?:from|period)\s*[:\-]?\s*(\d{1,2}[\-/\.]\d{1,2}[\-/\.]\d{2,4})\s*(?:to|[-\u2013])\s*(\d{1,2}[\-/\.]\d{1,2}[\-/\.]\d{2,4})""".toRegex(RegexOption.IGNORE_CASE)
        )

        for (pattern in periodPatterns) {
            val match = pattern.find(text)
            if (match != null) {
                val startStr = match.groupValues.getOrNull(1) ?: continue
                val endStr = match.groupValues.getOrNull(2) ?: continue
                val start = tryParseDate(startStr) ?: continue
                val end = tryParseDate(endStr) ?: continue
                return start to end
            }
        }

        // Try month-year pattern: "month: January 2024"
        val monthYearPattern =
            """(?:month|bill\s*month)\s*[:\-]?\s*(\w+)\s*(\d{4})""".toRegex(RegexOption.IGNORE_CASE)
        monthYearPattern.find(text)?.let { match ->
            val monthStr = match.groupValues.getOrNull(1) ?: return@let
            val yearStr = match.groupValues.getOrNull(2) ?: return@let
            val year = yearStr.toIntOrNull() ?: return@let
            val month = parseMonthName(monthStr) ?: return@let
            val start = LocalDate.of(year, month, 1)
            val end = start.withDayOfMonth(start.lengthOfMonth())
            return start to end
        }

        return null
    }

    private fun extractPreviousReading(text: String): Long? {
        val patterns = listOf(
            """(?:previous|prev|initial|opening|old)\s*(?:reading|rdg|meter)\s*[:\-]?\s*(\d{3,8})""".toRegex(),
            """(?:meter\s*reading)\s*[:\-]?\s*(\d{3,8})\s+\d{3,8}""".toRegex()
        )
        for (pattern in patterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.toLongOrNull()?.let { return it }
        }
        return null
    }

    private fun extractCurrentReading(text: String): Long? {
        val patterns = listOf(
            """(?:current|present|final|closing|new)\s*(?:reading|rdg|meter)\s*[:\-]?\s*(\d{3,8})""".toRegex(),
            """(?:meter\s*reading)\s*[:\-]?\s*\d{3,8}\s+(\d{3,8})""".toRegex()
        )
        for (pattern in patterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.toLongOrNull()?.let { return it }
        }
        return null
    }

    private fun extractUnitsConsumed(text: String): Int? {
        val patterns = listOf(
            """(?:units?\s*consumed|consumption|total\s*units?|kwh\s*consumed|units?\s*used)\s*[:\-]?\s*(\d{1,6})""".toRegex(),
            """(\d{1,6})\s*(?:units?|kwh)""".toRegex()
        )
        for (pattern in patterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.toIntOrNull()?.let {
                if (it in 1..99999) return it
            }
        }
        return null
    }

    private fun extractTotalAmount(text: String): Double? {
        val patterns = listOf(
            """(?:total\s*amount|net\s*(?:amount|payable)|amount\s*(?:payable|due)|grand\s*total|total\s*(?:payable|due))\s*[:\-]?\s*(?:rs\.?|inr|\u20b9)?\s*([\d,]+\.?\d*)""".toRegex(),
            """(?:amount\s*after\s*due\s*date|total\s*current\s*charges)\s*[:\-]?\s*(?:rs\.?|\u20b9)?\s*([\d,]+\.?\d*)""".toRegex(),
            """(?:net\s*bill\s*amount|current\s*bill\s*amount)\s*[:\-]?\s*(?:rs\.?|\u20b9)?\s*([\d,]+\.?\d*)""".toRegex(),
            """(?:rs\.?|\u20b9)\s*([\d,]+\.\d{2})""".toRegex()
        )
        for (pattern in patterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.let { match ->
                val amount = match.replace(",", "").toDoubleOrNull()
                if (amount != null && amount > 0) return amount
            }
        }
        return null
    }

    // ==================== Detection helpers ====================

    private fun detectBillType(text: String): BillType {
        return when {
            text.contains("electric") || text.contains("kwh") || text.contains("discom")
                    || (text.contains("unit") && text.contains("slab")) -> BillType.ELECTRICITY
            text.contains("water") || text.contains("jal") || text.contains("kl")
                    || text.contains("kilolitre") -> BillType.WATER
            text.contains("gas") || text.contains("scm") || text.contains("png")
                    || text.contains("piped natural") -> BillType.GAS
            else -> BillType.ELECTRICITY
        }
    }

    private fun extractUtilityProvider(text: String): String? {
        val providerKeywords = mapOf(
            "TSSPDCL" to listOf("tsspdcl", "telangana southern", "southern power distribution"),
            "TSNPDCL" to listOf("tsnpdcl", "telangana northern", "northern power distribution"),
            "APSPDCL" to listOf("apspdcl", "andhra pradesh southern", "ap southern power"),
            "APEPDCL" to listOf("apepdcl", "andhra pradesh eastern", "ap eastern power"),
            "BESCOM" to listOf("bescom", "bangalore electricity", "bengaluru electricity"),
            "MESCOM" to listOf("mescom", "mangalore electricity"),
            "MSEDCL" to listOf("msedcl", "maharashtra state electricity", "mahavitaran"),
            "TANGEDCO" to listOf("tangedco", "tneb", "tamil nadu electricity", "tamil nadu generation"),
            "BSES" to listOf("bses", "bses rajdhani", "bses yamuna"),
            "TATA_POWER_DELHI" to listOf("tata power", "tata power delhi", "tpddl"),
            "WBSEDCL" to listOf("wbsedcl", "west bengal state electricity")
        )
        val lower = text.lowercase(Locale.ENGLISH)
        for ((provider, keywords) in providerKeywords) {
            if (keywords.any { lower.contains(it) }) return provider
        }
        return null
    }

    private fun detectState(text: String): String? {
        for (state in Constants.SUPPORTED_STATES) {
            if (text.contains(state, ignoreCase = true)) return state
        }
        return null
    }

    // ==================== Date parsing utilities ====================

    private fun tryParseDate(raw: String): LocalDate? {
        val cleaned = raw.trim()
        for (formatter in dateFormatters) {
            try {
                return LocalDate.parse(cleaned, formatter)
            } catch (_: Exception) { /* try next */ }
        }
        return null
    }

    private fun parseMonthName(name: String): Int? {
        val lower = name.lowercase(Locale.ENGLISH).take(3)
        return mapOf(
            "jan" to 1, "feb" to 2, "mar" to 3, "apr" to 4,
            "may" to 5, "jun" to 6, "jul" to 7, "aug" to 8,
            "sep" to 9, "oct" to 10, "nov" to 11, "dec" to 12
        )[lower]
    }
}
