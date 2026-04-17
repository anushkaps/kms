package com.institute.ims.ui.examinations

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.model.AssessmentMode
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
        containerColor = Color(0xFFF5F3EE),
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
                onBack = onBack,
                modifier = Modifier.padding(innerPadding),
            )
            else -> Unit
        }
    }
}

@Composable
private fun ReportHeader(
    exam: Exam?,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(136.dp)
            .background(LedgerPalette.Amber),
    ) {
        Text(
            text = "< Exam detail",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier
                .padding(start = 24.dp, top = 8.dp)
                .clickable { onBack() },
        )
        Text(
            text = "Report Center",
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(start = 24.dp, top = 28.dp),
        )
        Text(
            text = exam?.let { "${it.title} · ${it.batchLabel}" } ?: "Exam report",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.65f),
            modifier = Modifier
                .padding(start = 24.dp, top = 64.dp)
                .fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier.padding(start = 24.dp, top = 94.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(26.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = exam?.assessmentMode?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Automated",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(26.dp)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Quick",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.55f),
                    textAlign = TextAlign.Center,
                )
            }
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(26.dp)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Detail",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.55f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
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
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        item { ReportHeader(exam = exam, onBack = onBack) }
        if (analytics == null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text = "No results entered yet for this exam.",
                        modifier = Modifier.padding(14.dp),
                        fontSize = 13.sp,
                        color = Color(0xFF6E6A62),
                    )
                }
            }
        } else {
            if (exam.assessmentMode == AssessmentMode.CUSTOM) {
                item { CustomExamSummaryCard(exam = exam) }
            }
            item {
                Text(
                    text = "SUMMARY",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6E6A62),
                    modifier = Modifier.padding(start = 24.dp, top = 20.dp, bottom = 8.dp),
                )
            }
            item {
                TightStatsGrid(
                    analytics = analytics,
                    exam = exam,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }
            item {
                GradeDistributionCard(
                    analytics = analytics,
                    exam = exam,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                )
            }
            item {
                Text(
                    text = "TOP RESULTS",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6E6A62),
                    modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
                )
            }
            item {
                TopResultsCard(
                    results = topResults,
                    exam = exam,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }
            item {
                ExportButton(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp))
            }
        }
    }
}

@Composable
private fun CustomExamSummaryCard(exam: Exam) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "CUSTOM SCHEME",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = LedgerPalette.Amber,
            )
            Text(
                text = exam.customSchemeName ?: "—",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1814),
            )
            Text(
                text = exam.customCriteriaSummary ?: "—",
                fontSize = 11.sp,
                color = Color(0xFF6E6A62),
            )
        }
    }
}

@Composable
private fun TightStatsGrid(analytics: ExamAnalytics, exam: Exam, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            StatMiniCard("Appeared", analytics.totalStudents.toString(), Modifier.weight(1f))
            StatMiniCard(
                when (exam.assessmentMode) {
                    AssessmentMode.MARKS -> "Pass rate"
                    AssessmentMode.GRADE_BASED -> "Pass rate"
                    AssessmentMode.CUSTOM -> "At/above bar"
                },
                analytics.passPercentage?.let { ExamAnalyticsCalculator.formatPercent(it) } ?: "-",
                Modifier.weight(1f),
                accent = LedgerPalette.Forest,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            when (exam.assessmentMode) {
                AssessmentMode.MARKS -> {
                    StatMiniCard(
                        "Average",
                        ExamAnalyticsCalculator.formatOneDecimal(analytics.averageMarks),
                        Modifier.weight(1f),
                        suffix = "Avg score",
                    )
                    StatMiniCard(
                        "Highest",
                        ExamAnalyticsCalculator.formatOneDecimal(analytics.highestMarks),
                        Modifier.weight(1f),
                        suffix = "Lowest: ${ExamAnalyticsCalculator.formatOneDecimal(analytics.lowestMarks)}",
                        suffixColor = Color(0xFFC0352B),
                    )
                }
                AssessmentMode.GRADE_BASED -> {
                    StatMiniCard(
                        "Mean scale",
                        ExamAnalyticsCalculator.formatOneDecimal(analytics.averageMarks),
                        Modifier.weight(1f),
                        suffix = "Mapped",
                    )
                    StatMiniCard(
                        "Top scale",
                        ExamAnalyticsCalculator.formatOneDecimal(analytics.highestMarks),
                        Modifier.weight(1f),
                        suffix = "Bottom: ${ExamAnalyticsCalculator.formatOneDecimal(analytics.lowestMarks)}",
                        suffixColor = Color(0xFFC0352B),
                    )
                }
                AssessmentMode.CUSTOM -> {
                    StatMiniCard(
                        "Mean rubric",
                        ExamAnalyticsCalculator.formatOneDecimal(analytics.averageMarks),
                        Modifier.weight(1f),
                        suffix = "Points",
                    )
                    StatMiniCard(
                        "Range",
                        "${ExamAnalyticsCalculator.formatOneDecimal(analytics.lowestMarks)}–${ExamAnalyticsCalculator.formatOneDecimal(analytics.highestMarks)}",
                        Modifier.weight(1f),
                        suffix = "Low–high",
                    )
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
    accent: Color = Color(0xFF1A1814),
    suffix: String? = null,
    suffixColor: Color? = null,
) {
    Card(
        modifier = modifier.height(60.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = accent,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = label,
                    fontSize = 9.sp,
                    color = Color(0xFF6E6A62),
                )
                if (suffix != null) {
                    Text(
                        text = suffix,
                        fontSize = 9.sp,
                        color = suffixColor ?: Color(0xFF6E6A62),
                    )
                }
            }
        }
    }
}

@Composable
private fun GradeDistributionCard(analytics: ExamAnalytics, exam: Exam, modifier: Modifier = Modifier) {
    val (title, bars) = when (exam.assessmentMode) {
        AssessmentMode.MARKS -> {
            val bs = analytics.buckets.map { it.label to it.count }
            "Score bands · % of max" to if (bs.isNotEmpty()) bs else ExamAnalyticsCalculator.emptyDistributionPreview().map { it.label to it.count }
        }
        AssessmentMode.GRADE_BASED -> {
            val g = analytics.gradeBreakdown.take(6)
            "Grade distribution · ${exam.evaluationType.name}" to if (g.isNotEmpty()) g else listOf("—" to 0)
        }
        AssessmentMode.CUSTOM -> {
            val bs = analytics.buckets.map { it.label to it.count }
            val fallback = analytics.gradeBreakdown.take(6)
            "Rubric spread · % of cap" to if (bs.isNotEmpty()) bs else if (fallback.isNotEmpty()) fallback else listOf("—" to 0)
        }
    }
    val maxCount = bars.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = LedgerPalette.Amber,
            )
            HorizontalDivider(color = Color(0xFFEEECE5))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                bars.forEachIndexed { index, (label, count) ->
                    val ratio = count.toFloat() / maxCount.toFloat()
                    val isLast = index == bars.lastIndex
                    val barColor = if (isLast && exam.assessmentMode == AssessmentMode.MARKS) {
                        Color(0xFFD4CFC5)
                    } else {
                        LedgerPalette.Plum.copy(alpha = (1.0f - index * 0.16f).coerceIn(0.2f, 1.0f))
                    }
                    val barHeight = (68.dp * ratio).coerceAtLeast(4.dp)
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                    ) {
                        Text(
                            text = count.toString(),
                            fontSize = 9.sp,
                            color = Color(0xFF6E6A62),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .height(barHeight)
                                .fillMaxWidth()
                                .background(barColor, RoundedCornerShape(3.dp)),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF6E6A62),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopResultsCard(results: List<ExamResult>, exam: Exam, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp),
        ) {
            results.forEachIndexed { idx, row ->
                if (idx != 0) {
                    HorizontalDivider(
                        color = Color(0xFFEEECE5),
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = (idx + 1).toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFD4CFC5),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = row.studentName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1814),
                        )
                        Text(
                            text = row.studentNumber,
                            fontSize = 10.sp,
                            color = Color(0xFF6E6A62),
                        )
                    }
                    Text(
                        text = row.score.toInt().toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1814),
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(36.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    GradePill(grade = row.gradeLabel)
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
        Color(0xFFEAF4EF) to LedgerPalette.Forest
    } else {
        Color(0xFFF3EDF9) to LedgerPalette.Plum
    }
    Box(
        modifier = Modifier
            .width(36.dp)
            .height(20.dp)
            .background(bg, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ExportButton(modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = { /* submission prototype: no export */ },
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        border = BorderStroke(1.dp, LedgerPalette.Amber),
        shape = RoundedCornerShape(8.dp),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
        ),
    ) {
        Text(
            text = "Export Report  →",
            color = LedgerPalette.Amber,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
