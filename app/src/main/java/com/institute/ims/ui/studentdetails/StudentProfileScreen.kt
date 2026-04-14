package com.institute.ims.ui.studentdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.model.Student
import com.institute.ims.data.model.StudentStatus
import com.institute.ims.ui.common.LedgerPalette

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.student?.name ?: "Student",
                        maxLines = 1,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LedgerPalette.Forest,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
            )
        },
    ) { innerPadding ->
        val student = state.student
        when {
            state.notFound -> StudentNotFoundBody(
                studentId = studentId,
                modifier = Modifier.padding(innerPadding),
            )
            student != null -> StudentProfileBody(
                student = student,
                batchDisplay = state.batchDisplay,
                modifier = Modifier.padding(innerPadding),
            )
            else -> Box(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
private fun StudentNotFoundBody(
    studentId: String,
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
            text = "Student not found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No student record for \"$studentId\". Go back and choose someone from the directory.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StudentProfileBody(
    student: Student,
    batchDisplay: String?,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ProfileHero(
                student = student,
                batchDisplay = batchDisplay,
            )
        }
        item {
            InfoSectionCard(
                title = "Academic status",
                leadingIcon = {
                    Icon(
                        Icons.Outlined.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
            ) {
                ProfileRow("Batch", batchDisplay ?: student.batchId)
                ProfileRow("Programme", student.courseLabel)
                ProfileRow("Academic year", student.academicYearLabel)
                ProfileRow("Category", student.category)
            }
        }
        item {
            InfoSectionCard(
                title = "Identity",
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
            ) {
                ProfileRow("Email", student.email)
                ProfileRow("Phone", student.phone)
            }
        }
        if (!student.guardianName.isNullOrBlank()) {
            item {
                InfoSectionCard(
                    title = "Guardian",
                    leadingIcon = null,
                ) {
                    ProfileRow("Name", student.guardianName!!)
                }
            }
        }
        item {
            InfoSectionCard(
                title = "Record",
                leadingIcon = null,
            ) {
                ProfileRow("Status", if (student.status == StudentStatus.CURRENT) "Current" else "Former")
                ProfileRow("Record ID", student.id)
            }
        }
    }
}

@Composable
private fun ProfileHero(
    student: Student,
    batchDisplay: String?,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = LedgerPalette.Forest,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = initials(student.name),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = student.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = student.studentNumber,
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFFE5F4EE),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = batchDisplay ?: student.batchId,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFD5EEE5),
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatusBadge(student.status)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    tint = Color(0xFFE5F4EE),
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = student.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: StudentStatus) {
    val (label, bg, fg) = when (status) {
        StudentStatus.CURRENT -> Triple(
            "Active",
            Color.White.copy(alpha = 0.2f),
            Color.White,
        )
        StudentStatus.FORMER -> Triple(
            "Alumni",
            Color(0xFFFFE7EB),
            Color(0xFF9C3A4A),
        )
    }
    Surface(
        shape = RoundedCornerShape(percent = 50),
        color = bg,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = fg,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun InfoSectionCard(
    title: String,
    leadingIcon: (@Composable () -> Unit)?,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = LedgerPalette.Surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                leadingIcon?.invoke()
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = LedgerPalette.Forest,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
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
