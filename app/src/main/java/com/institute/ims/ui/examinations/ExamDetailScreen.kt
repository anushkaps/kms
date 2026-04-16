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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.model.Exam
import com.institute.ims.data.model.ExamResult
import com.institute.ims.data.model.uiLabel
import com.institute.ims.ui.common.LedgerPalette

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
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            SummaryHeader(
                exam = exam,
                groupName = groupName,
                resultsCount = results.size,
                onBack = onBack,
            )
        }
        item { ExamDetailCard(exam = exam, groupName = groupName) }
        item { ResultSectionCard(results = results, onOpenReport = onOpenReport) }
        item {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, LedgerPalette.Plum),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = LedgerPalette.Plum),
            ) {
                Text("+ Add Student Results")
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LedgerPalette.Plum)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.padding(end = 2.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
                Text(
                    text = "Examinations",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.6f),
                )
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.White.copy(alpha = 0.15f),
            ) {
                Text(
                    text = "Edit",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                )
            }
        }

        Text(
            text = exam.title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
        )

        Text(
            text = "${groupName ?: exam.groupId} · ${exam.batchLabel}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.65f),
            maxLines = 1,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HeaderChip(exam.scheduleLabel)
            HeaderChip(exam.assessmentMode.uiLabel())
            HeaderChip(exam.evaluationType.name)
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color.White.copy(alpha = 0.35f),
            content = {},
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            HeaderStat(
                value = exam.maxScore.toInt().toString(),
                label = "Max marks",
                modifier = Modifier.weight(1f),
            )
            Surface(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .height(40.dp)
                    .width(1.dp),
                color = Color.White.copy(alpha = 0.35f),
                content = {},
            )
            HeaderStat(
                value = ((exam.maxScore * 0.4).toInt()).toString(),
                label = "Pass",
                modifier = Modifier.weight(1f),
            )
            Surface(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .height(40.dp)
                    .width(1.dp),
                color = Color.White.copy(alpha = 0.35f),
                content = {},
            )
            HeaderStat(
                value = resultsCount.toString(),
                label = "Results",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HeaderChip(text: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.2f)) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun HeaderStat(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun ExamDetailCard(exam: Exam, groupName: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "EXAM DETAILS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = LedgerPalette.Plum,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            DetailLine("Group", groupName ?: exam.groupId)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            DetailLine("Total marks", exam.maxScore.toInt().toString())
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            DetailLine("Pass threshold", "${(exam.maxScore * 0.4).toInt()} marks")
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            DetailLine("Evaluation", exam.evaluationType.name)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            DetailLine("Batch", exam.batchLabel)
        }
    }
}

@Composable
private fun ResultSectionCard(results: List<ExamResult>, onOpenReport: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "RESULTS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = LedgerPalette.Plum,
                )
                Text(
                    text = "View report  →",
                    style = MaterialTheme.typography.labelMedium,
                    color = LedgerPalette.Amber,
                    modifier = Modifier.clickable(onClick = onOpenReport),
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            if (results.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "No results entered yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "Add marks to generate a report",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                results.take(3).forEachIndexed { index, row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = row.studentName, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = row.score.toInt().toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    if (index < results.take(3).lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
    }
}
