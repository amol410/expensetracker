package com.dolphin.expense.data.repository

import com.dolphin.expense.data.BudgetEntity
import com.dolphin.expense.data.ExpenseEntity
import com.dolphin.expense.data.dao.BudgetDao
import com.dolphin.expense.data.dao.CategoryTotal
import com.dolphin.expense.data.dao.ExpenseDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao
) : ExpenseRepository {
    override fun getAllExpenses(): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllExpenses()
    }

    override fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesByDateRange(startDate, endDate)
    }

    override fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesByCategory(category)
    }

    override fun getTotalAmountByDateRange(startDate: Long, endDate: Long): Flow<Double?> {
        return expenseDao.getTotalAmountByDateRange(startDate, endDate)
    }

    override fun getTotalAmountByCategoryAndDateRange(
        category: String,
        startDate: Long,
        endDate: Long
    ): Flow<Double?> {
        return expenseDao.getTotalAmountByCategoryAndDateRange(category, startDate, endDate)
    }

    override fun getCategoryTotalsByDateRange(startDate: Long, endDate: Long): Flow<List<CategoryTotal>> {
        return expenseDao.getCategoryTotalsByDateRange(startDate, endDate)
    }

    override suspend fun insertExpense(expense: ExpenseEntity): Long {
        return expenseDao.insertExpense(expense)
    }

    override suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }

    override suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    override suspend fun getExpenseById(id: Long): ExpenseEntity? {
        return expenseDao.getExpenseById(id)
    }

    // Budget methods
    override fun getBudgetsForMonth(year: Int, month: Int): Flow<List<BudgetEntity>> {
        return budgetDao.getBudgetsForMonth(year, month)
    }

    override suspend fun getBudgetForCategory(category: String, year: Int, month: Int): BudgetEntity? {
        return budgetDao.getBudgetForCategory(category, year, month)
    }

    override suspend fun insertBudget(budget: BudgetEntity) {
        budgetDao.insertBudget(budget)
    }

    override suspend fun updateBudget(budget: BudgetEntity) {
        budgetDao.updateBudget(budget)
    }

    override suspend fun deleteBudget(budget: BudgetEntity) {
        budgetDao.deleteBudget(budget)
    }
}