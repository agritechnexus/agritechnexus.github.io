package com.fixmybill.app.util

import com.fixmybill.app.domain.model.Bill
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillParsingEngine @Inject constructor() {

    data class ParsedBillData(
        val billType: Bill.BillType = Bill.BillType.ELECTRICITY,
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

    // Provider detection patterns
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

    // State detection from provider
    private val providerToState = mapOf(
        "TSSPDCL" to "Telangana", "TSNPDCL" to "Telangana",
        "APSPDCL" to "Andhra Pradesh", "APEPDCL" to "Andhra Pradesh",
        "BESCOM" to "Karnataka", "MESCOM" to "Karnataka",
        "MSEDCL" to "Maharashtra",
        "TANGEDCO" to "Tamil Nadu", "TNEB" to "Tamil Nadu",
        "BSES" to "Delhi", "TATA_POWER_DELHI" to "Delhi",
        "WBSEDCL" to "West Bengal"
    )

    fun parseBill(ocrText: String): ParsedBillData {
        val cleanedText = cleanOcrText(ocrText)
        val lowerText = cleanedText.lowercase()

        val provider = detectUtilityProvider(lowerText)
        val billType = detectBillType(lowerText)
        val confidences = mutableMapOf<String, Float>()

        val consumerNumber = extractConsumerNumber(lowerText, provider)
        confidences["consumerNumber"] = if (consumerNumber != null) 0.85f else 0.0f

        val billingPeriod = extractBillingPeriod(lowerText)
        confidences["billingPeriod"] = if (billingPeriod != null) 0.8f else 0.0f

        val meterReadings = extractMeterReadings(lowerText)
        confidences["meterReadings"] = if (meterReadings != null) 0.75f else 0.0f

        val unitsConsumed = extractUnitsConsumed(lowerText)
            ?: meterReadings?.let { (it.second - it.first).toInt().takeIf { u -> u > 0 } }
        confidences["unitsConsumed"] = if (unitsConsumed != null) 0.85f else 0.0f

        val totalAmount = extractTotalAmount(lowerText)
        confidences["totalAmount"] = if (totalAmount != null) 0.9f else 0.0f

        val chargeBreakdown = extractChargeBreakdown(lowerText)
        confidences["chargeBreakdown"] = if (chargeBreakdown.isNotEmpty()) 0.7f else 0.0f

        val dueDate = extractDueDate(lowerText)
        confidences["dueDate"] = if (dueDate != null) 0.8f else 0.0f

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

    private fun cleanOcrText(text: String): String {
        return text
            // Fix common OCR O/0 confusion in numbers
            .replace(Regex("""(?<=\d)O(?=\d)"""), "0")
            .replace(Regex("""(?<=\d)o(?=\d)"""), "0")
            // Fix l/1 confusion
            .replace(Regex("""(?<=\d)l(?=\d)"""), "1")
            .replace(Regex("""(?<=\d)I(?=\d)"""), "1")
            // Remove extra spaces within numbers
            .replace(Regex("""(\d)\s+(\d)"""), "$1$2")
            // Normalize whitespace
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun detectBillType(text: String): Bill.BillType {
        return when {
            text.contains("electric") || text.contains("kwh") || text.contains("kw h") ||
                    text.contains("discom") || text.contains("unit") && text.contains("slab") -> Bill.BillType.ELECTRICITY
            text.contains("water") || text.contains("jal") || text.contains("kl ") ||
                    text.contains("kilolitre") -> Bill.BillType.WATER
            text.contains("gas") || text.contains("scm") || text.contains("png") ||
                    text.contains("piped natural") -> Bill.BillType.GAS
            else -> Bill.BillType.ELECTRICITY
        }
    }

    private fun detectUtilityProvider(text: String): String {
        for ((provider, keywords) in providerPatterns) {
            if (keywords.any { text.contains(it) }) {
                return provider
            }
        }
        return "Unknown"
    }

    private fun extractConsumerNumber(text: String, provider: String): String? {
        // Provider-specific patterns
        val patterns = when (provider) {
            "TSSPDCL", "TSNPDCL" -> listOf(
                """(?:consumer\s*no|service\s*number|sc\s*no)\s*[:\-.]?\s*(\d{10,13})""".toRegex(),
                """(?:consumer|svc)\s*[:\-.]?\s*(\d{10,13})""".toRegex()
            )
            "BESCOM" -> listOf(
                """(?:rr\s*number|rr\s*no|account\s*(?:no|id))\s*[:\-.]?\s*(\d{8,12})""".toRegex()
            )
            "MSEDCL" -> listOf(
                """(?:consumer\s*no|consumer\s*number)\s*[:\-.]?\s*(\d{12,16})""".toRegex()
            )
            "TANGEDCO", "TNEB" -> listOf(
                """(?:service\s*connection\s*no|sc\s*no|assessment)\s*[:\-.]?\s*(\d{8,12})""".toRegex()
            )
            "BSES", "TATA_POWER_DELHI" -> listOf(
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

        // Try provider-specific patterns first
        for (pattern in patterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.let { return it }
        }

        // Generic patterns as fallback
        val genericPatterns = listOf(
            """(?:consumer\s*(?:no|number|id)|account\s*(?:no|number|id)|ca\s*(?:no|number))\s*[:\-.]?\s*(\w[\w\-/]{6,15})""".toRegex(),
            """(?:k\s*no|service\s*(?:no|number))\s*[:\-.]?\s*(\d{8,16})""".toRegex()
        )
        for (pattern in genericPatterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.let { return it.uppercase() }
        }
        return null
    }

    private fun extractBillingPeriod(text: String): Pair<String, String>? {
        // Pattern: "billing period: 01-Jan-2024 to 31-Jan-2024"
        val patterns = listOf(
            """(?:billing\s*period|bill\s*period|period)\s*[:\-]?\s*(\d{1,2}[\-/]\w{3}[\-/]\d{2,4})\s*(?:to|[-\u2013])\s*(\d{1,2}[\-/]\w{3}[\-/]\d{2,4})""".toRegex(),
            """(?:from|period)\s*[:\-]?\s*(\d{1,2}[\-/]\d{1,2}[\-/]\d{2,4})\s*(?:to|[-\u2013])\s*(\d{1,2}[\-/]\d{1,2}[\-/]\d{2,4})""".toRegex(),
            """(?:month|bill\s*month)\s*[:\-]?\s*(\w+\s*\d{4})""".toRegex()
        )

        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                val start = match.groupValues.getOrNull(1) ?: continue
                val end = match.groupValues.getOrNull(2) ?: start
                return start to end
            }
        }
        return null
    }

    private fun extractMeterReadings(text: String): Pair<Long, Long>? {
        // Try to find previous and current readings
        val prevPatterns = listOf(
            """(?:previous|prev|initial|opening|old)\s*(?:reading|rdg|meter)\s*[:\-]?\s*(\d{3,8})""".toRegex()
        )
        val currPatterns = listOf(
            """(?:current|present|final|closing|new)\s*(?:reading|rdg|meter)\s*[:\-]?\s*(\d{3,8})""".toRegex()
        )

        var prevReading: Long? = null
        var currReading: Long? = null

        for (pattern in prevPatterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.toLongOrNull()?.let { prevReading = it }
        }
        for (pattern in currPatterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.toLongOrNull()?.let { currReading = it }
        }

        if (prevReading != null && currReading != null) {
            return prevReading!! to currReading!!
        }

        // Try tabular format: "Meter Reading    12345   12678"
        val twoNumberPattern = """(?:meter\s*reading|reading)\s*[:\-]?\s*(\d{3,8})\s+(\d{3,8})""".toRegex()
        twoNumberPattern.find(text)?.let { match ->
            val first = match.groupValues[1].toLongOrNull()
            val second = match.groupValues[2].toLongOrNull()
            if (first != null && second != null) {
                return if (first < second) first to second else second to first
            }
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
            """(?:total\s*amount|net\s*(?:amount|payable)|amount\s*(?:payable|due)|grand\s*total|total\s*(?:payable|due))\s*[:\-]?\s*(?:rs\.?|₹|inr)?\s*([\d,]+\.?\d*)""".toRegex(),
            """(?:amount\s*after\s*due\s*date|total\s*current\s*charges)\s*[:\-]?\s*(?:rs\.?|₹)?\s*([\d,]+\.?\d*)""".toRegex(),
            """(?:net\s*bill\s*amount|current\s*bill\s*amount)\s*[:\-]?\s*(?:rs\.?|₹)?\s*([\d,]+\.?\d*)""".toRegex()
        )
        for (pattern in patterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.let { match ->
                val amount = match.replace(",", "").toDoubleOrNull()
                if (amount != null && amount > 0) return amount
            }
        }
        return null
    }

    private fun extractChargeBreakdown(text: String): Map<String, Double> {
        val charges = mutableMapOf<String, Double>()

        val chargePatterns = mapOf(
            "Energy Charges" to listOf(
                """(?:energy\s*charges?|current\s*charges?|consumption\s*charges?)\s*[:\-]?\s*(?:rs\.?|₹)?\s*([\d,]+\.?\d*)""".toRegex()
            ),
            "Fixed Charges" to listOf(
                """(?:fixed\s*charges?|customer\s*charges?|demand\s*charges?|meter\s*rent)\s*[:\-]?\s*(?:rs\.?|₹)?\s*([\d,]+\.?\d*)""".toRegex()
            ),
            "Fuel Surcharge" to listOf(
                """(?:fuel\s*(?:surcharge|adjustment)|fac|fppca|fuel\s*cost\s*adjustment)\s*[:\-]?\s*(?:rs\.?|₹)?\s*([\d,]+\.?\d*)""".toRegex()
            ),
            "Electricity Duty" to listOf(
                """(?:electricity\s*duty|ed|govt\.?\s*duty|state\s*duty)\s*[:\-]?\s*(?:rs\.?|₹)?\s*([\d,]+\.?\d*)""".toRegex()
            ),
            "Tax" to listOf(
                """(?:tax|gst|cgst|sgst|cess)\s*[:\-]?\s*(?:rs\.?|₹)?\s*([\d,]+\.?\d*)""".toRegex()
            ),
            "Arrears" to listOf(
                """(?:arrears?|previous\s*balance|outstanding|due\s*amount)\s*[:\-]?\s*(?:rs\.?|₹)?\s*([\d,]+\.?\d*)""".toRegex()
            ),
            "Rebate" to listOf(
                """(?:rebate|discount|subsidy|incentive)\s*[:\-]?\s*(?:rs\.?|₹)?\s*-?([\d,]+\.?\d*)""".toRegex()
            )
        )

        for ((name, patterns) in chargePatterns) {
            for (pattern in patterns) {
                pattern.find(text)?.groupValues?.getOrNull(1)?.let { match ->
                    val amount = match.replace(",", "").toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        charges[name] = amount
                        return@let
                    }
                }
            }
        }

        return charges
    }

    private fun extractDueDate(text: String): String? {
        val patterns = listOf(
            """(?:due\s*date|pay\s*(?:by|before)|last\s*date)\s*[:\-]?\s*(\d{1,2}[\-/]\w{3}[\-/]\d{2,4})""".toRegex(),
            """(?:due\s*date|pay\s*(?:by|before))\s*[:\-]?\s*(\d{1,2}[\-/]\d{1,2}[\-/]\d{2,4})""".toRegex()
        )
        for (pattern in patterns) {
            pattern.find(text)?.groupValues?.getOrNull(1)?.let { return it }
        }
        return null
    }

    private fun detectState(text: String): String? {
        val stateKeywords = mapOf(
            "Telangana" to listOf("telangana", "hyderabad", "warangal", "nizamabad"),
            "Andhra Pradesh" to listOf("andhra pradesh", "vishakhapatnam", "vijayawada", "tirupati"),
            "Karnataka" to listOf("karnataka", "bangalore", "bengaluru", "mysore", "mangalore"),
            "Maharashtra" to listOf("maharashtra", "mumbai", "pune", "nagpur", "thane"),
            "Tamil Nadu" to listOf("tamil nadu", "tamilnadu", "chennai", "coimbatore", "madurai"),
            "Delhi" to listOf("delhi", "new delhi"),
            "West Bengal" to listOf("west bengal", "kolkata", "howrah"),
            "Uttar Pradesh" to listOf("uttar pradesh", "lucknow", "noida"),
            "Gujarat" to listOf("gujarat", "ahmedabad", "surat"),
            "Rajasthan" to listOf("rajasthan", "jaipur", "jodhpur"),
            "Kerala" to listOf("kerala", "kochi", "thiruvananthapuram"),
            "Madhya Pradesh" to listOf("madhya pradesh", "bhopal", "indore")
        )

        for ((state, keywords) in stateKeywords) {
            if (keywords.any { text.contains(it) }) return state
        }
        return null
    }

    private fun calculateOverallConfidence(fieldConfidences: Map<String, Float>): Float {
        if (fieldConfidences.isEmpty()) return 0.1f

        // Weight important fields more
        val weights = mapOf(
            "totalAmount" to 3.0f,
            "unitsConsumed" to 2.5f,
            "consumerNumber" to 2.0f,
            "billingPeriod" to 1.5f,
            "meterReadings" to 1.5f,
            "chargeBreakdown" to 2.0f,
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
