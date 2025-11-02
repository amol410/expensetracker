package com.dolphin.expense.data.dao

import androidx.room.*
import com.dolphin.expense.data.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE year = :year AND month = :month")
    fun getBudgetsForMonth(year: Int, month: Int): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE category = :category AND year = :year AND month = :month")
    suspend fun getBudgetForCategory(category: String, year: Int, month: Int): BudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE category = :category AND year = :year AND month = :month")
    suspend fun deleteBudgetForCategory(category: String, year: Int, month: Int)
}
