package com.institute.ims.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.institute.ims.data.model.RegionalPreferences
import com.institute.ims.data.repository.FakeRegionalPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class RegionalSettingsViewModel : ViewModel() {

    val preferences: StateFlow<RegionalPreferences> =
        FakeRegionalPreferencesRepository.prefsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FakeRegionalPreferencesRepository.prefsFlow.value,
        )

    fun setLanguage(code: String) {
        FakeRegionalPreferencesRepository.update { it.copy(languageCode = code) }
    }

    fun setCountry(code: String) {
        FakeRegionalPreferencesRepository.update { it.copy(countryCode = code.uppercase()) }
    }

    fun setCurrency(code: String) {
        FakeRegionalPreferencesRepository.update { it.copy(currencyCode = code.uppercase()) }
    }

    fun setTimeZone(zoneId: String) {
        FakeRegionalPreferencesRepository.update { it.copy(timeZoneId = zoneId) }
    }
}
