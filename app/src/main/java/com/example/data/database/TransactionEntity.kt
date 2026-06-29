package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "DEPOSIT", "WITHDRAWAL", "CARD_SPEND", "EXCHANGE", "SAVINGS_TRANSFER"
    val amount: Double,
    val currency: String = "UGX",
    val convertedAmount: Double = 0.0,
    val convertedCurrency: String = "UGX",
    val fee: Double = 0.0,
    val providerOrDetails: String, // e.g. "MTN Money", "Aeinz Card Spend", "Withdraw to Airtel"
    val timestamp: Long = System.currentTimeMillis(),
    val notificationTitle: String,
    val notificationMessage: String,
    val isRead: Boolean = false
)
