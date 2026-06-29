package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SwapHoriz
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
import com.example.ui.components.AeinzBackground
import com.example.ui.components.AeinzButton
import com.example.ui.theme.*

@Composable
fun TutorialScreen(
    onNavigateToRegister: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }

    val steps = listOf(
        TutorialPage(
            title = "Aeinz Pocket Growth",
            description = "Welcome to your premier mobile wallet designed to elevate low- and moderate-income earners in Uganda. We offer low-fee account management, automated saving, and online card payments.",
            icon = Icons.Default.Info
        ),
        TutorialPage(
            title = "Aeinz Transact Card",
            description = "Get an instant, customized virtual card. Pay safely online, freeze/unfreeze on demand, set your spending limits, and transact globally from your phone.",
            icon = Icons.Default.CardMembership
        ),
        TutorialPage(
            title = "Automated Savings Goals",
            description = "Create customizable savings goals. Start small with a minimum deposit of only 1,000 UGX! Watch your savings grow with real-time tracking.",
            icon = Icons.Default.Savings
        ),
        TutorialPage(
            title = "Low & Predictable Fees",
            description = "No hidden charges! Deposits are totally free. Withdrawals carry a clear 5% charge, and the minimal fee is guaranteed to be less than 15,000 UGX.",
            icon = Icons.Default.SwapHoriz
        )
    )

    val currentPage = steps[step - 1]

    AeinzBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .navigationBarsPadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier.testTag("skip_tutorial_button")
                ) {
                    Text("SKIP", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                }
            }

            // Page Content (Animated Transition)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(PrimaryOrange.copy(alpha = 0.2f), Color.Transparent)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = currentPage.icon,
                        contentDescription = "Icon",
                        tint = PrimaryOrange,
                        modifier = Modifier.size(56.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = currentPage.title,
                    color = TextPrimaryLight,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = currentPage.description,
                    color = TextSecondaryLight,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            // Bottom Nav and Indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Page Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    for (i in 1..4) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (i == step) 20.dp else 8.dp, height = 8.dp)
                                .clip(CircleShape)
                                .background(if (i == step) PrimaryOrange else Color(0xFFCBD5E1))
                        )
                    }
                }

                // Next Button
                AeinzButton(
                    text = if (step == 4) "GET STARTED" else "CONTINUE",
                    onClick = {
                        if (step < 4) {
                            step++
                        } else {
                            onNavigateToRegister()
                        }
                    },
                    testTag = "tutorial_next_button"
                )
            }
        }
    }
}

data class TutorialPage(
    val title: String,
    val description: String,
    val icon: ImageVector
)
