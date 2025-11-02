package com.dolphin.expense.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for Firebase Analytics
 * Provides centralized analytics tracking for the entire app
 */
@Singleton
class AnalyticsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    companion object {
        private const val TAG = "AnalyticsManager"

        // Custom Event Names
        object Events {
            const val EXPENSE_ADDED = "expense_added"
            const val EXPENSE_UPDATED = "expense_updated"
            const val EXPENSE_DELETED = "expense_deleted"
            const val BUDGET_SET = "budget_set"
            const val BUDGET_UPDATED = "budget_updated"
            const val BUDGET_DELETED = "budget_deleted"
            const val BUDGET_EXCEEDED = "budget_exceeded"
            const val BUDGET_WARNING = "budget_warning"
            const val CURRENCY_CHANGED = "currency_changed"
            const val PERIOD_CHANGED = "period_changed"
            const val CATEGORY_FILTERED = "category_filtered"
            const val DATE_RANGE_SELECTED = "date_range_selected"
            const val FIRST_TIME_USER = "first_time_user"
            const val USER_SIGNED_IN = "user_signed_in"
            const val USER_SIGNED_OUT = "user_signed_out"
        }

        // Custom Parameter Names
        object Params {
            const val EXPENSE_AMOUNT = "expense_amount"
            const val EXPENSE_CATEGORY = "expense_category"
            const val EXPENSE_NOTE = "expense_note"
            const val BUDGET_AMOUNT = "budget_amount"
            const val BUDGET_CATEGORY = "budget_category"
            const val CURRENCY_CODE = "currency_code"
            const val TIME_PERIOD = "time_period"
            const val CATEGORY_NAME = "category_name"
            const val TOTAL_EXPENSES = "total_expenses"
            const val IS_FIRST_EXPENSE = "is_first_expense"
            const val SIGN_IN_METHOD = "sign_in_method"
        }

        // Screen Names
        object Screens {
            const val DASHBOARD = "Dashboard"
            const val ADD_EXPENSE = "AddExpense"
            const val EDIT_EXPENSE = "EditExpense"
            const val ALL_EXPENSES = "AllExpenses"
            const val STATISTICS = "Statistics"
            const val SETTINGS = "Settings"
            const val MONTH_DETAIL = "MonthDetail"
            const val CURRENCY_SELECTION = "CurrencySelection"
            const val LOGIN = "Login"
            const val WELCOME = "Welcome"
        }
    }

    /**
     * Log a custom event with parameters
     */
    fun logEvent(eventName: String, params: Bundle? = null) {
        try {
            firebaseAnalytics.logEvent(eventName, params)
            Log.d(TAG, "Event logged: $eventName with params: $params")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log event: $eventName", e)
        }
    }

    /**
     * Log screen view event
     */
    fun logScreenView(screenName: String, screenClass: String? = null) {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                screenClass?.let { putString(FirebaseAnalytics.Param.SCREEN_CLASS, it) }
            }
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
            Log.d(TAG, "Screen view logged: $screenName")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log screen view: $screenName", e)
        }
    }

    /**
     * Track expense added event
     */
    fun trackExpenseAdded(
        amount: Double,
        category: String,
        description: String?,
        isFirstExpense: Boolean = false
    ) {
        val bundle = Bundle().apply {
            putDouble(Params.EXPENSE_AMOUNT, amount)
            putString(Params.EXPENSE_CATEGORY, category)
            description?.let { putString(Params.EXPENSE_NOTE, it) }
            putString(Params.IS_FIRST_EXPENSE, if (isFirstExpense) "true" else "false")
        }
        logEvent(Events.EXPENSE_ADDED, bundle)
    }

    /**
     * Track expense updated event
     */
    fun trackExpenseUpdated(amount: Double, category: String) {
        val bundle = Bundle().apply {
            putDouble(Params.EXPENSE_AMOUNT, amount)
            putString(Params.EXPENSE_CATEGORY, category)
        }
        logEvent(Events.EXPENSE_UPDATED, bundle)
    }

    /**
     * Track expense deleted event
     */
    fun trackExpenseDeleted(amount: Double, category: String) {
        val bundle = Bundle().apply {
            putDouble(Params.EXPENSE_AMOUNT, amount)
            putString(Params.EXPENSE_CATEGORY, category)
        }
        logEvent(Events.EXPENSE_DELETED, bundle)
    }

    /**
     * Track budget set event
     */
    fun trackBudgetSet(amount: Double, category: String = "Monthly") {
        val bundle = Bundle().apply {
            putDouble(Params.BUDGET_AMOUNT, amount)
            putString(Params.BUDGET_CATEGORY, category)
        }
        logEvent(Events.BUDGET_SET, bundle)
    }

    /**
     * Track budget updated event
     */
    fun trackBudgetUpdated(oldAmount: Double, newAmount: Double, category: String = "Monthly") {
        val bundle = Bundle().apply {
            putDouble("old_amount", oldAmount)
            putDouble(Params.BUDGET_AMOUNT, newAmount)
            putString(Params.BUDGET_CATEGORY, category)
        }
        logEvent(Events.BUDGET_UPDATED, bundle)
    }

    /**
     * Track budget deleted event
     */
    fun trackBudgetDeleted(amount: Double, category: String = "Monthly") {
        val bundle = Bundle().apply {
            putDouble(Params.BUDGET_AMOUNT, amount)
            putString(Params.BUDGET_CATEGORY, category)
        }
        logEvent(Events.BUDGET_DELETED, bundle)
    }

    /**
     * Track budget exceeded event
     */
    fun trackBudgetExceeded(budgetAmount: Double, spentAmount: Double, category: String = "Monthly") {
        val bundle = Bundle().apply {
            putDouble(Params.BUDGET_AMOUNT, budgetAmount)
            putDouble("spent_amount", spentAmount)
            putString(Params.BUDGET_CATEGORY, category)
            putLong("percentage", (spentAmount / budgetAmount * 100).toInt().toLong())
        }
        logEvent(Events.BUDGET_EXCEEDED, bundle)
    }

    /**
     * Track budget warning event (e.g., 75% spent)
     */
    fun trackBudgetWarning(budgetAmount: Double, spentAmount: Double, percentage: Int) {
        val bundle = Bundle().apply {
            putDouble(Params.BUDGET_AMOUNT, budgetAmount)
            putDouble("spent_amount", spentAmount)
            putLong("percentage", percentage.toLong())
        }
        logEvent(Events.BUDGET_WARNING, bundle)
    }

    /**
     * Track currency changed event
     */
    fun trackCurrencyChanged(newCurrency: String, oldCurrency: String? = null) {
        val bundle = Bundle().apply {
            putString(Params.CURRENCY_CODE, newCurrency)
            oldCurrency?.let { putString("old_currency", it) }
        }
        logEvent(Events.CURRENCY_CHANGED, bundle)
    }

    /**
     * Track time period changed event
     */
    fun trackPeriodChanged(period: String) {
        val bundle = Bundle().apply {
            putString(Params.TIME_PERIOD, period)
        }
        logEvent(Events.PERIOD_CHANGED, bundle)
    }

    /**
     * Track category filter applied
     */
    fun trackCategoryFiltered(category: String) {
        val bundle = Bundle().apply {
            putString(Params.CATEGORY_NAME, category)
        }
        logEvent(Events.CATEGORY_FILTERED, bundle)
    }

    /**
     * Track custom date range selection
     */
    fun trackDateRangeSelected(startDate: Long, endDate: Long) {
        val bundle = Bundle().apply {
            putLong("start_date", startDate)
            putLong("end_date", endDate)
            putLong("range_days", ((endDate - startDate) / (1000 * 60 * 60 * 24)))
        }
        logEvent(Events.DATE_RANGE_SELECTED, bundle)
    }

    /**
     * Track first time user
     */
    fun trackFirstTimeUser() {
        logEvent(Events.FIRST_TIME_USER, null)
    }

    /**
     * Track user sign in
     */
    fun trackUserSignIn(method: String = "Google") {
        val bundle = Bundle().apply {
            putString(Params.SIGN_IN_METHOD, method)
        }
        logEvent(Events.USER_SIGNED_IN, bundle)
    }

    /**
     * Track user sign out
     */
    fun trackUserSignOut() {
        logEvent(Events.USER_SIGNED_OUT, null)
    }

    /**
     * Set user property
     */
    fun setUserProperty(name: String, value: String) {
        try {
            firebaseAnalytics.setUserProperty(name, value)
            Log.d(TAG, "User property set: $name = $value")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set user property: $name", e)
        }
    }

    /**
     * Set user ID
     */
    fun setUserId(userId: String) {
        try {
            firebaseAnalytics.setUserId(userId)
            Log.d(TAG, "User ID set: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set user ID", e)
        }
    }

    /**
     * Reset analytics data (useful for testing)
     */
    fun resetAnalyticsData() {
        try {
            firebaseAnalytics.resetAnalyticsData()
            Log.d(TAG, "Analytics data reset")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reset analytics data", e)
        }
    }

    /**
     * Enable/disable analytics collection
     */
    fun setAnalyticsEnabled(enabled: Boolean) {
        try {
            firebaseAnalytics.setAnalyticsCollectionEnabled(enabled)
            Log.d(TAG, "Analytics collection ${if (enabled) "enabled" else "disabled"}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set analytics enabled state", e)
        }
    }
}
