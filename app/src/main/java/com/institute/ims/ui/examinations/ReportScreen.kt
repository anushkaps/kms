package com.institute.ims.ui.examinations

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.model.Exam
import com.institute.ims.data.model.ExamAnalytics
import com.institute.ims.data.model.ExamResult
import com.institute.ims.ui.common.LedgerPalette
import com.institute.ims.utils.ExamAnalyticsCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    examId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ReportViewModel = viewModel(
        key = examId,
        factory = ReportViewModel.Factory(examId),
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ReportHeader(
                exam = state.exam,
                selectedTab = state.selectedTab,
                onBack = onBack,
                onTabChange = viewModel::onReportTabChange,
            )
        },
    ) { innerPadding ->
        when {
            state.notFound -> ReportNotFoundBody(
                examId = examId,
                modifier = Modifier.padding(innerPadding),
            )
            state.exam != null -> ReportBody(
                exam = state.exam!!,
                analytics = state.analytics,
                topResults = state.topResults,
                modifier = Modifier.padding(innerPadding),
            )
            else -> Unit
        }
    }
}

@Composable
private fun ReportHeader(
    exam: Exam?,
    selectedTab: ReportCenterTab,
    onBack: () -> Unit,
    onTabChange: (ReportCenterTab) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LedgerPalette.Amber)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Report Center",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = exam?.let { "${it.title} · ${it.batchLabel}" } ?: "Exam report",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.65f),
                )
            }
        }
        // Kept for compatibility: selectedTab exists, but these chips are rendered as evaluation labels.
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val eval = exam?.evaluationType?.name
            HeaderMethodChip(
                text = "${eval ?: "GPA"} method",
                selected = true,
                onClick = { onTabChange(selectedTab) },
            )
            HeaderMethodChip(
                text = "CCE",
                selected = eval == "CCE",
                onClick = { onTabChange(selectedTab) },
            )
            HeaderMethodChip(
                text = "CWA",
                selected = eval == "CWA",
                onClick = { onTabChange(selectedTab) },
            )
        }
    }
}

@Composable
private fun HeaderMethodChip(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color.White.copy(alpha = 0.22f),
            selectedLabelColor = Color.White,
            containerColor = Color.White.copy(alpha = 0.12f),
            labelColor = Color.White.copy(alpha = 0.92f),
        ),
    )
}

@Composable
private fun ReportNotFoundBody(examId: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Exam not found", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text(
            text = "No exam record for \"$examId\".",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ReportBody(
    exam: Exam,
    analytics: ExamAnalytics?,
    topResults: List<ExamResult>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (analytics == null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Text(
                        text = "No results entered yet for this exam.",
                        modifier = Modifier.padding(14.dp),
                    )
                }
            }
        } else {
            item { TightStatsGrid(analytics = analytics) }
            item { GradeDistributionCard(analytics = analytics) }
            item { TopResultsCard(results = topResults) }
            item { ExportButton() }
        }
    }
}

@Composable
private fun TightStatsGrid(analytics: ExamAnalytics) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatMiniCard("Appeared", analytics.totalStudents.toString(), Modifier.weight(1f))
            StatMiniCard(
                "Pass rate",
                analytics.passPercentage?.let { ExamAnalyticsCalculator.formatPercent(it) } ?: "-",
                Modifier.weight(1f),
                accent = LedgerPalette.Forest,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatMiniCard(
                "Average",
                ExamAnalyticsCalculator.formatOneDecimal(analytics.averageMarks),
                Modifier.weight(1f),
                suffix = "Average score",
            )
            StatMiniCard(
                "Highest",
                ExamAnalyticsCalculator.formatOneDecimal(analytics.highestMarks),
                Modifier.weight(1f),
                suffix = "Lowest: ${ExamAnalyticsCalculator.formatOneDecimal(analytics.lowestMarks)}",
            )
        }
    }
}

@Composable
private fun StatMiniCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.onSurface,
    suffix: String? = null,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = accent)
            Text(text = label.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            suffix?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall, color = accent)
            }
        }
    }
}

@Composable
private fun GradeDistributionCard(analytics: ExamAnalytics) {
    val gradeLabels = listOf("O", "A+", "A", "B+", "F")
    val countsByLabel = analytics.gradeBreakdown.toMap()
    val bars = gradeLabels.map { label -> label to (countsByLabel[label] ?: 0) }
    val maxCount = bars.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "GRADE DISTRIBUTION - GPA",
                style = MaterialTheme.typography.labelLarge,
                color = LedgerPalette.Amber,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                bars.forEachIndexed { index, (label, count) ->
                    val ratio = count.toFloat() / maxCount.toFloat()
                    val alpha = (0.95f - (index * 0.17f)).coerceIn(0.2f, 0.95f)
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Box(
                            modifier = Modifier
                                .height(76.dp)
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(3.dp),
                                ),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(ratio)
                                    .fillMaxWidth()
                                    .background(
                                        color = LedgerPalette.Plum.copy(alpha = alpha),
                                        shape = RoundedCornerShape(3.dp),
                                    ),
                            )
                        }
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopResultsCard(results: List<ExamResult>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "TOP RESULTS",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            results.forEachIndexed { idx, row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = (idx + 1).toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.width(18.dp),
                        )
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = row.studentName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = row.studentNumber,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = row.score.toInt().toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        GradePill(grade = row.gradeLabel)
                    }
                }
                if (idx != results.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant),
                    )
                }
            }
        }
    }
}

@Composable
private fun GradePill(grade: String?) {
    val label = grade ?: "—"
    val isGood = label == "A" || label == "A+" || label == "O" || label == "Advanced" || label == "Proficient"
    val (bg, fg) = if (isGood) {
        LedgerPalette.Forest.copy(alpha = 0.12f) to LedgerPalette.Forest
    } else {
        LedgerPalette.Plum.copy(alpha = 0.12f) to LedgerPalette.Plum
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(4.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = fg,
        )
    }
}

@Composable
private fun ExportButton() {
    OutlinedButton(
        onClick = { /* submission prototype: no export */ },
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, LedgerPalette.Amber),
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = "Export Report  →",
            color = LedgerPalette.Amber,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}
