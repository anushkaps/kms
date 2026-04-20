package com.institute.ims.ui.examinations

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.institute.ims.data.model.ExamResult
import com.institute.ims.data.model.ExamStatus
import com.institute.ims.data.model.uiLabel
import com.institute.ims.ui.common.LedgerPalette
import com.institute.ims.utils.ExamAnalyticsCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamDetailScreen(
    examId: String,
    onBack: () -> Unit,
    onOpenReport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ExamDetailViewModel = viewModel(
        key = examId,
        factory = ExamDetailViewModel.Factory(examId),
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        val exam = state.exam
        when {
            state.notFound -> ExamNotFoundBody(
                examId = examId,
                modifier = Modifier.padding(innerPadding),
            )
            exam != null -> ExamDetailBody(
                exam = exam,
                groupName = state.groupName,
                results = state.results,
                onBack = onBack,
                onOpenReport = onOpenReport,
                onPublishExam = { viewModel.publishExam() },
                modifier = Modifier.padding(innerPadding),
            )
            else -> Spacer(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
private fun ExamNotFoundBody(
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
private fun ExamDetailBody(
    exam: Exam,
    groupName: String?,
    results: List<ExamResult>,
    onBack: () -> Unit,
    onOpenReport: () -> Unit,
    onPublishExam: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            SummaryHeader(
                exam = exam,
                groupName = groupName,
                resultsCount = results.size,
                onBack = onBack,
            )
        }
        item {
            ExamDetailCard(
                exam = exam,
                groupName = groupName,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
        item {
            ResultSectionCard(
                results = results,
                onOpenReport = onOpenReport,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
        item {
            OutlinedButton(
                onClick = {
                    if (exam.status == ExamStatus.DRAFT) onPublishExam()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(44.dp),
                border = BorderStroke(1.dp, LedgerPalette.Plum),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = LedgerPalette.Plum),
            ) {
                Text(
                    text = if (exam.status == ExamStatus.DRAFT) "Publish Exam" else "Add Demo Results",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun SummaryHeader(
    exam: Exam,
    groupName: String?,
    resultsCount: Int,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 232.dp)
            .background(LedgerPalette.Plum),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 12.dp),
        ) {
            Text(
                text = "< Examinations",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                lineHeight = 13.sp,
                modifier = Modifier.clickable(onClick = onBack),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = exam.title,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                modifier = Modifier.padding(end = 48.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${groupName ?: exam.groupId} · ${exam.batchLabel}",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 12.sp,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                HeaderChip(exam.scheduleLabel)
                HeaderChip(exam.assessmentMode.uiLabel())
                HeaderChip(exam.evaluationType.name)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
            when (exam.assessmentMode) {
                AssessmentMode.MARKS -> {
                    val passLine = exam.passMarksThreshold?.toInt()
                        ?: (exam.maxScore * ExamAnalyticsCalculator.PASS_MARK_FRACTION).toInt()
                    HeaderStat(
                        value = exam.maxScore.toInt().toString(),
                        label = "Total marks",
                        modifier = Modifier.weight(1f),
                    )
                    HeaderDivider()
                    HeaderStat(
                        value = passLine.toString(),
                        label = "Pass marks",
                        modifier = Modifier.weight(1f),
                    )
                    HeaderDivider()
                }
                AssessmentMode.GRADE_BASED -> {
                    HeaderStat(
                        value = exam.gradeSchemeName ?: "-",
                        label = "Grade scheme",
                        modifier = Modifier.weight(1f),
                        valueMaxLines = 2,
                    )
                    HeaderDivider()
                    HeaderStat(
                        value = exam.passingGradeLabel ?: "-",
                        label = "Passing grade",
                        modifier = Modifier.weight(1f),
                    )
                    HeaderDivider()
                }
                AssessmentMode.CUSTOM -> {
                    HeaderStat(
                        value = exam.customSchemeName ?: "-",
                        label = "Custom scheme",
                        modifier = Modifier.weight(1f),
                        valueMaxLines = 2,
                    )
                    HeaderDivider()
                    HeaderStat(
                        value = exam.customCriteriaSummary ?: "-",
                        label = "Criteria",
                        modifier = Modifier.weight(1f),
                        valueMaxLines = 2,
                    )
                    HeaderDivider()
                }
            }
            HeaderStat(
                value = resultsCount.toString(),
                label = "Results",
                modifier = Modifier.weight(1f),
            )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White),
            )
        }
    }
}

@Composable
private fun HeaderChip(text: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color.White.copy(alpha = 0.18f),
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            maxLines = 1,
        )
    }
}

@Composable
private fun HeaderDivider() {
    Surface(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .height(40.dp)
            .width(1.dp),
        color = Color.White.copy(alpha = 0.35f),
        content = {},
    )
}

@Composable
private fun HeaderStat(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueMaxLines: Int = 1,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = valueMaxLines,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            color = Color.White.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun ExamDetailCard(
    exam: Exam,
    groupName: String?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            Text(
                text = "EXAM DETAILS",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = LedgerPalette.Plum,
            )
            HorizontalDivider(color = Color(0xFFEEECE5), modifier = Modifier.padding(top = 6.dp))
            DetailLine("Assessment group", groupName ?: exam.groupId)
            HorizontalDivider(color = Color(0xFFEEECE5))
            when (exam.assessmentMode) {
                AssessmentMode.MARKS -> {
                    val passLine = exam.passMarksThreshold?.toInt()
                        ?: (exam.maxScore * ExamAnalyticsCalculator.PASS_MARK_FRACTION).toInt()
                    DetailLine("Total marks", exam.maxScore.toInt().toString())
                    HorizontalDivider(color = Color(0xFFEEECE5))
                    DetailLine("Pass marks", passLine.toString())
                    HorizontalDivider(color = Color(0xFFEEECE5))
                }
                AssessmentMode.GRADE_BASED -> {
                    DetailLine("Grade scheme", exam.gradeSchemeName ?: "-")
                    HorizontalDivider(color = Color(0xFFEEECE5))
                    DetailLine("Passing grade", exam.passingGradeLabel ?: "-")
                    HorizontalDivider(color = Color(0xFFEEECE5))
                }
                AssessmentMode.CUSTOM -> {
                    DetailLine("Custom scheme", exam.customSchemeName ?: "-")
                    HorizontalDivider(color = Color(0xFFEEECE5))
                    DetailLine("Criteria summary", exam.customCriteriaSummary ?: "-")
                    HorizontalDivider(color = Color(0xFFEEECE5))
                }
            }
            DetailLine("Evaluation", exam.evaluationType.name)
            HorizontalDivider(color = Color(0xFFEEECE5))
            DetailLine("Batch", exam.batchLabel)
        }
    }
}

@Composable
private fun ResultSectionCard(
    results: List<ExamResult>,
    onOpenReport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "RESULTS",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LedgerPalette.Plum,
                )
                Text(
                    text = "View report →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFB85C00),
                    modifier = Modifier.clickable(onClick = onOpenReport),
                )
            }
            HorizontalDivider(color = Color(0xFFEEECE5), modifier = Modifier.padding(top = 6.dp))
            if (results.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFF5F3EE), RoundedCornerShape(8.dp))
                            .then(
                                Modifier.border(1.dp, Color(0xFFD4CFC5), RoundedCornerShape(8.dp)),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "-",
                            fontSize = 16.sp,
                            color = Color(0xFFD4CFC5),
                        )
                    }
                    Text(
                        text = "No results entered yet",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6E6A62),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "Enter results to generate a report",
                        fontSize = 11.sp,
                        color = Color(0xFF888780),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                results.take(3).forEachIndexed { index, row ->
                    DetailLine(label = row.studentName, value = row.score.toInt().toString())
                    if (index < results.take(3).lastIndex) {
                        HorizontalDivider(color = Color(0xFFEEECE5))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF6E6A62),
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1814),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
        )
    }
}
