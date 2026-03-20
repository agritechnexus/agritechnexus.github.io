package com.fixmybill.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AnimatedCounter(
    targetValue: Double,
    prefix: String = "₹",
    modifier: Modifier = Modifier,
    style: TextStyle? = null,
    fontWeight: FontWeight = FontWeight.Bold,
    durationMillis: Int = 1200
) {
    var animationTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(targetValue) {
        animationTriggered = true
    }

    val animatedValue by animateFloatAsState(
        targetValue = if (animationTriggered) targetValue.toFloat() else 0f,
        animationSpec = tween(durationMillis = durationMillis),
        label = "counterAnimation"
    )

    Text(
        text = "$prefix${String.format("%,.2f", animatedValue.toDouble())}",
        modifier = modifier,
        style = style ?: MaterialTheme.typography.headlineMedium,
        fontWeight = fontWeight
    )
}
