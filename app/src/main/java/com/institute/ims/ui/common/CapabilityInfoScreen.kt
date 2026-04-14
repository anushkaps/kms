package com.institute.ims.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapabilityInfoScreen(
    stubId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (title, paragraphs) = capabilityCopy(stubId)
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(title) },
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
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(paragraphs, key = { it.hashCode() }) { paragraph ->
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

private fun capabilityCopy(stubId: String): Pair<String, List<String>> = when (stubId) {
    "admission_sms" -> Pair(
        "Admissions & SMS",
        listOf(
            "A production IMS ties configurable admission forms to your intake pipeline: field rules, document uploads, fee steps, and workflow states.",
            "The SMS module sends templated messages to one student or a broadcast list (exam venue change, fee due, emergency closure). It connects to your SMS provider or campus gateway.",
            "This prototype does not send real texts or save form blueprints; it only documents where those capabilities sit relative to the dashboard, Student Details, and Examinations you can demo today.",
        ),
    )
    else -> Pair(
        "Institute module",
        listOf(
            "No extra copy is registered for this key in the demo app.",
        ),
    )
}
