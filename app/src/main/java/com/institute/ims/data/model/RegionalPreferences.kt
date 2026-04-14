package com.institute.ims.data.model

/** Demo-only locale and regional display settings (in-memory, no persistence). */
data class RegionalPreferences(
    val languageCode: String,
    val countryCode: String,
    val currencyCode: String,
    val timeZoneId: String,
)
