package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.GlassPrimary
import com.example.ui.components.GlassSecondary
import com.example.ui.components.GlassAccent
import com.example.ui.components.GlassmorphicBackground
import com.example.ui.viewmodel.HealthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: HealthViewModel,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    var isRegisterMode by remember { mutableStateOf(false) }
    
    // Auth inputs
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("28") }
    var gender by remember { mutableStateOf("Male") }
    var height by remember { mutableStateOf("175") }
    var weight by remember { mutableStateOf("70") }

    var passwordVisible by remember { mutableStateOf(false) }
    val authError by viewModel.authError.collectAsState()

    GlassmorphicBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Branding Title
            Icon(
                imageVector = Icons.Default.Healing,
                contentDescription = null,
                tint = GlassPrimary,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Lumos Health",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "AI-Powered Health Intelligence",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Error Display
            if (authError != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassAccent.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error icon",
                            tint = GlassAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = authError ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassAccent
                        )
                    }
                }
            }

            // Authentication Card
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isRegisterMode) "Create Account" else "Welcome Back",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Common Inputs: Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GlassPrimary) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_input")
                        .padding(bottom = 12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                // Common Inputs: Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Secure Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GlassPrimary) },
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(icon, contentDescription = null)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input")
                        .padding(bottom = 16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                // Register Specific Inputs (Animated Toggle)
                AnimatedVisibility(
                    visible = isRegisterMode,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Full name
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GlassPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("name_input")
                                .padding(bottom = 12.dp)
                        )

                        // Age, Gender row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = age,
                                onValueChange = { age = it },
                                label = { Text("Age (yrs)") },
                                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = GlassPrimary) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("age_input"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            // Gender dropdown mockup
                            var genderExpanded by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = gender,
                                    onValueChange = {},
                                    label = { Text("Gender") },
                                    leadingIcon = { Icon(Icons.Default.Transgender, contentDescription = null, tint = GlassPrimary) },
                                    trailingIcon = {
                                        IconButton(onClick = { genderExpanded = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    },
                                    readOnly = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                DropdownMenu(
                                    expanded = genderExpanded,
                                    onDismissRequest = { genderExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Male") },
                                        onClick = { gender = "Male"; genderExpanded = false }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Female") },
                                        onClick = { gender = "Female"; genderExpanded = false }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Other") },
                                        onClick = { gender = "Other"; genderExpanded = false }
                                    )
                                }
                            }
                        }

                        // Height, Weight row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = height,
                                onValueChange = { height = it },
                                label = { Text("Height (cm)") },
                                leadingIcon = { Icon(Icons.Default.Height, contentDescription = null, tint = GlassPrimary) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("height_input"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            OutlinedTextField(
                                value = weight,
                                onValueChange = { weight = it },
                                label = { Text("Weight (kg)") },
                                leadingIcon = { Icon(Icons.Default.Scale, contentDescription = null, tint = GlassPrimary) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("weight_input"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }
                }

                // Action Button
                Button(
                    onClick = {
                        if (isRegisterMode) {
                            val userAge = age.toIntOrNull() ?: 28
                            val userHeight = height.toDoubleOrNull() ?: 175.0
                            val userWeight = weight.toDoubleOrNull() ?: 70.0
                            viewModel.register(
                                email = email,
                                passwordText = password,
                                name = name,
                                gender = gender,
                                age = userAge,
                                heightCm = userHeight,
                                weightKg = userWeight,
                                onSuccess = onAuthSuccess
                            )
                        } else {
                            viewModel.login(
                                email = email,
                                passwordText = password,
                                onSuccess = onAuthSuccess
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GlassPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("auth_submit_button")
                ) {
                    Text(
                        text = if (isRegisterMode) "Sign Up" else "Secure Login",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mode switcher toggle link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isRegisterMode) "Already have an account? " else "Don't have an account? ",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = if (isRegisterMode) "Log In" else "Sign Up",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlassPrimary,
                        modifier = Modifier
                            .clickable { isRegisterMode = !isRegisterMode }
                            .testTag("toggle_auth_mode")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider OR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                    Text(
                        text = "  OR  ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Continue with Google Button
                OutlinedButton(
                    onClick = {
                        viewModel.loginWithGoogle(
                            context = context,
                            onSuccess = onAuthSuccess
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("google_login_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Official Multi-Colored Google G Logo
                        GoogleLogoIcon(modifier = Modifier.size(22.dp))
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Extra Security Banner
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = GlassSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "End-to-End Cryptographic Local Authentication",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Official 4-Color Google Brand Logo Canvas Component
 * Colors: Red (#EA4335), Yellow (#FBBC05), Green (#34A853), Blue (#4285F4)
 */
@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier.size(22.dp)) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val strokePx = width * 0.20f

        // Red arc (Top)
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 200f,
            sweepAngle = 110f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokePx, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
        // Yellow arc (Bottom-Left)
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 120f,
            sweepAngle = 80f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokePx, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
        // Green arc (Bottom-Right)
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 25f,
            sweepAngle = 95f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokePx, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
        // Blue arc (Right)
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -25f,
            sweepAngle = 50f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokePx, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
        // Horizontal Blue G-Bar
        drawLine(
            color = Color(0xFF4285F4),
            start = androidx.compose.ui.geometry.Offset(width * 0.45f, height * 0.50f),
            end = androidx.compose.ui.geometry.Offset(width * 0.95f, height * 0.50f),
            strokeWidth = strokePx,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}
