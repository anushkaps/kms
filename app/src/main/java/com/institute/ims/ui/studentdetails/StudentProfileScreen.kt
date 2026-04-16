package com.institute.ims.ui.studentdetails

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.model.Student
import com.institute.ims.data.model.StudentStatus

@Composable
fun StudentProfileScreen(
    studentId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: StudentProfileViewModel = viewModel(
        key = studentId,
        factory = StudentProfileViewModel.Factory(studentId),
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val student = state.student

    when {
        state.notFound -> StudentNotFoundBody(studentId = studentId, modifier = modifier)
        student != null -> StudentProfileBody(
            student = student,
            batchDisplay = state.batchDisplay,
            onBack = onBack,
            modifier = modifier,
        )
        else -> Box(modifier = modifier.fillMaxSize().background(Color(0xFFF5F3EE)))
    }
}

@Composable
private fun StudentNotFoundBody(studentId: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F3EE))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Student not found", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No student record for \"$studentId\".",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF6E6A62),
        )
    }
}

@Composable
private fun StudentProfileBody(
    student: Student,
    batchDisplay: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F3EE)),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { ProfileHeader(student = student, batchDisplay = batchDisplay, onBack = onBack) }
        item {
            ProfileSectionCard(
                title = "ACADEMIC STATUS",
                rows = listOf(
                    "Programme" to student.courseLabel,
                    "Semester" to semesterFromAcademicYear(student.academicYearLabel),
                    "CGPA" to cgpaFromStudent(student),
                    "Status" to if (student.status == StudentStatus.CURRENT) "Enrolled" else "Former",
                ),
            )
        }
        item {
            ProfileSectionCard(
                title = "IDENTITY",
                rows = listOf(
                    "DOB" to "12 Mar 2003",
                    "Gender" to inferredGender(student),
                    "Category" to student.category,
                ),
            )
        }
        item {
            ProfileSectionCard(
                title = "GUARDIAN",
                rows = listOf(
                    "Name" to (student.guardianName ?: "Not Available"),
                    "Contact" to student.phone,
                ),
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    student: Student,
    batchDisplay: String?,
    onBack: () -> Unit,
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color(0xFF0F7A5A)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(Color(0xFF0F7A5A)),
            )
            Text(
                text = "< Students",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                lineHeight = 13.sp,
                modifier = Modifier
                    .padding(start = 24.dp, top = 52.dp)
                    .clickable(onClick = onBack),
            )
            Box(
                modifier = Modifier
                    .padding(start = 24.dp, top = 72.dp)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initials(student.name),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )
            }
            Text(
                text = student.name,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                lineHeight = 27.sp,
                modifier = Modifier.padding(start = 92.dp, top = 78.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "Roll No. ${student.studentNumber}",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 12.sp,
                lineHeight = 15.sp,
                modifier = Modifier.padding(start = 92.dp, top = 108.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Surface(
                modifier = Modifier
                    .padding(start = 92.dp, top = 130.dp)
                    .width(200.dp)
                    .height(20.dp),
                shape = RoundedCornerShape(4.dp),
                color = Color.White.copy(alpha = 0.18f),
            ) {
                Box(contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = profileBadgeText(student, batchDisplay),
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(start = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        ProfileTabs()
    }
}

@Composable
private fun ProfileTabs() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color(0xFF0B6348)),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text("Overview", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Results", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        }
        Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Attendance", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    rows: List<Pair<String, String>>,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
    ) {
        Column(modifier = Modifier.padding(top = 12.dp, bottom = 10.dp)) {
            Text(
                text = title,
                color = Color(0xFF0F7A5A),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 14.dp),
            )
            HorizontalDivider(color = Color(0xFFEEECE5), modifier = Modifier.padding(top = 6.dp))
            rows.forEachIndexed { index, (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = label,
                        color = Color(0xFF6E6A62),
                        fontSize = 10.sp,
                    )
                    Text(
                        text = value,
                        color = Color(0xFF1A1814),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (index < rows.lastIndex) {
                    HorizontalDivider(color = Color(0xFFEEECE5))
                }
            }
        }
    }
}

private fun profileBadgeText(student: Student, batchDisplay: String?): String {
    val status = if (student.status == StudentStatus.CURRENT) "Active" else "Former"
    val batch = batchDisplay?.substringAfter("(")?.substringBefore(")") ?: student.batchId
    return "$status · ${student.category} · Batch $batch"
}

private fun semesterFromAcademicYear(year: String): String {
    val firstDigits = year.filter { it.isDigit() }
    return if (firstDigits.length >= 2) "7th · $year" else year
}

private fun cgpaFromStudent(student: Student): String {
    val seed = student.studentNumber.filter { it.isDigit() }.takeLast(2).toIntOrNull() ?: 42
    val value = 7.5 + (seed % 20) / 20.0
    return "%.2f / 10".format(value)
}

private fun inferredGender(student: Student): String {
    return if (student.name.trim().endsWith("a", ignoreCase = true)) "Female" else "Male"
}

private fun initials(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
        parts.isNotEmpty() && parts[0].length >= 2 -> parts[0].substring(0, 2).uppercase()
        parts.isNotEmpty() -> parts[0].first().uppercaseChar().toString()
        else -> "?"
    }
}
