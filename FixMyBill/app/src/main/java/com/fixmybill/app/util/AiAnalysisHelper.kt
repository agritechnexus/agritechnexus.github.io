package com.fixmybill.app.util

import com.fixmybill.app.BuildConfig
import com.fixmybill.app.data.remote.api.AiAnalysisService
import com.fixmybill.app.data.remote.dto.ClaudeMessage
import com.fixmybill.app.data.remote.dto.ClaudeRequest
import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.model.BillAnalysis
import com.fixmybill.app.domain.model.OverchargeDetail.OverchargeReport
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiAnalysisHelper @Inject constructor(
    private val aiAnalysisService: AiAnalysisService
) {
    private val apiKey: String = BuildConfig.ANTHROPIC_API_KEY

    suspend fun getSmartBillExplanation(
        bill: Bill,
        chargeBreakdown: Map<String, Double>
    ): String {
        val prompt = buildString {
            appendLine("You are a helpful Indian utility bill expert. Explain this electricity bill in simple, everyday language that any household member can understand.")
            appendLine()
            appendLine("Bill Details:")
            appendLine("- Provider: ${bill.utilityProvider}")
            appendLine("- State: ${bill.state}")
            appendLine("- Billing Period: ${bill.billingPeriod.first} to ${bill.billingPeriod.second}")
            appendLine("- Units Consumed: ${bill.unitsConsumed}")
            appendLine("- Total Amount: ₹${bill.totalAmount}")
            appendLine()
            appendLine("Charge Breakdown:")
            chargeBreakdown.forEach { (component, amount) ->
                appendLine("- $component: ₹$amount")
            }
            appendLine()
            appendLine("Please explain:")
            appendLine("1. Start with a friendly summary: 'Your bill is ₹X this month. Here's why...'")
            appendLine("2. Break down each charge in everyday language (avoid jargon)")
            appendLine("3. Compare with typical usage for a similar household in ${bill.state}")
            appendLine("4. Mention if the usage seems normal, high, or low for the season")
            appendLine("Keep the response concise and helpful. Use ₹ symbol for amounts.")
        }

        return callClaude(prompt)
    }

    suspend fun getOverchargeAnalysis(
        bill: Bill,
        overchargeReport: OverchargeReport
    ): String {
        val prompt = buildString {
            appendLine("You are an expert Indian consumer rights advisor specializing in utility bill disputes.")
            appendLine()
            appendLine("An overcharge has been detected in this electricity bill:")
            appendLine("- Provider: ${bill.utilityProvider}")
            appendLine("- State: ${bill.state}")
            appendLine("- Units Consumed: ${bill.unitsConsumed}")
            appendLine("- Billed Amount: ₹${bill.totalAmount}")
            appendLine("- Expected Amount: ₹${bill.totalAmount - overchargeReport.overchargeAmount}")
            appendLine("- Overcharge Amount: ₹${overchargeReport.overchargeAmount}")
            appendLine("- Confidence: ${(overchargeReport.confidence * 100).toInt()}%")
            appendLine()
            appendLine("Discrepancies found:")
            overchargeReport.discrepancies.forEach { d ->
                appendLine("- ${d.component}: Expected ₹${d.expected}, Billed ₹${d.actual} (Difference: ₹${d.difference})")
            }
            appendLine()
            appendLine("Please provide:")
            appendLine("1. A clear explanation of WHY this overcharge likely happened")
            appendLine("2. How common this type of overcharge is with ${bill.utilityProvider}")
            appendLine("3. Step-by-step actionable next steps specific to ${bill.utilityProvider}")
            appendLine("4. Relevant consumer protection rights and sections")
            appendLine("5. Timeline expectations for resolution")
            appendLine("Be specific to Indian electricity regulations and ${bill.state} state rules.")
        }

        return callClaude(prompt)
    }

    suspend fun generateComplaintLetter(
        bill: Bill,
        overchargeReport: OverchargeReport,
        complaintType: String,
        language: String = "English"
    ): String {
        val recipientInfo = when (complaintType) {
            "UTILITY_COMPANY" -> "the Assistant Engineer / Junior Engineer of ${bill.utilityProvider}"
            "CONSUMER_FORUM" -> "the District Consumer Disputes Redressal Forum"
            "RTI" -> "the Public Information Officer under RTI Act 2005"
            else -> "the concerned authority"
        }

        val prompt = buildString {
            appendLine("Generate a formal complaint letter in $language for an electricity bill overcharge.")
            appendLine()
            appendLine("Letter type: ${complaintType.replace("_", " ")}")
            appendLine("Addressed to: $recipientInfo")
            appendLine()
            appendLine("Bill Details:")
            appendLine("- Consumer Number: ${bill.consumerNumber}")
            appendLine("- Provider: ${bill.utilityProvider}")
            appendLine("- State: ${bill.state}")
            appendLine("- Billing Period: ${bill.billingPeriod.first} to ${bill.billingPeriod.second}")
            appendLine("- Units Consumed: ${bill.unitsConsumed}")
            appendLine("- Billed Amount: ₹${bill.totalAmount}")
            appendLine("- Correct Amount: ₹${bill.totalAmount - overchargeReport.overchargeAmount}")
            appendLine("- Overcharge: ₹${overchargeReport.overchargeAmount}")
            appendLine()
            appendLine("Discrepancies:")
            overchargeReport.discrepancies.forEach { d ->
                appendLine("- ${d.component}: Expected ₹${d.expected}, Charged ₹${d.actual}")
            }
            appendLine()
            when (complaintType) {
                "UTILITY_COMPANY" -> {
                    appendLine("Include:")
                    appendLine("- Proper formal letter format with date and reference")
                    appendLine("- Clear statement of the overcharge with calculations")
                    appendLine("- Request for bill revision and refund")
                    appendLine("- Reference to Electricity Act 2003 Section 56")
                    appendLine("- Reference to SERC regulations for ${bill.state}")
                    appendLine("- Deadline for response (15 days as per regulations)")
                    appendLine("- Warning of escalation to Consumer Forum if unresolved")
                }
                "CONSUMER_FORUM" -> {
                    appendLine("Include:")
                    appendLine("- Consumer Protection Act 2019 references")
                    appendLine("- Section 35 - complaint filing procedure")
                    appendLine("- Details of prior complaint to utility company")
                    appendLine("- Prayer for refund with interest and compensation")
                    appendLine("- Proper legal format for consumer forum complaint")
                }
                "RTI" -> {
                    appendLine("Include:")
                    appendLine("- RTI Act 2005 Section 6 application format")
                    appendLine("- Specific questions about tariff calculation methodology")
                    appendLine("- Request for meter reading records")
                    appendLine("- Request for applicable tariff order copy")
                    appendLine("- ₹10 application fee mention")
                }
            }
            appendLine()
            appendLine("Make the letter professional, legally sound, and ready to submit. Include [CONSUMER_NAME], [ADDRESS], [DATE] as placeholders.")
        }

        return callClaude(prompt)
    }

    suspend fun getBillOptimizationTips(
        bill: Bill,
        monthlyHistory: List<Pair<String, Double>>
    ): List<String> {
        val prompt = buildString {
            appendLine("You are an energy efficiency advisor for Indian households.")
            appendLine()
            appendLine("Current bill details:")
            appendLine("- State: ${bill.state}")
            appendLine("- Units consumed: ${bill.unitsConsumed}")
            appendLine("- Amount: ₹${bill.totalAmount}")
            appendLine("- Provider: ${bill.utilityProvider}")
            appendLine()
            if (monthlyHistory.isNotEmpty()) {
                appendLine("Monthly consumption history:")
                monthlyHistory.forEach { (month, amount) ->
                    appendLine("- $month: ₹$amount")
                }
                appendLine()
            }
            appendLine("Provide exactly 5 specific, actionable money-saving tips. For each tip:")
            appendLine("- Be specific to Indian households and this consumption level")
            appendLine("- Include estimated savings in ₹ per month")
            appendLine("- Consider the state's tariff slab structure (reducing units to a lower slab saves disproportionately)")
            appendLine("- Include practical tips about appliance usage, time-of-day usage if applicable, solar options")
            appendLine()
            appendLine("Format each tip as a single line starting with a number. Be concise and practical.")
        }

        val response = callClaude(prompt)
        return response.lines()
            .filter { it.isNotBlank() }
            .map { it.trim() }
            .filter { it.firstOrNull()?.isDigit() == true || it.startsWith("-") }
            .take(5)
            .ifEmpty { getDefaultOptimizationTips() }
    }

    private fun getDefaultOptimizationTips(): List<String> = listOf(
        "1. Switch to LED bulbs throughout your home - saves ₹200-400/month on average",
        "2. Set AC temperature to 24°C instead of lower settings - each degree saves ~6% energy",
        "3. Use a 5-star rated refrigerator and keep it away from heat sources - saves ₹150-300/month",
        "4. Run washing machine and iron during off-peak hours if your state has ToD tariff",
        "5. Consider rooftop solar panels - government subsidies cover 40% of cost under PM Surya Ghar"
    )

    private suspend fun callClaude(userPrompt: String): String {
        return try {
            val request = ClaudeRequest(
                messages = listOf(
                    ClaudeMessage(role = "user", content = userPrompt)
                )
            )

            val response = aiAnalysisService.analyzeBill(apiKey, request)

            if (response.isSuccessful) {
                response.body()?.content?.firstOrNull()?.text
                    ?: "Analysis could not be generated. Please try again."
            } else {
                "Unable to connect to AI service. Error: ${response.code()}. Please check your internet connection and try again."
            }
        } catch (e: Exception) {
            "AI analysis is temporarily unavailable. Error: ${e.localizedMessage ?: "Unknown error"}. The bill calculation results are still accurate based on official tariff data."
        }
    }
}
