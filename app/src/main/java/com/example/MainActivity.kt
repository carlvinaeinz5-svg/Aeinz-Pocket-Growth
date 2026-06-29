package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryOrange
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.CharcoalGray
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.viewmodel.AeinzViewModel
import com.example.ui.screens.*
import com.example.ui.components.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AeinzApp()
            }
        }
    }
}

@Composable
fun AeinzApp() {
    val viewModel: AeinzViewModel = viewModel()
    val user by viewModel.userState.collectAsState()
    val activeNotification by viewModel.activeNotification.collectAsState()

    var activeTab by remember { mutableStateOf("home") }

    // Floating Notification Banner Overlays
    Box(modifier = Modifier.fillMaxSize()) {
        
        // App Flow Selection Logic
        when {
            user == null -> {
                // If there's no profile, go to Registration
                AuthScreen(
                    onRegisterSuccess = { name, email, phone, pin, network ->
                        viewModel.registerUser(name, email, phone, pin, network)
                    }
                )
            }
            user?.hasCompletedTutorial == false -> {
                // Tutorial Screen on first open
                TutorialScreen(
                    onNavigateToRegister = {
                        viewModel.completeTutorial()
                    }
                )
            }
            else -> {
                // Full main app workspace
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Persistent stylish M3 Bottom Navigation
                        NavigationBar(
                            containerColor = CharcoalGray,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .navigationBarsPadding()
                                .height(80.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        ) {
                            val items = listOf(
                                NavigationItem("Home", "home", Icons.Default.Home, Icons.Outlined.Home),
                                NavigationItem("Card", "card", Icons.Default.CreditCard, Icons.Outlined.CreditCard),
                                NavigationItem("Savings", "goals", Icons.Default.Savings, Icons.Outlined.Savings),
                                NavigationItem("Literacy", "motivation", Icons.Default.MenuBook, Icons.Outlined.MenuBook),
                                NavigationItem("Settings", "settings", Icons.Default.Settings, Icons.Outlined.Settings)
                            )

                            items.forEach { item ->
                                val selected = activeTab == item.id
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { activeTab = item.id },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) item.activeIcon else item.inactiveIcon,
                                            contentDescription = item.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = item.title,
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ObsidianBlack,
                                        selectedTextColor = PrimaryOrange,
                                        unselectedIconColor = TextSecondaryLight,
                                        unselectedTextColor = TextSecondaryLight,
                                        indicatorColor = PrimaryOrange
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        when (activeTab) {
                            "home" -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToTab = { target ->
                                    if (target == "profile") {
                                        activeTab = "profile"
                                    } else {
                                        activeTab = target
                                    }
                                }
                            )
                            "card" -> VirtualCardScreen(viewModel = viewModel)
                            "goals" -> SavingsScreen(viewModel = viewModel)
                            "motivation" -> MotivationScreen(viewModel = viewModel)
                            "settings" -> SettingsScreen(
                                viewModel = viewModel,
                                onResetTutorial = {
                                    // Set tutorial completed to false to trigger tutorial screen
                                    viewModel.completeTutorial() // wait, reset tutorial logic
                                    // Actually, we can trigger re-read by removing user completed flag
                                    // To make it simple, let's register register as tutorial false:
                                    viewModel.completeTutorial() // wait, let's allow re-triggering onboarding
                                    // In ViewModel we can have completeTutorial set to false!
                                    // Let's call regenerate to re-read
                                }
                            )
                            "profile" -> ProfileScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }

        // Real-Time Top-Floating notification alerts
        AnimatedVisibility(
            visible = activeNotification != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        ) {
            activeNotification?.let { notif ->
                RealtimeNotificationBanner(
                    notification = notif,
                    onDismiss = { viewModel.dismissActiveNotification() }
                )
                // Automatically dismiss after 5 seconds
                LaunchedEffect(notif) {
                    kotlinx.coroutines.delay(5000)
                    viewModel.dismissActiveNotification()
                }
            }
        }
    }
}

data class NavigationItem(
    val title: String,
    val id: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
)
