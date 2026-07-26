package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit
) {
    val report by viewModel.activeReport.collectAsState()
    val scrollState = rememberScrollState()

    GlassmorphicBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Patient Health Report",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0x0DFFFFFF) // Frosted TopAppBar
                    ),
                    actions = {
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(GlassPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "94% CONFIDENT",
                                color = GlassPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            if (report == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GlassPrimary)
                }
            } else {
                val r = report!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(scrollState)
                ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Overall Wellness Gauge Banner
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Biomarker Index",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = r.wellnessCategory.uppercase(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = when (r.wellnessCategory.lowercase()) {
                                    "excellent" -> GlassPrimary
                                    "good" -> GlassSecondary
                                    "moderate" -> GlassWarning
                                    else -> GlassAccent
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Based on multi-layer dermal microvascular flow and subcutaneous thermal perfusion metrics.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                lineHeight = 16.sp
                            )
                        }

                        // Gauge dial
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .weight(0.9f),
                            contentAlignment = Alignment.Center
                        ) {
                            GlassmorphicGauge(
                                modifier = Modifier.fillMaxSize(),
                                score = r.overallWellnessScore,
                                color = when (r.wellnessCategory.lowercase()) {
                                    "excellent" -> GlassPrimary
                                    "good" -> GlassSecondary
                                    "moderate" -> GlassWarning
                                    else -> GlassAccent
                                }
                            )
                        }
                    }
                }

                // AI Disclaimer Warning (Critical for Medical Estimations)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassWarning.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Medical Alert",
                                tint = GlassWarning,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CLINICAL ESTIMATION DISCLAIMER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlassWarning,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "This biometric report (including Heart Rate, estimated SpO₂, Blood Pressure, and Skin Hydration indexes) is generated exclusively using advanced face-scanning computer vision AI. It has NOT been measured by clinical hardware or cleared by medical authorities. This is for educational and wellness tracking purposes only and should never substitute professional clinical diagnostics.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 14.sp
                        )
                    }
                }



                // Expandable Section: Cardiovascular Telemetry
                ReportSection(
                    title = "Cardiovascular Estimates",
                    icon = Icons.Default.Favorite,
                    iconColor = GlassAccent
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ValueCard(modifier = Modifier.weight(1f), label = "Heart Rate", value = "${r.heartRate} bpm")
                        ValueCard(modifier = Modifier.weight(1f), label = "Blood Pressure", value = r.bloodPressure)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ValueCard(modifier = Modifier.weight(1f), label = "Est. SpO₂", value = "${r.spo2}%")
                        ValueCard(modifier = Modifier.weight(1f), label = "HRV (SDNN)", value = "${r.hrv} ms")
                    }
                }

                // Expandable Section: Epidermal & Skin Quality Index
                ReportSection(
                    title = "Skin & Dermal Indices",
                    icon = Icons.Default.Healing,
                    iconColor = GlassPrimary
                ) {
                    MetricBar(label = "Moisture & Hydration", score = r.skinHydration, color = GlassPrimary)
                    MetricBar(label = "Wrinkles & Elasticity", score = r.skinWrinkles, color = GlassWarning)
                    MetricBar(label = "Dark Circles Index", score = r.skinDarkCircles, color = GlassSecondary)
                    MetricBar(label = "Pigmentation Level", score = r.skinPigmentation, color = GlassWarning)
                    MetricBar(label = "Oiliness & Sebum", score = r.skinOiliness, color = GlassAccent)
                    MetricBar(label = "Acne & Dermal Strain", score = r.skinAcne, color = GlassAccent)
                }

                // Expandable Section: Ocular Health Analysis
                ReportSection(
                    title = "Ocular Analysis",
                    icon = Icons.Default.Visibility,
                    iconColor = GlassSecondary
                ) {
                    MetricBar(label = "Sclera Redness", score = r.eyeRedness, color = GlassWarning)
                    MetricBar(label = "Estimated Blink Rate", score = r.eyeBlinkRate * 5, color = GlassPrimary, labelSuffix = " bpm")
                    MetricBar(label = "Drowsiness Coefficient", score = r.eyeDrowsiness, color = GlassAccent)
                }

                // Expandable Section: Mental Wellness & Strain
                ReportSection(
                    title = "Mental Wellness Profile",
                    icon = Icons.Default.Mood,
                    iconColor = GlassWarning
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ValueCard(modifier = Modifier.weight(1f), label = "Stress Level", value = r.stressLevel)
                        ValueCard(modifier = Modifier.weight(1f), label = "Emotion Match", value = r.emotion)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    MetricBar(label = "Anxiety Indicator", score = r.anxietyIndicator, color = GlassWarning)
                    MetricBar(label = "Fatigue Score", score = r.fatigueScore, color = GlassAccent)
                }

                // Diagnostic Notes
                Text(
                    text = "AI Clinical Practitioner Notes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = r.notes,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
}

@Composable
fun ReportSection(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(iconColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand toggle"
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun ValueCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MetricBar(
    label: String,
    score: Int,
    color: Color,
    labelSuffix: String = "%"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Text(
                text = "$score$labelSuffix",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score.toFloat() / 100f },
            color = color,
            trackColor = Color.Gray.copy(alpha = 0.15f),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
        )
    }
}
