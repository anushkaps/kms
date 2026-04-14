package com.institute.ims.ui.examinations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.model.AssessmentMode
import com.institute.ims.data.model.EvaluationType
import com.institute.ims.data.model.ExamStatus
import com.institute.ims.data.model.uiLabel
import com.institute.ims.ui.common.LedgerPalette

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateExamScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateExamViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var typeMenu by remember { mutableStateOf(false) }
    var batchMenu by remember { mutableStateOf(false) }
    var groupMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("New exam") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LedgerPalette.Plum,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = "Exam details",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Create a new exam entry with clear separation between exam type and evaluation method.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                state.errorMessage?.let { err ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                    ) {
                        Text(
                            text = err,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp),
                        )
                    }
                }

                FormSectionCard(
                    title = "Exam identity",
                    subtitle = "Core details: exam name, category, batch, subject, and group.",
                ) {
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = viewModel::onTitleChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Exam title") },
                        singleLine = true,
                        shape = MaterialTheme.shapes.large,
                    )

                    ExposedDropdownMenuBox(
                        expanded = typeMenu,
                        onExpandedChange = { typeMenu = it },
                    ) {
                        OutlinedTextField(
                            value = state.examCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Exam category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenu)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                            shape = MaterialTheme.shapes.large,
                        )
                        ExposedDropdownMenu(
                            expanded = typeMenu,
                            onDismissRequest = { typeMenu = false },
                        ) {
                            state.examCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        viewModel.onExamCategoryChange(cat)
                                        typeMenu = false
                                    },
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = batchMenu,
                        onExpandedChange = { batchMenu = it },
                    ) {
                        val batchName = state.batches.find { it.id == state.batchId }?.let { b ->
                            "${b.name} (${b.code})"
                        }.orEmpty()
                        OutlinedTextField(
                            value = batchName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Batch") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = batchMenu)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                            shape = MaterialTheme.shapes.large,
                        )
                        ExposedDropdownMenu(
                            expanded = batchMenu,
                            onDismissRequest = { batchMenu = false },
                        ) {
                            state.batches.forEach { batch ->
                                DropdownMenuItem(
                                    text = { Text("${batch.name} (${batch.code})") },
                                    onClick = {
                                        viewModel.onBatchChange(batch.id)
                                        batchMenu = false
                                    },
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = state.subjectName,
                        onValueChange = viewModel::onSubjectChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Subject / course") },
                        placeholder = { Text("e.g. Operating Systems") },
                        singleLine = true,
                        shape = MaterialTheme.shapes.large,
                    )

                    ExposedDropdownMenuBox(
                        expanded = groupMenu,
                        onExpandedChange = { groupMenu = it },
                    ) {
                        val groupName = state.groups.find { it.id == state.groupId }?.name.orEmpty()
                        OutlinedTextField(
                            value = groupName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Exam group") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupMenu)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                            shape = MaterialTheme.shapes.large,
                        )
                        ExposedDropdownMenu(
                            expanded = groupMenu,
                            onDismissRequest = { groupMenu = false },
                        ) {
                            state.groups.forEach { group ->
                                DropdownMenuItem(
                                    text = { Text(group.name) },
                                    onClick = {
                                        viewModel.onGroupChange(group.id)
                                        groupMenu = false
                                    },
                                )
                            }
                        }
                    }
                }

                FormSectionCard(
                    title = "Exam type",
                    subtitle = "What is being assessed. Choose format and scoring range.",
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AssessmentMode.entries.forEach { mode ->
                            FilterChip(
                                selected = state.assessmentMode == mode,
                                onClick = { viewModel.onAssessmentModeChange(mode) },
                                label = { Text(mode.uiLabel()) },
                            )
                        }
                    }

                    OutlinedTextField(
                        value = state.maxMarksInput,
                        onValueChange = viewModel::onMaxMarksChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(maxScoreFieldLabel(state.assessmentMode)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large,
                    )
                    Text(
                        text = "Pass threshold in reports currently uses the default policy.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                FormSectionCard(
                    title = "Evaluation method",
                    subtitle = "How results are computed in reports and transcript-style outputs.",
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        EvaluationType.entries.forEach { type ->
                            FilterChip(
                                selected = state.evaluationType == type,
                                onClick = { viewModel.onEvaluationTypeChange(type) },
                                label = { Text(type.name) },
                            )
                        }
                    }
                }

                FormSectionCard(
                    title = "Schedule and status",
                    subtitle = "Set timing and lifecycle state for this exam entry.",
                ) {
                    OutlinedTextField(
                        value = state.scheduleLabel,
                        onValueChange = viewModel::onScheduleChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Schedule") },
                        placeholder = { Text("e.g. 15 May 2026 - 10:00") },
                        singleLine = true,
                        shape = MaterialTheme.shapes.large,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ExamStatus.entries.forEach { st ->
                            FilterChip(
                                selected = state.status == st,
                                onClick = { viewModel.onStatusChange(st) },
                                label = { Text(statusLabel(st)) },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Cancel")
                }
                FilledTonalButton(
                    onClick = { viewModel.save(onSaved) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = LedgerPalette.Plum,
                        contentColor = Color.White,
                    ),
                ) {
                    Text("Save exam ->")
                }
            }
        }
    }
}

@Composable
private fun FormSectionCard(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = LedgerPalette.Plum,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                content()
            },
        )
    }
}

private fun statusLabel(status: ExamStatus): String = when (status) {
    ExamStatus.DRAFT -> "Draft"
    ExamStatus.PUBLISHED -> "Published"
    ExamStatus.COMPLETED -> "Completed"
}

private fun maxScoreFieldLabel(mode: AssessmentMode): String = when (mode) {
    AssessmentMode.MARKS -> "Max marks"
    AssessmentMode.GRADE_BASED -> "Grade scale (max points)"
    AssessmentMode.CUSTOM -> "Rubric cap (max points)"
}
