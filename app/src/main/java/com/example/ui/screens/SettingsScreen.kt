package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.viewmodel.HealthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val activeUser by viewModel.activeUser.collectAsState()

    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var notificationsEnabled by remember { mutableStateOf(true) }

    var showSavedToast by remember { mutableStateOf(false) }

    LaunchedEffect(activeUser) {
        activeUser?.let {
            age = "${it.age}"
            gender = it.gender
            height = "${it.heightCm.toInt()}"
            weight = "${it.weightKg.toInt()}"
            notificationsEnabled = it.notificationsEnabled
        }
    }

    GlassmorphicBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Profile & Settings", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
                    .verticalScroll(rememberScrollState())
            ) {
            Spacer(modifier = Modifier.height(16.dp))

            // User identity header card
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(GlassPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = GlassPrimary, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = activeUser?.name ?: "Patient",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = activeUser?.email ?: "",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Section: Physical Attributes Grounding
            Text(
                text = "Biomarker Grounding Attributes",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                // Age Field
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age (yrs)") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = GlassPrimary) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Gender Selection Dialog Link
                var genderExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        label = { Text("Biological Gender") },
                        leadingIcon = { Icon(Icons.Default.Transgender, contentDescription = null, tint = GlassPrimary) },
                        trailingIcon = {
                            IconButton(onClick = { genderExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        readOnly = true,
                        shape = RoundedCornerShape(10.dp),
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
                    }
                }

                // Height & Weight Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height (cm)") },
                        leadingIcon = { Icon(Icons.Default.Height, contentDescription = null, tint = GlassPrimary) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        leadingIcon = { Icon(Icons.Default.Scale, contentDescription = null, tint = GlassPrimary) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val parsedAge = age.toIntOrNull() ?: 28
                        val parsedHeight = height.toDoubleOrNull() ?: 175.0
                        val parsedWeight = weight.toDoubleOrNull() ?: 70.0
                        viewModel.updateProfile(gender, parsedAge, parsedHeight, parsedWeight)
                        showSavedToast = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GlassPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("save_profile_button")
                ) {
                    Text("Save Attributes", fontWeight = FontWeight.Bold)
                }

                if (showSavedToast) {
                    LaunchedEffect(Unit) {
                        delay(2000)
                        showSavedToast = false
                    }
                    Text(
                        text = "Attributes updated successfully!",
                        color = GlassPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            }



            // Section: Preferences
            Text(
                text = "Account Preferences",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = GlassSecondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Health Reminders", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Get notified to do daily face checks", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = {
                            notificationsEnabled = it
                            viewModel.updateNotifications(it)
                        },
                        modifier = Modifier.testTag("notifications_switch")
                    )
                }
            }

            // Legal & Security info links
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = GlassSecondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Privacy & Security Agreement", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                }

                Divider(color = Color.Gray.copy(alpha = 0.15f), thickness = 1.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Assignment, contentDescription = null, tint = GlassPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Medical AI Terms of Use", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                }
            }

            // Secure Logout action button
            Button(
                onClick = {
                    viewModel.logout {
                        onLogoutSuccess()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GlassAccent.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(bottom = 12.dp)
                    .testTag("settings_logout_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = GlassAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Secure Log Out", color = GlassAccent, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
}
