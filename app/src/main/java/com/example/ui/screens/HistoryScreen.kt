package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit,
    onSelectReport: (Long) -> Unit
) {
    val reports by viewModel.scanReports.collectAsState()

    GlassmorphicBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Dermal & Vital History", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0x0DFFFFFF) // Frosted TopAppBar
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
            if (reports.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = "No reports",
                            tint = Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Biomarker Reports Yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Execute your first face scan to start plotting heart rate, skin hydration, and wellness records.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))

                // Historical Progress Graph
                Text(
                    text = "Historical Pulse Metrics",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                val pulsePoints = reports.take(6).reversed().map { it.heartRate }
                val timestamps = reports.take(6).reversed().map { formatDateShort(it.timestamp) }

                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .padding(bottom = 20.dp)
                ) {
                    HistoricalLineChart(
                        modifier = Modifier.fillMaxSize(),
                        points = pulsePoints,
                        timestamps = timestamps,
                        color = GlassAccent,
                        label = "Heart Rate Estimates Over Time (BPM)"
                    )
                }

                Text(
                    text = "Previous Scan Sessions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // List of reports
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("reports_history_list")
                ) {
                    items(reports) { r ->
                        HistoryItemCard(
                            report = r,
                            onClicked = { onSelectReport(r.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
}

@Composable
fun HistoryItemCard(
    report: ScanReportEntity,
    onClicked: () -> Unit
) {
    val categoryColor = when (report.wellnessCategory.lowercase()) {
        "excellent" -> GlassPrimary
        "good" -> GlassSecondary
        "moderate" -> GlassWarning
        else -> GlassAccent
    }

    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClicked() }
            .testTag("history_item_${report.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatDate(report.timestamp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Wellness Score: ${report.overallWellnessScore}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(categoryColor.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = report.wellnessCategory.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = categoryColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BiometricSmallIndicator(
                        icon = Icons.Default.Favorite,
                        tint = GlassAccent,
                        text = "${report.heartRate} bpm"
                    )
                    BiometricSmallIndicator(
                        icon = Icons.Default.Air,
                        tint = GlassSecondary,
                        text = "${report.spo2}% SpO₂"
                    )
                    BiometricSmallIndicator(
                        icon = Icons.Default.Healing,
                        tint = GlassPrimary,
                        text = "${report.skinHydration}% Hydr."
                    )
                }
            }

            IconButton(onClick = onClicked) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Open report details",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun BiometricSmallIndicator(
    icon: ImageVector,
    tint: Color,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}
