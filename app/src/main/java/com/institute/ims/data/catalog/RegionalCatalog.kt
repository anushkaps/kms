package com.institute.ims.data.catalog

import com.institute.ims.data.model.RegionalPreferences

data class RegionalPick(val code: String, val label: String)

object RegionalCatalog {
    val languages = listOf(
        RegionalPick("en", "English"),
        RegionalPick("hi", "Hindi"),
        RegionalPick("es", "Spanish"),
        RegionalPick("fr", "French"),
    )

    val countries = listOf(
        RegionalPick("US", "United States"),
        RegionalPick("IN", "India"),
        RegionalPick("GB", "United Kingdom"),
        RegionalPick("AU", "Australia"),
    )

    val currencies = listOf(
        RegionalPick("USD", "USD - US Dollar"),
        RegionalPick("INR", "INR - Indian Rupee"),
        RegionalPick("GBP", "GBP - British Pound"),
        RegionalPick("EUR", "EUR - Euro"),
    )

    fun timeZoneOptions(deviceZoneId: String): List<RegionalPick> {
        val base = listOf(
            RegionalPick("UTC", "UTC"),
            RegionalPick("America/New_York", "America/New_York"),
            RegionalPick("Europe/London", "Europe/London"),
            RegionalPick("Asia/Kolkata", "Asia/Kolkata"),
            RegionalPick("Australia/Sydney", "Australia/Sydney"),
        )
        if (base.none { it.code == deviceZoneId }) {
            return listOf(RegionalPick(deviceZoneId, "$deviceZoneId (device)")) + base
        }
        return base
    }

    fun summaryLine(prefs: RegionalPreferences): String {
        val lang = languages.find { it.code == prefs.languageCode }?.label ?: prefs.languageCode
        val country = countries.find { it.code.equals(prefs.countryCode, ignoreCase = true) }?.label
            ?: prefs.countryCode
        val cur = currencies.find { it.code.equals(prefs.currencyCode, ignoreCase = true) }?.label
            ?: prefs.currencyCode
        return "$lang · $country · $cur · ${prefs.timeZoneId}"
    }
}
