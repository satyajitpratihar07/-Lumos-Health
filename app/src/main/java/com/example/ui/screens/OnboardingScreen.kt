package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Security
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
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.GlassPrimary
import com.example.ui.components.GlassSecondary
import com.example.ui.components.GlassAccent
import com.example.ui.components.GlassmorphicBackground

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    var currentPage by remember { mutableStateOf(0) }
    var consentChecked by remember { mutableStateOf(false) }

    GlassmorphicBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Healing,
                        contentDescription = "Face Health Logo",
                        tint = GlassPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Face Health AI",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "Medical-Grade AI Biomarker Monitor",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sliding Onboarding Cards
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    label = "onboarding_cards"
                ) { page ->
                    when (page) {
                        0 -> OnboardingCard(
                            icon = Icons.Default.CameraAlt,
                            iconColor = GlassPrimary,
                            title = "Advanced rPPG Facial Scanning",
                            description = "Our optical imaging technology detects microscopic variations in skin color caused by subcutaneous blood volume fluctuations, tracking your vitals in real time without contact."
                        )
                        1 -> OnboardingCard(
                            icon = Icons.Default.Favorite,
                            iconColor = GlassAccent,
                            title = "Comprehensive Biomarkers",
                            description = "Track heart rate, heart rate variability (HRV), respiratory rate, blood pressure estimates, hydration levels, skin indices, and ocular fatigue scores in less than 30 seconds."
                        )
                        2 -> OnboardingCard(
                            icon = Icons.Default.Security,
                            iconColor = GlassSecondary,
                            title = "Privacy & Consent Agreement",
                            description = "All image processing is conducted securely. No video footage is stored or transmitted without encryption. Raw streams are compiled into numeric parameters locally."
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Consent Option on Last Page
            if (currentPage == 2) {
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = consentChecked,
                                onCheckedChange = { consentChecked = it },
                                modifier = Modifier.testTag("consent_checkbox")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "I understand and agree to the clinical terms and consent to facial scan processing.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Text(
                            text = "Disclaimer: This software is powered by AI biomarker estimates. Measurements like estimated SpO₂, cardiovascular risk indexes, blood pressure predictions, and wellness scores are for informational purposes only. This app is not a certified diagnostic medical device.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Start,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Navigation Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicator Bullets
                Row {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(width = if (currentPage == index) 24.dp else 8.dp, height = 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (currentPage == index) GlassPrimary else MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.2f
                                    )
                                )
                        )
                    }
                }

                // Next or Proceed button
                Button(
                    onClick = {
                        if (currentPage < 2) {
                            currentPage++
                        } else {
                            if (consentChecked) {
                                onFinished()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentPage == 2 && !consentChecked) Color.Gray else GlassPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("onboarding_next_button"),
                    enabled = currentPage < 2 || consentChecked
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (currentPage == 2) "Agree & Enter" else "Next",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Arrow Next"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String
) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}
