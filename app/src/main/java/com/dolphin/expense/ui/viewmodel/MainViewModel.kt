package com.dolphin.expense.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dolphin.expense.data.ExpenseEntity
import com.dolphin.expense.data.repository.ExpenseRepository
import com.dolphin.expense.utils.CurrencyPreferences
import com.dolphin.expense.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    val currencyPreferences: CurrencyPreferences
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    init {
        loadDashboardData()
    }
    
    private fun loadDashboardData() {
        viewModelScope.launch {
            val todayStart = DateUtils.getStartOfDay(Date())
            val todayEnd = DateUtils.getEndOfDay(Date())
            val monthStart = DateUtils.getStartOfMonth(Date())
            val monthEnd = DateUtils.getEndOfMonth(Date())

            // Calculate last month dates
            val calendar = java.util.Calendar.getInstance()
            calendar.add(java.util.Calendar.MONTH, -1)
            val lastMonthStart = DateUtils.getStartOfMonth(calendar.time)
            val lastMonthEnd = DateUtils.getEndOfMonth(calendar.time)

            // Get today's expenses
            val todayExpensesFlow = repository.getExpensesByDateRange(todayStart, todayEnd)

            // Get current month expenses
            val monthExpensesFlow = repository.getExpensesByDateRange(monthStart, monthEnd)

            // Get last month expenses
            val lastMonthExpensesFlow = repository.getExpensesByDateRange(lastMonthStart, lastMonthEnd)

            // Get all expenses to find the most expensive one
            val allExpensesFlow = repository.getAllExpenses()

            combine(
                todayExpensesFlow,
                monthExpensesFlow,
                lastMonthExpensesFlow,
                allExpensesFlow
            ) { todayExpenses, monthExpenses, lastMonthExpenses, allExpenses ->
                val todayTotal = todayExpenses.sumOf { it.amount }
                val monthTotal = monthExpenses.sumOf { it.amount }
                val lastMonthTotal = lastMonthExpenses.sumOf { it.amount }
                val recentExpenses = monthExpenses.take(10) // Last 10 expenses

                val highestExpense = allExpenses.maxByOrNull { it.amount }

                _uiState.value = _uiState.value.copy(
                    todayTotal = todayTotal,
                    monthTotal = monthTotal,
                    lastMonthTotal = lastMonthTotal,
                    recentExpenses = recentExpenses,
                    highestExpense = highestExpense,
                    isLoading = false
                )
            }.collect()
        }
    }
    
    fun refresh() {
        loadDashboardData()
    }

    fun clearAllData() {
        viewModelScope.launch {
            // TODO: Add repository methods to clear all expenses and budgets
            // For now, the app will restart after clearing preferences
        }
    }
}

data class MainUiState(
    val todayTotal: Double = 0.0,
    val monthTotal: Double = 0.0,
    val lastMonthTotal: Double = 0.0,
    val recentExpenses: List<ExpenseEntity> = emptyList(),
    val highestExpense: ExpenseEntity? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)