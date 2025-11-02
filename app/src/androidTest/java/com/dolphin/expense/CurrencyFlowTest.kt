package com.dolphin.expense

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dolphin.expense.data.Currency
import com.dolphin.expense.data.CurrencyList
import com.dolphin.expense.utils.CurrencyPreferences
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Instrumented tests for Currency flow
 * These tests run on an Android device and test the actual SharedPreferences implementation
 */
@RunWith(AndroidJUnit4::class)
class CurrencyFlowTest {

    private lateinit var context: Context
    private lateinit var currencyPreferences: CurrencyPreferences

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        currencyPreferences = CurrencyPreferences(context)

        // Clear preferences before each test
        context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @After
    fun cleanup() {
        // Clear preferences after each test
        context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun testAppContext() {
        assertEquals("com.dolphin.expense", context.packageName)
    }

    @Test
    fun testFirstLaunchDetection() {
        // On first launch, isFirstLaunch should return true
        assertTrue("First launch should be true initially", currencyPreferences.isFirstLaunch())

        // After marking complete, it should return false
        currencyPreferences.setFirstLaunchComplete()
        assertFalse("First launch should be false after completion", currencyPreferences.isFirstLaunch())
    }

    @Test
    fun testSaveAndRetrieveUSDCurrency() {
        // Given
        val usd = Currency(
            code = "USD",
            name = "US Dollar",
            symbol = "$",
            flag = "🇺🇸",
            locale = "en_US"
        )

        // When
        currencyPreferences.saveCurrency(usd)

        // Then
        val retrieved = currencyPreferences.getCurrency()
        assertEquals("USD", retrieved.code)
        assertEquals("US Dollar", retrieved.name)
        assertEquals("$", retrieved.symbol)
        assertEquals("🇺🇸", retrieved.flag)
        assertEquals("en_US", retrieved.locale)
    }

    @Test
    fun testSaveAndRetrieveEURCurrency() {
        // Given
        val eur = Currency(
            code = "EUR",
            name = "Euro",
            symbol = "€",
            flag = "🇪🇺",
            locale = "en_EU"
        )

        // When
        currencyPreferences.saveCurrency(eur)

        // Then
        val retrieved = currencyPreferences.getCurrency()
        assertEquals("EUR", retrieved.code)
        assertEquals("Euro", retrieved.name)
        assertEquals("€", retrieved.symbol)
        assertEquals("🇪🇺", retrieved.flag)
    }

    @Test
    fun testSaveAndRetrieveGBPCurrency() {
        // Given
        val gbp = Currency(
            code = "GBP",
            name = "British Pound",
            symbol = "£",
            flag = "🇬🇧",
            locale = "en_GB"
        )

        // When
        currencyPreferences.saveCurrency(gbp)

        // Then
        val retrieved = currencyPreferences.getCurrency()
        assertEquals("GBP", retrieved.code)
        assertEquals("£", retrieved.symbol)
    }

    @Test
    fun testDefaultCurrencyIsINR() {
        // When - Get currency without saving any
        val currency = currencyPreferences.getCurrency()

        // Then
        assertEquals("INR", currency.code)
        assertEquals("₹", currency.symbol)
    }

    @Test
    fun testCurrencyPersistsAcrossInstances() {
        // Given
        val jpy = Currency(
            code = "JPY",
            name = "Japanese Yen",
            symbol = "¥",
            flag = "🇯🇵",
            locale = "ja_JP"
        )

        // When - Save with first instance
        currencyPreferences.saveCurrency(jpy)

        // Create new instance
        val newInstance = CurrencyPreferences(context)
        val retrieved = newInstance.getCurrency()

        // Then
        assertEquals("JPY", retrieved.code)
        assertEquals("¥", retrieved.symbol)
    }

    @Test
    fun testCurrencyUpdate() {
        // Given - First save INR
        val inr = CurrencyList.getCurrencyByCode("INR")!!
        currencyPreferences.saveCurrency(inr)
        assertEquals("INR", currencyPreferences.getCurrency().code)

        // When - Update to USD
        val usd = CurrencyList.getCurrencyByCode("USD")!!
        currencyPreferences.saveCurrency(usd)

        // Then
        val retrieved = currencyPreferences.getCurrency()
        assertEquals("USD", retrieved.code)
        assertEquals("$", retrieved.symbol)
    }

    @Test
    fun testAllPopularCurrenciesSaveAndRetrieve() {
        // Given
        val popularCurrencies = listOf("USD", "EUR", "GBP", "JPY", "INR", "AUD", "CAD", "CHF", "CNY", "SGD")

        popularCurrencies.forEach { code ->
            // When
            val currency = CurrencyList.getCurrencyByCode(code)
            assertNotNull("Currency $code should exist", currency)

            currencyPreferences.saveCurrency(currency!!)

            // Then
            val retrieved = currencyPreferences.getCurrency()
            assertEquals("Currency code should match", code, retrieved.code)
            assertNotNull("Symbol should not be null", retrieved.symbol)
            assertFalse("Symbol should not be empty", retrieved.symbol.isEmpty())
        }
    }

    @Test
    fun testCurrencySymbolsAreUnique() {
        // Verify that major currencies have different symbols
        val usd = CurrencyList.getCurrencyByCode("USD")!!
        val eur = CurrencyList.getCurrencyByCode("EUR")!!
        val gbp = CurrencyList.getCurrencyByCode("GBP")!!
        val inr = CurrencyList.getCurrencyByCode("INR")!!

        assertNotEquals(usd.symbol, eur.symbol)
        assertNotEquals(usd.symbol, gbp.symbol)
        assertNotEquals(usd.symbol, inr.symbol)
        assertNotEquals(eur.symbol, gbp.symbol)
        assertNotEquals(eur.symbol, inr.symbol)
        assertNotEquals(gbp.symbol, inr.symbol)
    }

    @Test
    fun testFirstLaunchWorkflowComplete() {
        // Simulate the complete first launch workflow

        // 1. User launches app for first time
        assertTrue("Should be first launch", currencyPreferences.isFirstLaunch())

        // 2. User sees Welcome Screen
        // (No action needed)

        // 3. User selects currency (e.g., USD)
        val selectedCurrency = CurrencyList.getCurrencyByCode("USD")!!
        currencyPreferences.saveCurrency(selectedCurrency)

        // 4. User completes onboarding
        currencyPreferences.setFirstLaunchComplete()

        // 5. Verify state
        assertFalse("Should not be first launch anymore", currencyPreferences.isFirstLaunch())
        assertEquals("USD", currencyPreferences.getCurrency().code)

        // 6. On app restart, should remember currency
        val newInstance = CurrencyPreferences(context)
        assertFalse("Should still not be first launch", newInstance.isFirstLaunch())
        assertEquals("USD", newInstance.getCurrency().code)
    }

    @Test
    fun testCurrencyFormattingIntegers() {
        // Test that common amounts format correctly with different currencies
        val testCases = listOf(
            Triple("USD", "$", 100.0),
            Triple("EUR", "€", 50.0),
            Triple("INR", "₹", 1000.0),
            Triple("GBP", "£", 75.0)
        )

        testCases.forEach { (code, symbol, amount) ->
            val currency = CurrencyList.getCurrencyByCode(code)!!
            assertEquals("Currency $code symbol should match", symbol, currency.symbol)

            // Verify symbol formatting
            val formatted = "${currency.symbol}${String.format("%.2f", amount)}"
            assertTrue("Formatted amount should contain symbol", formatted.startsWith(symbol))
        }
    }

    @Test
    fun testCurrencyFormattingDecimals() {
        // Test decimal formatting
        val usd = CurrencyList.getCurrencyByCode("USD")!!
        val amount = 123.45

        val formatted = "${usd.symbol}${String.format("%.2f", amount)}"
        assertEquals("$123.45", formatted)
    }

    @Test
    fun testSharedPreferencesPersistence() {
        // Verify that SharedPreferences actually persists data
        val prefs = context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)

        // Save currency
        prefs.edit()
            .putString("currency_code", "EUR")
            .putString("currency_symbol", "€")
            .apply()

        // Retrieve
        val code = prefs.getString("currency_code", "")
        val symbol = prefs.getString("currency_symbol", "")

        assertEquals("EUR", code)
        assertEquals("€", symbol)
    }
}
