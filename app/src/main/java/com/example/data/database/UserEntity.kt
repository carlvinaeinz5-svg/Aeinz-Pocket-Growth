package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val email: String,
    val phone: String,
    val pin: String,
    val mobileNetwork: String, // "MTN" or "Airtel"
    val balanceUgx: Double = 0.0,
    val cardNumber: String,
    val cardHolder: String,
    val cardExpiry: String,
    val cardCvv: String,
    val cardLocked: Boolean = false,
    val cardSpendLimit: Double = 500000.0, // default limit in UGX (Daily Limit)
    val cardSpendLimitPerTx: Double = 150000.0, // default per transaction limit in UGX
    val hasCompletedTutorial: Boolean = false,
    
    // --- Gamified Savings Challenges metrics ---
    val savingsStreak: Int = 0,
    val lastSavingsTime: Long = 0L,
    val savingsLevel: Int = 1,
    val totalSavedAllTime: Double = 0.0,
    val unlockedBadgesJson: String = "FIRST_SAVER", // Comma-separated list of unlocked badges
    
    val createdAt: Long = System.currentTimeMillis()
)
