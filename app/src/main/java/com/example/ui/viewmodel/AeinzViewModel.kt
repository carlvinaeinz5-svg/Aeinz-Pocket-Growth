package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.*
import com.example.data.repository.AeinzRepository
import com.example.data.api.GeminiHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AeinzViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AeinzRepository
    
    // --- Currency Exchange Rates (Base: UGX per 1 unit of foreign currency) ---
    val exchangeRates = mapOf(
        "USD" to 3720.0,
        "EUR" to 3980.0,
        "GBP" to 4710.0,
        "KES" to 28.5,
        "TZS" to 1.42
    )

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AeinzRepository(database.aeinzDao())
        
        // Seed default financial tips if DB is empty
        viewModelScope.launch {
            if (repository.getTipCount() == 0) {
                seedInitialTips()
            }
        }
    }

    // --- State Flows ---
    val userState: StateFlow<UserEntity?> = repository.userFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val goalsState: StateFlow<List<SavingsGoalEntity>> = repository.allGoalsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactionsState: StateFlow<List<TransactionEntity>> = repository.allTransactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tipsState: StateFlow<List<FinancialTipEntity>> = repository.allTipsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Local UI States ---
    private val _notifications = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private val _aiInsightState = MutableStateFlow<String>("Select 'Get Insights' in the Analysis tab to analyze your wallet and goals.")
    val aiInsightState = _aiInsightState.asStateFlow()

    private val _isGeneratingInsight = MutableStateFlow<Boolean>(false)
    val isGeneratingInsight = _isGeneratingInsight.asStateFlow()

    // --- Notification Trigger (for in-app alerts) ---
    private val _activeNotification = MutableStateFlow<TransactionEntity?>(null)
    val activeNotification = _activeNotification.asStateFlow()

    fun dismissActiveNotification() {
        _activeNotification.value = null
    }

    // --- Registration & Auto-Login ---
    fun registerUser(name: String, email: String, phone: String, pin: String, network: String) {
        viewModelScope.launch {
            val generatedCardNo = generateRandomCardNumber()
            val generatedCvv = String.format("%03d", Random.nextInt(100, 999))
            val generatedExpiry = getFutureExpiryDate()

            val newUser = UserEntity(
                name = name,
                email = email,
                phone = phone,
                pin = pin,
                mobileNetwork = network,
                balanceUgx = 10000.0, // Start with a nice bonus of 10,000 UGX!
                cardNumber = generatedCardNo,
                cardHolder = name.uppercase(Locale.ROOT),
                cardExpiry = generatedExpiry,
                cardCvv = generatedCvv,
                cardSpendLimit = 500000.0,
                cardSpendLimitPerTx = 150000.0,
                hasCompletedTutorial = true,
                savingsStreak = 0,
                lastSavingsTime = 0L,
                savingsLevel = 1,
                totalSavedAllTime = 0.0,
                unlockedBadgesJson = "FIRST_SAVER"
            )
            repository.insertOrUpdateUser(newUser)

            // Log first transaction as bonus
            val bonusTx = TransactionEntity(
                type = "DEPOSIT",
                amount = 10000.0,
                providerOrDetails = "Welcome Bonus",
                notificationTitle = "Bonus Credited! 🎁",
                notificationMessage = "Welcome to Aeinz Pocket Growth! 10,000 UGX welcome bonus has been credited to your wallet!"
            )
            repository.insertTransaction(bonusTx)
            triggerRealtimeNotification(bonusTx)
        }
    }

    fun completeTutorial() {
        viewModelScope.launch {
            repository.updateTutorialCompleted(true)
        }
    }

    // --- Core Wallet Operations ---
    
    // Deposit: Minimum 1000 UgX, 0 fees
    fun depositFunds(amount: Double, provider: String): Result<String> {
        if (amount < 1000.0) {
            return Result.failure(Exception("Minimum deposit is 1000 UGX"))
        }

        viewModelScope.launch {
            val currentUser = userState.value ?: return@launch
            val updatedBalance = currentUser.balanceUgx + amount
            repository.updateUserBalance(updatedBalance)

            val tx = TransactionEntity(
                type = "DEPOSIT",
                amount = amount,
                providerOrDetails = "$provider Deposit",
                notificationTitle = "Deposit Successful",
                notificationMessage = "Deposited ${formatUgx(amount)} UGX successfully from $provider."
            )
            repository.insertTransaction(tx)
            triggerRealtimeNotification(tx)
        }
        return Result.success("Deposit initiated successfully")
    }

    // Withdrawal: 5% fee, capped at less than 15,000 UGX (maximum fee cap: 14,900 UGX)
    fun withdrawFunds(amount: Double, provider: String): Result<String> {
        val currentUser = userState.value ?: return Result.failure(Exception("No user found"))
        
        // Compute fee
        val rawFee = amount * 0.05
        // "minimal withdrawal fee should be less than 15000UGX" -> fee is 5% up to a maximum cap of 14,900 UGX
        val fee = if (rawFee > 14900.0) 14900.0 else rawFee
        val totalDebit = amount + fee

        if (currentUser.balanceUgx < totalDebit) {
            return Result.failure(Exception("Insufficient balance. Total cost (Amount + 5% Fee) is ${formatUgx(totalDebit)} UGX."))
        }

        viewModelScope.launch {
            val updatedBalance = currentUser.balanceUgx - totalDebit
            repository.updateUserBalance(updatedBalance)

            val tx = TransactionEntity(
                type = "WITHDRAWAL",
                amount = amount,
                fee = fee,
                providerOrDetails = "Withdraw to $provider",
                notificationTitle = "Withdrawal Successful",
                notificationMessage = "Withdrew ${formatUgx(amount)} UGX to $provider. Fee charged: ${formatUgx(fee)} UGX (5% with max cap under 15,000 UGX)."
            )
            repository.insertTransaction(tx)
            triggerRealtimeNotification(tx)
        }
        return Result.success("Withdrawal processed")
    }

    // Currency Exchanger (UGX to World Currencies)
    fun exchangeCurrency(ugxAmount: Double, targetCurrency: String): Result<String> {
        val currentUser = userState.value ?: return Result.failure(Exception("No active account"))
        if (ugxAmount <= 0) {
            return Result.failure(Exception("Amount must be greater than 0"))
        }
        if (currentUser.balanceUgx < ugxAmount) {
            return Result.failure(Exception("Insufficient balance in UGX"))
        }

        val rate = exchangeRates[targetCurrency] ?: return Result.failure(Exception("Unsupported currency"))
        val targetAmount = ugxAmount / rate

        viewModelScope.launch {
            // Deduct UGX
            val updatedBalance = currentUser.balanceUgx - ugxAmount
            repository.updateUserBalance(updatedBalance)

            // Log Transaction
            val tx = TransactionEntity(
                type = "EXCHANGE",
                amount = ugxAmount,
                currency = "UGX",
                convertedAmount = targetAmount,
                convertedCurrency = targetCurrency,
                providerOrDetails = "UGX to $targetCurrency Exchanger",
                notificationTitle = "Currency Exchanged",
                notificationMessage = "Exchanged ${formatUgx(ugxAmount)} UGX for ${String.format("%.2f", targetAmount)} $targetCurrency. Rate: 1 $targetCurrency = ${formatUgx(rate)} UGX."
            )
            repository.insertTransaction(tx)
            triggerRealtimeNotification(tx)
        }
        return Result.success("Successfully exchanged UGX to $targetCurrency")
    }

    // --- Savings Goal Operations & Gamified Savings challenges ---
    fun createSavingsGoal(name: String, targetAmount: Double, category: String, deadlineMonths: Int) {
        viewModelScope.launch {
            val deadlineMillis = System.currentTimeMillis() + (deadlineMonths.toLong() * 30 * 24 * 60 * 60 * 1000)
            val newGoal = SavingsGoalEntity(
                name = name,
                targetAmount = targetAmount,
                category = category,
                deadline = deadlineMillis
            )
            repository.insertGoal(newGoal)

            val tx = TransactionEntity(
                type = "SAVINGS_TRANSFER",
                amount = 0.0,
                providerOrDetails = "Goal Created: $name",
                notificationTitle = "Goal Started!",
                notificationMessage = "New savings goal '$name' successfully created! Target: ${formatUgx(targetAmount)} UGX."
            )
            repository.insertTransaction(tx)
            triggerRealtimeNotification(tx)
        }
    }

    fun allocateSavings(goalId: Int, amount: Double): Result<String> {
        val currentUser = userState.value ?: return Result.failure(Exception("No user found"))
        if (amount <= 0) {
            return Result.failure(Exception("Amount must be greater than 0"))
        }
        if (currentUser.balanceUgx < amount) {
            return Result.failure(Exception("Insufficient wallet balance to save"))
        }

        viewModelScope.launch {
            val goal = repository.getGoalById(goalId)
            if (goal != null) {
                // Update User Balance
                val updatedWalletBalance = currentUser.balanceUgx - amount
                repository.updateUserBalance(updatedWalletBalance)

                // Update Goal saved progress
                val updatedSavedAmount = goal.savedAmount + amount
                val updatedGoal = goal.copy(savedAmount = updatedSavedAmount)
                repository.updateGoal(updatedGoal)

                // 1. Calculate Gamification Streaks & Levels
                val now = System.currentTimeMillis()
                val diffHours = if (currentUser.lastSavingsTime > 0L) (now - currentUser.lastSavingsTime) / (1000 * 60 * 60) else 999L
                
                val newStreak = when {
                    currentUser.lastSavingsTime == 0L -> 1
                    diffHours < 24 -> currentUser.savingsStreak // Saved twice within 24h, maintain current streak
                    diffHours < 48 -> currentUser.savingsStreak + 1 // Consecutive savings day, increment!
                    else -> 1 // Over 48 hours, streak broken and restarts at 1
                }

                val newTotalSaved = currentUser.totalSavedAllTime + amount
                
                val newLevel = when {
                    newTotalSaved < 10000.0 -> 1
                    newTotalSaved < 50000.0 -> 2
                    newTotalSaved < 200000.0 -> 3
                    newTotalSaved < 1000000.0 -> 4
                    else -> 5
                }

                // 2. Unlock Badges dynamically
                val currentBadges = currentUser.unlockedBadgesJson.split(",").filter { it.isNotEmpty() }.toMutableSet()
                if (currentBadges.isEmpty()) currentBadges.add("FIRST_SAVER")
                
                val newlyUnlocked = mutableListOf<String>()
                if (newLevel >= 2 && !currentBadges.contains("BRONZE_SAVER")) {
                    currentBadges.add("BRONZE_SAVER")
                    newlyUnlocked.add("Bronze Saver 🥉")
                }
                if (newLevel >= 3 && !currentBadges.contains("SILVER_SAVER")) {
                    currentBadges.add("SILVER_SAVER")
                    newlyUnlocked.add("Silver Saver 🥈")
                }
                if (newLevel >= 4 && !currentBadges.contains("GOLD_SAVER")) {
                    currentBadges.add("GOLD_SAVER")
                    newlyUnlocked.add("Gold Saver 🥇")
                }
                if (newLevel >= 5 && !currentBadges.contains("SAVINGS_GURU")) {
                    currentBadges.add("SAVINGS_GURU")
                    newlyUnlocked.add("Savings Guru 👑")
                }
                if (newStreak >= 3 && !currentBadges.contains("STREAK_3")) {
                    currentBadges.add("STREAK_3")
                    newlyUnlocked.add("3-Day Savings Streak 🔥")
                }
                if (newStreak >= 7 && !currentBadges.contains("STREAK_7")) {
                    currentBadges.add("STREAK_7")
                    newlyUnlocked.add("7-Day Savings Streak ⚡")
                }

                val updatedUser = currentUser.copy(
                    balanceUgx = updatedWalletBalance,
                    savingsStreak = newStreak,
                    lastSavingsTime = now,
                    savingsLevel = newLevel,
                    totalSavedAllTime = newTotalSaved,
                    unlockedBadgesJson = currentBadges.joinToString(",")
                )
                repository.insertOrUpdateUser(updatedUser)

                // Log primary savings Tx
                val tx = TransactionEntity(
                    type = "SAVINGS_TRANSFER",
                    amount = amount,
                    providerOrDetails = "Saved for: ${goal.name}",
                    notificationTitle = "Savings Added! 💰",
                    notificationMessage = "Saved ${formatUgx(amount)} UGX to '${goal.name}'. Streak: $newStreak day(s)! Progress: ${(updatedSavedAmount / goal.targetAmount * 100).toInt()}%"
                )
                repository.insertTransaction(tx)
                triggerRealtimeNotification(tx)

                // Trigger LEVEL-UP and BADGES notification if any
                if (newLevel > currentUser.savingsLevel) {
                    val levelTx = TransactionEntity(
                        type = "SAVINGS_TRANSFER",
                        amount = 0.0,
                        providerOrDetails = "Level Up! 🌟",
                        notificationTitle = "Level Up! Reached Level $newLevel 🌟",
                        notificationMessage = "Outstanding! Your savings total has promoted you to Level $newLevel!"
                    )
                    repository.insertTransaction(levelTx)
                }

                newlyUnlocked.forEach { badgeName ->
                    val badgeTx = TransactionEntity(
                        type = "SAVINGS_TRANSFER",
                        amount = 0.0,
                        providerOrDetails = "Badge Earned: $badgeName",
                        notificationTitle = "Badge Unlocked! 🎉",
                        notificationMessage = "Incredible! You earned the '$badgeName' Milestone Badge for savings diligence!"
                    )
                    repository.insertTransaction(badgeTx)
                }
            }
        }
        return Result.success("Successfully saved toward goal")
    }

    fun deleteGoal(goalId: Int) {
        viewModelScope.launch {
            repository.deleteGoalById(goalId)
        }
    }

    // --- Aeinz Transact Card Segment Operations ---
    fun toggleCardFreeze() {
        val currentUser = userState.value ?: return
        viewModelScope.launch {
            val nextStatus = !currentUser.cardLocked
            repository.updateCardLockStatus(nextStatus)

            val tx = TransactionEntity(
                type = "CARD_SPEND",
                amount = 0.0,
                providerOrDetails = "Card Freeze Toggle",
                notificationTitle = if (nextStatus) "Card Frozen ❄️" else "Card Unfrozen ⚡",
                notificationMessage = if (nextStatus) {
                    "Your Aeinz Transact Card has been frozen. Authorization attempts will be declined."
                } else {
                    "Your Aeinz Transact Card has been un-frozen and is active for global online shopping!"
                }
            )
            repository.insertTransaction(tx)
            triggerRealtimeNotification(tx)
        }
    }

    fun updateCardLimit(limit: Double) {
        viewModelScope.launch {
            repository.updateCardSpendLimit(limit)
        }
    }

    fun updateCardLimitPerTx(limit: Double) {
        viewModelScope.launch {
            repository.updateCardSpendLimitPerTx(limit)
        }
    }

    fun regenerateCardDetails() {
        val currentUser = userState.value ?: return
        viewModelScope.launch {
            val newNo = generateRandomCardNumber()
            val newCvv = String.format("%03d", Random.nextInt(100, 999))
            val newExpiry = getFutureExpiryDate()

            val updatedUser = currentUser.copy(
                cardNumber = newNo,
                cardCvv = newCvv,
                cardExpiry = newExpiry
            )
            repository.insertOrUpdateUser(updatedUser)

            val tx = TransactionEntity(
                type = "CARD_SPEND",
                amount = 0.0,
                providerOrDetails = "Aeinz Transact Card Credentials Reissued",
                notificationTitle = "Card Credentials Reissued! 🔒",
                notificationMessage = "Your Aeinz Transact Card has been reissued with secure new card numbers."
            )
            repository.insertTransaction(tx)
            triggerRealtimeNotification(tx)
        }
    }

    fun simulateCardTransaction(merchant: String, amount: Double): Result<String> {
        val currentUser = userState.value ?: return Result.failure(Exception("No active account"))
        
        if (currentUser.cardLocked) {
            val tx = TransactionEntity(
                type = "CARD_SPEND_DECLINED",
                amount = amount,
                providerOrDetails = merchant,
                notificationTitle = "Card Declined ❌",
                notificationMessage = "Declined at $merchant: Aeinz Transact Card is frozen! Click unfreeze to activate."
            )
            viewModelScope.launch {
                repository.insertTransaction(tx)
                triggerRealtimeNotification(tx)
            }
            return Result.failure(Exception("Transaction declined. Aeinz Transact Card is frozen! Please unfreeze it first."))
        }
        
        if (amount > currentUser.cardSpendLimitPerTx) {
            val tx = TransactionEntity(
                type = "CARD_SPEND_DECLINED",
                amount = amount,
                providerOrDetails = merchant,
                notificationTitle = "Card Declined ❌",
                notificationMessage = "Declined at $merchant: Amount exceeds your transaction limit of ${formatUgx(currentUser.cardSpendLimitPerTx)} UGX."
            )
            viewModelScope.launch {
                repository.insertTransaction(tx)
                triggerRealtimeNotification(tx)
            }
            return Result.failure(Exception("Transaction declined. Amount exceeds your set transaction limit of ${formatUgx(currentUser.cardSpendLimitPerTx)} UGX."))
        }

        if (amount > currentUser.cardSpendLimit) {
            val tx = TransactionEntity(
                type = "CARD_SPEND_DECLINED",
                amount = amount,
                providerOrDetails = merchant,
                notificationTitle = "Card Declined ❌",
                notificationMessage = "Declined at $merchant: Amount exceeds your daily limit of ${formatUgx(currentUser.cardSpendLimit)} UGX."
            )
            viewModelScope.launch {
                repository.insertTransaction(tx)
                triggerRealtimeNotification(tx)
            }
            return Result.failure(Exception("Transaction declined. Amount exceeds your set daily limit of ${formatUgx(currentUser.cardSpendLimit)} UGX."))
        }

        if (currentUser.balanceUgx < amount) {
            val tx = TransactionEntity(
                type = "CARD_SPEND_DECLINED",
                amount = amount,
                providerOrDetails = merchant,
                notificationTitle = "Card Declined ❌",
                notificationMessage = "Declined at $merchant: Insufficient wallet funds."
            )
            viewModelScope.launch {
                repository.insertTransaction(tx)
                triggerRealtimeNotification(tx)
            }
            return Result.failure(Exception("Transaction declined. Insufficient wallet funds to complete this card purchase."))
        }

        viewModelScope.launch {
            val updatedBalance = currentUser.balanceUgx - amount
            repository.updateUserBalance(updatedBalance)

            val tx = TransactionEntity(
                type = "CARD_SPEND",
                amount = amount,
                providerOrDetails = merchant,
                notificationTitle = "Card Spend Approved ✅",
                notificationMessage = "Spent ${formatUgx(amount)} UGX at $merchant using Aeinz Transact Card."
            )
            repository.insertTransaction(tx)
            triggerRealtimeNotification(tx)
        }
        return Result.success("Transaction approved")
    }

    // --- Financial Literacy & AI Spending Insights ---
    fun generateSpendingAnalysis() {
        viewModelScope.launch {
            _isGeneratingInsight.value = true
            
            val user = userState.value
            val txList = transactionsState.value
            val goals = goalsState.value

            if (user == null) {
                _aiInsightState.value = "Create an account to analyze your budget!"
                _isGeneratingInsight.value = false
                return@launch
            }

            val prompt = """
                Analyze this user's budget and give personalized financial advice.
                User: ${user.name}
                Current Wallet Balance: ${user.balanceUgx} UGX
                Registered Mobile Money Network: ${user.mobileNetwork}
                Active Savings Goals:
                ${goals.joinToString { "- ${it.name}: Saved ${it.savedAmount}/${it.targetAmount} UGX (Category: ${it.category})" }}
                Recent Transactions:
                ${txList.take(6).joinToString { "- Type: ${it.type}, Amount: ${it.amount} UGX, Details: ${it.providerOrDetails}" }}
                Provide the analysis in two sections: (1) Spend Analysis and (2) Actionable Saving Hack. Keep the response friendly, inspiring, and concise.
            """.trimIndent()

            val response = GeminiHelper.getFinancialTip(prompt)
            _aiInsightState.value = response
            _isGeneratingInsight.value = false
        }
    }

    // Toggle favorite status on literacy tips
    fun toggleTipFavorite(tipId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateTipFavoriteStatus(tipId, isFavorite)
        }
    }

    // --- Helper Helpers ---
    private fun generateRandomCardNumber(): String {
        val bin = "4865" // Aeinz Transact standard BIN
        val part2 = String.format("%04d", Random.nextInt(1000, 9999))
        val part3 = String.format("%04d", Random.nextInt(1000, 9999))
        val part4 = String.format("%04d", Random.nextInt(1000, 9999))
        return "$bin $part2 $part3 $part4"
    }

    private fun getFutureExpiryDate(): String {
        val sdf = SimpleDateFormat("MM/yy", Locale.ROOT)
        val futureDate = Date(System.currentTimeMillis() + (3L * 365 * 24 * 60 * 60 * 1000)) // 3 years out
        return sdf.format(futureDate)
    }

    private fun triggerRealtimeNotification(tx: TransactionEntity) {
        _notifications.value = listOf(tx) + _notifications.value
        _activeNotification.value = tx
    }

    fun formatUgx(amount: Double): String {
        return String.format(Locale.ROOT, "%,.0f", amount)
    }

    private suspend fun seedInitialTips() {
        val tips = listOf(
            FinancialTipEntity(
                title = "The Power of Small Ugandan Change",
                content = "Did you know? Saving 1,000 UGX instead of spending it on an extra cup of tea daily saves you 30,000 UGX a month! That pays for tuition or minor household emergencies.",
                category = "Savings",
                dateString = "Daily Tip"
            ),
            FinancialTipEntity(
                title = "Reduce Mobile Money Cashout Costs",
                content = "Withdrawing cash frequently means paying double fees (network and cashout). Keep your money in Aeinz Pocket Growth and transact online via the Aeinz Transact Card for low fees!",
                category = "Budgeting",
                dateString = "Daily Tip"
            ),
            FinancialTipEntity(
                title = "Avoid Inflated Emergency Loans",
                content = "Quick-loan apps charge over 20-30% weekly interest! Accumulating a starter 50,000 UGX emergency goal shields your budget from these high-interest trap cycles.",
                category = "Emergency Fund",
                dateString = "Emergency Fund"
            ),
            FinancialTipEntity(
                title = "The 50/30/20 Budgeting Rule",
                content = "Even on moderate income, try dividing your money: 50% for vital needs (food, bills), 30% for your society/family needs, and 20% directly into your automated savings goals.",
                category = "Budgeting",
                dateString = "Daily Tip"
            )
        )
        repository.insertTips(tips)
    }
}
