package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.data.database.SavingsGoalEntity
import com.example.data.database.TransactionEntity
import com.example.data.database.UserEntity
import com.example.ui.components.AeinzBackground
import com.example.ui.components.AeinzButton
import com.example.ui.components.AeinzCard
import com.example.ui.viewmodel.AeinzViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: AeinzViewModel,
    onNavigateToTab: (String) -> Unit
) {
    val user by viewModel.userState.collectAsState()
    val transactions by viewModel.transactionsState.collectAsState()
    val goals by viewModel.goalsState.collectAsState()

    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showExchangeDialog by remember { mutableStateOf(false) }

    AeinzBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hello, ${user?.name ?: "Valued Earner"}",
                            color = Slate900,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Aeinz Pocket Growth Active Profile",
                            color = Slate500,
                            fontSize = 13.sp
                        )
                    }
                    IconButton(
                        onClick = { onNavigateToTab("profile") },
                        modifier = Modifier
                            .size(44.dp)
                            .background(PrimaryOrange.copy(alpha = 0.1f), CircleShape)
                            .border(1.dp, PrimaryOrange.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = PrimaryOrange)
                    }
                }
            }

            // Wallet Balance Card with Elegant Copper-Orange-Slate Gradient
            item {
                val balanceGradient = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFEA580C), Color(0xFFF97316), Color(0xFF1E293B))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(balanceGradient)
                        .border(1.dp, PrimaryOrange.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "POCKET WALLET BALANCE",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp
                            )
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Wallet",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "${viewModel.formatUgx(user?.balanceUgx ?: 0.0)} UGX",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Transaction Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ActionButton(
                                text = "Deposit",
                                icon = Icons.Default.AddCircle,
                                onClick = { showDepositDialog = true },
                                modifier = Modifier.weight(1f)
                            )
                            ActionButton(
                                text = "Withdraw",
                                icon = Icons.Default.RemoveCircle,
                                onClick = { showWithdrawDialog = true },
                                modifier = Modifier.weight(1f)
                            )
                            ActionButton(
                                text = "Exchange",
                                icon = Icons.Default.SwapHoriz,
                                onClick = { showExchangeDialog = true },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Virtual Card Segment Mini-Banner
            item {
                AeinzCard(
                    borderGlow = true,
                    onClick = { onNavigateToTab("card") }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = "Card",
                                tint = PrimaryOrange,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Aeinz Transact Card",
                                    color = Slate900,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (user?.cardLocked == true) "Locked • Click to Unfreeze" else "Active • Tap to view credentials",
                                    color = if (user?.cardLocked == true) CrimsonRed else EmeraldGreen,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Go",
                            tint = Slate400,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Savings Goals Summary Widget
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Active Savings Goals",
                            color = Slate900,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "See All",
                            color = PrimaryOrange,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToTab("goals") }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    if (goals.isEmpty()) {
                        AeinzCard {
                            Text(
                                text = "No saving goals yet! Create a goal to automate savings and lock down budgets.",
                                color = Slate500,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            goals.take(2).forEach { goal ->
                                val progress = if (goal.targetAmount > 0) goal.savedAmount / goal.targetAmount else 0.0
                                AeinzCard {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(goal.name, color = Slate900, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${(progress * 100).toInt()}%", color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = progress.toFloat(),
                                        color = PrimaryOrange,
                                        trackColor = Slate100,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${viewModel.formatUgx(goal.savedAmount)} / ${viewModel.formatUgx(goal.targetAmount)} UGX",
                                        color = Slate500,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recent Transactions Title
            item {
                Text(
                    text = "Recent Transactions",
                    color = Slate900,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            // Transaction History list
            if (transactions.isEmpty()) {
                item {
                    AeinzCard {
                        Text(
                            text = "No recent transactions found.",
                            color = Slate500,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                items(transactions.take(5)) { tx ->
                    TransactionItem(tx = tx, viewModel = viewModel)
                }
            }
        }
    }

    // --- Modals & Dialogs ---

    // 1. Deposit Dialog
    if (showDepositDialog) {
        var depositAmount by remember { mutableStateOf("") }
        var networkProvider by remember { mutableStateOf(user?.mobileNetwork ?: "MTN Mobile Money") }
        var inputError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showDepositDialog = false },
            containerColor = Color.White,
            title = { Text("Deposit Funds", color = Slate900, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Top up your Pocket account using mobile money. Deposits are completely free.", color = Slate500, fontSize = 13.sp)
                    
                    // Network Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("MTN Mobile Money", "Airtel Money").forEach { prov ->
                            val active = networkProvider == prov
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) PrimaryOrange.copy(alpha = 0.15f) else Slate50)
                                    .border(1.dp, if (active) PrimaryOrange else Slate200, RoundedCornerShape(8.dp))
                                    .clickable { networkProvider = prov },
                                contentAlignment = Alignment.Center
                            ) {
                                text_or_null(prov, active)
                            }
                        }
                    }

                    // Amount Text Field
                    OutlinedTextField(
                        value = depositAmount,
                        onValueChange = { depositAmount = it; inputError = null },
                        label = { Text("Amount (UGX)") },
                        placeholder = { Text("Min 1,000 UGX") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = Slate200,
                            focusedTextColor = Slate900,
                            unfocusedTextColor = Slate900
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (inputError != null) {
                        Text(inputError!!, color = CrimsonRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = depositAmount.toDoubleOrNull()
                        if (amt == null || amt < 1000.0) {
                            inputError = "Minimum deposit must be 1,000 UGX"
                        } else {
                            viewModel.depositFunds(amt, networkProvider)
                            showDepositDialog = false
                        }
                    }
                ) {
                    Text("DEPOSIT", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositDialog = false }) {
                    Text("CANCEL", color = Slate500)
                }
            }
        )
    }

    // 2. Withdrawal Dialog
    if (showWithdrawDialog) {
        var withdrawAmount by remember { mutableStateOf("") }
        var networkProvider by remember { mutableStateOf(user?.mobileNetwork ?: "MTN Mobile Money") }
        var inputError by remember { mutableStateOf<String?>(null) }

        val amountDouble = withdrawAmount.toDoubleOrNull() ?: 0.0
        val rawFee = amountDouble * 0.05
        val fee = if (rawFee > 14900.0) 14900.0 else rawFee
        val totalCost = amountDouble + fee

        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            containerColor = Color.White,
            title = { Text("Withdraw Funds", color = Slate900, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Cash out to your linked network account. Minimal fees apply to help low-earners.", color = Slate500, fontSize = 13.sp)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("MTN Mobile Money", "Airtel Money").forEach { prov ->
                            val active = networkProvider == prov
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) PrimaryOrange.copy(alpha = 0.15f) else Slate50)
                                    .border(1.dp, if (active) PrimaryOrange else Slate200, RoundedCornerShape(8.dp))
                                    .clickable { networkProvider = prov },
                                contentAlignment = Alignment.Center
                            ) {
                                text_or_null(prov, active)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it; inputError = null },
                        label = { Text("Amount (UGX)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = Slate200,
                            focusedTextColor = Slate900,
                            unfocusedTextColor = Slate900
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Fee Breakdown Summary
                    if (amountDouble > 0) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate50, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Withdrawal Charge (5%):", color = Slate500, fontSize = 12.sp)
                                Text("${viewModel.formatUgx(fee)} UGX", color = PrimaryOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Wallet Debit:", color = Slate500, fontSize = 12.sp)
                                Text("${viewModel.formatUgx(totalCost)} UGX", color = Slate900, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("*Withdrawal fees are safely capped below 15,000 UGX", color = PrimaryOrange, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    
                    if (inputError != null) {
                        Text(inputError!!, color = CrimsonRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = withdrawAmount.toDoubleOrNull()
                        if (amt == null || amt <= 0.0) {
                            inputError = "Enter a valid amount"
                        } else {
                            val balance = user?.balanceUgx ?: 0.0
                            if (balance < totalCost) {
                                inputError = "Insufficient balance. Total needed: ${viewModel.formatUgx(totalCost)} UGX."
                            } else {
                                viewModel.withdrawFunds(amt, networkProvider)
                                showWithdrawDialog = false
                            }
                        }
                    }
                ) {
                    Text("WITHDRAW", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("CANCEL", color = Slate500)
                }
            }
        )
    }

    // 3. Quick Exchange Dialog
    if (showExchangeDialog) {
        var exchangeAmt by remember { mutableStateOf("") }
        var targetCurr by remember { mutableStateOf("USD") }
        var inputError by remember { mutableStateOf<String?>(null) }

        val amountDouble = exchangeAmt.toDoubleOrNull() ?: 0.0
        val rate = viewModel.exchangeRates[targetCurr] ?: 1.0
        val converted = if (rate > 0) amountDouble / rate else 0.0

        AlertDialog(
            onDismissRequest = { showExchangeDialog = false },
            containerColor = Color.White,
            title = { Text("Currency Exchanger", color = Slate900, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Convert UGX to major global currencies instantly basing on real-time exchange rates.", color = Slate500, fontSize = 13.sp)

                    // Target Currency Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        viewModel.exchangeRates.keys.forEach { curr ->
                            val active = targetCurr == curr
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (active) PrimaryOrange.copy(alpha = 0.15f) else Slate50)
                                    .border(1.dp, if (active) PrimaryOrange else Slate200, RoundedCornerShape(6.dp))
                                    .clickable { targetCurr = curr },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(curr, color = if (active) PrimaryOrange else Slate500, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = exchangeAmt,
                        onValueChange = { exchangeAmt = it; inputError = null },
                        label = { Text("Amount to Exchange (UGX)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = Slate200,
                            focusedTextColor = Slate900,
                            unfocusedTextColor = Slate900
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (amountDouble > 0) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate50, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Exchange Rate:", color = Slate500, fontSize = 12.sp)
                                Text("1 $targetCurr = ${viewModel.formatUgx(rate)} UGX", color = PrimaryOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("You Receive:", color = Slate500, fontSize = 12.sp)
                                Text("${String.format("%.2f", converted)} $targetCurr", color = EmeraldGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (inputError != null) {
                        Text(inputError!!, color = CrimsonRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = exchangeAmt.toDoubleOrNull()
                        if (amt == null || amt <= 0.0) {
                            inputError = "Enter a valid amount"
                        } else {
                            val balance = user?.balanceUgx ?: 0.0
                            if (balance < amt) {
                                inputError = "Insufficient wallet balance in UGX"
                            } else {
                                viewModel.exchangeCurrency(amt, targetCurr)
                                showExchangeDialog = false
                            }
                        }
                    }
                ) {
                    Text("CONVERT", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExchangeDialog = false }) {
                    Text("CANCEL", color = Slate500)
                }
            }
        )
    }
}

@Composable
fun text_or_null(prov: String, active: Boolean) {
    Text(prov, color = if (active) PrimaryOrange else Slate500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
}

@Composable
fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Slate100, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = PrimaryOrange,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            color = Slate900,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TransactionItem(
    tx: TransactionEntity,
    viewModel: AeinzViewModel
) {
    val sdf = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.ROOT)
    val formattedDate = sdf.format(Date(tx.timestamp))

    val isDeclined = tx.type == "CARD_SPEND_DECLINED"

    val icon = when (tx.type) {
        "DEPOSIT" -> Icons.Default.TrendingUp
        "WITHDRAWAL" -> Icons.Default.TrendingDown
        "CARD_SPEND" -> Icons.Default.CreditCard
        "CARD_SPEND_DECLINED" -> Icons.Default.Cancel
        "EXCHANGE" -> Icons.Default.CurrencyExchange
        else -> Icons.Default.Savings
    }

    val iconColor = when (tx.type) {
        "DEPOSIT" -> EmeraldGreen
        "WITHDRAWAL" -> CrimsonRed
        "CARD_SPEND" -> PrimaryOrange
        "CARD_SPEND_DECLINED" -> CrimsonRed
        "EXCHANGE" -> BrightGold
        else -> LightOrange
    }

    val sign = when (tx.type) {
        "DEPOSIT" -> "+"
        "WITHDRAWAL" -> "-"
        "CARD_SPEND" -> "-"
        "CARD_SPEND_DECLINED" -> ""
        "SAVINGS_TRANSFER" -> if (tx.amount > 0) "-" else ""
        else -> "-"
    }

    val displayAmount = if (tx.type == "EXCHANGE") {
        "${viewModel.formatUgx(tx.amount)} UGX → ${String.format("%.2f", tx.convertedAmount)} ${tx.convertedCurrency}"
    } else {
        "$sign${viewModel.formatUgx(tx.amount)} UGX"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Slate100, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isDeclined) "${tx.providerOrDetails} (Declined)" else tx.providerOrDetails,
                color = if (isDeclined) CrimsonRed else Slate900,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formattedDate,
                color = Slate500,
                fontSize = 11.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = displayAmount,
                color = if (tx.type == "DEPOSIT") EmeraldGreen else if (isDeclined) CrimsonRed else Slate900,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            if (tx.fee > 0) {
                Text(
                    text = "Fee: ${viewModel.formatUgx(tx.fee)} UGX",
                    color = CrimsonRed,
                    fontSize = 10.sp
                )
            }
        }
    }
}
