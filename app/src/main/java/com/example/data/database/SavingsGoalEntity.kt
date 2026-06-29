package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val category: String, // e.g., "Education", "Business", "Emergency", "Health"
    val deadline: Long,
    val createdAt: Long = System.currentTimeMillis()
)
