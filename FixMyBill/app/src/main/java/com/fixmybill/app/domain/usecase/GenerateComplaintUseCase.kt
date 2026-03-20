package com.fixmybill.app.domain.usecase

import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.model.Complaint
import com.fixmybill.app.domain.model.ComplaintStatus
import com.fixmybill.app.domain.model.ComplaintType
import com.fixmybill.app.domain.model.OverchargeReport
import com.fixmybill.app.domain.repository.ComplaintRepository
import javax.inject.Inject

class GenerateComplaintUseCase @Inject constructor(
    private val complaintRepository: ComplaintRepository
) {

    suspend operator fun invoke(
        bill: Bill,
        complaintType: ComplaintType,
        overchargeReport: OverchargeReport?
    ): Complaint {
        val complaintText = generateComplaintText(bill, complaintType, overchargeReport)
        val recipientAddress = getRecipientAddress(bill, complaintType)

        val complaint = Complaint(
            billId = bill.id,
            complaintType = complaintType,
            complaintText = complaintText,
            recipientAddress = recipientAddress,
            status = ComplaintStatus.DRAFT
        )

        val id = complaintRepository.insertComplaint(complaint)
        return complaint.copy(id = id)
    }

    private fun generateComplaintText(
        bill: Bill,
        complaintType: ComplaintType,
        overchargeReport: OverchargeReport?
    ): String {
        val overchargeAmount = overchargeReport?.overchargeAmount ?: bill.overchargeAmount

        return when (complaintType) {
            ComplaintType.UTILITY_COMPANY -> buildString {
                appendLine("To,")
                appendLine("The Superintendent Engineer / Billing Department")
                appendLine(bill.utilityProvider)
                appendLine()
                appendLine("Subject: Complaint regarding overcharge in ${bill.billType.name.lowercase()} bill")
                appendLine()
                appendLine("Respected Sir/Madam,")
                appendLine()
                appendLine("I am writing to bring to your notice an apparent overcharge in my ${bill.billType.name.lowercase()} bill.")
                appendLine()
                appendLine("Consumer Number: ${bill.consumerNumber}")
                appendLine("Billing Period: ${bill.periodStart} to ${bill.periodEnd}")
                appendLine("Meter Reading: ${bill.previousReading} to ${bill.currentReading}")
                appendLine("Units Consumed: ${bill.unitsConsumed}")
                appendLine("Amount Billed: ₹${"%.2f".format(bill.totalAmount)}")
                appendLine("Estimated Overcharge: ₹${"%.2f".format(overchargeAmount)}")
                appendLine()
                if (overchargeReport != null && overchargeReport.discrepancies.isNotEmpty()) {
                    appendLine("The following discrepancies were identified:")
                    overchargeReport.discrepancies.forEach { d ->
                        appendLine("  - ${d.component}: Expected ₹${"%.2f".format(d.expected)}, Billed ₹${"%.2f".format(d.actual)}")
                    }
                    appendLine()
                }
                appendLine("I request you to kindly review and rectify the bill at the earliest. Please issue a revised bill with the correct charges.")
                appendLine()
                appendLine("Thanking you,")
                appendLine("Yours faithfully")
            }

            ComplaintType.CONSUMER_FORUM -> buildString {
                appendLine("BEFORE THE DISTRICT CONSUMER DISPUTES REDRESSAL FORUM")
                appendLine()
                appendLine("COMPLAINT UNDER SECTION 35 OF THE CONSUMER PROTECTION ACT, 2019")
                appendLine()
                appendLine("Complainant: [Your Name]")
                appendLine("Versus")
                appendLine("Opposite Party: ${bill.utilityProvider}")
                appendLine()
                appendLine("FACTS OF THE CASE:")
                appendLine()
                appendLine("1. The complainant is a consumer of ${bill.billType.name.lowercase()} services provided by ${bill.utilityProvider}.")
                appendLine("2. Consumer Number: ${bill.consumerNumber}")
                appendLine("3. For the billing period ${bill.periodStart} to ${bill.periodEnd}, a bill of ₹${"%.2f".format(bill.totalAmount)} was raised.")
                appendLine("4. Upon verification, the bill was found to be overcharged by approximately ₹${"%.2f".format(overchargeAmount)}.")
                appendLine("5. Despite raising the matter with the utility company, no satisfactory resolution was provided.")
                appendLine()
                appendLine("PRAYER:")
                appendLine("It is humbly prayed that this Hon'ble Forum may:")
                appendLine("a) Direct the opposite party to refund the overcharged amount of ₹${"%.2f".format(overchargeAmount)}")
                appendLine("b) Award compensation for mental agony and harassment")
                appendLine("c) Direct the opposite party to issue a corrected bill")
            }

            ComplaintType.RTI -> buildString {
                appendLine("APPLICATION UNDER THE RIGHT TO INFORMATION ACT, 2005")
                appendLine()
                appendLine("To,")
                appendLine("The Public Information Officer")
                appendLine(bill.utilityProvider)
                appendLine()
                appendLine("Subject: Seeking information regarding tariff calculation and billing methodology")
                appendLine()
                appendLine("Sir/Madam,")
                appendLine()
                appendLine("Under the provisions of the Right to Information Act, 2005, I seek the following information:")
                appendLine()
                appendLine("1. The applicable tariff rates for ${bill.billType.name.lowercase()} supply for the period ${bill.periodStart} to ${bill.periodEnd}.")
                appendLine("2. Detailed slab-wise calculation for Consumer Number: ${bill.consumerNumber}.")
                appendLine("3. The methodology used for computing the bill amount of ₹${"%.2f".format(bill.totalAmount)} for ${bill.unitsConsumed} units.")
                appendLine("4. Any surcharges, taxes, or additional charges applied and their legal basis.")
                appendLine("5. Copy of the regulatory order approving the current tariff structure.")
                appendLine()
                appendLine("I am enclosing the prescribed fee of ₹10 via [mode of payment].")
                appendLine()
                appendLine("Thanking you,")
                appendLine("[Your Name]")
                appendLine("[Your Address]")
            }
        }
    }

    private fun getRecipientAddress(bill: Bill, complaintType: ComplaintType): String {
        return when (complaintType) {
            ComplaintType.UTILITY_COMPANY -> "${bill.utilityProvider}, ${bill.state}"
            ComplaintType.CONSUMER_FORUM -> "District Consumer Disputes Redressal Forum, ${bill.state}"
            ComplaintType.RTI -> "Public Information Officer, ${bill.utilityProvider}, ${bill.state}"
        }
    }
}
