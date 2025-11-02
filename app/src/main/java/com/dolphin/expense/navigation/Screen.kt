package com.dolphin.expense.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Welcome : Screen("welcome")
    object CurrencySelection : Screen("currency_selection")
    object Dashboard : Screen("dashboard")
    object AllExpenses : Screen("all_expenses")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
    object MonthDetail : Screen("month_detail/{year}/{month}") {
        fun createRoute(year: Int, month: Int): String = "month_detail/$year/$month"
    }
    object AddEditExpense : Screen("add_edit_expense")
    object AddEditExpenseWithId : Screen("add_edit_expense/{expenseId}") {
        fun createRoute(expenseId: Long): String = "add_edit_expense/$expenseId"
    }
}