package com.dolphin.expense.utils

import com.dolphin.expense.data.Currency

object CurrencyFormatter {
    fun format(amount: Double, currency: Currency): String {
        return "${currency.symbol}${String.format("%.2f", amount)}"
    }

    fun formatWithCode(amount: Double, currency: Currency): String {
        return "${currency.symbol}${String.format("%.2f", amount)} (${currency.code})"
    }
}
