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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.SavingsGoalEntity
import com.example.ui.components.*
import com.example.ui.viewmodel.AeinzViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavingsScreen(
    viewModel: AeinzViewModel
) {
    val user by viewModel.userState.collectAsState()
    val goals by viewModel.goalsState.collectAsState()
    val transactions by viewModel.transactionsState.collectAsState()
    val aiInsight by viewModel.aiInsightState.collectAsState()
    val isGeneratingInsight by viewModel.isGeneratingInsight.collectAsState()

    var showCreateGoalDialog by remember { mutableStateOf(false) }
    var showAddSavingsDialog by remember { mutableStateOf<SavingsGoalEntity?>(null) }

    // Aggregate values for the Custom Chart
    val depositsSum = transactions.filter { it.type == "DEPOSIT" }.sumOf { it.amount }.toFloat()
    val withdrawalsSum = transactions.filter { it.type == "WITHDRAWAL" }.sumOf { it.amount }.toFloat()
    val cardSpendSum = transactions.filter { it.type == "CARD_SPEND" }.sumOf { it.amount }.toFloat()

    val chartData = listOf(depositsSum, withdrawalsSum, cardSpendSum)
    val chartLabels = listOf("Deposits", "Withdrawals", "Card Spends")

    val totalSaved = goals.sumOf { it.savedAmount }
    val totalTarget = goals.sumOf { it.targetAmount }
    val overallProgress = if (totalTarget > 0) (totalSaved / totalTarget).toFloat() else 0.0f

    AeinzBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Screen Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Saving Track & Analysis",
                            color = Slate900,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Automated goals & gamified challenges",
                            color = Slate500,
                            fontSize = 13.sp
                        )
                    }
                    IconButton(
                        onClick = { showCreateGoalDialog = true },
                        modifier = Modifier
                            .size(44.dp)
                            .background(PrimaryOrange, CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create Goal", tint = Color.White)
                    }
                }
            }

            // Gamified Streaks & Levels Header Card
            item {
                val streakCount = user?.savingsStreak ?: 0
                val savingsLvl = user?.savingsLevel ?: 1
                val totalSavedAllTime = user?.totalSavedAllTime ?: 0.0
                
                val levelLabel = when (savingsLvl) {
                    1 -> "Starter Saver 🌱"
                    2 -> "Bronze Saver 🥉"
                    3 -> "Silver Saver 🥈"
                    4 -> "Gold Saver 🥇"
                    else -> "Savings Guru 👑"
                }

                val nextLvlTarget = when (savingsLvl) {
                    1 -> 10000.0
                    2 -> 50000.0
                    3 -> 200000.0
                    4 -> 1000000.0
                    else -> 1000000.0
                }
                
                val lvlProgress = if (nextLvlTarget > 0) (totalSavedAllTime / nextLvlTarget).coerceAtMost(1.0).toFloat() else 1.0f

                AeinzCard(borderGlow = true) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Savings Level & Streak",
                                color = Slate900,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .background(PrimaryOrange.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "Streak",
                                        tint = PrimaryOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$streakCount Day Streak",
                                        color = PrimaryOrange,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Level badge & Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(DarkOrange.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$savingsLvl",
                                    color = DarkOrange,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = levelLabel,
                                    color = Slate900,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "All-time Saved: ${viewModel.formatUgx(totalSavedAllTime)} / ${viewModel.formatUgx(nextLvlTarget)} UGX to Level Up",
                                    color = Slate500,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = lvlProgress,
                            color = PrimaryOrange,
                            trackColor = Slate100,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }

            // Badges & Milestones Card
            item {
                val unlockedBadges = user?.unlockedBadgesJson?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
                
                AeinzCard {
                    Text(
                        text = "Milestone Achievements & Badges",
                        color = Slate900,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Display Badges in a Row / Flow
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val badgeList = listOf(
                            BadgeItem("FIRST_SAVER", "Welcome Saver", "🥉", "Registered Aeinz account"),
                            BadgeItem("BRONZE_SAVER", "Bronze Giant", "🥉", "Saved over 10K UGX"),
                            BadgeItem("SILVER_SAVER", "Silver Master", "🥈", "Saved over 50K UGX"),
                            BadgeItem("GOLD_SAVER", "Gold Champion", "🥇", "Saved over 200K UGX"),
                            BadgeItem("SAVINGS_GURU", "Savings Legend", "👑", "Saved over 1M UGX"),
                            BadgeItem("STREAK_3", "Diligence Spark", "🔥", "3-Day Savings streak"),
                            BadgeItem("STREAK_7", "Savings Fire", "⚡", "7-Day Savings streak")
                        )

                        badgeList.forEach { badge ->
                            val isUnlocked = unlockedBadges.contains(badge.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isUnlocked) Slate50 else Slate100.copy(alpha = 0.5f))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(if (isUnlocked) PrimaryOrange.copy(alpha = 0.15f) else Slate200, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isUnlocked) badge.icon else "🔒",
                                        fontSize = 16.sp
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = badge.name,
                                        color = if (isUnlocked) Slate900 else Slate400,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = badge.desc,
                                        color = if (isUnlocked) Slate500 else Slate400,
                                        fontSize = 11.sp
                                    )
                                }
                                if (isUnlocked) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Unlocked",
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text(
                                        text = "Locked",
                                        color = Slate400,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Overall Savings Goal Progress Circle
            item {
                AeinzCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1.2f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Savings Goals Progress",
                                color = Slate900,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total saved toward active goals:",
                                color = Slate500,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${viewModel.formatUgx(totalSaved)} UGX",
                                color = PrimaryOrange,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Target: ${viewModel.formatUgx(totalTarget)} UGX",
                                color = Slate500,
                                fontSize = 11.sp
                            )
                        }
                        
                        AeinzCircularProgress(
                            progress = overallProgress,
                            title = "${(overallProgress * 100).toInt()}%",
                            subtitle = "Completed",
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                }
            }

            // Spending & Flow Analysis Custom Chart Segment
            item {
                AeinzCard {
                    Text(
                        text = "Account Flows (UGX)",
                        color = Slate900,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time comparison of incoming deposits vs outgoing cashout and virtual card spends.",
                        color = Slate500,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (depositsSum == 0f && withdrawalsSum == 0f && cardSpendSum == 0f) {
                        Text(
                            text = "No flow data yet. Complete transactions to see your spending breakdown analysis chart.",
                            color = Slate500,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        )
                    } else {
                        AeinzBarChart(data = chartData, labels = chartLabels)
                    }
                }
            }

            // Gemini-powered Spending Insights Module
            item {
                AeinzCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI",
                                tint = PrimaryOrange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Aeinz AI Budget Assistant",
                                color = Slate900,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (isGeneratingInsight) {
                            CircularProgressIndicator(
                                color = PrimaryOrange,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = aiInsight,
                        color = Slate900,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate50, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    AeinzButton(
                        text = if (isGeneratingInsight) "ANALYZING BUDGET..." else "GET PERSONALIZED INSIGHTS",
                        onClick = { viewModel.generateSpendingAnalysis() },
                        enabled = !isGeneratingInsight,
                        testTag = "get_ai_insights_button"
                    )
                }
            }

            // Goals Section Header
            item {
                Text(
                    text = "Goal Allocations",
                    color = Slate900,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Goals List
            if (goals.isEmpty()) {
                item {
                    AeinzCard {
                        Text(
                            text = "You don't have any savings goals yet. Create one by clicking the '+' button at the top!",
                            color = Slate500,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                items(goals) { goal ->
                    GoalItem(
                        goal = goal,
                        viewModel = viewModel,
                        onAddSavingsClick = { showAddSavingsDialog = goal }
                    )
                }
            }
        }
    }

    // --- Modals & Dialogs ---

    // 1. Create Goal Dialog
    if (showCreateGoalDialog) {
        var goalName by remember { mutableStateOf("") }
        var targetAmt by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf("Education") }
        var deadlineMonths by remember { mutableStateOf("6") }
        var inputError by remember { mutableStateOf<String?>(null) }

        val categories = listOf("Education", "Business", "Emergency Fund", "Health", "Leisure")

        AlertDialog(
            onDismissRequest = { showCreateGoalDialog = false },
            containerColor = Color.White,
            title = { Text("Create Savings Goal", color = Slate900, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Lock away money and track progress. Minimum deposit for goals is 1,000 UGX.", color = Slate500, fontSize = 12.sp)

                    OutlinedTextField(
                        value = goalName,
                        onValueChange = { goalName = it; inputError = null },
                        label = { Text("Goal Name") },
                        placeholder = { Text("e.g. School Fees, Shop Rent") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = Slate200,
                            focusedTextColor = Slate900,
                            unfocusedTextColor = Slate900
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = targetAmt,
                        onValueChange = { targetAmt = it; inputError = null },
                        label = { Text("Target Amount (UGX)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = Slate200,
                            focusedTextColor = Slate900,
                            unfocusedTextColor = Slate900
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = deadlineMonths,
                        onValueChange = { deadlineMonths = it; inputError = null },
                        label = { Text("Deadline (Months)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = Slate200,
                            focusedTextColor = Slate900,
                            unfocusedTextColor = Slate900
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category Chooser
                    Column {
                        Text("Category", color = Slate900, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            categories.take(3).forEach { cat ->
                                val active = selectedCategory == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) PrimaryOrange.copy(alpha = 0.15f) else Slate50)
                                        .border(1.dp, if (active) PrimaryOrange else Slate200, RoundedCornerShape(6.dp))
                                        .clickable { selectedCategory = cat },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(cat.substringBefore(" "), color = if (active) PrimaryOrange else Slate500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            categories.takeLast(2).forEach { cat ->
                                val active = selectedCategory == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) PrimaryOrange.copy(alpha = 0.15f) else Slate50)
                                        .border(1.dp, if (active) PrimaryOrange else Slate200, RoundedCornerShape(6.dp))
                                        .clickable { selectedCategory = cat },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(cat, color = if (active) PrimaryOrange else Slate500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
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
                        val target = targetAmt.toDoubleOrNull()
                        val months = deadlineMonths.toIntOrNull()

                        if (goalName.trim().isEmpty()) {
                            inputError = "Goal Name is required"
                        } else if (target == null || target <= 0.0) {
                            inputError = "Enter a valid target amount"
                        } else if (months == null || months <= 0) {
                            inputError = "Enter a valid deadline"
                        } else {
                            viewModel.createSavingsGoal(goalName, target, selectedCategory, months)
                            showCreateGoalDialog = false
                        }
                    }
                ) {
                    Text("CREATE GOAL", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateGoalDialog = false }) {
                    Text("CANCEL", color = Slate500)
                }
            }
        )
    }

    // 2. Add Savings Dialog
    if (showAddSavingsDialog != null) {
        val targetGoal = showAddSavingsDialog!!
        var savingsAmount by remember { mutableStateOf("") }
        var inputError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddSavingsDialog = null },
            containerColor = Color.White,
            title = { Text("Add Progress to Goal", color = Slate900, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Transfer funds from your wallet balance directly into your goal: '${targetGoal.name}'.", color = Slate500, fontSize = 12.sp)

                    OutlinedTextField(
                        value = savingsAmount,
                        onValueChange = { savingsAmount = it; inputError = null },
                        label = { Text("Transfer Amount (UGX)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = Slate200,
                            focusedTextColor = Slate900,
                            unfocusedTextColor = Slate900
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Current Wallet Balance: ${viewModel.formatUgx(user?.balanceUgx ?: 0.0)} UGX",
                        color = Slate500,
                        fontSize = 11.sp
                    )

                    if (inputError != null) {
                        Text(inputError!!, color = CrimsonRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = savingsAmount.toDoubleOrNull()
                        val balance = user?.balanceUgx ?: 0.0

                        if (amt == null || amt <= 0.0) {
                            inputError = "Please enter a valid amount"
                        } else if (balance < amt) {
                            inputError = "Insufficient wallet balance"
                        } else {
                            viewModel.allocateSavings(targetGoal.id, amt)
                            showAddSavingsDialog = null
                        }
                    }
                ) {
                    Text("ALLOCATE", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSavingsDialog = null }) {
                    Text("CANCEL", color = Slate500)
                }
            }
        )
    }
}

@Composable
fun GoalItem(
    goal: SavingsGoalEntity,
    viewModel: AeinzViewModel,
    onAddSavingsClick: () -> Unit
) {
    val progress = if (goal.targetAmount > 0) goal.savedAmount / goal.targetAmount else 0.0
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.ROOT)
    val formattedDeadline = sdf.format(Date(goal.deadline))

    val catIcon = when (goal.category) {
        "Education" -> Icons.Default.School
        "Business" -> Icons.Default.Store
        "Emergency Fund" -> Icons.Default.HealthAndSafety
        "Health" -> Icons.Default.MedicalServices
        else -> Icons.Default.FlightTakeoff
    }

    AeinzCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(PrimaryOrange.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = catIcon,
                        contentDescription = goal.category,
                        tint = PrimaryOrange,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = goal.name,
                        color = Slate900,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Category: ${goal.category} • Deadline: $formattedDeadline",
                        color = Slate500,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = { viewModel.deleteGoal(goal.id) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonRed.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress Details
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "${viewModel.formatUgx(goal.savedAmount)} / ${viewModel.formatUgx(goal.targetAmount)} UGX",
                    color = Slate900,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(progress * 100).toInt()}% Achieved",
                    color = if (progress >= 1.0) EmeraldGreen else DarkOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onAddSavingsClick,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(Icons.Default.Savings, "Save", tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("ADD SAVINGS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        AeinzProgressBar(progress = progress.toFloat())
    }
}

data class BadgeItem(
    val id: String,
    val name: String,
    val icon: String,
    val desc: String
)
