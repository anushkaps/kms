package com.institute.ims.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.catalog.RegionalCatalog
import com.institute.ims.data.catalog.RegionalPick
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionalSettingsScreen(
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegionalSettingsViewModel = viewModel(),
) {
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val deviceZone = remember { ZoneId.systemDefault().id }
    val timeZones = remember(deviceZone) { RegionalCatalog.timeZoneOptions(deviceZone) }

    val languages = remember(prefs.languageCode) {
        mergePick(RegionalCatalog.languages, prefs.languageCode, "Language (${prefs.languageCode})")
    }
    val countries = remember(prefs.countryCode) {
        mergePick(RegionalCatalog.countries, prefs.countryCode, "Country (${prefs.countryCode})")
    }
    val currencies = remember(prefs.currencyCode) {
        mergePick(RegionalCatalog.currencies, prefs.currencyCode, "Currency (${prefs.currencyCode})")
    }
    val zones = remember(prefs.timeZoneId, timeZones) {
        mergePick(timeZones, prefs.timeZoneId, prefs.timeZoneId)
    }

    var langOpen by remember { mutableStateOf(false) }
    var countryOpen by remember { mutableStateOf(false) }
    var currencyOpen by remember { mutableStateOf(false) }
    var zoneOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Language & region") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Basic configuration",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Demo only: choices apply for this session in memory (no cloud sync).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            RegionalDropdown(
                label = "Language",
                options = languages,
                selectedCode = prefs.languageCode,
                expanded = langOpen,
                onExpandedChange = { langOpen = it },
                onSelect = { code ->
                    viewModel.setLanguage(code)
                    langOpen = false
                },
            )
            RegionalDropdown(
                label = "Country / region",
                options = countries,
                selectedCode = prefs.countryCode,
                expanded = countryOpen,
                onExpandedChange = { countryOpen = it },
                onSelect = { code ->
                    viewModel.setCountry(code)
                    countryOpen = false
                },
            )
            RegionalDropdown(
                label = "Currency",
                options = currencies,
                selectedCode = prefs.currencyCode,
                expanded = currencyOpen,
                onExpandedChange = { currencyOpen = it },
                onSelect = { code ->
                    viewModel.setCurrency(code)
                    currencyOpen = false
                },
            )
            RegionalDropdown(
                label = "Time zone",
                options = zones,
                selectedCode = prefs.timeZoneId,
                expanded = zoneOpen,
                onExpandedChange = { zoneOpen = it },
                onSelect = { code ->
                    viewModel.setTimeZone(code)
                    zoneOpen = false
                },
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text("Sign out")
            }
        }
    }
}

private fun mergePick(options: List<RegionalPick>, current: String, fallbackLabel: String): List<RegionalPick> {
    if (options.any { it.code.equals(current, ignoreCase = true) }) return options
    return listOf(RegionalPick(current, fallbackLabel)) + options
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegionalDropdown(
    label: String,
    options: List<RegionalPick>,
    selectedCode: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (String) -> Unit,
) {
    val selected = options.find { it.code.equals(selectedCode, ignoreCase = true) } ?: options.first()
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
    ) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
            shape = MaterialTheme.shapes.large,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt.label) },
                    onClick = { onSelect(opt.code) },
                )
            }
        }
    }
}
