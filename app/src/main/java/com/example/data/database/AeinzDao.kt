package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AeinzDao {
    // --- User Queries ---
    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    fun getUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    suspend fun getUserSync(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    @Query("UPDATE users SET balanceUgx = :newBalance WHERE id = 1")
    suspend fun updateUserBalance(newBalance: Double)

    @Query("UPDATE users SET cardLocked = :isLocked WHERE id = 1")
    suspend fun updateCardLockStatus(isLocked: Boolean)

    @Query("UPDATE users SET cardSpendLimit = :limit WHERE id = 1")
    suspend fun updateCardSpendLimit(limit: Double)

    @Query("UPDATE users SET cardSpendLimitPerTx = :limit WHERE id = 1")
    suspend fun updateCardSpendLimitPerTx(limit: Double)

    @Query("UPDATE users SET hasCompletedTutorial = :completed WHERE id = 1")
    suspend fun updateTutorialCompleted(completed: Boolean)

    // --- Savings Goals Queries ---
    @Query("SELECT * FROM savings_goals ORDER BY createdAt DESC")
    fun getAllGoalsFlow(): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM savings_goals WHERE id = :id LIMIT 1")
    suspend fun getGoalById(id: Int): SavingsGoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingsGoalEntity)

    @Update
    suspend fun updateGoal(goal: SavingsGoalEntity)

    @Query("DELETE FROM savings_goals WHERE id = :id")
    suspend fun deleteGoalById(id: Int)

    // --- Transaction Queries ---
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    // --- Financial Tips Queries ---
    @Query("SELECT * FROM financial_tips ORDER BY id DESC")
    fun getAllTipsFlow(): Flow<List<FinancialTipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTips(tips: List<FinancialTipEntity>)

    @Query("UPDATE financial_tips SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateTipFavoriteStatus(id: Int, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM financial_tips")
    suspend fun getTipCount(): Int
}
