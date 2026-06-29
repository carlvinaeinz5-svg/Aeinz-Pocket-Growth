package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_tips")
data class FinancialTipEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: String, // "Budgeting", "Savings", "Emergency Fund", "Debt Management"
    val dateString: String,
    val isFavorite: Boolean = false
)
