package com.institute.ims.ui.examinations

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.model.AssessmentMode
import com.institute.ims.data.model.EvaluationType
import com.institute.ims.data.model.shortFormatLabel
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
                    .padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                CreateExamHeader(onBack = onBack)

                state.errorMessage?.let { err ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp),
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
                    subtitle = "",
                    modifier = Modifier.padding(horizontal = 20.dp),
                ) {
                    CompactRowField(
                        label = "Exam name",
                        value = state.title,
                        onValueChange = viewModel::onTitleChange,
                        placeholder = "e.g. Data Structures — Final",
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
                            shape = RoundedCornerShape(6.dp),
                            colors = compactFieldColors(),
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
                        if (state.batches.size in 2..5) {
                            CompactChipRow(
                                label = "Batch",
                                items = state.batches.map { it.id to it.code.removePrefix("CS-") },
                                selectedId = state.batchId,
                                onSelected = viewModel::onBatchChange,
                            )
                        } else {
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
                                shape = RoundedCornerShape(6.dp),
                                colors = compactFieldColors(),
                            )
                        }
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

                    CompactRowField(
                        label = "Subject",
                        value = state.subjectName,
                        onValueChange = viewModel::onSubjectChange,
                        placeholder = "e.g. Operating Systems",
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
                            label = { Text("Assessment group") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupMenu)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                            shape = RoundedCornerShape(6.dp),
                            colors = compactFieldColors(),
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
                    title = "Exam type - what is being assessed",
                    subtitle = "Select the format and scoring method of this exam.",
                    modifier = Modifier.padding(horizontal = 20.dp),
                ) {
                    CompactChipRow(
                        label = "Format",
                        items = AssessmentMode.entries.map { it.name to it.shortFormatLabel() },
                        selectedId = state.assessmentMode.name,
                        onSelected = { id ->
                            AssessmentMode.entries.firstOrNull { it.name == id }?.let(viewModel::onAssessmentModeChange)
                        },
                    )

                    when (state.assessmentMode) {
                        AssessmentMode.MARKS -> {
                            CompactRowField(
                                label = "Total marks",
                                value = state.maxMarksInput,
                                onValueChange = viewModel::onMaxMarksChange,
                                placeholder = "100",
                                keyboardType = KeyboardType.Decimal,
                            )
                            CompactRowField(
                                label = "Pass threshold",
                                value = state.passThresholdInput,
                                onValueChange = viewModel::onPassThresholdChange,
                                placeholder = "40",
                                keyboardType = KeyboardType.Decimal,
                            )
                        }
                        AssessmentMode.GRADE_BASED -> {
                            CompactRowField(
                                label = "Grade scheme",
                                value = state.gradeSchemeInput,
                                onValueChange = viewModel::onGradeSchemeChange,
                                placeholder = "e.g. Letter O–F",
                            )
                            CompactRowField(
                                label = "Passing grade",
                                value = state.passingGradeInput,
                                onValueChange = viewModel::onPassingGradeChange,
                                placeholder = "e.g. C",
                            )
                        }
                        AssessmentMode.CUSTOM -> {
                            CompactRowField(
                                label = "Custom scheme",
                                value = state.customSchemeNameInput,
                                onValueChange = viewModel::onCustomSchemeNameChange,
                                placeholder = "e.g. Viva rubric",
                            )
                            CompactRowMultiline(
                                label = "Criteria summary",
                                value = state.customCriteriaInput,
                                onValueChange = viewModel::onCustomCriteriaChange,
                                placeholder = "Short rule / criteria text",
                            )
                        }
                    }
                }

                FormSectionCard(
                    title = "Evaluation method - how results are computed",
                    subtitle = "This determines how final scores map to transcripts.",
                    modifier = Modifier.padding(horizontal = 20.dp),
                ) {
                    CompactChipRow(
                        label = "Method",
                        items = EvaluationType.entries.map { it.name to it.name },
                        selectedId = state.evaluationType.name,
                        onSelected = { id ->
                            EvaluationType.entries.firstOrNull { it.name == id }?.let(viewModel::onEvaluationTypeChange)
                        },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                FilledTonalButton(
                    onClick = { viewModel.save(onSaved) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = LedgerPalette.Plum,
                        contentColor = Color.White,
                    ),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text = "Save Exam  →",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun FormSectionCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = LedgerPalette.Plum,
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    content = {},
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    )
                }
                content()
            },
        )
    }
}

@Composable
private fun CreateExamHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LedgerPalette.Plum)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(onClick = onBack) {
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
        Text(
            text = "New Exam",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
        Text(
            text = "Fill in details to create an exam.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun CompactRowField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 10.dp)
                .weight(0.38f),
            maxLines = 1,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(0.62f),
            placeholder = { Text(placeholder) },
            singleLine = true,
            shape = RoundedCornerShape(6.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = compactFieldColors(),
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun CompactChipRow(
    label: String,
    items: List<Pair<String, String>>,
    selectedId: String?,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items.forEach { (id, display) ->
                val selected = selectedId == id
                FilterChip(
                    selected = selected,
                    onClick = { onSelected(id) },
                    label = {
                        Text(
                            text = display,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    shape = RoundedCornerShape(5.dp),
                    border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LedgerPalette.Plum,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

@Composable
private fun CompactRowMultiline(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 10.dp)
                .weight(0.38f),
            maxLines = 2,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(0.62f),
            placeholder = { Text(placeholder) },
            minLines = 2,
            maxLines = 4,
            shape = RoundedCornerShape(6.dp),
            colors = compactFieldColors(),
        )
    }
}

@Composable
private fun compactFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
)

