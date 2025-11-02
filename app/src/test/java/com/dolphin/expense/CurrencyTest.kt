package com.dolphin.expense

import com.dolphin.expense.data.Currency
import com.dolphin.expense.data.CurrencyList
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for Currency data class and CurrencyList object
 * Tests currency data validation and currency lookup functionality
 */
class CurrencyTest {

    @Test
    fun `Currency data class should hold all properties correctly`() {
        // Given
        val currency = Currency(
            code = "USD",
            name = "US Dollar",
            symbol = "$",
            flag = "🇺🇸",
            locale = "en_US"
        )

        // Then
        assertEquals("USD", currency.code)
        assertEquals("US Dollar", currency.name)
        assertEquals("$", currency.symbol)
        assertEquals("🇺🇸", currency.flag)
        assertEquals("en_US", currency.locale)
    }

    @Test
    fun `CurrencyList should contain all major currencies`() {
        // Then
        assertTrue(CurrencyList.currencies.size >= 44)

        // Verify some major currencies exist
        val currencyCodes = CurrencyList.currencies.map { it.code }
        assertTrue(currencyCodes.contains("USD"))
        assertTrue(currencyCodes.contains("EUR"))
        assertTrue(currencyCodes.contains("GBP"))
        assertTrue(currencyCodes.contains("JPY"))
        assertTrue(currencyCodes.contains("INR"))
        assertTrue(currencyCodes.contains("AUD"))
        assertTrue(currencyCodes.contains("CAD"))
        assertTrue(currencyCodes.contains("CHF"))
        assertTrue(currencyCodes.contains("CNY"))
        assertTrue(currencyCodes.contains("SGD"))
    }

    @Test
    fun `getCurrencyByCode should return correct currency for valid code`() {
        // Given
        val validCodes = listOf("USD", "EUR", "GBP", "JPY", "INR")

        // When & Then
        validCodes.forEach { code ->
            val currency = CurrencyList.getCurrencyByCode(code)
            assertNotNull("Currency for $code should not be null", currency)
            assertEquals(code, currency?.code)
        }
    }

    @Test
    fun `getCurrencyByCode should return null for invalid code`() {
        // Given
        val invalidCode = "INVALID"

        // When
        val currency = CurrencyList.getCurrencyByCode(invalidCode)

        // Then
        assertNull(currency)
    }

    @Test
    fun `getDefaultCurrency should return INR`() {
        // When
        val defaultCurrency = CurrencyList.getDefaultCurrency()

        // Then
        assertEquals("INR", defaultCurrency.code)
        assertEquals("Indian Rupee", defaultCurrency.name)
        assertEquals("₹", defaultCurrency.symbol)
        assertEquals("🇮🇳", defaultCurrency.flag)
    }

    @Test
    fun `all currencies should have valid symbols`() {
        // Then
        CurrencyList.currencies.forEach { currency ->
            assertNotNull("Currency ${currency.code} should have a symbol", currency.symbol)
            assertFalse("Currency ${currency.code} symbol should not be empty", currency.symbol.isEmpty())
        }
    }

    @Test
    fun `all currencies should have valid names`() {
        // Then
        CurrencyList.currencies.forEach { currency ->
            assertNotNull("Currency ${currency.code} should have a name", currency.name)
            assertFalse("Currency ${currency.code} name should not be empty", currency.name.isEmpty())
        }
    }

    @Test
    fun `all currencies should have valid codes`() {
        // Then
        CurrencyList.currencies.forEach { currency ->
            assertNotNull("Currency should have a code", currency.code)
            assertEquals("Currency ${currency.code} code should be 3 characters", 3, currency.code.length)
            assertTrue("Currency ${currency.code} code should be uppercase", currency.code == currency.code.uppercase())
        }
    }

    @Test
    fun `all currencies should have valid flags`() {
        // Then
        CurrencyList.currencies.forEach { currency ->
            assertNotNull("Currency ${currency.code} should have a flag", currency.flag)
            assertFalse("Currency ${currency.code} flag should not be empty", currency.flag.isEmpty())
        }
    }

    @Test
    fun `all currencies should have valid locales`() {
        // Then
        CurrencyList.currencies.forEach { currency ->
            assertNotNull("Currency ${currency.code} should have a locale", currency.locale)
            assertFalse("Currency ${currency.code} locale should not be empty", currency.locale.isEmpty())
            assertTrue("Currency ${currency.code} locale should contain underscore", currency.locale.contains("_"))
        }
    }

    @Test
    fun `currency codes should be unique`() {
        // Given
        val codes = CurrencyList.currencies.map { it.code }

        // Then
        val uniqueCodes = codes.toSet()
        assertEquals("All currency codes should be unique", codes.size, uniqueCodes.size)
    }

    @Test
    fun `popular currencies should include USD, EUR, GBP, JPY, INR`() {
        // Given
        val popularCurrencies = listOf("USD", "EUR", "GBP", "JPY", "INR", "AUD", "CAD", "CHF", "CNY", "SGD")

        // Then
        popularCurrencies.forEach { code ->
            val currency = CurrencyList.getCurrencyByCode(code)
            assertNotNull("Popular currency $code should exist", currency)
        }
    }

    @Test
    fun `USD currency should have correct properties`() {
        // When
        val usd = CurrencyList.getCurrencyByCode("USD")

        // Then
        assertNotNull(usd)
        assertEquals("USD", usd?.code)
        assertEquals("US Dollar", usd?.name)
        assertEquals("$", usd?.symbol)
        assertEquals("🇺🇸", usd?.flag)
    }

    @Test
    fun `EUR currency should have correct properties`() {
        // When
        val eur = CurrencyList.getCurrencyByCode("EUR")

        // Then
        assertNotNull(eur)
        assertEquals("EUR", eur?.code)
        assertEquals("Euro", eur?.name)
        assertEquals("€", eur?.symbol)
        assertEquals("🇪🇺", eur?.flag)
    }

    @Test
    fun `GBP currency should have correct properties`() {
        // When
        val gbp = CurrencyList.getCurrencyByCode("GBP")

        // Then
        assertNotNull(gbp)
        assertEquals("GBP", gbp?.code)
        assertEquals("British Pound", gbp?.name)
        assertEquals("£", gbp?.symbol)
        assertEquals("🇬🇧", gbp?.flag)
    }

    @Test
    fun `INR currency should have correct properties`() {
        // When
        val inr = CurrencyList.getCurrencyByCode("INR")

        // Then
        assertNotNull(inr)
        assertEquals("INR", inr?.code)
        assertEquals("Indian Rupee", inr?.name)
        assertEquals("₹", inr?.symbol)
        assertEquals("🇮🇳", inr?.flag)
    }
}
