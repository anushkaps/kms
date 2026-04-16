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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
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
import androidx.compose.material3.IconButton
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
    onSignOut: () -> Unit,
    onOpenStudents: () -> Unit,
    onOpenExams: () -> Unit,
    onOpenNews: (query: String) -> Unit,
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

    val navSuggestions = remember(uiState.searchQuery, uiState.news) {
        viewModel.navigationSuggestions()
    }

    val displayedNews = remember(uiState.news, uiState.newsSpotlightId) {
        when {
            uiState.newsSpotlightId != null ->
                uiState.news.filter { it.id == uiState.newsSpotlightId }
            else -> uiState.news
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
            is DashboardNavAction.OpenNews -> {
                viewModel.onSearchQueryChange("")
                onOpenNews(a.query)
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 0.dp,
                bottom = 28.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                ) {
                    DashboardHeader(
                        displayName = uiState.displayName,
                        role = uiState.role,
                        initials = uiState.userInitials,
                        greetingLine = uiState.greetingLine,
                        instituteSubtitle = uiState.instituteSubtitle,
                        onSignOut = onSignOut,
                    )
                    DashboardSearchBar(
                        query = uiState.searchQuery,
                        onClick = { searchPaletteVisible = true },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                    )
                }
            }

            item {
                TodayRow(uiState.overviewLine)
            }

            item {
                Text(
                    text = "OVERVIEW",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6E6A62),
                    fontWeight = FontWeight.SemiBold,
                )
                val topStats = uiState.stats.take(4)
                val rowA = topStats.take(2)
                val rowB = topStats.drop(2).take(2)
                if (rowA.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        rowA.forEachIndexed { index, stat ->
                            DashboardStatMiniCard(
                                stat = stat,
                                iconColor = statDotColor(index),
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (rowA.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                if (rowB.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        rowB.forEachIndexed { index, stat ->
                            DashboardStatMiniCard(
                                stat = stat,
                                iconColor = statDotColor(index + 2),
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
                Text(
                    text = "MODULES",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6E6A62),
                    fontWeight = FontWeight.SemiBold,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    val studentsModule = uiState.modules.firstOrNull { it.id.name == "STUDENTS" }
                    val examsModule = uiState.modules.firstOrNull { it.id.name == "EXAMS" }
                    studentsModule?.let { card ->
                        ModuleTileCard(
                            title = card.title,
                            subtitle = card.description,
                            accent = LedgerPalette.Cobalt,
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.onModuleClick(
                                    id = card.id,
                                    onOpenStudents = onOpenStudents,
                                    onOpenExams = onOpenExams,
                                )
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    examsModule?.let { card ->
                        ModuleTileCard(
                            title = card.title,
                            subtitle = card.description,
                            accent = LedgerPalette.Plum,
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.onModuleClick(
                                    id = card.id,
                                    onOpenStudents = onOpenStudents,
                                    onOpenExams = onOpenExams,
                                )
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "LATEST NEWS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6E6A62),
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "All ->",
                        style = MaterialTheme.typography.labelSmall,
                        color = LedgerPalette.Cobalt,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onOpenNews("") },
                    )
                }
            }

            item { DashboardNewsCard(displayedNews.take(2)) }
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
                                        onOpenNews("")
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
private fun DashboardSearchBar(
    query: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD4CFC5)),
            )
            Text(
                text = query.ifBlank { "Search students, exams, news..." },
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF888780),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFF5F3EE),
                border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
            ) {
                Text(
                    text = "SK",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF888780),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                )
            }
        }
    }
}

@Composable
private fun TodayRow(overviewLine: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color(0xFFEEF2FB),
        ) {
            Text(
                text = "Today",
                style = MaterialTheme.typography.labelSmall,
                color = LedgerPalette.Cobalt,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            )
        }
        Text(
            text = overviewLine,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6E6A62),
        )
    }
}

@Composable
private fun ModuleTileCard(
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = accent),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            if (accent == LedgerPalette.Cobalt) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f)),
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.3f)),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.65f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "->",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

@Composable
private fun DashboardNewsCard(newsItems: List<NewsItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val first = newsItems.getOrNull(0)
            if (first != null) {
                NewsRowLine(first)
            }
            HorizontalDivider(color = Color(0xFFEEECE5))
            val second = newsItems.getOrNull(1)
            if (second != null) {
                NewsRowLine(second)
            }
        }
    }
}

@Composable
private fun NewsRowLine(item: NewsItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatNewsDate(item.publishedAtEpochMs).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6E6A62),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = newsSummary(item.body),
                style = MaterialTheme.typography.bodyMedium,
                color = LedgerPalette.Ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        item.tag?.let { tag ->
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (tag.equals("Admin", true)) Color(0xFFEEF2FB) else Color(0xFFF3EDF9),
            ) {
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (tag.equals("Admin", true)) LedgerPalette.Cobalt else LedgerPalette.Plum,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                )
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
    onSignOut: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(LedgerPalette.Ink),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(LedgerPalette.Ink),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, end = 0.dp, top = 56.dp)
                .padding(horizontal = 0.dp),
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 0.dp),
            )
        }
        Box(
            modifier = Modifier
                .padding(start = 0.dp),
        )
        Box(
            modifier = Modifier
                .padding(start = 24.dp, top = 56.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(LedgerPalette.Cobalt),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Text(
            text = role?.let(::roleLabel) ?: instituteSubtitle,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF6E6A62),
            modifier = Modifier.padding(start = 68.dp, top = 58.dp),
        )
        Text(
            text = displayName,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFF5F3EE),
            modifier = Modifier.padding(start = 68.dp, top = 72.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp, top = 58.dp)
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2C2B27))
                .clickable(onClick = onSignOut),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "Sign out",
                tint = Color(0xFFD4CFC5),
                modifier = Modifier.size(16.dp),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 20.dp, top = 56.dp)
                .size(7.dp)
                .clip(CircleShape)
                .background(Color(0xFFC0352B)),
        )
        Text(
            text = greetingLine.ifBlank { "Good morning." },
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFF5F3EE),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 24.dp, top = 106.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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
    iconColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(iconColor),
            )
            Text(
                text = stat.value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = LedgerPalette.Ink,
                modifier = Modifier.padding(top = 10.dp),
            )
            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6E6A62),
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
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
    UserRole.ADMIN -> "Institute Admin"
    UserRole.FACULTY -> "Faculty Member"
}

private fun statDotColor(index: Int): Color = when (index) {
    0 -> LedgerPalette.Forest
    1 -> LedgerPalette.Plum
    2 -> LedgerPalette.Amber
    else -> LedgerPalette.Cobalt
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
