package com.institute.ims.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.institute.ims.data.model.NewsItem
import com.institute.ims.data.repository.FakeNewsRepository
import com.institute.ims.ui.common.LedgerPalette
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    initialQuery: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember(initialQuery) { mutableStateOf(initialQuery) }
    val newsItems = remember { FakeNewsRepository.getNews().sortedByDescending { it.publishedAtEpochMs } }
    val filtered = remember(newsItems, query) {
        val q = query.trim()
        if (q.isBlank()) {
            newsItems
        } else {
            newsItems.filter { item ->
                item.title.contains(q, ignoreCase = true) ||
                    item.body.contains(q, ignoreCase = true) ||
                    (item.tag?.contains(q, ignoreCase = true) == true)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Latest news") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LedgerPalette.Cobalt,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search latest news...") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LedgerPalette.Cobalt,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    ),
                )
            }

            if (filtered.isEmpty()) {
                item {
                    Text(
                        text = "No news items match this search.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            } else {
                items(filtered, key = { it.id }) { item ->
                    NewsDetailCard(item)
                }
            }
        }
    }
}

@Composable
private fun NewsDetailCard(item: NewsItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = formatNewsDate(item.publishedAtEpochMs),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            item.tag?.let { tag ->
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = LedgerPalette.Cobalt.copy(alpha = 0.12f),
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        color = LedgerPalette.Cobalt,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
            Text(
                text = item.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatNewsDate(epochMs: Long): String {
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
    val date = Instant.ofEpochMilli(epochMs).atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(formatter)
}
