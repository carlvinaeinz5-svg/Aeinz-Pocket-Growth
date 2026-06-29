package com.example.data.repository

import com.example.data.database.*
import kotlinx.coroutines.flow.Flow

class AeinzRepository(private val aeinzDao: AeinzDao) {

    val userFlow: Flow<UserEntity?> = aeinzDao.getUserFlow()
    val allGoalsFlow: Flow<List<SavingsGoalEntity>> = aeinzDao.getAllGoalsFlow()
    val allTransactionsFlow: Flow<List<TransactionEntity>> = aeinzDao.getAllTransactionsFlow()
    val allTipsFlow: Flow<List<FinancialTipEntity>> = aeinzDao.getAllTipsFlow()

    suspend fun getUserSync(): UserEntity? = aeinzDao.getUserSync()

    suspend fun insertOrUpdateUser(user: UserEntity) {
        aeinzDao.insertOrUpdateUser(user)
    }

    suspend fun updateUserBalance(newBalance: Double) {
        aeinzDao.updateUserBalance(newBalance)
    }

    suspend fun updateCardLockStatus(isLocked: Boolean) {
        aeinzDao.updateCardLockStatus(isLocked)
    }

    suspend fun updateCardSpendLimit(limit: Double) {
        aeinzDao.updateCardSpendLimit(limit)
    }

    suspend fun updateCardSpendLimitPerTx(limit: Double) {
        aeinzDao.updateCardSpendLimitPerTx(limit)
    }

    suspend fun updateTutorialCompleted(completed: Boolean) {
        aeinzDao.updateTutorialCompleted(completed)
    }

    suspend fun getGoalById(id: Int): SavingsGoalEntity? = aeinzDao.getGoalById(id)

    suspend fun insertGoal(goal: SavingsGoalEntity) {
        aeinzDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: SavingsGoalEntity) {
        aeinzDao.updateGoal(goal)
    }

    suspend fun deleteGoalById(id: Int) {
        aeinzDao.deleteGoalById(id)
    }

    suspend fun insertTransaction(transaction: TransactionEntity) {
        aeinzDao.insertTransaction(transaction)
    }

    suspend fun insertTips(tips: List<FinancialTipEntity>) {
        aeinzDao.insertTips(tips)
    }

    suspend fun updateTipFavoriteStatus(id: Int, isFavorite: Boolean) {
        aeinzDao.updateTipFavoriteStatus(id, isFavorite)
    }

    suspend fun getTipCount(): Int = aeinzDao.getTipCount()
}
