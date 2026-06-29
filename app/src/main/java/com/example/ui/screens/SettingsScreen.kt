package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AeinzBackground
import com.example.ui.components.AeinzCard
import com.example.ui.components.AeinzOutlinedButton
import com.example.ui.viewmodel.AeinzViewModel
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: AeinzViewModel,
    onResetTutorial: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    
    var notificationsEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(false) }

    AeinzBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Screen Header
            Column {
                Text(
                    text = "System Settings",
                    color = TextPrimaryLight,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Control notification parameters, security, and view fee schedules",
                    color = TextSecondaryLight,
                    fontSize = 13.sp
                )
            }

            // Notification preferences
            AeinzCard {
                Text(
                    text = "Security & Notifications",
                    color = TextPrimaryLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Toggle 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, "Notif", tint = PrimaryOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Real-Time Push Alerts", color = TextPrimaryLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Instant notifications for wallet and card actions", color = TextSecondaryLight, fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ObsidianBlack,
                            checkedTrackColor = PrimaryOrange,
                            uncheckedThumbColor = TextSecondaryLight,
                            uncheckedTrackColor = Color(0x11FFFFFF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Fingerprint, "Bio", tint = PrimaryOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Biometric Security Lock", color = TextPrimaryLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Secure wallet access using fingerprint/face ID", color = TextSecondaryLight, fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = biometricEnabled,
                        onCheckedChange = { biometricEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ObsidianBlack,
                            checkedTrackColor = PrimaryOrange,
                            uncheckedThumbColor = TextSecondaryLight,
                            uncheckedTrackColor = Color(0x11FFFFFF)
                        )
                    )
                }
            }

            // Legal Fee Schedule & Rules Document
            AeinzCard(borderGlow = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Official Pocket Fee Schedules",
                        color = TextPrimaryLight,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(Icons.Default.Policy, "Policy", tint = PrimaryOrange)
                }
                Text(
                    text = "Aeinz Pocket Growth is dedicated to helping Ugandan moderate and low-income society. We guarantee transparent and highly predictable charging systems:",
                    color = TextSecondaryLight,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RuleRow(title = "Account Setup & Management", desc = "100% Free. No monthly charges.")
                    RuleRow(title = "Minimum Account Deposit", desc = "1,000 UGX")
                    RuleRow(title = "Withdrawal Outward Fee", desc = "5% of cashout value")
                    RuleRow(title = "Minimal Cashout Limit", desc = "Capped strictly under 15,000 UGX")
                    RuleRow(title = "Virtual Card Issuance", desc = "Free. Zero monthly maintenance.")
                }
            }

            // Support Channels Card
            AeinzCard {
                Text(
                    text = "Corporate Support Contacts",
                    color = TextPrimaryLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Call, "Phone", tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("WhatsApp Chat: 0747944559 (WhatsApp Only)", color = TextSecondaryLight, fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, "Email", tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Email support: carlvinaeinz5@gmail.com", color = TextSecondaryLight, fontSize = 12.sp)
                    }
                }
            }

            // Onboarding Reset Action
            AeinzCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Reset Application Tour",
                        color = TextPrimaryLight,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Need a refresher about Aeinz Pocket Growth policies, automated tools, and card setup? Click below to restart the interactive tour tutorial.",
                        color = TextSecondaryLight,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AeinzOutlinedButton(
                        text = "RELAUNCH TOUR TUTORIAL",
                        onClick = {
                            onResetTutorial()
                            Toast.makeText(context, "Tutorial reset!", Toast.LENGTH_SHORT).show()
                        },
                        testTag = "reset_onboarding_tutorial_btn"
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun RuleRow(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x06FFFFFF))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TextPrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(desc, color = PrimaryOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
