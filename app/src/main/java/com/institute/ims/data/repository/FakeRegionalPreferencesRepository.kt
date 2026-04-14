package com.institute.ims.data.repository

import com.institute.ims.data.model.RegionalPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.ZoneId
import java.util.Locale

/** In-memory language / country / currency / timezone for the dashboard prototype. */
object FakeRegionalPreferencesRepository {

    private val defaultZone = ZoneId.systemDefault().id

    private val _prefs = MutableStateFlow(
        RegionalPreferences(
            languageCode = Locale.getDefault().language.ifEmpty { "en" }.lowercase(),
            countryCode = Locale.getDefault().country.ifEmpty { "US" }.uppercase(),
            currencyCode = defaultCurrencyFor(Locale.getDefault().country.ifEmpty { "US" }),
            timeZoneId = defaultZone,
        ),
    )

    val prefsFlow: StateFlow<RegionalPreferences> = _prefs.asStateFlow()

    fun update(transform: (RegionalPreferences) -> RegionalPreferences) {
        _prefs.update(transform)
    }

    private fun defaultCurrencyFor(country: String): String = when (country.uppercase()) {
        "IN" -> "INR"
        "GB" -> "GBP"
        "DE", "FR", "IT", "ES" -> "EUR"
        else -> "USD"
    }
}
