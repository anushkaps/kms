@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.institute.ims.ui.dashboard

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.model.DashboardCapabilityHighlight
import com.institute.ims.data.model.DashboardStat
import com.institute.ims.data.model.DemoNotification
import com.institute.ims.data.model.NewsItem
import com.institute.ims.data.model.UserRole
import com.institute.ims.data.repository.FakeNotificationRepository
import com.institute.ims.ui.common.LedgerPalette
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DashboardScreen(
    userId: String,
    onSignOut: () -> Unit,
    onSwitchRole: () -> Unit,
    onOpenStudents: () -> Unit,
    onOpenExams: () -> Unit,
    onOpenNews: (query: String) -> Unit,
    onOpenStudentProfile: (studentId: String) -> Unit,
    onOpenExamDetail: (examId: String) -> Unit,
    onOpenRegionalSettings: () -> Unit,
    onOpenCapabilityInfo: (stubId: String) -> Unit,
    onOpenSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: DashboardViewModel = viewModel(
        key = userId,
        factory = DashboardViewModel.Factory(userId),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAccountSheet by remember { mutableStateOf(false) }
    var showNotificationsSheet by remember { mutableStateOf(false) }
    var selectedNewsDetail by remember { mutableStateOf<NewsItem?>(null) }
    var statExplainer by remember { mutableStateOf<DashboardStat?>(null) }

    val displayedNews = remember(uiState.news, uiState.newsSpotlightId) {
        when {
            uiState.newsSpotlightId != null ->
                uiState.news.filter { it.id == uiState.newsSpotlightId }
            else -> uiState.news
        }
    }

    fun onStatCardClick(stat: DashboardStat) {
        when (stat.id) {
            "students" -> onOpenStudents()
            "exams", "upcoming_exams" -> onOpenExams()
            "reports", "grading_queue", "pending_approval" -> {
                showNotificationsSheet = true
            }
            "batches" -> {
                statExplainer = stat
            }
            else -> statExplainer = stat
        }
    }

    val accountSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val notificationsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val newsDetailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val statSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LedgerPalette.Ink,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 0.dp,
                    bottom = 28.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(156.dp),
                        ) {
                            DashboardHeader(
                                displayName = uiState.displayName,
                                role = uiState.role,
                                initials = uiState.userInitials,
                                timeOfDayGreeting = uiState.timeOfDayGreeting,
                                roleContextLine = uiState.roleContextLine,
                                instituteSubtitle = uiState.instituteSubtitle,
                                onProfileClick = { showAccountSheet = true },
                                onOpenSettings = { onOpenRegionalSettings() },
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth(),
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .clickable { onOpenSearch() },
                                    shape = RoundedCornerShape(26.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Search,
                                            contentDescription = null,
                                            tint = Color(0xFFD4CFC5),
                                            modifier = Modifier.size(18.dp),
                                        )
                                        Text(
                                            text = "Search students, exams, news...",
                                            fontSize = 14.sp,
                                            color = Color(0xFF888780),
                                            modifier = Modifier.weight(1f),
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFF5F3EE),
                                            border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
                                        ) {
                                            Text(
                                                text = "⌘K",
                                                fontSize = 9.sp,
                                                color = Color(0xFF888780),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    TodayRow(
                        overviewLine = uiState.overviewLine,
                        onClick = { viewModel.refreshHubSummary() },
                    )
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

                item {
                    DashboardNewsCard(
                        newsItems = displayedNews.take(4),
                        onNewsClick = { item -> selectedNewsDetail = item },
                    )
                }
            }

        }
    }

    if (showAccountSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAccountSheet = false },
            sheetState = accountSheetState,
        ) {
            AccountSheetContent(
                displayName = uiState.displayName,
                role = uiState.role,
                onSwitchRole = {
                    showAccountSheet = false
                    onSwitchRole()
                },
                onSignOut = {
                    showAccountSheet = false
                    onSignOut()
                },
                onClose = { showAccountSheet = false },
            )
        }
    } else if (showNotificationsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNotificationsSheet = false },
            sheetState = notificationsSheetState,
        ) {
            NotificationsSheetContent(
                items = FakeNotificationRepository.notifications(),
                onItemClick = { FakeNotificationRepository.markRead(it.id) },
                onMarkAllRead = {
                    FakeNotificationRepository.markAllRead()
                    showNotificationsSheet = false
                },
                onClose = { showNotificationsSheet = false },
            )
        }
    } else if (selectedNewsDetail != null) {
        val news = selectedNewsDetail!!
        ModalBottomSheet(
            onDismissRequest = { selectedNewsDetail = null },
            sheetState = newsDetailSheetState,
        ) {
            NewsDetailSheetContent(
                news = news,
                onClose = { selectedNewsDetail = null },
            )
        }
    } else if (statExplainer != null) {
        val stat = statExplainer!!
        ModalBottomSheet(
            onDismissRequest = { statExplainer = null },
            sheetState = statSheetState,
        ) {
            StatExplainerSheetContent(
                stat = stat,
                onClose = { statExplainer = null },
            )
        }
    }
}

@Composable
internal fun InlineSearchResultsCard(
    groups: List<Pair<String, List<DashboardNavSuggestion>>>,
    onPick: (DashboardNavSuggestion) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
    ) {
        Column(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
        ) {
            groups.forEach { (label, suggestions) ->
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6E6A62),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
                HubSearchSuggestionsCard(
                    suggestions = suggestions,
                    onPick = onPick,
                )
            }
        }
    }
}

@Composable
private fun AccountSheetContent(
    displayName: String,
    role: UserRole?,
    onSwitchRole: () -> Unit,
    onSignOut: () -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 8.dp),
    ) {
        Text("Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = role?.let(::roleLabel) ?: "Signed in",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onSwitchRole, modifier = Modifier.fillMaxWidth()) {
            Text("Switch demo role")
        }
        TextButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth()) {
            Text("Sign out")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text("Close")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun NotificationsSheetContent(
    items: List<DemoNotification>,
    onItemClick: (DemoNotification) -> Unit,
    onMarkAllRead: () -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 8.dp),
    ) {
        Text("Notifications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        items.forEach { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item) }
                    .padding(vertical = 10.dp),
            ) {
                Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    item.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    item.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                HorizontalDivider(modifier = Modifier.padding(top = 10.dp))
            }
        }
        TextButton(onClick = onMarkAllRead, modifier = Modifier.fillMaxWidth()) {
            Text("Mark all as read")
        }
        TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text("Close")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
internal fun NewsDetailSheetContent(
    news: NewsItem,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 8.dp),
    ) {
        news.tag?.let { tag ->
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFEEF2FB),
            ) {
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = LedgerPalette.Cobalt,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(
            text = formatNewsDate(news.publishedAtEpochMs),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF6E6A62),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(news.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(news.body, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text("Close")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatExplainerSheetContent(
    stat: DashboardStat,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 8.dp),
    ) {
        Text(stat.value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        Text(stat.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        stat.caption?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "This value is computed from the local seed data used across the demo (students, exams, batches).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text("Close")
        }
        Spacer(modifier = Modifier.height(16.dp))
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
internal fun HubSearchSuggestionsCard(
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
private fun TodayRow(
    overviewLine: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
        onClick = onClick,
        modifier = modifier.height(100.dp),
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
private fun DashboardNewsCard(
    newsItems: List<NewsItem>,
    onNewsClick: (NewsItem) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            newsItems.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(color = Color(0xFFEEECE5))
                }
                NewsRowLine(item = item, onClick = { onNewsClick(item) })
            }
        }
    }
}

@Composable
private fun NewsRowLine(
    item: NewsItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
    timeOfDayGreeting: String,
    roleContextLine: String,
    instituteSubtitle: String,
    onProfileClick: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(136.dp)
            .background(LedgerPalette.Ink),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 12.dp)
                .clickable(onClick = onProfileClick),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
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
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = role?.let(::roleLabel) ?: instituteSubtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6E6A62),
                )
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFF5F3EE),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp, top = 14.dp)
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2C2B27))
                .clickable(onClick = onOpenSettings),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = Color(0xFFD4CFC5),
                modifier = Modifier.size(16.dp),
            )
        }
        Column(
            modifier = Modifier.padding(start = 24.dp, top = 62.dp, end = 24.dp),
        ) {
            Text(
                text = timeOfDayGreeting.ifBlank { "Good morning." },
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFF5F3EE),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (roleContextLine.isNotBlank()) {
                Text(
                    text = roleContextLine,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFD4CFC5),
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
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
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
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
