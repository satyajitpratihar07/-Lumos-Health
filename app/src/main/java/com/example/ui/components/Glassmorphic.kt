package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import kotlin.math.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

// Medical Grade Glowing Colors - Frosted Glass Theme
val GlassPrimary = Color(0xFF6366F1) // Indigo-500
val GlassSecondary = Color(0xFF10B981) // Emerald-500
val GlassAccent = Color(0xFFF43F5E) // Rose-500
val GlassWarning = Color(0xFFF59E0B) // Amber-500

val GlassWhiteBack = Color(0x1AFFFFFF) // White 10%
val GlassWhiteBorder = Color(0x26FFFFFF) // White 15%
val GlassDarkBack = Color(0x0DFFFFFF) // White 5%
val GlassDarkBorder = Color(0x1AFFFFFF) // White 10%

@Composable
fun getGlassBack(): Color {
    return if (isSystemInDarkTheme()) GlassDarkBack else GlassWhiteBack
}

@Composable
fun getGlassBorder(): Color {
    return if (isSystemInDarkTheme()) GlassDarkBorder else GlassWhiteBorder
}

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val backColor = getGlassBack()
    val borderColor = getGlassBorder()

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = backColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    strokeWidth: Dp = 10.dp,
    color: Color = GlassPrimary,
    backgroundColor: Color = Color.Gray.copy(alpha = 0.15f),
    title: String = "",
    subtitle: String = ""
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val radius = (width.coerceAtMost(height) - strokeWidth.toPx()) / 2

            // Draw background circle
            drawCircle(
                color = backgroundColor,
                radius = radius,
                style = Stroke(width = strokeWidth.toPx())
            )

            // Draw progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun GlassmorphicGauge(
    modifier: Modifier = Modifier,
    score: Int,
    maxScore: Int = 100,
    color: Color = GlassPrimary,
    title: String = "Wellness"
) {
    val progress = score.toFloat() / maxScore
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "gauge"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val strokePx = 14.dp.toPx()
            val radius = (width.coerceAtMost(height) - strokePx) / 2

            // Draw full backing arc (from 140 degrees to 40 degrees clockwise)
            drawArc(
                color = Color.Gray.copy(alpha = 0.15f),
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Draw glowing active progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    0.0f to color.copy(alpha = 0.4f),
                    1.0f to color
                ),
                startAngle = 140f,
                sweepAngle = 260f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                text = "$score",
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
fun HeartRateWaveform(
    modifier: Modifier = Modifier,
    bpm: Int = 72,
    isScanning: Boolean = false
) {
    val transition = rememberInfiniteTransition(label = "ecg_scrolling")
    
    // Smooth scrolling phase offset for continuous medical telemetry wave
    val scrollPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 4f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(if (isScanning) 1200 else 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scroll_phase"
    )

    // Sweeping radar laser beam
    val sweepProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isScanning) 1500 else 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    val waveColor = if (isScanning) GlassAccent else Color(0xFF4C8EFF)
    val gridLineColor = Color(0xFF4C8EFF).copy(alpha = 0.12f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val midY = height / 2f

        // 1. Draw Medical Grid Gridlines (Horizontal & Vertical)
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)

        // Horizontal Gridlines
        drawLine(color = gridLineColor, start = Offset(0f, height * 0.20f), end = Offset(width, height * 0.20f), strokeWidth = 1.dp.toPx(), pathEffect = dashEffect)
        drawLine(color = Color.White.copy(alpha = 0.15f), start = Offset(0f, midY), end = Offset(width, midY), strokeWidth = 1.2.dp.toPx())
        drawLine(color = gridLineColor, start = Offset(0f, height * 0.80f), end = Offset(width, height * 0.80f), strokeWidth = 1.dp.toPx(), pathEffect = dashEffect)

        // Vertical Gridlines (Medical ECG Grid)
        val numVerticalLines = 8
        val vStep = width / numVerticalLines
        for (v in 1 until numVerticalLines) {
            val vx = v * vStep
            drawLine(color = gridLineColor, start = Offset(vx, 0f), end = Offset(vx, height), strokeWidth = 1.dp.toPx(), pathEffect = dashEffect)
        }

        // 2. Calculate Continuous PPG Medical Waveform with Scrolling Motion
        val strokePath = Path()
        val fillPath = Path()
        val numPoints = 140
        val step = width / numPoints
        var leadingX = 0f
        var leadingY = midY

        fillPath.moveTo(0f, height)

        for (i in 0..numPoints) {
            val x = i * step
            val normalizedX = x / width
            // Continuous scrolling angle
            val angle = (normalizedX * 4f * Math.PI.toFloat()) - scrollPhase

            val pWave = sin(angle) * 0.08f
            val qrsComplex = java.lang.Math.pow(sin(angle * 2.5f).coerceAtLeast(0f).toDouble(), 8.0).toFloat() * 0.70f
            val tWave = java.lang.Math.pow(sin(angle + 1.1f).coerceAtLeast(0f).toDouble(), 3.0).toFloat() * 0.18f

            val yOffset = (pWave + qrsComplex + tWave) * (height * 0.40f)
            val currentY = midY - yOffset

            if (i == 0) {
                strokePath.moveTo(x, currentY)
                fillPath.lineTo(x, currentY)
            } else {
                strokePath.lineTo(x, currentY)
                fillPath.lineTo(x, currentY)
            }

            if (normalizedX <= sweepProgress) {
                leadingX = x
                leadingY = currentY
            }
        }

        fillPath.lineTo(width, height)
        fillPath.close()

        // 3. Draw Glowing Area Fill Under PPG Wave
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    waveColor.copy(alpha = 0.28f),
                    waveColor.copy(alpha = 0.02f)
                ),
                startY = 0f,
                endY = height
            )
        )

        // 4. Draw Ambient Outer Glow Path
        drawPath(
            path = strokePath,
            color = waveColor.copy(alpha = 0.35f),
            style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // 5. Draw Sharp Main Wave Line
        drawPath(
            path = strokePath,
            color = waveColor,
            style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // 6. Draw Sweeping Telemetry Laser Beam & Leading Dot
        val laserX = sweepProgress * width
        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, waveColor, Color.Transparent),
                startY = 0f,
                endY = height
            ),
            start = Offset(laserX, 0f),
            end = Offset(laserX, height),
            strokeWidth = 2.dp.toPx()
        )

        // Glowing Telemetry Node
        drawCircle(
            color = Color.White,
            radius = 3.5.dp.toPx(),
            center = Offset(leadingX, leadingY)
        )
        drawCircle(
            color = waveColor,
            radius = 7.dp.toPx(),
            center = Offset(leadingX, leadingY),
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = waveColor.copy(alpha = 0.40f),
            radius = 12.dp.toPx(),
            center = Offset(leadingX, leadingY)
        )
    }
}

@Composable
fun HistoricalLineChart(
    modifier: Modifier = Modifier,
    points: List<Int>,
    timestamps: List<String>,
    color: Color = GlassPrimary,
    label: String = "Heart Rate"
) {
    if (points.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No scanning history recorded yet",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
        return
    }

    val maxVal = points.maxOrNull()?.coerceAtLeast(100) ?: 100
    val minVal = (points.minOrNull() ?: 50).coerceAtMost(50).coerceAtLeast(0)
    val diff = (maxVal - minVal).coerceAtLeast(1)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Range: $minVal - $maxVal",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val width = size.width
            val height = size.height
            val strokePx = 3.dp.toPx()
            val circleRadiusPx = 5.dp.toPx()

            // Draw bottom axis gridline
            drawLine(
                color = Color.Gray.copy(alpha = 0.15f),
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = 1.dp.toPx()
            )

            // Draw top axis gridline
            drawLine(
                color = Color.Gray.copy(alpha = 0.15f),
                start = Offset(0f, 0f),
                end = Offset(width, 0f),
                strokeWidth = 1.dp.toPx()
            )

            if (points.size == 1) {
                // Draw single point
                val x = width / 2f
                val y = height - ((points[0] - minVal).toFloat() / diff * height)
                drawCircle(color = color, radius = circleRadiusPx, center = Offset(x, y))
                return@Canvas
            }

            val xInterval = width / (points.size - 1)
            val path = Path()

            // Map points to pixel coordinates
            val coordinates = points.mapIndexed { idx, value ->
                val x = idx * xInterval
                val y = height - ((value - minVal).toFloat() / diff * height)
                Offset(x, y)
            }

            // Draw connecting spline lines
            path.moveTo(coordinates[0].x, coordinates[0].y)
            for (i in 1 until coordinates.size) {
                val prev = coordinates[i - 1]
                val curr = coordinates[i]
                // Control points for smooth bezier cubic curve
                val cp1 = Offset(prev.x + xInterval / 2f, prev.y)
                val cp2 = Offset(curr.x - xInterval / 2f, curr.y)
                path.cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, curr.x, curr.y)
            }

            // Draw line graph path
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Fill gradient under the curve
            val fillPath = Path().apply {
                addPath(path)
                lineTo(coordinates.last().x, height)
                lineTo(coordinates.first().x, height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(color.copy(alpha = 0.25f), Color.Transparent),
                    startY = 0f,
                    endY = height
                )
            )

            // Draw coordinate highlights
            coordinates.forEach { pt ->
                drawCircle(
                    color = Color.White,
                    radius = circleRadiusPx,
                    center = pt
                )
                drawCircle(
                    color = color,
                    radius = circleRadiusPx - 1.dp.toPx(),
                    center = pt,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Timestamps labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            timestamps.forEach { ts ->
                Text(
                    text = ts,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun GlassmorphicBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF05070A))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw top-right Indigo glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF6366F1).copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.15f),
                    radius = size.width * 0.75f
                ),
                center = Offset(size.width * 0.85f, size.height * 0.15f),
                radius = size.width * 0.75f
            )

            // Draw bottom-left Emerald glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF10B981).copy(alpha = 0.10f), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.85f),
                    radius = size.width * 0.75f
                ),
                center = Offset(size.width * 0.15f, size.height * 0.85f),
                radius = size.width * 0.75f
            )
        }
        content()
    }
}

