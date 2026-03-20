package com.fixmybill.app.util

import com.fixmybill.app.domain.model.BillType
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Advanced OCR bill parsing engine that extracts structured data from raw OCR text
 * of Indian electricity bills. Supports all major state electricity boards with
 * provider-specific regex patterns, OCR artefact correction, and per-field
 * confidence scoring.
 */
@Singleton
class BillParsingEngine @Inject constructor() {

    // ======================== Data classes ========================

    data class ParsedBillData(
        val billType: BillType = BillType.ELECTRICITY,
        val utilityProvider: String = "Unknown",
        val consumerNumber: String? = null,
        val billingPeriodStart: String? = null,
        val billingPeriodEnd: String? = null,
        val previousReading: Long? = null,
        val currentReading: Long? = null,
        val unitsConsumed: Int? = null,
        val totalAmount: Double? = null,
        val chargeBreakdown: Map<String, Double> = emptyMap(),
        val dueDate: String? = null,
        val state: String? = null,
        val overallConfidence: Float = 0.5f,
        val fieldConfidences: Map<String, Float> = emptyMap()
    )

    // ======================== Provider metadata ========================

    /** Keywords that identify each electricity board in OCR text. */
    private val providerPatterns = mapOf(
        "TSSPDCL" to listOf("tsspdcl", "telangana southern", "southern power distribution"),
        "TSNPDCL" to listOf("tsnpdcl", "telangana northern", "northern power distribution"),
        "APSPDCL" to listOf("apspdcl", "andhra pradesh southern", "ap southern power"),
        "APEPDCL" to listOf("apepdcl", "apepcdl", "andhra pradesh eastern", "ap eastern power"),
        "BESCOM" to listOf("bescom", "bangalore electricity", "bengaluru electricity"),
        "MESCOM" to listOf("mescom", "mangalore electricity"),
        "MSEDCL" to listOf("msedcl", "maharashtra state electricity", "mahavitaran"),
        "TANGEDCO" to listOf("tangedco", "tneb", "tamil nadu electricity", "tamil nadu generation"),
        "BSES" to listOf("bses", "bses rajdhani", "bses yamuna"),
        "TATA_POWER_DELHI" to listOf("tata power", "tata power delhi", "tpddl"),
        "WBSEDCL" to listOf("wbsedcl", "west bengal state electricity")
    )

    /** Maps provider codes to their home state. */
    private val providerToState = mapOf(
        "TSSPDCL" to "Telangana", "TSNPDCL" to "Telangana",
        "APSPDCL" to "Andhra Pradesh", "APEPDCL" to "Andhra Pradesh",
        "BESCOM" to "Karnataka", "MESCOM" to "Karnataka",
        "MSEDCL" to "Maharashtra",
        "TANGEDCO" to "Tamil Nadu", "TNEB" to "Tamil Nadu",
        "BSES" to "Delhi", "TATA_POWER_DELHI" to "Delhi",
        "WBSEDCL" to "West Bengal"
    )

    // ======================== Public API ========================

    /**
     * Main entry point. Cleans the raw OCR text, detects the provider, and
     * extracts every available field with individual confidence scores.
     */
    fun parseBill(ocrText: String): ParsedBillData {
        val cleanedText = cleanOcrText(ocrText)
        val lowerText = cleanedText.lowercase(Locale.ENGLISH)

        val provider = detectUtilityProvider(lowerText)
        val billType = detectBillType(lowerText)
        val confidences = mutableMapOf<String, Float>()

        // --- Consumer number ---
        val consumerNumber = extractConsumerNumber(lowerText, provider)
        confidences["consumerNumber"] = calculateConfidence(
            consumerNumber, if (provider != "Unknown") "provider_specific" else "generic"
        )

        // --- Billing period ---
        val billingPeriod = extractBillingPeriod(lowerText)
        confidences["billingPeriod"] = calculateConfidence(
            billingPeriod?.first, "regex"
        )

        // --- Meter readings ---
        val meterReadings = extractMeterReadings(lowerText)
        confidences["meterReadings"] = calculateConfidence(
            meterReadings?.first?.toString(), "regex"
        )

        // --- Units consumed (direct or derived from readings) ---
        val directUnits = extractUnitsConsumed(lowerText)
        val derivedUnits = meterReadings?.let { (prev, curr) ->
            (curr - prev).toInt().takeIf { it > 0 }
        }
        val unitsConsumed = directUnits ?: derivedUnits

        // Cross-validate: if both direct and derived are available, check they match
        val unitsConfidence = when {
            directUnits != null && derivedUnits != null && directUnits == derivedUnits -> 0.95f
            directUnits != null && derivedUnits != null && directUnits != derivedUnits -> 0.60f
            directUnits != null -> 0.85f
            derivedUnits != null -> 0.75f
            else -> 0.0f
        }
        confidences["unitsConsumed"] = unitsConfidence

        // --- Total amount ---
        val totalAmount = extractTotalAmount(lowerText)
        confidences["totalAmount"] = calculateConfidence(totalAmount?.toString(), "amount")

        // --- Charge breakdown ---
        val chargeBreakdown = extractChargeBreakdown(lowerText)
        confidences["chargeBreakdown"] = when {
            chargeBreakdown.size >= 4 -> 0.85f
            chargeBreakdown.size >= 2 -> 0.70f
            chargeBreakdown.isNotEmpty() -> 0.55f
            else -> 0.0f
        }

        // Cross-validate: sum of breakdown should roughly equal total
        if (chargeBreakdown.isNotEmpty() && totalAmount != null) {
            val breakdownSum = chargeBreakdown.values.sum()
            val ratio = breakdownSum / totalAmount
            if (ratio in 0.85..1.15) {
                confidences["chargeBreakdown"] = (confidences["chargeBreakdown"]!! + 0.10f).coerceAtMost(0.95f)
            }
        }

        // --- Due date ---
        val dueDate = extractDueDate(lowerText)
        confidences["dueDate"] = calculateConfidence(dueDate, "regex")

        // --- State ---
        val state = providerToState[provider] ?: detectState(lowerText)

        val overallConfidence = calculateOverallConfidence(confidences)

        return ParsedBillData(
            billType = billType,
            utilityProvider = provider,
            consumerNumber = consumerNumber,
            billingPeriodStart = billingPeriod?.first,
            billingPeriodEnd = billingPeriod?.second,
            previousReading = meterReadings?.first,
            currentReading = meterReadings?.second,
            unitsConsumed = unitsConsumed,
            totalAmount = totalAmount,
            chargeBreakdown = chargeBreakdown,
            dueDate = dueDate,
            state = state,
            overallConfidence = overallConfidence,
            fieldConfidences = confidences
        )
    }

    // ======================== OCR text cleanup ========================

    /**
     * Fixes the most frequent OCR misrecognitions:
     * - O (letter) read as 0 (zero) and vice-versa within digit sequences
     * - l / I read as 1 within digit sequences
     * - Spurious spaces splitting a number in two
     * - Multiple consecutive whitespace characters
     */
    private fun cleanOcrText(text: String): String {
        return text
            // O/o within digits -> 0
            .replace(Regex("""(?<=\d)[Oo](?=\d)"""), "0")
            // l/I within digits -> 1
            .replace(Regex("""(?<=\d)[lI](?=\d)"""), "1")
            // Remove spaces inside numbers (e.g. "12 345" -> "12345")
            .replace(Regex("""(\d)\s+(\d)"""), "$1$2")
            // Fix missing decimal: e.g. "Rs 1234 50" -> "Rs 1234.50" when preceded by currency
            .replace(Regex("""(?:rs\.?|\u20b9|inr)\s*(\d+)\s+(\d{2})(?=\s|$)""", RegexOption.IGNORE_CASE)) {
                "${it.groupValues[0].substringBefore(it.groupValues[1])}${it.groupValues[1]}.${it.groupValues[2]}"
            }
            // Normalise whitespace
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    // ======================== Detection methods ========================

    private fun detectBillType(text: String): BillType {
        return when {
            text.contains("electric") || text.contains("kwh") || text.contains("kw h")
                    || text.contains("discom")
                    || (text.contains("unit") && text.contains("slab")) -> BillType.ELECTRICITY
            text.contains("water") || text.contains("jal") || text.contains("kl ")
                    || text.contains("kilolitre") -> BillType.WATER
            text.contains("gas") || text.contains("scm") || text.contains("png")
                    || text.contains("piped natural") -> BillType.GAS
            else -> BillType.ELECTRICITY
        }
    }

    private fun detectUtilityProvider(text: String): String {
        for ((provider, keywords) in providerPatterns) {
            if (keywords.any { text.contains(it) }) return provider
        }
        return "Unknown"
    }

    // ======================== Extraction methods ========================

    /**
     * Extracts the consumer / service / account number using provider-specific
     * patterns first, falling back to generic patterns.
     *
     * Supported label variants:
     * - TSSPDCL/TSNPDCL: "Consumer No", "Service Number"
     * - BESCOM: "RR Number"
     * - MSEDCL: "Consumer No"
     * - TANGEDCO: "Service Connection No", "Assessment"
     * - BSES/Tata Power Delhi: "CA Number", "Contract Account"
     * - APSPDCL: "Service Number", "CC Bill"
     * - WBSEDCL: "Consumer ID", "Consumer No"
     */
    private fun extractConsumerNumber(text: String, provider: String): String? {
        val patterns: List<Regex> = when (provider) {
            "TSSPDCL", "TSNPDCL" -> listOf(
                """(?:consumer\s*no|service\s*number|sc\s*no|consumer\s*id)\s*[:\-.]?\s*(\d{10,13})""".toRegex(),
                """(?:consumer|svc)\s*[:\-.]?\s*(\d{10,13})""".toRegex()
            )
            "BESCOM", "MESCOM" -> listOf(
                """(?:rr\s*number|rr\s*no|account\s*(?:no|id)|installation\s*no)\s*[:\-.]?\s*(\d{8,12})""".toRegex(),
                """(?:meter\s*reading)\s*.*?(\d{8,12})""".toRegex()
            )
            "MSEDCL" -> listOf(
                """(?:consumer\s*no|consumer\s*number|consumer\s*id)\s*[:\-.]?\s*(\d{12,16})""".toRegex(),
                """(?:bu\s*no|billing\s*unit)\s*[:\-.]?\s*(\d{12,16})""".toRegex()
            )
            "TANGEDCO", "TNEB" -> listOf(
                """(?:service\s*connection\s*no|sc\s*no|assessment\s*no)\s*[:\-.]?\s*(\d{8,12})""".toRegex(),
                """(?:assessment)\s*[:\-.]?\s*(\d{8,12})""".toRegex()
            )
            "BSES", "TATA_POWER_DELHI" -> listOf(
                """(?:ca\s*number|contract\s*account|ca\s*no)\s*[:\-.]?\s*(\d{10,12})""".toRegex(),
                """(?:contract\s*acc)\s*[:\-.]?\s*(\d{10,12})""".toRegex()
            )
            "APSPDCL", "APEPDCL" -> listOf(
                """(?:service\s*number|consumer\s*no|svc\s*no|cc\s*bill\s*no)\s*[:\-.]?\s*(\d{10,13})""".toRegex()
            )
            "WBSEDCL" -> listOf(
                """(?:consumer\s*id|consumer\s*no|consumer\s*number)\s*[:\-.]?\s*(\d{10,14})""".toRegex()
            )
            else -> emptyList()
        }

        // Try provider-specific patterns
        for (pattern in patterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.let { return it }
        }

        // Generic fallback
        val genericPatterns = listOf(
            """(?:consumer\s*(?:no|number|id)|account\s*(?:no|number|id)|ca\s*(?:no|number)|service\s*(?:no|number))\s*[:\-.]?\s*(\w[\w\-/]{6,15})""".toRegex(),
            """(?:k\s*no)\s*[:\-.]?\s*(\d{8,16})""".toRegex()
        )
        for (pattern in genericPatterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.let { return it.uppercase() }
        }
        return null
    }

    /**
     * Extracts billing period start and end dates as string pairs.
     * Handles multiple date formats found across Indian utility bills.
     */
    private fun extractBillingPeriod(text: String): Pair<String, String>? {
        val patterns = listOf(
            // "billing period: 01-Jan-2024 to 31-Jan-2024"
            """(?:billing\s*period|bill\s*period|period)\s*[:\-]?\s*(\d{1,2}[\-/]\w{3}[\-/]\d{2,4})\s*(?:to|[-\u2013])\s*(\d{1,2}[\-/]\w{3}[\-/]\d{2,4})""".toRegex(),
            // "from 01/01/2024 to 31/01/2024"
            """(?:from|period)\s*[:\-]?\s*(\d{1,2}[\-/]\d{1,2}[\-/]\d{2,4})\s*(?:to|[-\u2013])\s*(\d{1,2}[\-/]\d{1,2}[\-/]\d{2,4})""".toRegex(),
            // "billing period: 01-01-2024 - 31-01-2024"
            """(?:billing\s*period|bill\s*period)\s*[:\-]?\s*(\d{1,2}[\-/]\d{1,2}[\-/]\d{2,4})\s*[-\u2013]\s*(\d{1,2}[\-/]\d{1,2}[\-/]\d{2,4})""".toRegex(),
            // "bill date: 15-Jan-2024" (single date, use as both start and end)
            """(?:bill\s*date)\s*[:\-]?\s*(\d{1,2}[\-/]\w{3}[\-/]\d{2,4})""".toRegex(),
            // "month: January 2024"
            """(?:month|bill\s*month)\s*[:\-]?\s*(\w+\s*\d{4})""".toRegex()
        )

        for (pattern in patterns) {
            val match = pattern.find(text) ?: continue
            val start = match.groupValues.getOrNull(1) ?: continue
            val end = match.groupValues.getOrNull(2).takeIf { !it.isNullOrBlank() } ?: start
            return start to end
        }
        return null
    }

    /**
     * Extracts previous and current meter readings. Tries explicit labels first,
     * then falls back to tabular patterns where two numbers appear side by side.
     */
    private fun extractMeterReadings(text: String): Pair<Long, Long>? {
        val prevPatterns = listOf(
            """(?:previous|prev|initial|opening|old)\s*(?:reading|rdg|meter)\s*[:\-]?\s*(\d{3,8})""".toRegex()
        )
        val currPatterns = listOf(
            """(?:current|present|final|closing|new)\s*(?:reading|rdg|meter)\s*[:\-]?\s*(\d{3,8})""".toRegex()
        )

        var prevReading: Long? = null
        var currReading: Long? = null

        for (p in prevPatterns) {
            p.find(text)?.groupValues?.getOrNull(1)?.toLongOrNull()?.let { prevReading = it }
        }
        for (p in currPatterns) {
            p.find(text)?.groupValues?.getOrNull(1)?.toLongOrNull()?.let { currReading = it }
        }

        if (prevReading != null && currReading != null) {
            return prevReading!! to currReading!!
        }

        // Tabular format: "Meter Reading  12345  12678"
        val twoNumberPattern =
            """(?:meter\s*reading|reading)\s*[:\-]?\s*(\d{3,8})\s+(\d{3,8})""".toRegex()
        twoNumberPattern.find(text)?.let { match ->
            val first = match.groupValues[1].toLongOrNull()
            val second = match.groupValues[2].toLongOrNull()
            if (first != null && second != null) {
                return if (first < second) first to second else second to first
            }
        }

        return null
    }

    /**
     * Extracts units consumed. Recognises labels used by each board:
     * "Units Consumed", "Consumption", "Total Units", "kWh Consumed",
     * "Assessment" (TANGEDCO), and bare "NNN units".
     */
    private fun extractUnitsConsumed(text: String): Int? {
        val patterns = listOf(
            """(?:units?\s*consumed|consumption|total\s*units?|kwh\s*consumed|units?\s*used|assessment)\s*[:\-]?\s*(\d{1,6})""".toRegex(),
            """(?:cc\s*bill|current\s*charges)\s*.*?(\d{1,6})\s*(?:units?|kwh)""".toRegex(),
            """(\d{1,6})\s*(?:units?|kwh)\b""".toRegex()
        )
        for (pattern in patterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.toIntOrNull()?.let {
                if (it in 1..99999) return it
            }
        }
        return null
    }

    /**
     * Extracts the total payable amount. Handles labels such as:
     * "Amount Payable", "Net Amount", "Total Amount", "Amount Due",
     * "Current Charges", "Grand Total".
     */
    private fun extractTotalAmount(text: String): Double? {
        val patterns = listOf(
            """(?:total\s*amount|net\s*(?:amount|payable)|amount\s*(?:payable|due)|grand\s*total|total\s*(?:payable|due))\s*[:\-]?\s*(?:rs\.?|\u20b9|inr)?\s*([\d,]+\.?\d*)""".toRegex(),
            """(?:amount\s*after\s*due\s*date|total\s*current\s*charges|current\s*charges)\s*[:\-]?\s*(?:rs\.?|\u20b9)?\s*([\d,]+\.?\d*)""".toRegex(),
            """(?:net\s*bill\s*amount|current\s*bill\s*amount|cc\s*bill\s*amount)\s*[:\-]?\s*(?:rs\.?|\u20b9)?\s*([\d,]+\.?\d*)""".toRegex(),
            // Bare currency match as last resort
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

    /**
     * Extracts individual charge components from the bill text.
     * Returns a map of component name to amount. Recognised components:
     * Energy/Consumption Charges, Fixed/Customer/Demand Charges,
     * Fuel Surcharge/FAC/FPPCA, Electricity Duty, Tax/GST, Arrears, Rebate.
     */
    private fun extractChargeBreakdown(text: String): Map<String, Double> {
        val charges = mutableMapOf<String, Double>()

        val chargePatterns = mapOf(
            "Energy Charges" to listOf(
                """(?:energy\s*charges?|current\s*charges?|consumption\s*charges?|slab\s*charges?)\s*[:\-]?\s*(?:rs\.?|\u20b9)?\s*([\d,]+\.?\d*)""".toRegex()
            ),
            "Fixed Charges" to listOf(
                """(?:fixed\s*charges?|customer\s*charges?|demand\s*charges?|meter\s*rent|monthly\s*minimum\s*charges?)\s*[:\-]?\s*(?:rs\.?|\u20b9)?\s*([\d,]+\.?\d*)""".toRegex()
            ),
            "Fuel Surcharge" to listOf(
                """(?:fuel\s*(?:surcharge|adjustment)|fac|fppca|fuel\s*cost\s*adjustment|fsa)\s*[:\-]?\s*(?:rs\.?|\u20b9)?\s*([\d,]+\.?\d*)""".toRegex()
            ),
            "Electricity Duty" to listOf(
                """(?:electricity\s*duty|ed|govt\.?\s*duty|state\s*duty|e\.?\s*d\.?)\s*[:\-]?\s*(?:rs\.?|\u20b9)?\s*([\d,]+\.?\d*)""".toRegex()
            ),
            "Tax" to listOf(
                """(?:tax|gst|cgst|sgst|cess|additional\s*surcharge)\s*[:\-]?\s*(?:rs\.?|\u20b9)?\s*([\d,]+\.?\d*)""".toRegex()
            ),
            "Arrears" to listOf(
                """(?:arrears?|previous\s*balance|outstanding|due\s*amount|pending\s*amount)\s*[:\-]?\s*(?:rs\.?|\u20b9)?\s*([\d,]+\.?\d*)""".toRegex()
            ),
            "Rebate" to listOf(
                """(?:rebate|discount|subsidy|incentive|govt\.?\s*subsidy)\s*[:\-]?\s*(?:rs\.?|\u20b9)?\s*-?([\d,]+\.?\d*)""".toRegex()
            ),
            "Wheeling Charges" to listOf(
                """(?:wheeling\s*charges?)\s*[:\-]?\s*(?:rs\.?|\u20b9)?\s*([\d,]+\.?\d*)""".toRegex()
            ),
            "Meter Rent" to listOf(
                """(?:meter\s*rent|meter\s*charges?)\s*[:\-]?\s*(?:rs\.?|\u20b9)?\s*([\d,]+\.?\d*)""".toRegex()
            )
        )

        for ((name, patterns) in chargePatterns) {
            for (pattern in patterns) {
                pattern.find(text)?.groupValues?.getOrNull(1)?.let { match ->
                    val amount = match.replace(",", "").toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        charges[name] = amount
                        return@let // found for this component, skip remaining patterns
                    }
                }
            }
        }

        return charges
    }

    /**
     * Extracts the payment due date. Supports formats like
     * "Due Date: 15-Jan-2024", "Pay By: 15/01/2024", "Last Date: 15.01.2024".
     */
    private fun extractDueDate(text: String): String? {
        val patterns = listOf(
            """(?:due\s*date|pay\s*(?:by|before)|last\s*date|payment\s*due)\s*[:\-]?\s*(\d{1,2}[\-/\.]\w{3,9}[\-/\.]\d{2,4})""".toRegex(),
            """(?:due\s*date|pay\s*(?:by|before)|last\s*date|payment\s*due)\s*[:\-]?\s*(\d{1,2}[\-/\.]\d{1,2}[\-/\.]\d{2,4})""".toRegex()
        )
        for (pattern in patterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.let { return it }
        }
        return null
    }

    // ======================== State detection ========================

    /**
     * Detects the state from city names and state names present in the OCR text.
     */
    private fun detectState(text: String): String? {
        val stateKeywords = mapOf(
            "Telangana" to listOf("telangana", "hyderabad", "warangal", "nizamabad", "khammam", "karimnagar"),
            "Andhra Pradesh" to listOf("andhra pradesh", "vishakhapatnam", "vijayawada", "tirupati", "guntur", "nellore"),
            "Karnataka" to listOf("karnataka", "bangalore", "bengaluru", "mysore", "mangalore", "hubli"),
            "Maharashtra" to listOf("maharashtra", "mumbai", "pune", "nagpur", "thane", "nashik", "aurangabad"),
            "Tamil Nadu" to listOf("tamil nadu", "tamilnadu", "chennai", "coimbatore", "madurai", "salem", "trichy"),
            "Delhi" to listOf("delhi", "new delhi"),
            "West Bengal" to listOf("west bengal", "kolkata", "howrah", "durgapur", "siliguri"),
            "Uttar Pradesh" to listOf("uttar pradesh", "lucknow", "noida", "agra", "varanasi", "kanpur"),
            "Gujarat" to listOf("gujarat", "ahmedabad", "surat", "vadodara", "rajkot"),
            "Rajasthan" to listOf("rajasthan", "jaipur", "jodhpur", "udaipur", "kota"),
            "Kerala" to listOf("kerala", "kochi", "thiruvananthapuram", "kozhikode", "thrissur"),
            "Madhya Pradesh" to listOf("madhya pradesh", "bhopal", "indore", "jabalpur", "gwalior")
        )

        for ((state, keywords) in stateKeywords) {
            if (keywords.any { text.contains(it) }) return state
        }
        return null
    }

    // ======================== Confidence scoring ========================

    /**
     * Returns a confidence score for a single extracted field based on
     * whether a value was found and the extraction method used.
     */
    private fun calculateConfidence(field: String?, method: String): Float {
        if (field == null) return 0.0f
        return when (method) {
            "provider_specific" -> 0.90f
            "amount" -> 0.88f
            "regex" -> 0.80f
            "generic" -> 0.65f
            "fallback" -> 0.50f
            else -> 0.70f
        }
    }

    /**
     * Computes a weighted overall confidence from individual field confidences.
     * Fields that are more critical to bill analysis receive higher weights.
     */
    private fun calculateOverallConfidence(fieldConfidences: Map<String, Float>): Float {
        if (fieldConfidences.isEmpty()) return 0.1f

        val weights = mapOf(
            "totalAmount" to 3.0f,
            "unitsConsumed" to 2.5f,
            "consumerNumber" to 2.0f,
            "chargeBreakdown" to 2.0f,
            "billingPeriod" to 1.5f,
            "meterReadings" to 1.5f,
            "dueDate" to 0.5f
        )

        var weightedSum = 0f
        var totalWeight = 0f

        for ((field, confidence) in fieldConfidences) {
            val weight = weights[field] ?: 1.0f
            weightedSum += confidence * weight
            totalWeight += weight
        }

        return if (totalWeight > 0) (weightedSum / totalWeight).coerceIn(0.1f, 0.99f) else 0.1f
    }
}
