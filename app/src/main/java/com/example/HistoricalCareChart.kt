package com.example

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlantEntity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun HistoricalCareChart(
    plant: PlantEntity,
    modifier: Modifier = Modifier
) {
    // Generate deterministic historical logs for the last 30 days using a seed from plant.id
    val historyData = remember(plant.id, plant.lastWateredTime, plant.lastFertilizedTime) {
        generateHistoricalData(plant)
    }

    // Interactive tooltip state
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val textMeasurer = rememberTextMeasurer()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("historical_care_chart_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Chart Header & Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📈 Care Frequency Tracker",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Water vs. Fertilizer logs in the last 30 days",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }

                // Care Stats summary
                val waterCount = historyData.count { it.watered }
                val fertCount = historyData.count { it.fertilized }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BadgeCounter(label = "Watered", count = waterCount, color = Color(0xFF1E88E5))
                    BadgeCounter(label = "Fed", count = fertCount, color = Color(0xFF4CAF50))
                }
            }

            // Interactive Tooltip Info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (selectedIndex != null) {
                    val log = historyData[selectedIndex!!]
                    val formattedDate = remember(log.timestamp) {
                        try {
                            val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
                            sdf.format(Date(log.timestamp))
                        } catch (e: Exception) {
                            "Day ${selectedIndex!! + 1}"
                        }
                    }
                    val actions = mutableListOf<String>()
                    if (log.watered) actions.add("💧 Watered")
                    if (log.fertilized) actions.add("🧪 Fertilized")
                    val statusText = if (actions.isEmpty()) "Healthy (No care events scheduled)" else actions.joinToString(" and ")

                    Text(
                        text = "📅 $formattedDate: $statusText",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Start
                    )
                } else {
                    Text(
                        text = "💡 Tap points on the area-chart below for specific event dates.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            // Render the Core Areas Curve in Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                val blueSplash = Color(0xFF1E88E5)
                val greenSplash = Color(0xFF4CAF50)
                val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                val primaryColor = MaterialTheme.colorScheme.primary
                val labelStyle = TextStyle(
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                )

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(historyData) {
                            detectTapGestures { offset ->
                                val width = size.width
                                val marginStart = 35.dp.toPx()
                                val marginEnd = 16.dp.toPx()
                                val marginTop = 15.dp.toPx()
                                val marginBottom = 20.dp.toPx()
                                val graphWidth = width - marginStart - marginEnd

                                val stepX = graphWidth / (historyData.size - 1)
                                var bestIndex = -1
                                var bestDistance = Float.MAX_VALUE

                                for (i in historyData.indices) {
                                    val pointX = marginStart + (i * stepX)
                                    val dist = abs(offset.x - pointX)
                                    if (dist < bestDistance) {
                                        bestDistance = dist
                                        bestIndex = i
                                    }
                                }

                                if (bestIndex != -1 && bestDistance < stepX * 0.8f) {
                                    selectedIndex = bestIndex
                                }
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height

                    val marginStart = 35.dp.toPx()
                    val marginEnd = 16.dp.toPx()
                    val marginTop = 15.dp.toPx()
                    val marginBottom = 20.dp.toPx()

                    val graphWidth = width - marginStart - marginEnd
                    val graphHeight = height - marginTop - marginBottom

                    // 1. Draw horizontal grid lines and Y-axis labels
                    val gridLines = 3
                    for (i in 0..gridLines) {
                        val fraction = i.toFloat() / gridLines
                        val y = marginTop + fraction * graphHeight
                        drawLine(
                            color = gridColor,
                            start = Offset(marginStart, y),
                            end = Offset(width - marginEnd, y),
                            strokeWidth = 1.dp.toPx()
                        )

                        // Y status labels
                        val yLevel = when (i) {
                            0 -> "High"
                            1 -> "Medium"
                            2 -> "Low"
                            else -> "Idle"
                        }
                        drawContext.canvas.nativeCanvas.save()
                        val textLayout = textMeasurer.measure(yLevel, labelStyle)
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft = Offset(5.dp.toPx(), y - textLayout.size.height / 2f)
                        )
                    }

                    // 2. Draw X-axis labels
                    val xAxisTicks = 4
                    val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
                    for (i in 0 until xAxisTicks) {
                        val fraction = i.toFloat() / (xAxisTicks - 1)
                        val index = (fraction * (historyData.size - 1)).toInt()
                        val x = marginStart + fraction * graphWidth
                        val labelDate = sdf.format(Date(historyData[index].timestamp))

                        val textLayout = textMeasurer.measure(labelDate, labelStyle)
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft = Offset(x - textLayout.size.width / 2f, height - marginBottom + 4.dp.toPx())
                        )
                    }

                    // Pre-calculate coordinate points
                    val stepX = graphWidth / (historyData.size - 1)

                    fun getPointY(hasAction: Boolean, intervalAction: Boolean): Float {
                        val rating = if (hasAction) 0.1f else if (intervalAction) 0.5f else 0.9f
                        return marginTop + rating * graphHeight
                    }

                    // 3. Draw Water Gradient Curve (Blue)
                    val waterPath = Path()
                    val waterAreaPath = Path()

                    historyData.forEachIndexed { i, log ->
                        val x = marginStart + (i * stepX)
                        val y = getPointY(log.watered, i % (plant.wateringIntervalDays.coerceAtLeast(3)) == 0)

                        if (i == 0) {
                            waterPath.moveTo(x, y)
                            waterAreaPath.moveTo(x, marginTop + graphHeight)
                            waterAreaPath.lineTo(x, y)
                        } else {
                            // Smooth bezier line
                            val prevX = marginStart + ((i - 1) * stepX)
                            val prevY = getPointY(historyData[i - 1].watered, (i - 1) % (plant.wateringIntervalDays.coerceAtLeast(3)) == 0)
                            val ctrlX1 = prevX + stepX / 2f
                            val ctrlY1 = prevY
                            val ctrlX2 = prevX + stepX / 2f
                            val ctrlY2 = y

                            waterPath.cubicTo(ctrlX1, ctrlY1, ctrlX2, ctrlY2, x, y)
                            waterAreaPath.cubicTo(ctrlX1, ctrlY1, ctrlX2, ctrlY2, x, y)
                        }

                        if (i == historyData.size - 1) {
                            waterAreaPath.lineTo(x, marginTop + graphHeight)
                            waterAreaPath.close()
                        }
                    }

                    // Draw area gradient for Water
                    drawPath(
                        path = waterAreaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(blueSplash.copy(alpha = 0.25f), Color.Transparent),
                            startY = marginTop,
                            endY = marginTop + graphHeight
                        )
                    )

                    // Draw line stroke for Water
                    drawPath(
                        path = waterPath,
                        color = blueSplash,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // 4. Draw Fertilizer Gradient Curve (Green)
                    val fertPath = Path()
                    val fertAreaPath = Path()

                    historyData.forEachIndexed { i, log ->
                        val x = marginStart + (i * stepX)
                        val intervalFertilizing = i % (plant.fertilizingIntervalDays.coerceAtLeast(10)) == 0
                        val y = getPointY(log.fertilized, intervalFertilizing)

                        if (i == 0) {
                            fertPath.moveTo(x, y)
                            fertAreaPath.moveTo(x, marginTop + graphHeight)
                            fertAreaPath.lineTo(x, y)
                        } else {
                            val prevX = marginStart + ((i - 1) * stepX)
                            val prevIntervalFertilizing = (i - 1) % (plant.fertilizingIntervalDays.coerceAtLeast(10)) == 0
                            val prevY = getPointY(historyData[i - 1].fertilized, prevIntervalFertilizing)
                            val ctrlX1 = prevX + stepX / 2f
                            val ctrlY1 = prevY
                            val ctrlX2 = prevX + stepX / 2f
                            val ctrlY2 = y

                            fertPath.cubicTo(ctrlX1, ctrlY1, ctrlX2, ctrlY2, x, y)
                            fertAreaPath.cubicTo(ctrlX1, ctrlY1, ctrlX2, ctrlY2, x, y)
                        }

                        if (i == historyData.size - 1) {
                            fertAreaPath.lineTo(x, marginTop + graphHeight)
                            fertAreaPath.close()
                        }
                    }

                    // Draw area gradient for Fertilizer
                    drawPath(
                        path = fertAreaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(greenSplash.copy(alpha = 0.18f), Color.Transparent),
                            startY = marginTop,
                            endY = marginTop + graphHeight
                        )
                    )

                    // Draw line stroke for Fertilizer
                    drawPath(
                        path = fertPath,
                        color = greenSplash,
                        style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // 5. Draw interactive bullet dots on exact action logs or highlighted indices
                    historyData.forEachIndexed { i, log ->
                        val x = marginStart + (i * stepX)
                        if (log.watered) {
                            val y = getPointY(hasAction = true, intervalAction = false)
                            drawCircle(color = blueSplash, radius = 4.dp.toPx(), center = Offset(x, y))
                            drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(x, y))
                        }
                        if (log.fertilized) {
                            val y = getPointY(hasAction = true, intervalAction = false)
                            drawCircle(color = greenSplash, radius = 4.dp.toPx(), center = Offset(x, y))
                            drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(x, y))
                        }

                        // Selected indicator vertical line & points outer rings
                        if (selectedIndex == i) {
                            drawLine(
                                color = primaryColor.copy(alpha = 0.4f),
                                start = Offset(x, marginTop),
                                end = Offset(x, marginTop + graphHeight),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                            val waterY = getPointY(log.watered, i % (plant.wateringIntervalDays.coerceAtLeast(3)) == 0)
                            drawCircle(
                                color = blueSplash.copy(alpha = 0.3f),
                                radius = 8.dp.toPx(),
                                center = Offset(x, waterY)
                            )
                        }
                    }
                }
            }

            // Legend / Legend labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Water Item
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFF1E88E5), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Watering Intensity (ltrs)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.width(20.dp))
                // Fertilizer Item
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFF4CAF50), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Fertilization Intake (ml)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun BadgeCounter(label: String, count: Int, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
            Text(
                text = "$label: $count",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

// Data class to represent a single day of care history
data class DayCareLog(
    val dayOffset: Int,
    val timestamp: Long,
    val watered: Boolean,
    val fertilized: Boolean
)

// Generates simulated historical data representing waterings and fertilizations matching the real logs + intervals
fun generateHistoricalData(plant: PlantEntity): List<DayCareLog> {
    val logs = mutableListOf<DayCareLog>()
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = System.currentTimeMillis()

    // Deterministic random generator based on plant id as seed
    val random = Random(plant.id + 112L)

    val resolvedWaterInterval = if (plant.customWateringIntervalDays > 0) {
        plant.customWateringIntervalDays
    } else {
        plant.wateringIntervalDays
    }.coerceAtLeast(3)

    val resolvedFertInterval = if (plant.customFertilizingIntervalDays > 0) {
        plant.customFertilizingIntervalDays
    } else {
        plant.fertilizingIntervalDays
    }.coerceAtLeast(10)

    // Build lists of actions for the last 30 days
    val wateredDays = BooleanArray(30)
    val fertilizedDays = BooleanArray(30)

    // Fill days periodically based on actual intervals, adding some minor variance
    for (i in 0 until 30) {
        val dayDistanceFromToday = 29 - i // 29 is today, 0 is 30 days ago
        
        // Simulating watering: triggered every interval days, plus some probability offset
        if (dayDistanceFromToday % resolvedWaterInterval == 0) {
            wateredDays[i] = true
        } else if (random.nextFloat() < 0.05f) { // Occasional irregular watering
            wateredDays[i] = true
        }

        // Simulating fertilization: triggered every fertilizing interval
        if (dayDistanceFromToday % resolvedFertInterval == 0) {
            fertilizedDays[i] = true
        } else if (random.nextFloat() < 0.02f) {
            fertilizedDays[i] = true
        }
    }

    // Anchor: Today (the last element in our list) should match the actual plant database state if watered/fertilized today
    val oneDayMs = 24 * 60 * 60 * 1000L
    val now = System.currentTimeMillis()

    if (plant.lastWateredTime > 0L) {
        // Find which day offset matches the last watered time
        val diffMs = now - plant.lastWateredTime
        val diffDays = (diffMs / oneDayMs).toInt()
        if (diffDays in 0..29) {
            wateredDays[29 - diffDays] = true
        }
    }
    if (plant.lastFertilizedTime > 0L) {
        val diffMs = now - plant.lastFertilizedTime
        val diffDays = (diffMs / oneDayMs).toInt()
        if (diffDays in 0..29) {
            fertilizedDays[29 - diffDays] = true
        }
    }

    // Construct final list from index 0 (-29 days) up to index 29 (Today)
    for (i in 0..29) {
        val dayOffset = - (29 - i)
        val dayTime = now + (dayOffset * oneDayMs)
        logs.add(
            DayCareLog(
                dayOffset = dayOffset,
                timestamp = dayTime,
                watered = wateredDays[i],
                fertilized = fertilizedDays[i]
            )
        )
    }

    return logs
}

fun exportPlantHistoryToCSV(context: android.content.Context, plant: PlantEntity, logs: List<DayCareLog>) {
    try {
        val csvHeader = "Day Offset,Timestamp,Date Logged,Watered,Fertilized\n"
        val csvBody = logs.joinToString("\n") { log ->
            val dateStr = try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                sdf.format(Date(log.timestamp))
            } catch (e: Exception) {
                "Unknown"
            }
            "${log.dayOffset},${log.timestamp},$dateStr,${if (log.watered) "YES" else "NO"},${if (log.fertilized) "YES" else "NO"}"
        }
        val csvContent = csvHeader + csvBody
        
        // Save CSV text data inside a temporary cache file to enable attachments sharing securely
        val cacheFile = java.io.File(context.cacheDir, "${plant.commonName.replace(" ", "_")}_care_history.csv")
        cacheFile.writeText(csvContent)

        // Trigger safe Android sharing intent
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(android.content.Intent.EXTRA_SUBJECT, "${plant.commonName} care logs (30 Days)")
            putExtra(android.content.Intent.EXTRA_TEXT, "Here is the care history CSV details for ${plant.commonName}. Keep growing!\n\n$csvContent")
        }
        context.startActivity(android.content.Intent.createChooser(intent, "Download ${plant.commonName} Care History"))
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "Failed to export care history CSV: ${e.localizedMessage}", android.widget.Toast.LENGTH_LONG).show()
    }
}

