package com.institute.ims.ui.examinations

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.model.AssessmentMode
import com.institute.ims.data.model.Exam
import com.institute.ims.data.model.ExamAnalytics
import com.institute.ims.data.model.ExamStatus
import com.institute.ims.data.model.uiLabel
import com.institute.ims.data.model.ScoreBucket
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
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Report center",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = state.exam?.title ?: "Report",
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
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
                groupName = state.groupName,
                analytics = state.analytics,
                selectedTab = state.selectedTab,
                onTabChange = viewModel::onReportTabChange,
                modifier = Modifier.padding(innerPadding),
            )
            else -> Spacer(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
private fun ReportNotFoundBody(
    examId: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Exam not found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No exam record for \"$examId\". Go back and choose an exam from the list.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ReportBody(
    exam: Exam,
    groupName: String?,
    analytics: ExamAnalytics?,
    selectedTab: ReportCenterTab,
    onTabChange: (ReportCenterTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text(
                text = "On-demand reports — pick a view below. All sections use the same local results snapshot.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            ReportMetadataCard(exam = exam, groupName = groupName)
        }
        item {
            ReportCenterTabRow(
                selected = selectedTab,
                onSelect = onTabChange,
            )
        }
        when (selectedTab) {
            ReportCenterTab.QUICK_SUMMARY -> {
                item {
                    SectionTitle("Quick summary")
                    ReportSummaryCard(
                        exam = exam,
                        resultCount = analytics?.totalStudents ?: 0,
                    )
                }
                item {
                    if (analytics == null) {
                        ReportEmptyQuickStats()
                    } else {
                        ReportQuickStatsGrid(analytics = analytics)
                    }
                }
            }
            ReportCenterTab.PERFORMANCE_OVERVIEW -> {
                item {
                    SectionTitle("Performance overview")
                    Text(
                        text = "Grade mix and cohort outcomes for this exam.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                item {
                    if (analytics == null) {
                        ReportEmptyQuickStats()
                    } else {
                        ReportPerformanceOverview(exam = exam, analytics = analytics)
                    }
                }
            }
            ReportCenterTab.RESULT_DISTRIBUTION -> {
                item {
                    SectionTitle("Result distribution")
                    Text(
                        text = "Score buckets as % of max marks (automated from recorded results).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                item {
                    if (analytics == null) {
                        ReportDistributionEmpty()
                    } else {
                        ReportDistributionSection(exam = exam, analytics = analytics)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 6.dp),
    )
}

@Composable
private fun ReportMetadataCard(
    exam: Exam,
    groupName: String?,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = exam.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(percent = 50),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Text(
                        text = exam.evaluationType.name,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(percent = 50),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = reportStatusLabel(exam.status),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            ReportDetailLine("Exam category", exam.examType)
            ReportDetailLine("Assessment mode", exam.assessmentMode.uiLabel())
            ReportDetailLine("Subject", exam.subjectName)
            ReportDetailLine("Batch", exam.batchLabel)
            ReportDetailLine("Group", groupName ?: exam.groupId)
            ReportDetailLine("Schedule", exam.scheduleLabel)
            ReportDetailLine("Evaluation type", exam.evaluationType.name)
        }
    }
}

@Composable
private fun ReportDetailLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ReportSummaryCard(
    exam: Exam,
    resultCount: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ReportDetailLine("Results on file", resultCount.toString())
            ReportDetailLine(
                when (exam.assessmentMode) {
                    AssessmentMode.MARKS -> "Max marks"
                    AssessmentMode.GRADE_BASED -> "Grade scale (max pts)"
                    AssessmentMode.CUSTOM -> "Rubric cap (max pts)"
                },
                when {
                    !exam.maxScore.isFinite() -> "Not set"
                    exam.maxScore > 0 -> exam.maxScore.toString()
                    else -> "Not set"
                },
            )
            Text(
                text = "Pass criterion: marks ≥ ${(ExamAnalyticsCalculator.PASS_MARK_FRACTION * 100).toInt()}% of max marks (requires max marks > 0).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ReportCenterTabRow(
    selected: ReportCenterTab,
    onSelect: (ReportCenterTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selected == ReportCenterTab.QUICK_SUMMARY,
            onClick = { onSelect(ReportCenterTab.QUICK_SUMMARY) },
            label = { Text("Quick summary") },
        )
        FilterChip(
            selected = selected == ReportCenterTab.PERFORMANCE_OVERVIEW,
            onClick = { onSelect(ReportCenterTab.PERFORMANCE_OVERVIEW) },
            label = { Text("Performance overview") },
        )
        FilterChip(
            selected = selected == ReportCenterTab.RESULT_DISTRIBUTION,
            onClick = { onSelect(ReportCenterTab.RESULT_DISTRIBUTION) },
            label = { Text("Result distribution") },
        )
    }
}

@Composable
private fun ReportEmptyQuickStats() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        ),
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "No results on file",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Record marks for this exam to populate quick stats, performance breakdown, and distribution charts.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ReportQuickStatsGrid(analytics: ExamAnalytics) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StatMiniCard(
                label = "Average",
                value = ExamAnalyticsCalculator.formatOneDecimal(analytics.averageMarks),
                modifier = Modifier.weight(1f),
            )
            StatMiniCard(
                label = "Highest",
                value = ExamAnalyticsCalculator.formatOneDecimal(analytics.highestMarks),
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StatMiniCard(
                label = "Lowest",
                value = ExamAnalyticsCalculator.formatOneDecimal(analytics.lowestMarks),
                modifier = Modifier.weight(1f),
            )
            StatMiniCard(
                label = "Pass rate",
                value = analytics.passPercentage?.let { ExamAnalyticsCalculator.formatPercent(it) } ?: "—",
                modifier = Modifier.weight(1f),
            )
        }
        Text(
            text = "Students with results: ${analytics.totalStudents}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ReportPerformanceOverview(
    exam: Exam,
    analytics: ExamAnalytics,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ReportQuickStatsGrid(analytics = analytics)
        if (analytics.passedCount != null && analytics.failedCount != null) {
            Text(
                text = "Passed: ${analytics.passedCount} · Not passed: ${analytics.failedCount}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else if (!exam.maxScore.isFinite() || exam.maxScore <= 0) {
            Text(
                text = "Pass counts apply only when max marks is a positive number.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (analytics.gradeBreakdown.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Recorded grade labels",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    analytics.gradeBreakdown.forEach { (label, count) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = label, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = "No letter-grade rows yet — labels appear once marks are stored.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ReportDistributionEmpty() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Mark distribution (% of max)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Preview — bars fill once scores exist.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val preview = ExamAnalyticsCalculator.emptyDistributionPreview()
            preview.forEach { bucket ->
                BucketBarRow(bucket = bucket, maxCount = 1)
            }
        }
    }
}

@Composable
private fun ReportDistributionSection(
    exam: Exam,
    analytics: ExamAnalytics,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (analytics.buckets.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Mark distribution (% of max)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Each student is placed in a bucket by score ÷ max marks.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    val maxBucket = analytics.buckets.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1
                    analytics.buckets.forEach { bucket ->
                        BucketBarRow(bucket = bucket, maxCount = maxBucket)
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
                    ),
                ) {
                    Text(
                        text = "Percent-based distribution needs max marks > 0.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(14.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Mark distribution (% of max)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "Unavailable until max marks is set — bucket layout preview:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        val preview = ExamAnalyticsCalculator.emptyDistributionPreview()
                        preview.forEach { bucket ->
                            BucketBarRow(bucket = bucket, maxCount = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMiniCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun BucketBarRow(
    bucket: ScoreBucket,
    maxCount: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = bucket.label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(72.dp),
        )
        Text(
            text = bucket.count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(28.dp),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(22.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            val frac = if (maxCount > 0) bucket.count.toFloat() / maxCount else 0f
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(frac)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.88f)),
            )
        }
    }
}

private fun reportStatusLabel(status: ExamStatus): String = when (status) {
    ExamStatus.DRAFT -> "Draft"
    ExamStatus.PUBLISHED -> "Published"
    ExamStatus.COMPLETED -> "Completed"
}
