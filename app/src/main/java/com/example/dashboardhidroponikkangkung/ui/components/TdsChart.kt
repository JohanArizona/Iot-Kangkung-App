package com.example.dashboardhidroponikkangkung.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dashboardhidroponikkangkung.data.model.HistoryData
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TdsChart(
    historyData: List<HistoryData>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Grafik TDS 24 Jam",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (historyData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada data historis",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                // Tanpa Filter
                val filtered = historyData.sortedBy { it.timestamp }

                // Filter 24 jam terakhir
                /*
                val now = System.currentTimeMillis()
                val oneDayAgo = now - (24 * 60 * 60 * 1000)
                val filtered = historyData
                    .filter { it.timestamp >= oneDayAgo }
                    .sortedBy { it.timestamp }
                */

                if (filtered.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada data 24 jam terakhir",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                } else {
                    LineChartWithCanvas(
                        data = filtered,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LineChartWithCanvas(
    data: List<HistoryData>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurface = MaterialTheme.colorScheme.onSurface

    // Calculate min and max values for scaling
    val tdsValues = data.map { it.tds.toFloat() }
    val maxTds = (tdsValues.maxOrNull() ?: 1000f) * 1.1f // Tambah 10% untuk margin
    val minTds = maxOf((tdsValues.minOrNull() ?: 0f) * 0.9f, 0f) // Kurang 10% untuk margin, minimal 0
    val tdsRange = maxTds - minTds

    // Padding for chart area
    val leftPadding = 50f
    val rightPadding = 20f
    val topPadding = 30f
    val bottomPadding = 40f

    Canvas(modifier = modifier) {
        val canvasWidth = size.width - leftPadding - rightPadding
        val canvasHeight = size.height - topPadding - bottomPadding

        // Draw grid lines and Y-axis labels
        val yStepCount = 5
        repeat(yStepCount + 1) { i ->
            val y = topPadding + (canvasHeight * i / yStepCount)
            val value = maxTds - (tdsRange * i / yStepCount)

            // Draw grid line
            drawLine(
                color = secondaryColor.copy(alpha = 0.3f),
                start = Offset(leftPadding, y),
                end = Offset(size.width - rightPadding, y),
                strokeWidth = 1f
            )

            // Draw Y-axis label
            val yLabelText = "%.0f".format(value)
            val yTextLayoutResult = textMeasurer.measure(
                text = yLabelText,
                style = TextStyle(
                    color = onSurface,
                    fontSize = 10.sp
                )
            )

            drawText(
                textMeasurer = textMeasurer,
                text = yLabelText,
                style = TextStyle(
                    color = onSurface,
                    fontSize = 10.sp
                ),
                topLeft = Offset(5f, y - (yTextLayoutResult.size.height / 2))
            )
        }

        // Draw X-axis labels (time)
        val xStepCount = minOf(6, data.size)
        val timeIndices = if (data.size <= xStepCount) {
            data.indices.toList()
        } else {
            (0 until xStepCount).map { it * (data.size - 1) / (xStepCount - 1) }
        }

        timeIndices.forEach { index ->
            if (index < data.size) {
                val x = leftPadding + (canvasWidth * index / (data.size - 1))
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                val timeText = sdf.format(Date(data[index].timestamp))

                val timeTextLayout = textMeasurer.measure(
                    text = timeText,
                    style = TextStyle(
                        color = onSurface,
                        fontSize = 10.sp
                    )
                )

                drawText(
                    textMeasurer = textMeasurer,
                    text = timeText,
                    style = TextStyle(
                        color = onSurface,
                        fontSize = 10.sp
                    ),
                    topLeft = Offset(x - (timeTextLayout.size.width / 2), size.height - bottomPadding + 15f)
                )
            }
        }

        // Draw chart line
        if (data.size > 1) {
            val path = Path().apply {
                data.forEachIndexed { index, historyData ->
                    val x = leftPadding + (canvasWidth * index / (data.size - 1))
                    val y = if (tdsRange > 0) {
                        topPadding + canvasHeight * (1 - (historyData.tds - minTds) / tdsRange)
                    } else {
                        topPadding + canvasHeight / 2
                    }

                    if (index == 0) {
                        moveTo(x, y)
                    } else {
                        lineTo(x, y)
                    }
                }
            }

            // Draw filled area under the line
            val fillPath = path.copy().apply {
                lineTo(leftPadding + canvasWidth, size.height - bottomPadding)
                lineTo(leftPadding, size.height - bottomPadding)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.3f),
                        primaryColor.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    startY = topPadding,
                    endY = size.height - bottomPadding
                )
            )

            // Draw the main line
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(
                    width = 3f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Draw data points (hanya untuk beberapa titik agar tidak terlalu padat)
            val pointStep = maxOf(1, data.size / 10) // Tampilkan ~10 titik
            data.forEachIndexed { index, historyData ->
                if (index % pointStep == 0 || index == data.size - 1) {
                    val x = leftPadding + (canvasWidth * index / (data.size - 1))
                    val y = if (tdsRange > 0) {
                        topPadding + canvasHeight * (1 - (historyData.tds - minTds) / tdsRange)
                    } else {
                        topPadding + canvasHeight / 2
                    }

                    drawCircle(
                        color = primaryColor,
                        radius = 4f,
                        center = Offset(x, y)
                    )

                    drawCircle(
                        color = surfaceColor,
                        radius = 2f,
                        center = Offset(x, y)
                    )
                }
            }
        }

        // Draw axis lines
        drawLine(
            color = secondaryColor.copy(alpha = 0.5f),
            start = Offset(leftPadding, topPadding),
            end = Offset(leftPadding, size.height - bottomPadding),
            strokeWidth = 2f
        )

        drawLine(
            color = secondaryColor.copy(alpha = 0.5f),
            start = Offset(leftPadding, size.height - bottomPadding),
            end = Offset(size.width - rightPadding, size.height - bottomPadding),
            strokeWidth = 2f
        )

        // Draw chart title (TDS Value)
        val titleText = "TDS (ppm)"
        val titleLayout = textMeasurer.measure(
            text = titleText,
            style = TextStyle(
                color = onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        )

        drawText(
            textMeasurer = textMeasurer,
            text = titleText,
            style = TextStyle(
                color = onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            ),
            topLeft = Offset(5f, 10f)
        )
    }
}