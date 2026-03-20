package com.fixmybill.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixmybill.app.presentation.theme.ErrorRed
import com.fixmybill.app.presentation.theme.SuccessGreen
import com.fixmybill.app.presentation.theme.WarningOrange

@Composable
fun TariffComparisonBar(
    billed: Double,
    expected: Double,
    label: String,
    modifier: Modifier = Modifier
) {
    val maxAmount = maxOf(billed, expected, 1.0)
    val isOvercharged = billed > expected * 1.01 // 1% tolerance
    val overchargePercent = if (expected > 0) ((billed - expected) / expected * 100) else 0.0

    val billedColor = when {
        !isOvercharged -> SuccessGreen
        overchargePercent > 20 -> ErrorRed
        else -> WarningOrange
    }
    val expectedColor = SuccessGreen

    var animationTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animationTriggered = true
    }

    val billedFraction by animateFloatAsState(
        targetValue = if (animationTriggered) (billed / maxAmount).toFloat().coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 800, delayMillis = 100),
        label = "billedAnimation"
    )

    val expectedFraction by animateFloatAsState(
        targetValue = if (animationTriggered) (expected / maxAmount).toFloat().coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 800, delayMillis = 300),
        label = "expectedAnimation"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Label row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            if (isOvercharged) {
                Text(
                    text = "+${String.format("%.1f", overchargePercent)}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = billedColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Billed bar
        BarRow(
            label = "Billed",
            amount = billed,
            fraction = billedFraction,
            color = billedColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Expected bar
        BarRow(
            label = "Expected",
            amount = expected,
            fraction = expectedFraction,
            color = expectedColor
        )
    }
}

@Composable
private fun BarRow(
    label: String,
    amount: Double,
    fraction: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            Text(
                text = "₹${String.format("%,.2f", amount)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = color,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Bar background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(color.copy(alpha = 0.1f))
        ) {
            // Filled bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color)
            )
        }
    }
}
