package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AeinzBackground
import com.example.ui.components.AeinzButton
import com.example.ui.components.AeinzCard
import com.example.ui.components.AeinzOutlinedButton
import com.example.ui.viewmodel.AeinzViewModel
import com.example.ui.theme.*

@Composable
fun VirtualCardScreen(
    viewModel: AeinzViewModel
) {
    val user by viewModel.userState.collectAsState()
    var isRevealed by remember { mutableStateOf(false) }
    var showSimulatorDialog by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()

    AeinzBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Screen Title
            Column {
                Text(
                    text = "Aeinz Transact Card",
                    color = TextPrimaryLight,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Your secure virtual gateway for global online payments",
                    color = TextSecondaryLight,
                    fontSize = 13.sp
                )
            }

            // Visual Virtual Card (Master Card feel)
            val cardGradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFEA580C), // Vibrant Orange-600
                    Color(0xFFF97316), // Vibrant Orange-500
                    Color(0xFF0F172A)  // Elegant Slate-900 (Black step)
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(cardGradient)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(listOf(PrimaryOrange, LightOrange.copy(alpha = 0.5f))),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Card Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AEINZ TRANSACT",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Wifi,
                                contentDescription = "Contactless",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DEBIT",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Chip & Locked Overlay
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Chip
                        Box(
                            modifier = Modifier
                                .size(38.dp, 28.dp)
                                .background(BrightGold.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                .border(1.dp, BrightGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        )

                        if (user?.cardLocked == true) {
                            Badge(
                                containerColor = CrimsonRed,
                                contentColor = ObsidianBlack,
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Lock, "Locked", tint = Color.White, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("FROZEN", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Card Number
                    val rawCardNum = user?.cardNumber ?: "4865 0000 0000 0000"
                    val displayCardNum = if (isRevealed) rawCardNum else "4865 •••• •••• " + rawCardNum.takeLast(4)
                    
                    Text(
                        text = displayCardNum,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    // Card Footer (Holder & Expiry / CVV)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("CARD HOLDER", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                            Text(user?.cardHolder ?: "HOLDER NAME", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("EXPIRES", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                Text(user?.cardExpiry ?: "MM/YY", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("CVV", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                Text(if (isRevealed) (user?.cardCvv ?: "000") else "•••", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quick Actions: Reveal, Freeze, Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButtonCard(
                    text = if (isRevealed) "Hide Info" else "Reveal Info",
                    icon = if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    onClick = { isRevealed = !isRevealed },
                    modifier = Modifier.weight(1f)
                )
                IconButtonCard(
                    text = if (user?.cardLocked == true) "Activate" else "Freeze Card",
                    icon = if (user?.cardLocked == true) Icons.Default.LockOpen else Icons.Default.Lock,
                    iconColor = if (user?.cardLocked == true) EmeraldGreen else CrimsonRed,
                    onClick = { viewModel.toggleCardFreeze() },
                    modifier = Modifier.weight(1f)
                )
            }

            // Spend Limit Segment
            AeinzCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Card Spend Limit",
                            color = TextPrimaryLight,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Current limit: ${viewModel.formatUgx(user?.cardSpendLimit ?: 500000.0)} UGX",
                            color = TextSecondaryLight,
                            fontSize = 12.sp
                        )
                    }
                    Icon(Icons.Default.Security, "Limit", tint = PrimaryOrange)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                var sliderValue by remember(user?.cardSpendLimit) {
                    mutableFloatStateOf((user?.cardSpendLimit ?: 500000.0).toFloat())
                }

                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    onValueChangeFinished = {
                        viewModel.updateCardLimit(sliderValue.toDouble())
                    },
                    valueRange = 50000f..2000000f,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryOrange,
                        activeTrackColor = PrimaryOrange,
                        inactiveTrackColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("50,000 UGX", color = TextSecondaryLight, fontSize = 11.sp)
                    Text("2,000,000 UGX", color = TextSecondaryLight, fontSize = 11.sp)
                }
            }

            // Quick Spend Simulator Card
            AeinzCard(borderGlow = true) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Aeinz Card Spend Simulator",
                        color = TextPrimaryLight,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Test your virtual card instantly at major global and local online merchants! Simulated transactions deduct directly from your Pocket Wallet with zero hidden charges.",
                        color = TextSecondaryLight,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    AeinzButton(
                        text = "OPEN SPEND SIMULATOR",
                        onClick = { showSimulatorDialog = true },
                        testTag = "open_spend_simulator_btn"
                    )
                }
            }

            // Card Regeneration Action
            AeinzCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Regenerate Card Credentials",
                        color = TextPrimaryLight,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Compromised or want fresh details? Clicking below instantly issues a new Card Number, CVV, and expiry date. Previous credentials become permanently inactive.",
                        color = TextSecondaryLight,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AeinzOutlinedButton(
                        text = "REISSUE NEW CARD",
                        onClick = { viewModel.regenerateCardDetails() },
                        testTag = "reissue_card_btn"
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Spend Simulator Dialog
    if (showSimulatorDialog) {
        var selectedMerchant by remember { mutableStateOf("Jumia Uganda") }
        var inputSpendAmount by remember { mutableStateOf("15000") }
        var simError by remember { mutableStateOf<String?>(null) }

        val merchants = listOf("Netflix Kampala", "Jumia Uganda", "AWS Cloud", "Uganda Airlines", "KFC Kampala", "SafeBoda Kampala")

        AlertDialog(
            onDismissRequest = { showSimulatorDialog = false },
            containerColor = CharcoalGray,
            title = { Text("Simulate Online Card Spend", color = TextPrimaryLight, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Select a merchant and input the spend amount to simulate an online transaction.", color = TextSecondaryLight, fontSize = 12.sp)

                    // Merchant Dropdown / Chips
                    Column {
                        Text("Target Merchant", color = TextPrimaryLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            merchants.take(3).forEach { mer ->
                                val active = selectedMerchant == mer
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) PrimaryOrange.copy(alpha = 0.15f) else Color(0xFFF1F5F9))
                                        .border(1.dp, if (active) PrimaryOrange else Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                                        .clickable { selectedMerchant = mer },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(mer.substringBefore(" "), color = if (active) PrimaryOrange else TextSecondaryLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            merchants.takeLast(3).forEach { mer ->
                                val active = selectedMerchant == mer
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) PrimaryOrange.copy(alpha = 0.15f) else Color(0xFFF1F5F9))
                                        .border(1.dp, if (active) PrimaryOrange else Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                                        .clickable { selectedMerchant = mer },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(mer.substringBefore(" "), color = if (active) PrimaryOrange else TextSecondaryLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = inputSpendAmount,
                        onValueChange = { inputSpendAmount = it; simError = null },
                        label = { Text("Transaction Amount (UGX)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedTextColor = TextPrimaryLight,
                            unfocusedTextColor = TextPrimaryLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (simError != null) {
                        Text(simError!!, color = CrimsonRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = inputSpendAmount.toDoubleOrNull()
                        if (amt == null || amt <= 0.0) {
                            simError = "Please enter a valid amount"
                        } else {
                            val res = viewModel.simulateCardTransaction(selectedMerchant, amt)
                            if (res.isSuccess) {
                                showSimulatorDialog = false
                            } else {
                                simError = res.exceptionOrNull()?.message ?: "Transaction failed"
                            }
                        }
                    }
                ) {
                    Text("AUTHORIZE SPEND", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSimulatorDialog = false }) {
                    Text("CANCEL", color = TextSecondaryLight)
                }
            }
        )
    }
}

@Composable
fun IconButtonCard(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconColor: Color = PrimaryOrange
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = TextPrimaryLight,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
