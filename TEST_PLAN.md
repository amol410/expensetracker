# Expense Tracker - Comprehensive Test Plan
## Currency Feature Testing

This document contains both **Automated Tests** and **Manual Test Cases** for the currency feature.

---

## 🤖 AUTOMATED TESTS

### Running the Tests

#### 1. Unit Tests (Fast - No device needed)
```bash
# Run all unit tests
gradlew test

# Run specific test class
gradlew test --tests CurrencyPreferencesTest
gradlew test --tests CurrencyTest

# Run with detailed output
gradlew test --info
```

#### 2. Instrumented Tests (Requires Android device/emulator)
```bash
# Make sure device is connected
adb devices

# Run all instrumented tests
gradlew connectedAndroidTest

# Run specific test class
gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.dolphin.expense.CurrencyFlowTest
```

#### 3. View Test Reports
After running tests, view HTML reports at:
- Unit Tests: `app/build/reports/tests/testDebugUnitTest/index.html`
- Instrumented Tests: `app/build/reports/androidTests/connected/index.html`

---

## 📝 MANUAL TEST CASES

### Test Suite 1: First Launch Experience

#### TC-001: First Time App Launch
**Objective:** Verify new users see currency selection screen

**Preconditions:**
- Fresh install of the app OR cleared app data
- No previous currency selection

**Steps:**
1. Launch the app
2. Tap "Get Started" on Welcome screen
3. Observe Currency Selection screen appears

**Expected Result:**
- ✅ Currency Selection screen is displayed
- ✅ Popular currencies (USD, EUR, GBP, JPY, INR, etc.) are shown at top
- ✅ Search bar is visible
- ✅ Device's locale-based currency is suggested/highlighted

**Status:** [ ] Pass [ ] Fail

---

#### TC-002: Currency Selection - Search Functionality
**Objective:** Verify currency search works correctly

**Steps:**
1. On Currency Selection screen
2. Type "USD" in search bar
3. Verify US Dollar appears
4. Clear search and type "euro"
5. Verify Euro appears
6. Type invalid text "xyz123"

**Expected Result:**
- ✅ Search filters currencies correctly
- ✅ Case-insensitive search works
- ✅ No results shown for invalid search
- ✅ Clearing search shows all currencies again

**Status:** [ ] Pass [ ] Fail

---

#### TC-003: Currency Selection - Popular Currencies
**Objective:** Verify popular currencies are accessible

**Steps:**
1. On Currency Selection screen
2. Verify the following are visible in "Popular Currencies" section:
   - USD (US Dollar) - $
   - EUR (Euro) - €
   - GBP (British Pound) - £
   - JPY (Japanese Yen) - ¥
   - INR (Indian Rupee) - ₹
   - AUD (Australian Dollar) - A$
   - CAD (Canadian Dollar) - C$
   - CHF (Swiss Franc) - CHF
   - CNY (Chinese Yuan) - ¥
   - SGD (Singapore Dollar) - S$

**Expected Result:**
- ✅ All 10 popular currencies are visible
- ✅ Each shows correct flag emoji
- ✅ Each shows correct symbol
- ✅ Each shows correct name

**Status:** [ ] Pass [ ] Fail

---

#### TC-004: Currency Selection - Preview Amount
**Objective:** Verify currency preview shows correctly formatted amount

**Steps:**
1. On Currency Selection screen
2. Tap on USD
3. Observe preview shows "$1,234.56"
4. Tap on EUR
5. Observe preview shows "€1,234.56"
6. Tap on INR
7. Observe preview shows "₹1,234.56"

**Expected Result:**
- ✅ Preview updates when currency is selected
- ✅ Correct symbol is displayed
- ✅ Amount is properly formatted with commas

**Status:** [ ] Pass [ ] Fail

---

#### TC-005: Currency Selection Confirmation
**Objective:** Verify selected currency is saved and app navigates to Dashboard

**Steps:**
1. On Currency Selection screen
2. Select "EUR (Euro)"
3. Tap "Continue" or confirm button
4. Observe navigation to Dashboard

**Expected Result:**
- ✅ App navigates to Dashboard screen
- ✅ Currency selection is saved
- ✅ No errors occur

**Status:** [ ] Pass [ ] Fail

---

### Test Suite 2: Dashboard Screen Currency Display

#### TC-006: Dashboard - Today Card Currency Symbol
**Objective:** Verify Today's expenses card shows selected currency

**Preconditions:**
- User has selected EUR (€) as currency

**Steps:**
1. Navigate to Dashboard
2. Observe "Today" card
3. Verify currency symbol shown

**Expected Result:**
- ✅ Amount shows "€0" (or current total)
- ✅ Symbol is EUR symbol (€), not rupee (₹)

**Status:** [ ] Pass [ ] Fail

---

#### TC-007: Dashboard - This Month Card Currency Symbol
**Objective:** Verify This Month's expenses card shows selected currency

**Preconditions:**
- User has selected USD ($) as currency

**Steps:**
1. Navigate to Dashboard
2. Observe "This Month" card
3. Verify currency symbol shown

**Expected Result:**
- ✅ Amount shows "$0" (or current total)
- ✅ Symbol is USD symbol ($), not rupee (₹)

**Status:** [ ] Pass [ ] Fail

---

#### TC-008: Dashboard - Last Month Comparison
**Objective:** Verify last month comparison shows selected currency

**Preconditions:**
- User has selected GBP (£) as currency
- Previous month has some expenses

**Steps:**
1. Navigate to Dashboard
2. Scroll to "vs Last Month" card
3. Verify currency symbol

**Expected Result:**
- ✅ Last month amount shows "£X" format
- ✅ Percentage change is displayed correctly

**Status:** [ ] Pass [ ] Fail

---

#### TC-009: Dashboard - Highest Expense Card
**Objective:** Verify highest expense shows selected currency

**Preconditions:**
- User has at least one expense
- Currency is set to JPY (¥)

**Steps:**
1. Add an expense (e.g., 1000 JPY)
2. Navigate to Dashboard
3. Observe "Highest Expense" card

**Expected Result:**
- ✅ Amount shows "¥1000"
- ✅ Category is displayed
- ✅ Correct symbol is shown

**Status:** [ ] Pass [ ] Fail

---

#### TC-010: Dashboard - Recent Transactions List
**Objective:** Verify recent transactions show selected currency

**Preconditions:**
- User has expenses
- Currency is set to CAD (C$)

**Steps:**
1. Navigate to Dashboard
2. Scroll to "Recent Transactions"
3. Observe expense items

**Expected Result:**
- ✅ All expense amounts show "C$X.XX" format
- ✅ No hardcoded ₹ symbols appear

**Status:** [ ] Pass [ ] Fail

---

### Test Suite 3: Add Expense Screen

#### TC-011: Add Expense - Currency Symbol Prefix
**Objective:** Verify amount field shows selected currency symbol

**Preconditions:**
- Currency is set to EUR (€)

**Steps:**
1. Tap the + (Add Expense) button
2. Observe the Amount input field
3. Check the prefix/leading text

**Expected Result:**
- ✅ Field shows "€ " prefix before amount input
- ✅ Icon is generic (Payments icon, not rupee-specific)
- ✅ Placeholder or label says "Amount"

**Status:** [ ] Pass [ ] Fail

---

#### TC-012: Add Expense - Different Currencies
**Objective:** Verify amount field adapts to different currencies

**Test Data:**
| Currency | Symbol | Expected Prefix |
|----------|--------|-----------------|
| USD      | $      | $ |
| EUR      | €      | € |
| GBP      | £      | £ |
| JPY      | ¥      | ¥ |
| INR      | ₹      | ₹ |

**Steps:**
For each currency:
1. Change app currency to test currency (via Settings if implemented)
2. Tap + to add expense
3. Observe amount field prefix

**Expected Result:**
- ✅ Prefix matches currency symbol
- ✅ No hardcoded rupee symbol

**Status:** [ ] Pass [ ] Fail

---

#### TC-013: Add Expense - Save Functionality
**Objective:** Verify expense is saved without currency symbol in amount

**Preconditions:**
- Currency is set to GBP (£)

**Steps:**
1. Tap + to add expense
2. Enter amount: 50
3. Select category: Food
4. Enter description: "Groceries"
5. Tap Save

**Expected Result:**
- ✅ Expense is saved successfully
- ✅ Amount stored is 50.00 (numeric, no symbol)
- ✅ Dashboard shows "£50.00"

**Status:** [ ] Pass [ ] Fail

---

### Test Suite 4: All Expenses Screen

#### TC-014: All Expenses - Expense List Currency
**Objective:** Verify all expenses show selected currency

**Preconditions:**
- User has multiple expenses
- Currency is set to AUD (A$)

**Steps:**
1. Navigate to "All Expenses" screen
2. Observe list of expenses
3. Check currency symbols

**Expected Result:**
- ✅ All expense amounts show "A$X.XX"
- ✅ Consistent currency across all items
- ✅ No hardcoded ₹ symbols

**Status:** [ ] Pass [ ] Fail

---

#### TC-015: All Expenses - Monthly Budget Display
**Objective:** Verify monthly budget card shows selected currency

**Preconditions:**
- Monthly budget is set (e.g., 1000 SGD)
- Currency is SGD (S$)

**Steps:**
1. Navigate to All Expenses screen
2. Observe budget card at top
3. Check currency symbols

**Expected Result:**
- ✅ Budget shows "S$1000.00"
- ✅ Spent amount shows "S$X.XX"
- ✅ Remaining shows "S$X.XX remaining"

**Status:** [ ] Pass [ ] Fail

---

#### TC-016: All Expenses - Delete Confirmation
**Objective:** Verify delete dialog shows correct currency

**Preconditions:**
- User has at least one expense
- Currency is set to CHF (CHF)

**Steps:**
1. Navigate to All Expenses
2. Tap Delete on an expense (e.g., 100 CHF)
3. Observe confirmation dialog

**Expected Result:**
- ✅ Dialog says "delete this expense of CHF100.00"
- ✅ Correct currency symbol shown
- ✅ No hardcoded ₹

**Status:** [ ] Pass [ ] Fail

---

### Test Suite 5: Statistics Screen

#### TC-017: Statistics - Total Expenses Display
**Objective:** Verify total expenses shows selected currency

**Preconditions:**
- User has expenses in current month
- Currency is set to CNY (¥)

**Steps:**
1. Navigate to Statistics screen
2. Observe "Total Expenses" card
3. Check currency symbol

**Expected Result:**
- ✅ Total shows "¥X.XX"
- ✅ Correct symbol displayed
- ✅ Amount is accurate

**Status:** [ ] Pass [ ] Fail

---

#### TC-018: Statistics - 6 Month History
**Objective:** Verify monthly breakdown uses selected currency

**Preconditions:**
- User has expenses across multiple months
- Currency is set to USD ($)

**Steps:**
1. Navigate to Statistics screen
2. Scroll through 6-month history
3. Observe each month's total

**Expected Result:**
- ✅ All months show "$X.XX" format
- ✅ Consistent currency across all months
- ✅ Charts/graphs display correctly

**Status:** [ ] Pass [ ] Fail

---

### Test Suite 6: Month Detail Screen

#### TC-019: Month Detail - Header Total
**Objective:** Verify month header shows selected currency

**Preconditions:**
- User has expenses in a specific month
- Currency is set to EUR (€)

**Steps:**
1. Navigate to Statistics
2. Tap on a specific month (e.g., December 2024)
3. Observe month detail header

**Expected Result:**
- ✅ Total shows "Total: €X.XX"
- ✅ Correct currency symbol
- ✅ Accurate sum of expenses

**Status:** [ ] Pass [ ] Fail

---

#### TC-020: Month Detail - Expense List
**Objective:** Verify expense list items show selected currency

**Preconditions:**
- Month has multiple expenses
- Currency is set to GBP (£)

**Steps:**
1. Open Month Detail for a month with expenses
2. Scroll through expense list
3. Check currency symbols

**Expected Result:**
- ✅ All expenses show "£X.XX"
- ✅ Consistent formatting
- ✅ No hardcoded symbols

**Status:** [ ] Pass [ ] Fail

---

#### TC-021: Month Detail - PDF Export
**Objective:** Verify PDF export includes correct currency

**Preconditions:**
- Month has expenses
- Currency is set to CAD (C$)

**Steps:**
1. Open Month Detail screen
2. Tap "Export PDF" or download icon
3. Open generated PDF file
4. Check currency symbols in PDF

**Expected Result:**
- ✅ PDF shows "Total: C$X.XX"
- ✅ Each expense row shows "C$X.XX"
- ✅ Currency is consistent throughout PDF
- ✅ PDF is readable and formatted correctly

**Status:** [ ] Pass [ ] Fail

---

### Test Suite 7: Settings & Currency Change

#### TC-022: Settings - View Current Currency
**Objective:** Verify current currency is displayed in Settings

**Preconditions:**
- Currency is set to INR (₹)

**Steps:**
1. Navigate to Settings screen
2. Look for Currency section
3. Observe current currency display

**Expected Result:**
- ✅ Shows "₹ Indian Rupee (INR)" or similar
- ✅ Flag emoji is displayed
- ✅ Correctly reflects saved currency

**Status:** [ ] Pass [ ] Fail

---

#### TC-023: Settings - Change Currency Warning
**Objective:** Verify changing currency shows appropriate warning

**Steps:**
1. Navigate to Settings
2. Tap to change currency
3. Observe warning message (if any)

**Expected Result:**
- ✅ Warning about existing expenses (if implemented)
- ✅ Clear explanation of changes
- ✅ Option to confirm or cancel

**Status:** [ ] Pass [ ] Fail

---

#### TC-024: Currency Change - Immediate Effect
**Objective:** Verify currency change takes effect immediately

**Preconditions:**
- User has expenses
- Current currency is USD ($)

**Steps:**
1. Note current Dashboard shows "$" symbols
2. Go to Settings
3. Change currency to EUR (€)
4. Return to Dashboard
5. Observe currency symbols

**Expected Result:**
- ✅ Dashboard immediately shows "€" symbols
- ✅ No app restart required
- ✅ All screens updated
- ✅ Expense amounts remain numerically same (50 USD becomes 50 EUR in display only)

**Status:** [ ] Pass [ ] Fail

---

### Test Suite 8: Currency Persistence

#### TC-025: Currency Persists After App Restart
**Objective:** Verify selected currency is remembered after closing app

**Preconditions:**
- Currency is set to JPY (¥)

**Steps:**
1. Verify Dashboard shows ¥ symbols
2. Close app completely (swipe away from recent apps)
3. Relaunch app
4. Check Dashboard currency symbols

**Expected Result:**
- ✅ Currency is still JPY (¥)
- ✅ No need to select currency again
- ✅ All screens show ¥ symbols

**Status:** [ ] Pass [ ] Fail

---

#### TC-026: Currency Persists After Device Reboot
**Objective:** Verify currency survives device restart

**Preconditions:**
- Currency is set to GBP (£)

**Steps:**
1. Set currency to GBP
2. Reboot Android device
3. Launch app after reboot
4. Check currency

**Expected Result:**
- ✅ Currency is still GBP (£)
- ✅ SharedPreferences data persisted
- ✅ No data loss

**Status:** [ ] Pass [ ] Fail

---

### Test Suite 9: Edge Cases & Error Handling

#### TC-027: Multiple Quick Currency Changes
**Objective:** Verify app handles rapid currency changes

**Steps:**
1. Quickly change currency: USD → EUR → GBP → JPY → INR
2. Observe app behavior
3. Check final currency display

**Expected Result:**
- ✅ No crashes
- ✅ Final selection is saved correctly
- ✅ UI updates properly
- ✅ No race conditions

**Status:** [ ] Pass [ ] Fail

---

#### TC-028: Currency with Large Amounts
**Objective:** Verify currency formatting works with large numbers

**Test Data:**
| Amount | Currency | Expected Display |
|--------|----------|------------------|
| 1000000 | USD | $1,000,000.00 |
| 999999.99 | EUR | €999,999.99 |
| 5000000 | INR | ₹5,000,000.00 |

**Steps:**
For each test case:
1. Add expense with large amount
2. Observe display on all screens

**Expected Result:**
- ✅ Numbers formatted with commas
- ✅ Currency symbol shown
- ✅ No overflow issues
- ✅ Readable formatting

**Status:** [ ] Pass [ ] Fail

---

#### TC-029: Currency with Decimal Precision
**Objective:** Verify currency handles decimals correctly

**Steps:**
1. Add expense: 123.45 (any currency)
2. Check Dashboard display
3. Edit expense to 123.456
4. Save and check rounding

**Expected Result:**
- ✅ Displays 2 decimal places: X.XX
- ✅ Rounds correctly (123.456 → 123.46)
- ✅ No precision loss
- ✅ Consistent across all screens

**Status:** [ ] Pass [ ] Fail

---

#### TC-030: Zero Amount with Currency
**Objective:** Verify zero amounts display correctly

**Steps:**
1. Observe Dashboard with no expenses
2. Check "Today" card
3. Check "This Month" card

**Expected Result:**
- ✅ Shows selected currency symbol with 0
- ✅ e.g., "$0" or "€0.00"
- ✅ Not just "0" without symbol

**Status:** [ ] Pass [ ] Fail

---

### Test Suite 10: All Supported Currencies

#### TC-031: Test All 44+ Currencies
**Objective:** Verify every supported currency works correctly

**Method:**
Create a checklist and test each currency:

**Popular Currencies (10):**
- [ ] USD - US Dollar - $ - 🇺🇸
- [ ] EUR - Euro - € - 🇪🇺
- [ ] GBP - British Pound - £ - 🇬🇧
- [ ] JPY - Japanese Yen - ¥ - 🇯🇵
- [ ] INR - Indian Rupee - ₹ - 🇮🇳
- [ ] AUD - Australian Dollar - A$ - 🇦🇺
- [ ] CAD - Canadian Dollar - C$ - 🇨🇦
- [ ] CHF - Swiss Franc - CHF - 🇨🇭
- [ ] CNY - Chinese Yuan - ¥ - 🇨🇳
- [ ] SGD - Singapore Dollar - S$ - 🇸🇬

**Additional Currencies to Test:**
- [ ] AED - UAE Dirham
- [ ] ARS - Argentine Peso
- [ ] BRL - Brazilian Real
- [ ] CLP - Chilean Peso
- [ ] CZK - Czech Koruna
- [ ] DKK - Danish Krone
- [ ] EGP - Egyptian Pound
- [ ] HKD - Hong Kong Dollar
- [ ] HUF - Hungarian Forint
- [ ] IDR - Indonesian Rupiah
- [ ] ILS - Israeli Shekel
- [ ] KRW - South Korean Won
- [ ] KWD - Kuwaiti Dinar
- [ ] MXN - Mexican Peso
- [ ] MYR - Malaysian Ringgit
- [ ] NOK - Norwegian Krone
- [ ] NZD - New Zealand Dollar
- [ ] PHP - Philippine Peso
- [ ] PKR - Pakistani Rupee
- [ ] PLN - Polish Zloty
- [ ] QAR - Qatari Riyal
- [ ] RON - Romanian Leu
- [ ] RUB - Russian Ruble
- [ ] SAR - Saudi Riyal
- [ ] SEK - Swedish Krona
- [ ] THB - Thai Baht
- [ ] TRY - Turkish Lira
- [ ] TWD - Taiwan Dollar
- [ ] VND - Vietnamese Dong
- [ ] ZAR - South African Rand

**For Each Currency:**
1. Select currency
2. Add an expense
3. Verify symbol displays correctly on:
   - [ ] Dashboard
   - [ ] Add Expense screen
   - [ ] All Expenses list
   - [ ] Statistics
   - [ ] Month Detail

**Status:** [ ] Pass [ ] Fail

---

## 📊 TEST SUMMARY REPORT

### Test Execution Summary
- **Total Test Cases:** 31
- **Passed:** ___
- **Failed:** ___
- **Blocked:** ___
- **Not Executed:** ___

### Critical Issues Found
1.
2.
3.

### Medium Issues Found
1.
2.
3.

### Low Priority Issues
1.
2.

### Recommendations
1.
2.
3.

### Sign-off
- **Tester Name:** _______________
- **Date:** _______________
- **Build Version:** _______________
- **Device(s) Used:** _______________

---

## 🔧 TROUBLESHOOTING

### If Tests Fail:

1. **Clean and Rebuild:**
   ```bash
   gradlew clean
   gradlew build
   ```

2. **Clear App Data:**
   ```bash
   adb shell pm clear com.dolphin.expense
   ```

3. **Check Device/Emulator:**
   ```bash
   adb devices
   ```

4. **View Logcat:**
   ```bash
   adb logcat | grep "dolphin"
   ```

5. **Reinstall APK:**
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 📱 RECOMMENDED TEST DEVICES

- **Minimum:** Android 7.0 (API 24)
- **Recommended:** Android 12+ (API 31+)
- **Form Factors:** Phone (small, medium, large screens)
- **Locales:** US, EU, India, Japan (to test currency suggestions)

---

**END OF TEST PLAN**
