package com.dolphin.expense.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dolphin.expense.analytics.AnalyticsManager
import com.dolphin.expense.data.BudgetEntity
import com.dolphin.expense.data.ExpenseEntity
import com.dolphin.expense.data.dao.CategoryTotal
import com.dolphin.expense.data.repository.ExpenseRepository
import com.dolphin.expense.inappmessaging.InAppMessagingManager
import com.dolphin.expense.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()
    
    private val _selectedPeriod = MutableStateFlow(TimePeriod.TODAY)
    val selectedPeriod: StateFlow<TimePeriod> = _selectedPeriod.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    private val _dateRange = MutableStateFlow(
        Pair(
            DateUtils.getStartOfDay(Date()),
            DateUtils.getEndOfDay(Date())
        )
    )
    
    init {
        loadExpenses()
    }
    
    private fun loadExpenses() {
        viewModelScope.launch {
            val dateRange = _dateRange.value
            val expensesFlow = if (_selectedCategory.value != null) {
                repository.getExpensesByCategory(_selectedCategory.value!!)
            } else {
                repository.getExpensesByDateRange(dateRange.first, dateRange.second)
            }
            
            expensesFlow
                .combine(_selectedCategory.asStateFlow()) { expenses, category ->
                    if (category != null) {
                        repository.getExpensesByCategory(category)
                    } else {
                        repository.getExpensesByDateRange(dateRange.first, dateRange.second)
                    }
                }
                .collect { expensesFlow ->
                    expensesFlow.collect { expenses ->
                        val totalAmount = expenses.sumOf { it.amount }
                        _uiState.value = _uiState.value.copy(
                            expenses = expenses,
                            totalAmount = totalAmount,
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    fun addExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            val existingExpenses = repository.getExpensesByDateRange(0, Long.MAX_VALUE).first()
            val isFirstExpense = existingExpenses.isEmpty()

            repository.insertExpense(expense)

            // Track analytics
            analyticsManager.trackExpenseAdded(
                amount = expense.amount,
                category = expense.category,
                description = expense.description,
                isFirstExpense = isFirstExpense
            )

            // Trigger In-App Messaging events
            if (isFirstExpense) {
                InAppMessagingManager.triggerEvent(InAppMessagingManager.Events.FIRST_EXPENSE)
            }
            InAppMessagingManager.triggerEvent(InAppMessagingManager.Events.EXPENSE_ADDED)
        }
    }
    
    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)

            // Track analytics
            analyticsManager.trackExpenseUpdated(
                amount = expense.amount,
                category = expense.category
            )
        }
    }
    
    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)

            // Track analytics
            analyticsManager.trackExpenseDeleted(
                amount = expense.amount,
                category = expense.category
            )

            // Trigger In-App Messaging event
            InAppMessagingManager.triggerEvent(InAppMessagingManager.Events.EXPENSE_DELETED)
        }
    }
    
    fun setSelectedPeriod(period: TimePeriod) {
        _selectedPeriod.value = period
        updateDateRange(period)

        // Track analytics
        analyticsManager.trackPeriodChanged(period.name)
    }
    
    private fun updateDateRange(period: TimePeriod) {
        val currentDate = Date()
        val dateRange = when (period) {
            TimePeriod.TODAY -> Pair(
                DateUtils.getStartOfDay(currentDate),
                DateUtils.getEndOfDay(currentDate)
            )
            TimePeriod.WEEK -> Pair(
                DateUtils.getStartOfWeek(currentDate),
                DateUtils.getEndOfWeek(currentDate)
            )
            TimePeriod.MONTH -> Pair(
                DateUtils.getStartOfMonth(currentDate),
                DateUtils.getEndOfMonth(currentDate)
            )
            TimePeriod.QUARTER -> Pair(
                DateUtils.getStartOfQuarter(currentDate),
                DateUtils.getEndOfQuarter(currentDate)
            )
            TimePeriod.YEAR -> Pair(
                DateUtils.getStartOfYear(currentDate),
                DateUtils.getEndOfYear(currentDate)
            )
            TimePeriod.CUSTOM -> _dateRange.value // Use existing custom date range
        }
        _dateRange.value = dateRange

        viewModelScope.launch {
            val expenses: List<ExpenseEntity> = if (_selectedCategory.value != null) {
                repository.getExpensesByCategory(_selectedCategory.value!!).first()
            } else {
                repository.getExpensesByDateRange(dateRange.first, dateRange.second).first()
            }

            val totalAmount = expenses.sumOf { it.amount }
            val categoryTotals = if (_selectedCategory.value == null) {
                repository.getCategoryTotalsByDateRange(dateRange.first, dateRange.second).first() ?: emptyList()
            } else {
                emptyList()
            }

            _uiState.value = _uiState.value.copy(
                expenses = expenses,
                totalAmount = totalAmount,
                categoryTotals = categoryTotals,
                isLoading = false
            )
        }
    }
    
    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
        if (category != null) {
            // Track analytics
            analyticsManager.trackCategoryFiltered(category)

            viewModelScope.launch {
                val expenses = repository.getExpensesByCategory(category).first()
                val totalAmount = expenses.sumOf { it.amount }

                _uiState.value = _uiState.value.copy(
                    expenses = expenses,
                    totalAmount = totalAmount,
                    isLoading = false
                )
            }
        } else {
            // If category is null, go back to date-based filtering
            updateDateRange(_selectedPeriod.value)
        }
    }
    
    fun clearSelectedCategory() {
        _selectedCategory.value = null
        updateDateRange(_selectedPeriod.value)
    }
    
    suspend fun getExpenseById(id: Long) = repository.getExpenseById(id)

    fun getExpensesByDateRange(startDate: Long, endDate: Long) =
        repository.getExpensesByDateRange(startDate, endDate)

    fun setCustomDateRange(startDate: Long, endDate: Long) {
        _dateRange.value = Pair(startDate, endDate)
        _selectedPeriod.value = TimePeriod.CUSTOM

        // Track analytics
        analyticsManager.trackDateRangeSelected(startDate, endDate)

        viewModelScope.launch {
            val expenses = repository.getExpensesByDateRange(startDate, endDate).first()
            val totalAmount = expenses.sumOf { it.amount }
            val categoryTotals = repository.getCategoryTotalsByDateRange(startDate, endDate).first() ?: emptyList()

            _uiState.value = _uiState.value.copy(
                expenses = expenses,
                totalAmount = totalAmount,
                categoryTotals = categoryTotals,
                isLoading = false
            )
        }
    }
    
    fun refresh() {
        if (_selectedCategory.value != null) {
            setSelectedCategory(_selectedCategory.value)
        } else {
            updateDateRange(_selectedPeriod.value)
        }
    }

    // Budget methods
    fun getBudgetsForCurrentMonth(): Flow<List<BudgetEntity>> {
        val calendar = Calendar.getInstance()
        return repository.getBudgetsForMonth(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH)
        )
    }

    fun getCurrentMonthExpenses(): Flow<List<ExpenseEntity>> {
        val calendar = Calendar.getInstance()
        val startOfMonth = DateUtils.getStartOfMonth(calendar.time)
        val endOfMonth = DateUtils.getEndOfMonth(calendar.time)
        return repository.getExpensesByDateRange(startOfMonth, endOfMonth)
    }

    fun insertBudget(amount: Double) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val budget = BudgetEntity(
                category = "Monthly", // We'll use a single overall budget
                monthlyLimit = amount,
                year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH)
            )
            repository.insertBudget(budget)

            // Track analytics
            analyticsManager.trackBudgetSet(amount, "Monthly")

            // Trigger In-App Messaging event
            InAppMessagingManager.triggerEvent(InAppMessagingManager.Events.BUDGET_SET)
        }
    }

    fun updateBudget(budget: BudgetEntity, newAmount: Double) {
        viewModelScope.launch {
            repository.updateBudget(budget.copy(monthlyLimit = newAmount))

            // Track analytics
            analyticsManager.trackBudgetUpdated(budget.monthlyLimit, newAmount, budget.category)
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)

            // Track analytics
            analyticsManager.trackBudgetDeleted(budget.monthlyLimit, budget.category)
        }
    }
}

data class ExpenseUiState(
    val expenses: List<ExpenseEntity> = emptyList(),
    val totalAmount: Double = 0.0,
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

enum class TimePeriod {
    TODAY, WEEK, MONTH, QUARTER, YEAR, CUSTOM
}