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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val batch2022 = state.batches.firstOrNull { it.code.contains("2022") }?.id
    val grouped = state.students.groupBy { it.batchId }
    val orderedBatches = state.batches.filter { grouped.containsKey(it.id) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F3EE)),
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
            isAll = state.statusFilter == null && state.batchIdFilter == null,
            isCurrent = state.statusFilter == StudentStatus.CURRENT,
            isFormer = state.statusFilter == StudentStatus.FORMER,
            isBatch2022 = state.batchIdFilter == batch2022 && batch2022 != null,
            onAll = {
                viewModel.onStatusFilterChange(null)
                viewModel.onBatchFilterChange(null)
            },
            onCurrent = {
                viewModel.onBatchFilterChange(null)
                viewModel.onStatusFilterChange(if (state.statusFilter == StudentStatus.CURRENT) null else StudentStatus.CURRENT)
            },
            onFormer = {
                viewModel.onBatchFilterChange(null)
                viewModel.onStatusFilterChange(if (state.statusFilter == StudentStatus.FORMER) null else StudentStatus.FORMER)
            },
            onBatch2022 = {
                viewModel.onStatusFilterChange(null)
                if (batch2022 != null) {
                    viewModel.onBatchFilterChange(if (state.batchIdFilter == batch2022) null else batch2022)
                }
            },
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFF0F7A5A)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color(0xFF0F7A5A)),
        )
        Text(
            text = "< Dashboard",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            lineHeight = 13.sp,
            modifier = Modifier
                .padding(start = 24.dp, top = 52.dp)
                .clickable(onClick = onBack),
        )
        Text(
            text = "Students",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp,
            lineHeight = 31.sp,
            modifier = Modifier.padding(start = 24.dp, top = 72.dp),
        )
        Text(
            text = "$totalStudents enrolled · $totalBatches batches",
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 12.sp,
            lineHeight = 15.sp,
            modifier = Modifier.padding(start = 24.dp, top = 108.dp),
        )
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .padding(start = 24.dp, top = 136.dp, end = 24.dp)
                .fillMaxWidth()
                .height(40.dp),
            placeholder = {
                Text(
                    text = "Search by name, roll no...",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.65f),
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.12f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.12f),
                focusedBorderColor = Color.White.copy(alpha = 0.2f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchDone() }),
        )
    }
}

@Composable
private fun FilterStrip(
    isAll: Boolean,
    isCurrent: Boolean,
    isFormer: Boolean,
    isBatch2022: Boolean,
    onAll: () -> Unit,
    onCurrent: () -> Unit,
    onFormer: () -> Unit,
    onBatch2022: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(1.dp, Color(0xFFD4CFC5))
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ListChip("All", 44.dp, isAll, onAll)
        ListChip("Current", 76.dp, isCurrent, onCurrent)
        ListChip("Former", 68.dp, isFormer, onFormer)
        ListChip("Batch 2022", 100.dp, isBatch2022, onBatch2022)
    }
}

@Composable
private fun ListChip(
    label: String,
    width: androidx.compose.ui.unit.Dp,
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
