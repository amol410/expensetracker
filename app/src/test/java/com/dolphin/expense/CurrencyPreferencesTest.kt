package com.dolphin.expense

import android.content.Context
import android.content.SharedPreferences
import com.dolphin.expense.data.Currency
import com.dolphin.expense.data.CurrencyList
import com.dolphin.expense.utils.CurrencyPreferences
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Unit tests for CurrencyPreferences class
 * Tests currency storage, retrieval, and first launch detection
 */
class CurrencyPreferencesTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var currencyPreferences: CurrencyPreferences

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Setup mock behavior
        `when`(mockContext.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE))
            .thenReturn(mockSharedPreferences)
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        `when`(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor)

        currencyPreferences = CurrencyPreferences(mockContext)
    }

    @Test
    fun `saveCurrency should save all currency fields to SharedPreferences`() {
        // Given
        val currency = Currency(
            code = "USD",
            name = "US Dollar",
            symbol = "$",
            flag = "🇺🇸",
            locale = "en_US"
        )

        // When
        currencyPreferences.saveCurrency(currency)

        // Then
        verify(mockEditor).putString("currency_code", "USD")
        verify(mockEditor).putString("currency_name", "US Dollar")
        verify(mockEditor).putString("currency_symbol", "$")
        verify(mockEditor).putString("currency_flag", "🇺🇸")
        verify(mockEditor).putString("currency_locale", "en_US")
        verify(mockEditor).apply()
    }

    @Test
    fun `getCurrency should return saved currency`() {
        // Given
        `when`(mockSharedPreferences.getString("currency_code", "INR")).thenReturn("EUR")
        `when`(mockSharedPreferences.getString("currency_name", "Indian Rupee")).thenReturn("Euro")
        `when`(mockSharedPreferences.getString("currency_symbol", "₹")).thenReturn("€")
        `when`(mockSharedPreferences.getString("currency_flag", "🇮🇳")).thenReturn("🇪🇺")
        `when`(mockSharedPreferences.getString("currency_locale", "en_IN")).thenReturn("en_EU")

        // When
        val result = currencyPreferences.getCurrency()

        // Then
        assertEquals("EUR", result.code)
        assertEquals("Euro", result.name)
        assertEquals("€", result.symbol)
        assertEquals("🇪🇺", result.flag)
        assertEquals("en_EU", result.locale)
    }

    @Test
    fun `getCurrency should return default INR when no currency saved`() {
        // Given - return default values
        `when`(mockSharedPreferences.getString("currency_code", "INR")).thenReturn("INR")
        `when`(mockSharedPreferences.getString("currency_name", "Indian Rupee")).thenReturn("Indian Rupee")
        `when`(mockSharedPreferences.getString("currency_symbol", "₹")).thenReturn("₹")
        `when`(mockSharedPreferences.getString("currency_flag", "🇮🇳")).thenReturn("🇮🇳")
        `when`(mockSharedPreferences.getString("currency_locale", "en_IN")).thenReturn("en_IN")

        // When
        val result = currencyPreferences.getCurrency()

        // Then
        assertEquals("INR", result.code)
        assertEquals("Indian Rupee", result.name)
        assertEquals("₹", result.symbol)
    }

    @Test
    fun `isFirstLaunch should return true when first_launch is true`() {
        // Given
        `when`(mockSharedPreferences.getBoolean("first_launch", true)).thenReturn(true)

        // When
        val result = currencyPreferences.isFirstLaunch()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isFirstLaunch should return false after setFirstLaunchComplete`() {
        // Given
        `when`(mockSharedPreferences.getBoolean("first_launch", true)).thenReturn(false)

        // When
        val result = currencyPreferences.isFirstLaunch()

        // Then
        assertFalse(result)
    }

    @Test
    fun `setFirstLaunchComplete should save false to first_launch`() {
        // When
        currencyPreferences.setFirstLaunchComplete()

        // Then
        verify(mockEditor).putBoolean("first_launch", false)
        verify(mockEditor).apply()
    }
}
