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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.model.EvaluationType
import com.institute.ims.data.model.Exam
import com.institute.ims.data.model.ExamGroup
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
    val filteredExams = remember(state.exams, searchQuery) {
        val query = searchQuery.trim()
        if (query.isEmpty()) {
            state.exams
        } else {
            state.exams.filter { exam ->
                exam.title.contains(query, ignoreCase = true) ||
                    exam.subjectName.contains(query, ignoreCase = true) ||
                    exam.batchLabel.contains(query, ignoreCase = true) ||
                    exam.id.contains(query, ignoreCase = true)
            }
        }
    }
    val groupedExams = remember(filteredExams, state.groups) {
        examsGroupedByAssessmentGroup(filteredExams, state.groups)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateExam,
                containerColor = LedgerPalette.Plum,
                contentColor = Color.White,
                shape = RoundedCornerShape(14.dp),
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "New exam")
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
                AssessmentGroupFilterStrip(
                    groups = state.groups,
                    selectedGroupId = state.selectedGroupId,
                    onGroupSelected = viewModel::onGroupFilterChange,
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
                groupedExams.forEach { (sectionTitle, exams) ->
                    item {
                        Text(
                            text = sectionTitle.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LedgerPalette.Plum,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                        )
                    }
                    item {
                        GroupedExamCard(
                            exams = exams,
                            onOpenExam = onOpenExam,
                            modifier = Modifier.padding(horizontal = 24.dp),
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(152.dp)
            .background(LedgerPalette.Plum),
    ) {
        Text(
            text = "< Dashboard",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            lineHeight = 13.sp,
            modifier = Modifier
                .padding(start = 24.dp, top = 8.dp)
                .clickable(onClick = onBack),
        )
        Text(
            text = "Examinations",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp,
            lineHeight = 31.sp,
            modifier = Modifier.padding(start = 24.dp, top = 28.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "$totalExams exams · $totalGroups groups",
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 12.sp,
            lineHeight = 15.sp,
            modifier = Modifier.padding(start = 24.dp, top = 62.dp),
        )
        Surface(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp, top = 88.dp)
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(12.dp),
                )
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White,
                        fontSize = 12.sp,
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(Color.White),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search exams…",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.6f),
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun AssessmentGroupFilterStrip(
    groups: List<ExamGroup>,
    selectedGroupId: String?,
    onGroupSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Text(
                text = "Assessment group",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilterChip(
                    selected = selectedGroupId == null,
                    onClick = { onGroupSelected(null) },
                    label = { Text("All") },
                    shape = RoundedCornerShape(percent = 50),
                    border = if (selectedGroupId == null) null else BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    ),
                )
                groups.forEach { group ->
                    val title = assessmentGroupChipTitle(group)
                    FilterChip(
                        selected = selectedGroupId == group.id,
                        onClick = {
                            onGroupSelected(
                                if (selectedGroupId == group.id) null else group.id,
                            )
                        },
                        label = {
                            Text(
                                text = title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        shape = RoundedCornerShape(percent = 50),
                        border = if (selectedGroupId == group.id) null else BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                        ),
                    )
                }
            }
        }
    }
}

/** Short labels for chips; list sections use full [ExamGroup.name]. */
private fun assessmentGroupChipTitle(group: ExamGroup): String = when (group.id) {
    "grp-finals" -> "End-term finals"
    "grp-spring" -> "Spring session"
    else -> group.name
}

private fun examsGroupedByAssessmentGroup(
    exams: List<Exam>,
    groups: List<ExamGroup>,
): List<Pair<String, List<Exam>>> {
    val byGroupId = exams.groupBy { it.groupId }
    val ordered = groups.mapNotNull { g ->
        byGroupId[g.id]?.takeIf { it.isNotEmpty() }?.let { assessmentGroupChipTitle(g) to it }
    }.toMutableList()
    val known = groups.map { it.id }.toSet()
    val other = exams.filter { it.groupId !in known }
    if (other.isNotEmpty()) {
        ordered.add("Other assessments" to other)
    }
    return ordered
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
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFFEEECE5),
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
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 3.dp)
                .size(10.dp)
                .background(dotColorFor(exam.evaluationType), CircleShape),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exam.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1814),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${exam.batchLabel} · ${exam.maxScore.toInt()} marks",
                fontSize = 11.sp,
                color = Color(0xFF6E6A62),
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
