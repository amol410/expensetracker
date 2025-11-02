package com.dolphin.expense.data.repository

import com.dolphin.expense.data.BudgetEntity
import com.dolphin.expense.data.ExpenseEntity
import com.dolphin.expense.data.dao.CategoryTotal
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<ExpenseEntity>>
    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<ExpenseEntity>>
    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>>
    fun getTotalAmountByDateRange(startDate: Long, endDate: Long): Flow<Double?>
    fun getTotalAmountByCategoryAndDateRange(category: String, startDate: Long, endDate: Long): Flow<Double?>
    fun getCategoryTotalsByDateRange(startDate: Long, endDate: Long): Flow<List<CategoryTotal>>

    suspend fun insertExpense(expense: ExpenseEntity): Long
    suspend fun updateExpense(expense: ExpenseEntity)
    suspend fun deleteExpense(expense: ExpenseEntity)
    suspend fun getExpenseById(id: Long): ExpenseEntity?

    // Budget methods
    fun getBudgetsForMonth(year: Int, month: Int): Flow<List<BudgetEntity>>
    suspend fun getBudgetForCategory(category: String, year: Int, month: Int): BudgetEntity?
    suspend fun insertBudget(budget: BudgetEntity)
    suspend fun updateBudget(budget: BudgetEntity)
    suspend fun deleteBudget(budget: BudgetEntity)
}