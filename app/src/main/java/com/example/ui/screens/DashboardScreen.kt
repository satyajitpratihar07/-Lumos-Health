package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScanReportEntity
import com.example.ui.components.*
import com.example.ui.viewmodel.HealthViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: HealthViewModel,
    onNavigateToScan: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToChat: () -> Unit,
    onSelectReport: (Long) -> Unit
) {
    val activeUser by viewModel.activeUser.collectAsState()
    val reports by viewModel.scanReports.collectAsState()

    val lastReport = reports.firstOrNull()
    val hasHistory = reports.isNotEmpty()

    GlassmorphicBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = Color(0x0DFFFFFF) // Frosted Navigation Bar
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Home") },
                        modifier = Modifier.testTag("nav_home_tab")
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToHistory,
                        icon = { Icon(Icons.Default.History, contentDescription = "History") },
                        label = { Text("Reports") },
                        modifier = Modifier.testTag("nav_history_tab")
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToChat,
                        icon = { Icon(Icons.Default.Chat, contentDescription = "AI Chat") },
                        label = { Text("AI Assist") },
                        modifier = Modifier.testTag("nav_chat_tab")
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToSettings,
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Profile") },
                        modifier = Modifier.testTag("nav_settings_tab")
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
            Spacer(modifier = Modifier.height(16.dp))

            // User Header Row (Profile DP on LEFT, Greeting Text on RIGHT)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar DP (LEFT SIDE)
                val photoUrl = activeUser?.avatarSeed
                val isImageUrl = photoUrl != null && (photoUrl.startsWith("http://") || photoUrl.startsWith("https://"))

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GlassPrimary.copy(alpha = 0.8f), GlassSecondary.copy(alpha = 0.8f))
                            )
                        )
                        .clickable { onNavigateToSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isImageUrl && photoUrl != null) {
                        coil.compose.AsyncImage(
                            model = photoUrl,
                            contentDescription = "User Profile DP",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        val initial = activeUser?.name?.take(1)?.uppercase() ?: "P"
                        Text(
                            text = initial,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Greeting Text (RIGHT SIDE OF DP)
                Column {
                    Text(
                        text = "Good day,",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = activeUser?.name ?: "Patient",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Clinical Estimation Notification Disclaimer Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Disclaimer Alert",
                        tint = GlassSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Disclaimer: All biometric figures are AI estimates. This does not substitute clinical diagnostics.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pulse Wave and Central Scan Action Card (Futuristic Medical Telemetry UI)
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToScan() }
                    .testTag("dashboard_scan_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Pulsing Green Live Telemetry Dot
                            val pulseTransition = rememberInfiniteTransition(label = "pulse_dot")
                            val pulseScale by pulseTransition.animateFloat(
                                initialValue = 0.6f,
                                targetValue = 1.1f,
                                animationSpec = infiniteRepeatable(tween(800), repeatMode = RepeatMode.Reverse),
                                label = "scale"
                            )
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(GlassSecondary.copy(alpha = pulseScale))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Awaiting Scan",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlassPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Position your face under the lens for optical PPG & cardiac estimate telemetry.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        Button(
                            onClick = onNavigateToScan,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GlassPrimary),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("start_scan_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Scan Face",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Medical ECG / PPG Waveform Display
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.35f))
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        HeartRateWaveform(
                            modifier = Modifier.fillMaxSize(),
                            bpm = 72,
                            isScanning = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Facial Behavior & Gender Demographics Card
            if (lastReport != null) {
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable { onSelectReport(lastReport.id) }
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = "Face Analysis",
                                    tint = GlassSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Facial Behavior & Dermal AI",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Animated live badge (single line, no vertical wrap)
                            val transition = rememberInfiniteTransition(label = "pulse_badge")
                            val badgeAlpha by transition.animateFloat(
                                initialValue = 0.4f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(tween(1000), repeatMode = RepeatMode.Reverse),
                                label = "alpha"
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GlassPrimary.copy(alpha = 0.15f * badgeAlpha))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "LIVE AI SCAN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GlassPrimary,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Vitality Index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Vitality Index",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${lastReport.overallWellnessScore}%",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GlassSecondary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = lastReport.wellnessCategory,
                                        fontSize = 9.sp,
                                        color = GlassPrimary,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Expression & Affective Tone
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Facial Behavior",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = lastReport.emotion,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GlassAccent,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${lastReport.stressLevel} Stress",
                                        fontSize = 9.sp,
                                        color = GlassSecondary,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Dermal Perfusion
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Dermal Perfusion",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${lastReport.skinHydration}%",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GlassPrimary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Flow Active",
                                        fontSize = 9.sp,
                                        color = GlassPrimary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Awaiting Scan Placeholder Card
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable { onNavigateToScan() }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null,
                            tint = GlassSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Facial Behavior & Dermal AI",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Complete a Face Scan to calculate live facial micro-expressions & dermal telemetry.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recent Analysis Summary
            if (lastReport != null) {
                Text(
                    text = "Recent Biomarkers (${formatDate(lastReport.timestamp)})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 8 Workable Biometric Widgets Grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricWidget(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectReport(lastReport.id) },
                        icon = Icons.Default.Favorite,
                        iconColor = GlassAccent,
                        title = "Heart Rate",
                        value = "${lastReport.heartRate}",
                        unit = "BPM",
                        status = "Normal"
                    )

                    MetricWidget(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectReport(lastReport.id) },
                        icon = Icons.Default.Air,
                        iconColor = GlassSecondary,
                        title = "SpO₂ Estimate",
                        value = "${lastReport.spo2}%",
                        unit = "Oxygen",
                        status = "Optimal"
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricWidget(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectReport(lastReport.id) },
                        icon = Icons.Default.Speed,
                        iconColor = GlassWarning,
                        title = "Blood Pressure",
                        value = lastReport.bloodPressure,
                        unit = "mmHg",
                        status = "Stable"
                    )

                    MetricWidget(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectReport(lastReport.id) },
                        icon = Icons.Default.Shield,
                        iconColor = GlassPrimary,
                        title = "Wellness",
                        value = "${lastReport.overallWellnessScore}",
                        unit = "/100",
                        status = lastReport.wellnessCategory
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricWidget(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectReport(lastReport.id) },
                        icon = Icons.Default.Air,
                        iconColor = GlassPrimary,
                        title = "Respiratory Rate",
                        value = "${lastReport.respiratoryRate}",
                        unit = "RPM",
                        status = "Healthy"
                    )

                    MetricWidget(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectReport(lastReport.id) },
                        icon = Icons.Default.Psychology,
                        iconColor = GlassWarning,
                        title = "Stress Score",
                        value = "${lastReport.stressScore}",
                        unit = "/100",
                        status = lastReport.stressLevel
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricWidget(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectReport(lastReport.id) },
                        icon = Icons.Default.Bedtime,
                        iconColor = GlassSecondary,
                        title = "Fatigue Score",
                        value = "${lastReport.fatigueScore}%",
                        unit = "Strain",
                        status = "Normal"
                    )

                    MetricWidget(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectReport(lastReport.id) },
                        icon = Icons.Default.MonitorHeart,
                        iconColor = GlassAccent,
                        title = "HRV (SDNN)",
                        value = "${lastReport.hrv}",
                        unit = "ms",
                        status = "Optimal"
                    )
                }

                // Show Report button
                Button(
                    onClick = { onSelectReport(lastReport.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(bottom = 12.dp)
                        .testTag("view_full_report_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "View Detailed Patient Report",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Empty state card
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = GlassPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Biomarkers Logged Yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Execute your first facial diagnostic check to populate your vitals & cardiovascular profile.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Historical Trend Section
            if (hasHistory) {
                Text(
                    text = "Wellness & HRV Comparison",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val trendPoints = reports.take(5).reversed().map { it.overallWellnessScore }
                val trendLabels = reports.take(5).reversed().map { formatDateShort(it.timestamp) }

                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .padding(bottom = 24.dp)
                ) {
                    HistoricalLineChart(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        points = trendPoints,
                        timestamps = trendLabels,
                        color = GlassPrimary,
                        label = "Overall Wellness Score Timeline"
                    )
                }
            }
        }
    }
}
}

@Composable
fun MetricWidget(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String,
    unit: String,
    status: String
) {
    GlassmorphicCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(iconColor.copy(alpha = 0.12f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconColor
                )
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Recent"
    }
}

fun formatDateShort(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}
