package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.HealthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainNavigationApp()
            }
        }
    }
}

@Composable
fun MainNavigationApp() {
    val navController = rememberNavController()
    val viewModel: HealthViewModel = viewModel()
    val activeUser by viewModel.activeUser.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.fillMaxSize(),
        enterTransition = { fadeIn(animationSpec = tween(400)) },
        exitTransition = { fadeOut(animationSpec = tween(400)) }
    ) {
        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    val destination = if (activeUser != null) "dashboard" else "login"
                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                onFinished = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onAuthSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToScan = { navController.navigate("scan") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToChat = { navController.navigate("chat") },
                onSelectReport = { reportId ->
                    viewModel.selectReport(reportId)
                    navController.navigate("report")
                }
            )
        }

        composable("chat") {
            ChatScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("scan") {
            ScanScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onScanFinished = { reportId ->
                    viewModel.selectReport(reportId)
                    navController.navigate("report") {
                        popUpTo("scan") { inclusive = true }
                    }
                }
            )
        }

        composable("report") {
            ReportScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = false }
                    }
                }
            )
        }

        composable("history") {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onSelectReport = { reportId ->
                    viewModel.selectReport(reportId)
                    navController.navigate("report")
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onLogoutSuccess = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
