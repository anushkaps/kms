package com.institute.ims.ui.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.model.DashboardCapabilityAction
import com.institute.ims.data.model.DashboardCapabilityHighlight
import com.institute.ims.data.model.DashboardStat
import com.institute.ims.data.model.NewsItem
import com.institute.ims.data.model.UserRole
import com.institute.ims.ui.common.LedgerPalette
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userId: String,
    onOpenStudents: () -> Unit,
    onOpenExams: () -> Unit,
    onOpenStudentProfile: (studentId: String) -> Unit,
    onOpenExamDetail: (examId: String) -> Unit,
    onOpenRegionalSettings: () -> Unit,
    onOpenCapabilityInfo: (stubId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: DashboardViewModel = viewModel(
        key = userId,
        factory = DashboardViewModel.Factory(userId),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val recentSuggestions = remember { mutableStateListOf<DashboardNavSuggestion>() }
    var searchPaletteVisible by rememberSaveable { mutableStateOf(false) }

    val queryTrimmed = uiState.searchQuery.trim()
    val navSuggestions = remember(uiState.searchQuery, uiState.news) {
        viewModel.navigationSuggestions()
    }

    val displayedNews = remember(uiState.news, uiState.newsSpotlightId, queryTrimmed) {
        when {
            uiState.newsSpotlightId != null ->
                uiState.news.filter { it.id == uiState.newsSpotlightId }
            queryTrimmed.isEmpty() -> uiState.news
            else ->
                uiState.news.filter { item ->
                    item.title.contains(queryTrimmed, ignoreCase = true) ||
                        item.body.contains(queryTrimmed, ignoreCase = true) ||
                        (item.tag?.contains(queryTrimmed, ignoreCase = true) == true)
                }
        }
    }
    BackHandler(enabled = searchPaletteVisible) {
        searchPaletteVisible = false
    }

    fun applySuggestion(s: DashboardNavSuggestion) {
        focusManager.clearFocus()
        when (val a = s.action) {
            DashboardNavAction.OpenStudentDirectory -> {
                viewModel.onSearchQueryChange("")
                onOpenStudents()
            }
            DashboardNavAction.OpenExamList -> {
                viewModel.onSearchQueryChange("")
                onOpenExams()
            }
            is DashboardNavAction.OpenStudentProfile -> {
                viewModel.onSearchQueryChange("")
                onOpenStudentProfile(a.studentId)
            }
            is DashboardNavAction.OpenExamDetail -> {
                viewModel.onSearchQueryChange("")
                onOpenExamDetail(a.examId)
            }
            is DashboardNavAction.FocusNews -> {
                viewModel.spotlightNews(a.newsId)
            }
            DashboardNavAction.OpenRegionalSettings -> {
                viewModel.onSearchQueryChange("")
                onOpenRegionalSettings()
            }
        }
    }

    fun applySuggestionFromPalette(suggestion: DashboardNavSuggestion) {
        recentSuggestions.removeAll { it.id == suggestion.id }
        recentSuggestions.add(0, suggestion)
        if (recentSuggestions.size > 4) {
            recentSuggestions.subList(4, recentSuggestions.size).clear()
        }
        searchPaletteVisible = false
        applySuggestion(suggestion)
    }

    fun applyCapabilityAction(action: DashboardCapabilityAction) {
        focusManager.clearFocus()
        when (action) {
            DashboardCapabilityAction.OPEN_STUDENTS -> onOpenStudents()
            DashboardCapabilityAction.OPEN_EXAMS -> onOpenExams()
            DashboardCapabilityAction.OPEN_REGIONAL -> onOpenRegionalSettings()
            DashboardCapabilityAction.OPEN_ADMISSION_SMS_INFO ->
                onOpenCapabilityInfo("admission_sms")
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = 28.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            item {
                DashboardHeader(
                    displayName = uiState.displayName,
                    role = uiState.role,
                    initials = uiState.userInitials,
                    greetingLine = uiState.greetingLine,
                    instituteSubtitle = uiState.instituteSubtitle,
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { searchPaletteVisible = true },
                        shape = MaterialTheme.shapes.extraLarge,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = uiState.searchQuery.ifBlank { "Search students, exams..." },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = "Open",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    Text(
                        text = "Command palette for quick jump navigation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp),
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    uiState.quickChips.forEach { label ->
                        SuggestionChip(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.onQuickChipClick(
                                    label = label,
                                    onOpenStudents = onOpenStudents,
                                    onOpenExams = onOpenExams,
                                )
                            },
                            label = { Text(label) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                            border = null,
                        )
                    }
                }
            }

            item {
                RegionalSettingsSummaryCard(
                    summaryLine = uiState.regionalSummaryLine,
                    onOpenSettings = {
                        focusManager.clearFocus()
                        onOpenRegionalSettings()
                    },
                )
            }

            item { SectionTitle("Modules") }

            items(uiState.modules, key = { it.id }) { card ->
                ElevatedCard(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.onModuleClick(
                            id = card.id,
                            onOpenStudents = onOpenStudents,
                            onOpenExams = onOpenExams,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = moduleAccent(card.id.name),
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = Color.White.copy(alpha = 0.16f),
                            modifier = Modifier.size(48.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = moduleIcon(card.id.name),
                                    contentDescription = null,
                                    tint = moduleAccentContent(card.id.name),
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = card.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = moduleAccentContent(card.id.name),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = card.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = moduleAccentContent(card.id.name).copy(alpha = 0.9f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = moduleAccentContent(card.id.name).copy(alpha = 0.75f),
                        )
                    }
                }
            }

            item {
                SectionTitle("Institute capabilities (full IMS scope)")
                Text(
                    text = "Reference cards for everything the hub is meant to represent. Interactive flows are linked where this prototype implements them.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            items(
                uiState.capabilityHighlights,
                key = { it.id },
            ) { cap ->
                CapabilityHighlightCard(
                    highlight = cap,
                    onAction = cap.action?.let { act ->
                        { applyCapabilityAction(act) }
                    },
                )
            }

            item {
                SectionTitle("Overview")
                val topStats = uiState.stats.take(4)
                val rowA = topStats.take(2)
                val rowB = topStats.drop(2).take(2)
                if (rowA.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        rowA.forEach { stat ->
                            DashboardStatMiniCard(
                                stat = stat,
                                icon = statIcon(stat.id),
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (rowA.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                if (rowB.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        rowB.forEach { stat ->
                            DashboardStatMiniCard(
                                stat = stat,
                                icon = statIcon(stat.id),
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (rowB.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                SectionTitle("Today")
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    ),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Today,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = uiState.overviewLine,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "LATEST NEWS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (uiState.newsSpotlightId != null) {
                        TextButton(onClick = { viewModel.clearNewsSpotlight() }) {
                            Text("Show all news")
                        }
                    }
                }
            }

            if (queryTrimmed.isNotEmpty() && uiState.newsSpotlightId == null) {
                item {
                    Text(
                        text = "${displayedNews.size} news match(es) for \"$queryTrimmed\"",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
            }

            items(displayedNews, key = { it.id }) { news ->
                NewsRowCard(newsItem = news)
            }

            if (displayedNews.isEmpty() && uiState.news.isNotEmpty()) {
                item {
                    Text(
                        text = "No news matches your filters. Clear hub search or tap Show all news.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                }
            }
        }
        AnimatedVisibility(visible = searchPaletteVisible) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Search students, exams, news") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                            )
                        },
                        trailingIcon = {
                            TextButton(onClick = { searchPaletteVisible = false }) {
                                Text("Done")
                            }
                        },
                        singleLine = true,
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        if (recentSuggestions.isNotEmpty()) {
                            item { SectionTitle("Recent") }
                            item {
                                HubSearchSuggestionsCard(
                                    suggestions = recentSuggestions,
                                    onPick = ::applySuggestionFromPalette,
                                )
                            }
                        }
                        item { SectionTitle("Jump to") }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                SuggestionChip(
                                    onClick = {
                                        searchPaletteVisible = false
                                        viewModel.onSearchQueryChange("")
                                        onOpenStudents()
                                    },
                                    label = { Text("Students") },
                                )
                                SuggestionChip(
                                    onClick = {
                                        searchPaletteVisible = false
                                        viewModel.onSearchQueryChange("")
                                        onOpenExams()
                                    },
                                    label = { Text("Exams") },
                                )
                                SuggestionChip(
                                    onClick = {
                                        searchPaletteVisible = false
                                        viewModel.onSearchQueryChange("news")
                                    },
                                    label = { Text("News") },
                                )
                            }
                        }
                        if (uiState.searchQuery.isNotBlank()) {
                            item { SectionTitle("Results") }
                            if (navSuggestions.isEmpty()) {
                                item {
                                    Text(
                                        text = "No matches yet. Try student name, ID, exam title, or module keywords.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            } else {
                                item {
                                    HubSearchSuggestionsCard(
                                        suggestions = navSuggestions,
                                        onPick = ::applySuggestionFromPalette,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CapabilityHighlightCard(
    highlight: DashboardCapabilityHighlight,
    onAction: (() -> Unit)?,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = highlight.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(10.dp))
            highlight.lines.forEach { line ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "\u2022",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            if (onAction != null && highlight.actionLabel != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onAction) {
                    Text(highlight.actionLabel)
                }
            }
        }
    }
}

@Composable
private fun RegionalSettingsSummaryCard(
    summaryLine: String,
    onOpenSettings: () -> Unit,
) {
    ElevatedCard(
        onClick = onOpenSettings,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier.size(44.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Language & region",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = summaryLine.ifBlank { "Tap to set language, country, currency, and time zone." },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Composable
private fun HubSearchSuggestionsCard(
    suggestions: List<DashboardNavSuggestion>,
    onPick: (DashboardNavSuggestion) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            suggestions.forEachIndexed { index, suggestion ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPick(suggestion) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = suggestion.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = suggestion.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    displayName: String,
    role: UserRole?,
    initials: String,
    greetingLine: String,
    instituteSubtitle: String,
) {
    Surface(
        color = LedgerPalette.Ink,
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = instituteSubtitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFA8A090),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF3EEE1),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                role?.let { r ->
                    Text(
                        text = roleLabel(r),
                        style = MaterialTheme.typography.labelMedium,
                        color = LedgerPalette.Cobalt,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = greetingLine,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD3CCBC),
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
    )
}

@Composable
private fun DashboardStatMiniCard(
    stat: DashboardStat,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = stat.value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            stat.caption?.let { cap ->
                Text(
                    text = cap,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun NewsRowCard(newsItem: NewsItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                newsItem.tag?.let { tag ->
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
                Text(
                    text = formatNewsDate(newsItem.publishedAtEpochMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = newsSummary(newsItem.body),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun newsSummary(body: String): String {
    val line = body.lineSequence().firstOrNull().orEmpty().trim()
    return if (line.length > 140) line.take(140).trimEnd() + "…" else line
}

private fun formatNewsDate(epochMs: Long): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
    val date = Instant.ofEpochMilli(epochMs).atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(formatter)
}

private fun roleLabel(role: UserRole): String = when (role) {
    UserRole.ADMIN -> "Administrator"
    UserRole.FACULTY -> "Faculty"
}

private fun statIcon(statId: String): ImageVector = when (statId) {
    "students" -> Icons.Outlined.People
    "batches" -> Icons.Outlined.Groups
    "exams" -> Icons.Outlined.Schedule
    "reports" -> Icons.AutoMirrored.Outlined.Assignment
    "pending_approval" -> Icons.Outlined.HourglassTop
    "published_reports" -> Icons.AutoMirrored.Outlined.Assignment
    "grading_queue" -> Icons.Outlined.EditNote
    "upcoming_exams" -> Icons.Outlined.Schedule
    else -> Icons.Outlined.Newspaper
}

private fun moduleIcon(moduleIdName: String): ImageVector = when (moduleIdName) {
    "STUDENTS" -> Icons.Outlined.School
    else -> Icons.AutoMirrored.Outlined.Assignment
}

private fun moduleAccent(moduleIdName: String): Color = when (moduleIdName) {
    "STUDENTS" -> LedgerPalette.Forest
    "EXAMS" -> LedgerPalette.Plum
    "REPORTS" -> LedgerPalette.Amber
    else -> LedgerPalette.Cobalt
}

private fun moduleAccentContent(moduleIdName: String): Color {
    return when (moduleIdName) {
        "REPORTS" -> Color.White
        else -> Color.White
    }
}
