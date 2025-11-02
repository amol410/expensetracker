package com.dolphin.expense.data

data class Currency(
    val code: String,
    val name: String,
    val symbol: String,
    val flag: String,
    val locale: String = "en_US"
)

object CurrencyList {
    val currencies = listOf(
        // Popular currencies
        Currency("INR", "Indian Rupee", "₹", "🇮🇳", "en_IN"),
        Currency("USD", "US Dollar", "$", "🇺🇸", "en_US"),
        Currency("EUR", "Euro", "€", "🇪🇺", "en_EU"),
        Currency("GBP", "British Pound", "£", "🇬🇧", "en_GB"),
        Currency("JPY", "Japanese Yen", "¥", "🇯🇵", "ja_JP"),
        Currency("CNY", "Chinese Yuan", "¥", "🇨🇳", "zh_CN"),
        Currency("AUD", "Australian Dollar", "A$", "🇦🇺", "en_AU"),
        Currency("CAD", "Canadian Dollar", "C$", "🇨🇦", "en_CA"),
        Currency("CHF", "Swiss Franc", "CHF", "🇨🇭", "de_CH"),
        Currency("SEK", "Swedish Krona", "kr", "🇸🇪", "sv_SE"),
        Currency("NZD", "New Zealand Dollar", "NZ$", "🇳🇿", "en_NZ"),
        Currency("SGD", "Singapore Dollar", "S$", "🇸🇬", "en_SG"),
        Currency("HKD", "Hong Kong Dollar", "HK$", "🇭🇰", "zh_HK"),
        Currency("KRW", "South Korean Won", "₩", "🇰🇷", "ko_KR"),
        Currency("NOK", "Norwegian Krone", "kr", "🇳🇴", "no_NO"),
        Currency("MXN", "Mexican Peso", "Mex$", "🇲🇽", "es_MX"),
        Currency("BRL", "Brazilian Real", "R$", "🇧🇷", "pt_BR"),
        Currency("ZAR", "South African Rand", "R", "🇿🇦", "en_ZA"),
        Currency("RUB", "Russian Ruble", "₽", "🇷🇺", "ru_RU"),
        Currency("TRY", "Turkish Lira", "₺", "🇹🇷", "tr_TR"),
        Currency("THB", "Thai Baht", "฿", "🇹🇭", "th_TH"),
        Currency("IDR", "Indonesian Rupiah", "Rp", "🇮🇩", "id_ID"),
        Currency("MYR", "Malaysian Ringgit", "RM", "🇲🇾", "ms_MY"),
        Currency("PHP", "Philippine Peso", "₱", "🇵🇭", "en_PH"),
        Currency("PLN", "Polish Zloty", "zł", "🇵🇱", "pl_PL"),
        Currency("DKK", "Danish Krone", "kr", "🇩🇰", "da_DK"),
        Currency("AED", "UAE Dirham", "د.إ", "🇦🇪", "ar_AE"),
        Currency("SAR", "Saudi Riyal", "﷼", "🇸🇦", "ar_SA"),
        Currency("ILS", "Israeli Shekel", "₪", "🇮🇱", "he_IL"),
        Currency("PKR", "Pakistani Rupee", "₨", "🇵🇰", "ur_PK"),
        Currency("BDT", "Bangladeshi Taka", "৳", "🇧🇩", "bn_BD"),
        Currency("LKR", "Sri Lankan Rupee", "Rs", "🇱🇰", "si_LK"),
        Currency("NPR", "Nepalese Rupee", "रू", "🇳🇵", "ne_NP"),
        Currency("VND", "Vietnamese Dong", "₫", "🇻🇳", "vi_VN"),
        Currency("EGP", "Egyptian Pound", "E£", "🇪🇬", "ar_EG"),
        Currency("NGN", "Nigerian Naira", "₦", "🇳🇬", "en_NG"),
        Currency("KES", "Kenyan Shilling", "KSh", "🇰🇪", "sw_KE"),
        Currency("CZK", "Czech Koruna", "Kč", "🇨🇿", "cs_CZ"),
        Currency("HUF", "Hungarian Forint", "Ft", "🇭🇺", "hu_HU"),
        Currency("RON", "Romanian Leu", "lei", "🇷🇴", "ro_RO"),
        Currency("CLP", "Chilean Peso", "$", "🇨🇱", "es_CL"),
        Currency("ARS", "Argentine Peso", "$", "🇦🇷", "es_AR"),
        Currency("COP", "Colombian Peso", "$", "🇨🇴", "es_CO"),
        Currency("PEN", "Peruvian Sol", "S/", "🇵🇪", "es_PE")
    )

    fun getCurrencyByCode(code: String): Currency? {
        return currencies.find { it.code == code }
    }

    fun getDefaultCurrency(): Currency {
        return currencies.first { it.code == "INR" }
    }
}
