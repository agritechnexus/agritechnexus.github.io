package com.fixmybill.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixmybill.app.domain.model.Bill
import com.fixmybill.app.domain.model.BillType
import com.fixmybill.app.presentation.theme.DeepTeal
import com.fixmybill.app.presentation.theme.SuccessGreen
import com.fixmybill.app.presentation.theme.WarningOrange
import com.fixmybill.app.presentation.theme.WarningOrangeContainer
import com.fixmybill.app.presentation.theme.WarningOrangeOnContainer
import java.time.format.DateTimeFormatter

@Composable
fun BillCard(
    bill: Bill,
    onClick: () -> Unit
) {
    val billTypeInfo = remember(bill.billType) {
        when (bill.billType) {
            BillType.ELECTRICITY -> BillTypeInfo(
                icon = Icons.Filled.Bolt,
                label = "Electricity",
                tint = Color(0xFFFFC107)
            )
            BillType.WATER -> BillTypeInfo(
                icon = Icons.Filled.WaterDrop,
                label = "Water",
                tint = Color(0xFF2196F3)
            )
            BillType.GAS -> BillTypeInfo(
                icon = Icons.Filled.LocalFireDepartment,
                label = "Gas",
                tint = Color(0xFFFF5722)
            )
        }
    }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy") }
    val periodFormatter = remember { DateTimeFormatter.ofPattern("MMM dd") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bill type icon
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = billTypeInfo.tint.copy(alpha = 0.12f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = billTypeInfo.icon,
                            contentDescription = billTypeInfo.label,
                            tint = billTypeInfo.tint,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Provider and billing period
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = bill.utilityProvider,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${bill.periodStart.format(periodFormatter)} - ${bill.periodEnd.format(periodFormatter)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Total amount
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${String.format("%,.2f", bill.totalAmount)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (bill.isOvercharged) WarningOrange else DeepTeal
                    )
                    Text(
                        text = billTypeInfo.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Overcharge badge and date row
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Overcharge badge
                if (bill.isOvercharged && bill.overchargeAmount > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WarningOrangeContainer
                    ) {
                        Text(
                            text = "Overcharged ₹${String.format("%,.2f", bill.overchargeAmount)}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = WarningOrangeOnContainer
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SuccessGreen.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Normal",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = SuccessGreen
                        )
                    }
                }

                // Date at bottom right
                Text(
                    text = bill.createdAt.format(dateFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

private data class BillTypeInfo(
    val icon: ImageVector,
    val label: String,
    val tint: Color
)
