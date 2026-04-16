package com.institute.ims.ui.examinations

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.model.EvaluationType
import com.institute.ims.data.model.Exam
import com.institute.ims.ui.common.LedgerPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamListScreen(
    onBack: () -> Unit,
    onCreateExam: () -> Unit,
    onOpenExam: (examId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExamListViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var examFilter by rememberSaveable { mutableStateOf(ExamListFilter.ALL) }
    val filteredExams = remember(state.exams, searchQuery, examFilter) {
        val query = searchQuery.trim()
        val byType = state.exams.filter { exam ->
            when (examFilter) {
                ExamListFilter.ALL -> true
                ExamListFilter.SEMESTER -> exam.examType.equals("Final", ignoreCase = true)
                ExamListFilter.INTERNAL -> {
                    exam.examType.equals("Mid-term", ignoreCase = true) ||
                        exam.examType.equals("Quiz", ignoreCase = true)
                }
                ExamListFilter.CUSTOM -> {
                    !exam.examType.equals("Final", ignoreCase = true) &&
                        !exam.examType.equals("Mid-term", ignoreCase = true) &&
                        !exam.examType.equals("Quiz", ignoreCase = true)
                }
            }
        }
        if (query.isEmpty()) byType else {
            byType.filter { exam ->
                exam.title.contains(query, ignoreCase = true) ||
                    exam.subjectName.contains(query, ignoreCase = true) ||
                    exam.batchLabel.contains(query, ignoreCase = true)
            }
        }
    }
    val groupedExams = remember(filteredExams) {
        filteredExams.groupBy { examSectionLabel(it) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            Box(modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)) {
                FloatingActionButton(
                    onClick = onCreateExam,
                    containerColor = LedgerPalette.Plum,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "New exam")
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            item {
                HeaderBlock(
                    totalExams = state.exams.size,
                    totalGroups = state.groups.size,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onBack = onBack,
                )
            }
            item {
                FilterStrip(
                    selected = examFilter,
                    onSelected = { examFilter = it },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (filteredExams.isEmpty()) {
                item {
                    Text(
                        text = "No exams match this view. Try another filter or search term.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                    )
                }
            } else {
                groupedExams.forEach { (groupLabel, exams) ->
                    item {
                        Text(
                            text = groupLabel.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = LedgerPalette.Plum,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        )
                    }
                    item {
                        GroupedExamCard(
                            exams = exams,
                            onOpenExam = onOpenExam,
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderBlock(
    totalExams: Int,
    totalGroups: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(LedgerPalette.Plum)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Examinations",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
                Text(
                    text = "$totalExams active • $totalGroups groups",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.65f),
                )
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search exams…",
                    color = Color.White.copy(alpha = 0.6f),
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White.copy(alpha = 0.35f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                focusedContainerColor = Color.White.copy(alpha = 0.15f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
            ),
        )
    }
}

@Composable
private fun FilterStrip(
    selected: ExamListFilter,
    onSelected: (ExamListFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ExamListFilter.entries.forEach { filter ->
                FilterChip(
                    selected = selected == filter,
                    onClick = { onSelected(filter) },
                    label = { Text(filter.label) },
                    shape = RoundedCornerShape(percent = 50),
                    border = if (selected == filter) null else BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    ),
                )
            }
        }
    }
}

private enum class ExamListFilter(val label: String) {
    ALL("All"),
    SEMESTER("Semester"),
    INTERNAL("Internal"),
    CUSTOM("Custom"),
}

private fun examSectionLabel(exam: Exam): String = when {
    exam.examType.equals("Final", ignoreCase = true) -> "Semester exams - May 2025"
    exam.examType.equals("Mid-term", ignoreCase = true) ||
        exam.examType.equals("Quiz", ignoreCase = true) -> "Internal assessments - Apr 2025"
    else -> "Custom assessments"
}

@Composable
private fun GroupedExamCard(
    exams: List<Exam>,
    onOpenExam: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column {
            exams.forEachIndexed { idx, exam ->
                ExamRow(
                    exam = exam,
                    onClick = { onOpenExam(exam.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onOpenExam(exam.id) })
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                )
                if (idx != exams.lastIndex) {
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .height(1.dp)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        content = {},
                    )
                }
            }
        }
    }
}

@Composable
private fun ExamRow(
    exam: Exam,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Circle,
            contentDescription = null,
            tint = dotColorFor(exam.evaluationType),
            modifier = Modifier.size(10.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exam.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${exam.batchLabel} · ${exam.maxScore.toInt()} marks",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        EvalBadge(type = exam.evaluationType)
    }
}

@Composable
private fun EvalBadge(type: EvaluationType) {
    val (bg, fg) = when (type) {
        EvaluationType.GPA -> LedgerPalette.Plum.copy(alpha = 0.12f) to LedgerPalette.Plum
        EvaluationType.CCE -> LedgerPalette.Amber.copy(alpha = 0.18f) to LedgerPalette.Amber
        EvaluationType.CWA -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = bg,
    ) {
        Text(
            text = type.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .width(36.dp)
                .padding(vertical = 3.dp),
            color = fg,
            maxLines = 1,
        )
    }
}

@Composable
private fun dotColorFor(type: EvaluationType): Color = when (type) {
    EvaluationType.GPA -> LedgerPalette.Plum
    EvaluationType.CCE -> LedgerPalette.Amber
    EvaluationType.CWA -> MaterialTheme.colorScheme.outline
}
