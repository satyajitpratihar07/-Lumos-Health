package com.example.ui.screens

import android.Manifest
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.example.ui.components.*
import com.example.ui.viewmodel.HealthViewModel
import com.example.ui.viewmodel.ScanStep
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit,
    onScanFinished: (Long) -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val scanStep by viewModel.scanStep.collectAsState()
    val scanProgress by viewModel.scanProgress.collectAsState()
    val scanInstruction by viewModel.scanInstruction.collectAsState()

    var showFlashOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (cameraPermissionState.status.isGranted) {
            viewModel.startScanFlow { reportId ->
                showFlashOverlay = true
                onScanFinished(reportId)
            }
        } else {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (cameraPermissionState.status.isGranted) {
            // Live camera viewfinder
            CameraPreview(modifier = Modifier.fillMaxSize())
        } else {
            // Permission fallback / Simulated lens backdrop
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "No Camera",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Camera Permission Required",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { cameraPermissionState.launchPermissionRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = GlassPrimary)
                    ) {
                        Text("Grant Permission")
                    }
                }
            }
        }

        // SCI-FI HUD Overlay Canvas
        CameraHUDOverlay(
            modifier = Modifier.fillMaxSize(),
            scanStep = scanStep,
            scanProgress = scanProgress
        )

        // Floating Control Panel & Live Diagnostics State
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "PPG ACTIVE",
                        color = GlassAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Bottom Glassmorphic Control Panel
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scan_control_card")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Status Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (scanStep) {
                                    ScanStep.FINISHED -> GlassPrimary.copy(alpha = 0.2f)
                                    ScanStep.PROCESSING -> GlassSecondary.copy(alpha = 0.2f)
                                    ScanStep.SCANNING -> GlassAccent.copy(alpha = 0.2f)
                                    else -> GlassWarning.copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when (scanStep) {
                                        ScanStep.FINISHED -> GlassPrimary
                                        ScanStep.PROCESSING -> GlassSecondary
                                        ScanStep.SCANNING -> GlassAccent
                                        else -> GlassWarning
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = scanStep.name.replace("_", " "),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = when (scanStep) {
                                ScanStep.FINISHED -> GlassPrimary
                                ScanStep.PROCESSING -> GlassSecondary
                                ScanStep.SCANNING -> GlassAccent
                                else -> GlassWarning
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Main instructions text
                    Text(
                        text = scanInstruction,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.height(48.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress indicators
                    if (scanStep == ScanStep.PROCESSING) {
                        CircularProgressIndicator(
                            color = GlassSecondary,
                            modifier = Modifier.size(44.dp)
                        )
                    } else {
                        LinearProgressIndicator(
                            progress = { scanProgress },
                            color = when (scanStep) {
                                ScanStep.SCANNING -> GlassAccent
                                ScanStep.HOLD_STILL -> GlassPrimary
                                else -> GlassSecondary
                            },
                            trackColor = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.startScanFlow { reportId ->
                                showFlashOverlay = true
                                onScanFinished(reportId)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(23.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GlassPrimary)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Instant Capture & Analyze Face",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // White capture flash animation overlay
        AnimatedVisibility(
            visible = showFlashOverlay,
            enter = fadeIn(animationSpec = tween(50)),
            exit = fadeOut(animationSpec = tween(600))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
        }
    }
}

@Composable
fun CameraPreview(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    LaunchedEffect(previewView) {
        com.presagetech.smartspectra.SmartSpectraSdk.shared.config.previewSurfaceProvider = previewView.surfaceProvider
    }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                } else {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
            } catch (e: Exception) {
                Log.e("CameraPreview", "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

@Composable
fun CameraHUDOverlay(
    modifier: Modifier = Modifier,
    scanStep: ScanStep,
    scanProgress: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hud_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val alignmentColor = when (scanStep) {
        ScanStep.ALIGN_FACE -> GlassWarning
        ScanStep.CHECK_LIGHTING -> GlassWarning
        ScanStep.CHECK_DISTANCE -> GlassWarning
        ScanStep.HOLD_STILL -> GlassPrimary
        ScanStep.SCANNING -> GlassAccent
        else -> GlassSecondary
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val ovalWidth = width * 0.65f
        val ovalHeight = height * 0.45f
        val centerX = width / 2f
        val centerY = height * 0.45f

        // Draw dark vignette around the scan zone
        val clipPath = androidx.compose.ui.graphics.Path().apply {
            addOval(
                androidx.compose.ui.geometry.Rect(
                    left = centerX - ovalWidth / 2f,
                    top = centerY - ovalHeight / 2f,
                    right = centerX + ovalWidth / 2f,
                    bottom = centerY + ovalHeight / 2f
                )
            )
        }
        
        // Draw main camera tracking target oval
        drawOval(
            color = alignmentColor.copy(alpha = if (scanStep == ScanStep.HOLD_STILL) pulseAlpha else 0.8f),
            topLeft = Offset(centerX - ovalWidth / 2f, centerY - ovalHeight / 2f),
            size = androidx.compose.ui.geometry.Size(ovalWidth, ovalHeight),
            style = Stroke(
                width = 3.dp.toPx(),
                pathEffect = if (scanStep == ScanStep.ALIGN_FACE) PathEffect.dashPathEffect(floatArrayOf(15f, 15f)) else null
            )
        )

        // Draw crosshair indicators in scanning mode
        if (scanStep == ScanStep.SCANNING || scanStep == ScanStep.HOLD_STILL) {
            val length = 20.dp.toPx()
            val offset = 10.dp.toPx()

            // Top-left bracket
            drawLine(
                color = alignmentColor,
                start = Offset(centerX - ovalWidth / 2f - offset, centerY - ovalHeight / 2f - offset),
                end = Offset(centerX - ovalWidth / 2f - offset + length, centerY - ovalHeight / 2f - offset),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = alignmentColor,
                start = Offset(centerX - ovalWidth / 2f - offset, centerY - ovalHeight / 2f - offset),
                end = Offset(centerX - ovalWidth / 2f - offset, centerY - ovalHeight / 2f - offset + length),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Top-right bracket
            drawLine(
                color = alignmentColor,
                start = Offset(centerX + ovalWidth / 2f + offset, centerY - ovalHeight / 2f - offset),
                end = Offset(centerX + ovalWidth / 2f + offset - length, centerY - ovalHeight / 2f - offset),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = alignmentColor,
                start = Offset(centerX + ovalWidth / 2f + offset, centerY - ovalHeight / 2f - offset),
                end = Offset(centerX + ovalWidth / 2f + offset, centerY - ovalHeight / 2f - offset + length),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Bottom-left bracket
            drawLine(
                color = alignmentColor,
                start = Offset(centerX - ovalWidth / 2f - offset, centerY + ovalHeight / 2f + offset),
                end = Offset(centerX - ovalWidth / 2f - offset + length, centerY + ovalHeight / 2f + offset),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = alignmentColor,
                start = Offset(centerX - ovalWidth / 2f - offset, centerY + ovalHeight / 2f + offset),
                end = Offset(centerX - ovalWidth / 2f - offset, centerY + ovalHeight / 2f + offset - length),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Bottom-right bracket
            drawLine(
                color = alignmentColor,
                start = Offset(centerX + ovalWidth / 2f + offset, centerY + ovalHeight / 2f + offset),
                end = Offset(centerX + ovalWidth / 2f + offset - length, centerY + ovalHeight / 2f + offset),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = alignmentColor,
                start = Offset(centerX + ovalWidth / 2f + offset, centerY + ovalHeight / 2f + offset),
                end = Offset(centerX + ovalWidth / 2f + offset, centerY + ovalHeight / 2f + offset - length),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Real-time Facial Landmark Telemetry Tracking Nodes
            val landmarkColor = alignmentColor.copy(alpha = pulseAlpha)

            // Forehead node
            drawCircle(landmarkColor, 4.dp.toPx(), Offset(centerX, centerY - ovalHeight * 0.30f))
            
            // Left & Right Eye Nodes
            drawCircle(landmarkColor, 5.dp.toPx(), Offset(centerX - ovalWidth * 0.22f, centerY - ovalHeight * 0.12f))
            drawCircle(landmarkColor, 5.dp.toPx(), Offset(centerX + ovalWidth * 0.22f, centerY - ovalHeight * 0.12f))
            
            // Nose Bridge Node
            drawCircle(landmarkColor, 4.dp.toPx(), Offset(centerX, centerY))

            // Left & Right Cheek Dermal Perfusion Nodes
            drawCircle(GlassAccent.copy(alpha = pulseAlpha), 6.dp.toPx(), Offset(centerX - ovalWidth * 0.28f, centerY + ovalHeight * 0.08f))
            drawCircle(GlassAccent.copy(alpha = pulseAlpha), 6.dp.toPx(), Offset(centerX + ovalWidth * 0.28f, centerY + ovalHeight * 0.08f))

            // Chin / Jawline Node
            drawCircle(landmarkColor, 4.dp.toPx(), Offset(centerX, centerY + ovalHeight * 0.34f))

            // Animated Laser Scanning Beam
            val laserY = centerY - ovalHeight / 2f + (ovalHeight * pulseAlpha)
            drawLine(
                color = GlassAccent,
                start = Offset(centerX - ovalWidth / 2f + 10.dp.toPx(), laserY),
                end = Offset(centerX + ovalWidth / 2f - 10.dp.toPx(), laserY),
                strokeWidth = 2.5.dp.toPx()
            )
        }
    }
}
