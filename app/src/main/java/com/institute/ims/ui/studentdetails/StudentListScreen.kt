package com.institute.ims.ui.studentdetails

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.model.Batch
import com.institute.ims.data.model.Student
import com.institute.ims.data.model.StudentStatus
import com.institute.ims.ui.common.LedgerPalette
import com.institute.ims.utils.studentCategoryShortLabel

@Composable
fun StudentListScreen(
    onBack: () -> Unit,
    onOpenProfile: (studentId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StudentListViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val grouped = state.students.groupBy { it.batchId }
    val orderedBatches = state.batches.filter { grouped.containsKey(it.id) }
    val hasAdvancedSelection =
        state.courseLabelFilter != null || state.categoryFilter != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F3EE))
            .systemBarsPadding(),
    ) {
        StudentHeader(
            totalStudents = state.students.size,
            totalBatches = state.batches.size,
            query = state.searchQuery,
            onBack = onBack,
            onQueryChange = viewModel::onSearchQueryChange,
            onSearchDone = { focusManager.clearFocus() },
        )

        FilterStrip(
            batches = state.batches,
            isAll = state.statusFilter == null && state.batchIdFilter == null && !hasAdvancedSelection,
            isCurrent = state.statusFilter == StudentStatus.CURRENT,
            isFormer = state.statusFilter == StudentStatus.FORMER,
            selectedBatchId = state.batchIdFilter,
            onAll = {
                viewModel.onStatusFilterChange(null)
                viewModel.onBatchFilterChange(null)
                viewModel.onCourseFilterChange(null)
                viewModel.onCategoryFilterChange(null)
            },
            onCurrent = {
                viewModel.onBatchFilterChange(null)
                viewModel.onCourseFilterChange(null)
                viewModel.onCategoryFilterChange(null)
                viewModel.onStatusFilterChange(if (state.statusFilter == StudentStatus.CURRENT) null else StudentStatus.CURRENT)
            },
            onFormer = {
                viewModel.onBatchFilterChange(null)
                viewModel.onCourseFilterChange(null)
                viewModel.onCategoryFilterChange(null)
                viewModel.onStatusFilterChange(if (state.statusFilter == StudentStatus.FORMER) null else StudentStatus.FORMER)
            },
            onBatchSelected = { batchId ->
                viewModel.onStatusFilterChange(null)
                viewModel.onCourseFilterChange(null)
                viewModel.onCategoryFilterChange(null)
                viewModel.onBatchFilterChange(
                    if (state.batchIdFilter == batchId) null else batchId,
                )
            },
        )

        AdvancedFiltersStrip(
            expanded = state.advancedPanelExpanded,
            courseLabels = state.courseLabels,
            categories = state.categories,
            selectedCourse = state.courseLabelFilter,
            selectedCategory = state.categoryFilter,
            onToggle = viewModel::onToggleAdvancedPanel,
            onCourse = viewModel::onCourseFilterChange,
            onCategory = viewModel::onCategoryFilterChange,
            onClearAdvanced = viewModel::onClearAdvancedFilters,
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (orderedBatches.isEmpty()) {
                item {
                    Text(
                        text = "No students match this filter.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6E6A62),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                    )
                }
            } else {
                items(orderedBatches, key = { it.id }) { batch ->
                    val students = grouped[batch.id].orEmpty()
                    BatchSection(
                        batch = batch,
                        students = students,
                        onOpenProfile = onOpenProfile,
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentHeader(
    totalStudents: Int,
    totalBatches: Int,
    query: String,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearchDone: () -> Unit,
) {
    val headerGreen = Color(0xFF0F7A5A)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(headerGreen),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier
                    .clickable(onClick = onBack),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White.copy(alpha = 0.75f),
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "Dashboard",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                )
            }
            Text(
                text = "Students",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                lineHeight = 31.sp,
            )
            Text(
                text = "$totalStudents enrolled · $totalBatches batches",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 12.sp,
                lineHeight = 15.sp,
            )
            // Capsule search: translucent fill + hairline border (no clipped OutlinedTextField chrome)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(percent = 50),
                color = Color.White.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.White.copy(alpha = 0.18f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            fontSize = 14.sp,
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(Color.White),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onSearchDone() }),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth()) {
                                if (query.isEmpty()) {
                                    Text(
                                        text = "Search by name, roll no...",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.55f),
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
}

@Composable
private fun FilterStrip(
    batches: List<Batch>,
    isAll: Boolean,
    isCurrent: Boolean,
    isFormer: Boolean,
    selectedBatchId: String?,
    onAll: () -> Unit,
    onCurrent: () -> Unit,
    onFormer: () -> Unit,
    onBatchSelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(1.dp, Color(0xFFD4CFC5))
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ListChip("All", 44.dp, isAll, onAll)
        ListChip("Current", 76.dp, isCurrent, onCurrent)
        ListChip("Former", 68.dp, isFormer, onFormer)
        batches.forEach { batch ->
            val label = batchFilterChipLabel(batch)
            val selected = selectedBatchId == batch.id
            ListChip(
                label = label,
                width = (label.length * 9 + 28).coerceIn(52, 160).dp,
                selected = selected,
                onClick = { onBatchSelected(batch.id) },
            )
        }
    }
}

private fun batchFilterChipLabel(batch: Batch): String {
    val years = Regex("(20\\d{2})").findAll(batch.name).map { it.value }.toList()
    return when (years.size) {
        2 -> "${years[0]}–${years[1]}"
        1 -> years[0]
        else -> batch.code.uppercase()
    }
}

@Composable
private fun AdvancedFiltersStrip(
    expanded: Boolean,
    courseLabels: List<String>,
    categories: List<String>,
    selectedCourse: String?,
    selectedCategory: String?,
    onToggle: () -> Unit,
    onCourse: (String?) -> Unit,
    onCategory: (String?) -> Unit,
    onClearAdvanced: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFD4CFC5))
            .padding(horizontal = 24.dp, vertical = 6.dp),
    ) {
        TextButton(onClick = onToggle, modifier = Modifier.padding(vertical = 0.dp)) {
            Text(
                text = if (expanded) "Hide advanced filters" else "Advanced filters (course · category)",
                fontSize = 11.sp,
                color = Color(0xFF0F7A5A),
                fontWeight = FontWeight.SemiBold,
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column {
                Text(
                    text = "Course",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6E6A62),
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    courseLabels.forEach { label ->
                        val selected = selectedCourse == label
                        AdvancedPill(
                            text = label,
                            selected = selected,
                            onClick = {
                                onCourse(if (selected) null else label)
                            },
                        )
                    }
                }
                Text(
                    text = "Category",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6E6A62),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    categories.forEach { label ->
                        val selected = selectedCategory == label
                        AdvancedPill(
                            text = label,
                            selected = selected,
                            onClick = {
                                onCategory(if (selected) null else label)
                            },
                        )
                    }
                }
                TextButton(onClick = onClearAdvanced) {
                    Text("Clear course & category", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun AdvancedPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0xFF0F7A5A) else Color.White,
        border = BorderStroke(
            1.dp,
            if (selected) Color(0xFF0F7A5A) else Color(0xFFD4CFC5),
        ),
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) Color.White else Color(0xFF6E6A62),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ListChip(
    label: String,
    width: Dp,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .width(width)
            .height(24.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0xFF0F7A5A) else Color.White,
        border = BorderStroke(
            1.dp,
            if (selected) Color(0xFF0F7A5A) else Color(0xFFD4CFC5),
        ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (selected) Color.White else Color(0xFF6E6A62),
            )
        }
    }
}

@Composable
private fun BatchSection(
    batch: Batch,
    students: List<Student>,
    onOpenProfile: (String) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${batch.code.uppercase()}",
                color = Color(0xFF6E6A62),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(130.dp),
            )
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                color = Color(0xFFD4CFC5),
            )
            Text(
                text = "${students.size} students",
                color = Color(0xFF6E6A62),
                fontSize = 9.sp,
                modifier = Modifier.width(60.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
        ) {
            students.forEachIndexed { index, student ->
                StudentRowLine(
                    student = student,
                    onClick = { onOpenProfile(student.id) },
                    accentSet = index % 4,
                )
                if (index < students.lastIndex) {
                    HorizontalDivider(color = Color(0xFFEEECE5))
                }
            }
        }
    }
}

@Composable
private fun StudentRowLine(
    student: Student,
    onClick: () -> Unit,
    accentSet: Int,
) {
    val (avatarBg, avatarFg) = when (accentSet) {
        0 -> Pair(Color(0xFFEAF4EF), Color(0xFF0F7A5A))
        1 -> Pair(Color(0xFFF3EDF9), Color(0xFF7B3FBE))
        2 -> Pair(Color(0xFFEEF2FB), Color(0xFF1B4FBF))
        else -> Pair(Color(0xFFFDF0E5), Color(0xFFB85C00))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(avatarBg, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = studentInitials(student.name),
                color = avatarFg,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = student.name,
                color = LedgerPalette.Ink,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${student.studentNumber} · ${studentCategoryShortLabel(student.category)}",
                color = Color(0xFF6E6A62),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        StatusPill(student.status)
    }
}

@Composable
private fun StatusPill(status: StudentStatus) {
    val (label, bg, fg) = when (status) {
        StudentStatus.CURRENT -> Triple("Active", Color(0xFFEAF4EF), Color(0xFF0F7A5A))
        StudentStatus.FORMER -> Triple("Alumni", Color(0xFFFDECEA), Color(0xFFC0352B))
    }
    Surface(
        modifier = Modifier
            .width(44.dp)
            .height(18.dp),
        shape = RoundedCornerShape(4.dp),
        color = bg,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = fg,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}

private fun studentInitials(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "ST"
        parts.size == 1 -> parts.first().take(2).uppercase()
        else -> "${parts[0].first()}${parts[1].first()}".uppercase()
    }
}
