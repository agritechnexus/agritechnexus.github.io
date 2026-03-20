package com.fixmybill.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixmybill.app.presentation.theme.DeepTeal
import com.fixmybill.app.presentation.theme.SuccessGreen
import kotlin.math.abs

@Composable
fun SavingsChart(
    data: List<Pair<String, Double>>,
    predictedData: List<Pair<String, Double>> = emptyList(),
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    var selectedIndex by remember { mutableStateOf(-1) }

    val allValues = (data.map { it.second } + predictedData.map { it.second })
    val maxValue = (allValues.maxOrNull() ?: 0.0) * 1.15
    val minValue = (allValues.minOrNull() ?: 0.0) * 0.85

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Monthly Bills",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Legend
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(10.dp),
                    shape = RoundedCornerShape(2.dp),
                    color = SuccessGreen
                ) {}
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Actual",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(16.dp))
                if (predictedData.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.size(10.dp),
                        shape = RoundedCornerShape(2.dp),
                        color = DeepTeal.copy(alpha = 0.5f)
                    ) {}
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Predicted",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Selected value tooltip
            if (selectedIndex in data.indices) {
                val selected = data[selectedIndex]
                Text(
                    text = "${selected.first}: ₹${String.format("%,.0f", selected.second)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepTeal
                )
            } else {
                Text(
                    text = "Tap chart to see values",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val textColor = MaterialTheme.colorScheme.onSurfaceVariant

            Box(modifier = Modifier.fillMaxWidth()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .pointerInput(data) {
                            detectTapGestures { offset ->
                                val chartWidth = size.width.toFloat()
                                val padding = 60f
                                val drawableWidth = chartWidth - padding * 2
                                if (data.size > 1) {
                                    val stepX = drawableWidth / (data.size - 1)
                                    val tappedIndex = ((offset.x - padding) / stepX).toInt()
                                        .coerceIn(0, data.size - 1)
                                    // Check if tap is close enough to a data point
                                    val pointX = padding + tappedIndex * stepX
                                    if (abs(offset.x - pointX) < stepX / 2 + 20f) {
                                        selectedIndex = tappedIndex
                                    }
                                }
                            }
                        }
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val paddingLeft = 60f
                    val paddingRight = 20f
                    val paddingTop = 10f
                    val paddingBottom = 40f

                    val drawableWidth = canvasWidth - paddingLeft - paddingRight
                    val drawableHeight = canvasHeight - paddingTop - paddingBottom

                    val range = maxValue - minValue
                    if (range <= 0) return@Canvas

                    // Draw Y-axis labels and grid lines
                    val ySteps = 4
                    for (i in 0..ySteps) {
                        val value = minValue + (range * i / ySteps)
                        val y = paddingTop + drawableHeight - (drawableHeight * i / ySteps)

                        // Grid line
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            start = Offset(paddingLeft, y),
                            end = Offset(canvasWidth - paddingRight, y),
                            strokeWidth = 1f
                        )

                        // Y-axis label
                        drawContext.canvas.nativeCanvas.drawText(
                            "₹${String.format("%,.0f", value)}",
                            4f,
                            y + 5f,
                            android.graphics.Paint().apply {
                                color = textColor.hashCode()
                                textSize = 22f
                                isAntiAlias = true
                            }
                        )
                    }

                    // Draw X-axis labels
                    if (data.size > 1) {
                        val stepX = drawableWidth / (data.size - 1)
                        data.forEachIndexed { index, (month, _) ->
                            val x = paddingLeft + index * stepX
                            drawContext.canvas.nativeCanvas.drawText(
                                month.take(3),
                                x - 15f,
                                canvasHeight - 5f,
                                android.graphics.Paint().apply {
                                    color = textColor.hashCode()
                                    textSize = 22f
                                    isAntiAlias = true
                                }
                            )
                        }
                    }

                    // Draw actual data line
                    drawDataLine(
                        data = data.map { it.second },
                        minValue = minValue,
                        range = range,
                        paddingLeft = paddingLeft,
                        paddingTop = paddingTop,
                        drawableWidth = drawableWidth,
                        drawableHeight = drawableHeight,
                        color = SuccessGreen,
                        isDashed = false,
                        drawPoints = true,
                        selectedIndex = selectedIndex
                    )

                    // Draw predicted data line (dashed)
                    if (predictedData.isNotEmpty()) {
                        drawDataLine(
                            data = predictedData.map { it.second },
                            minValue = minValue,
                            range = range,
                            paddingLeft = paddingLeft + (if (data.isNotEmpty()) drawableWidth * (data.size - 1) / ((data.size + predictedData.size - 2).coerceAtLeast(1)) else 0f),
                            paddingTop = paddingTop,
                            drawableWidth = drawableWidth * predictedData.size / ((data.size + predictedData.size - 1).coerceAtLeast(1)),
                            drawableHeight = drawableHeight,
                            color = DeepTeal,
                            isDashed = true,
                            drawPoints = false,
                            selectedIndex = -1
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawDataLine(
    data: List<Double>,
    minValue: Double,
    range: Double,
    paddingLeft: Float,
    paddingTop: Float,
    drawableWidth: Float,
    drawableHeight: Float,
    color: Color,
    isDashed: Boolean,
    drawPoints: Boolean,
    selectedIndex: Int
) {
    if (data.size < 2) return

    val stepX = drawableWidth / (data.size - 1)
    val points = data.mapIndexed { index, value ->
        val x = paddingLeft + index * stepX
        val y = paddingTop + drawableHeight - ((value - minValue) / range * drawableHeight).toFloat()
        Offset(x, y)
    }

    // Draw the line path
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size) {
            // Smooth curve using cubic bezier
            val prev = points[i - 1]
            val curr = points[i]
            val controlX = (prev.x + curr.x) / 2
            cubicTo(
                controlX, prev.y,
                controlX, curr.y,
                curr.x, curr.y
            )
        }
    }

    val strokeStyle = if (isDashed) {
        Stroke(
            width = 3f,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
        )
    } else {
        Stroke(width = 3f, cap = StrokeCap.Round)
    }

    drawPath(path = path, color = color, style = strokeStyle)

    // Draw data points
    if (drawPoints) {
        points.forEachIndexed { index, point ->
            val radius = if (index == selectedIndex) 8f else 5f
            drawCircle(
                color = color,
                radius = radius,
                center = point
            )
            drawCircle(
                color = Color.White,
                radius = radius - 2f,
                center = point
            )
            drawCircle(
                color = color,
                radius = radius - 3.5f,
                center = point
            )
        }
    }
}
