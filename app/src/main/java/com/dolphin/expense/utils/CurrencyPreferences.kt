package com.dolphin.expense.utils

import android.content.Context
import android.content.SharedPreferences
import com.dolphin.expense.data.Currency
import com.dolphin.expense.data.CurrencyList
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "currency_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_CURRENCY_CODE = "currency_code"
        private const val KEY_CURRENCY_NAME = "currency_name"
        private const val KEY_CURRENCY_SYMBOL = "currency_symbol"
        private const val KEY_CURRENCY_FLAG = "currency_flag"
        private const val KEY_CURRENCY_LOCALE = "currency_locale"
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }

    fun saveCurrency(currency: Currency) {
        prefs.edit().apply {
            putString(KEY_CURRENCY_CODE, currency.code)
            putString(KEY_CURRENCY_NAME, currency.name)
            putString(KEY_CURRENCY_SYMBOL, currency.symbol)
            putString(KEY_CURRENCY_FLAG, currency.flag)
            putString(KEY_CURRENCY_LOCALE, currency.locale)
            apply()
        }
    }

    fun getCurrency(): Currency {
        val code = prefs.getString(KEY_CURRENCY_CODE, null)
        val name = prefs.getString(KEY_CURRENCY_NAME, null)
        val symbol = prefs.getString(KEY_CURRENCY_SYMBOL, null)
        val flag = prefs.getString(KEY_CURRENCY_FLAG, null)
        val locale = prefs.getString(KEY_CURRENCY_LOCALE, null)

        return if (code != null && name != null && symbol != null && flag != null) {
            Currency(code, name, symbol, flag, locale ?: "en_US")
        } else {
            CurrencyList.getDefaultCurrency()
        }
    }

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchComplete() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
