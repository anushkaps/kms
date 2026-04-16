package com.institute.ims.ui.examinations

import androidx.lifecycle.ViewModel
import com.institute.ims.data.model.AssessmentMode
import com.institute.ims.data.model.EvaluationType
import com.institute.ims.data.model.Exam
import com.institute.ims.data.model.ExamStatus
import com.institute.ims.data.repository.ExamRepository
import com.institute.ims.data.repository.FakeExamRepository
import com.institute.ims.data.repository.FakeStudentRepository
import com.institute.ims.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class CreateExamViewModel(
    private val examRepository: ExamRepository = FakeExamRepository,
    private val studentRepository: StudentRepository = FakeStudentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateExamUiState())
    val uiState: StateFlow<CreateExamUiState> = _uiState.asStateFlow()

    init {
        val batches = studentRepository.getBatches()
        val groups = examRepository.getGroups()
        _uiState.update {
            it.copy(
                batches = batches,
                groups = groups,
                batchId = batches.firstOrNull()?.id.orEmpty(),
                groupId = groups.firstOrNull()?.id.orEmpty(),
            )
        }
    }

    fun onTitleChange(value: String) = patch { it.copy(title = value, errorMessage = null) }

    fun onExamCategoryChange(value: String) = patch { it.copy(examCategory = value, errorMessage = null) }

    fun onAssessmentModeChange(mode: AssessmentMode) =
        patch { it.copy(assessmentMode = mode, errorMessage = null) }

    fun onBatchChange(batchId: String) = patch { it.copy(batchId = batchId, errorMessage = null) }

    fun onSubjectChange(value: String) = patch { it.copy(subjectName = value, errorMessage = null) }

    fun onMaxMarksChange(value: String) = patch { it.copy(maxMarksInput = value, errorMessage = null) }

    fun onPassThresholdChange(value: String) =
        patch { it.copy(passThresholdInput = value, errorMessage = null) }

    fun onGradeSchemeChange(value: String) = patch { it.copy(gradeSchemeInput = value, errorMessage = null) }

    fun onPassingGradeChange(value: String) = patch { it.copy(passingGradeInput = value, errorMessage = null) }

    fun onCustomSchemeNameChange(value: String) =
        patch { it.copy(customSchemeNameInput = value, errorMessage = null) }

    fun onCustomCriteriaChange(value: String) =
        patch { it.copy(customCriteriaInput = value, errorMessage = null) }

    fun onGroupChange(groupId: String) = patch { it.copy(groupId = groupId, errorMessage = null) }

    fun onEvaluationTypeChange(type: EvaluationType) =
        patch { it.copy(evaluationType = type, errorMessage = null) }

    fun onScheduleChange(value: String) = patch { it.copy(scheduleLabel = value, errorMessage = null) }

    fun onStatusChange(status: ExamStatus) = patch { it.copy(status = status, errorMessage = null) }

    fun save(onSuccess: () -> Unit) {
        val s = _uiState.value
        when {
            s.title.isBlank() -> {
                patch { it.copy(errorMessage = "Please enter a title.") }
                return
            }
            s.batchId.isBlank() -> {
                patch { it.copy(errorMessage = "Please select a batch.") }
                return
            }
            s.groupId.isBlank() -> {
                patch { it.copy(errorMessage = "Please select an assessment group.") }
                return
            }
        }

        val batch = studentRepository.getBatches().find { it.id == s.batchId }
        val batchLabel = batch?.let { b -> "${b.name} (${b.code})" } ?: s.batchId

        val built = when (s.assessmentMode) {
            AssessmentMode.MARKS -> {
                val max = s.maxMarksInput.toDoubleOrNull()
                if (max == null || max <= 0) {
                    patch { it.copy(errorMessage = "Enter a valid positive total marks value.") }
                    return
                }
                val pass = s.passThresholdInput.toDoubleOrNull()
                if (pass == null || pass < 0 || pass > max) {
                    patch { it.copy(errorMessage = "Enter a valid pass threshold (0…total marks).") }
                    return
                }
                BuiltExamFields(max, pass, null, null, null, null)
            }
            AssessmentMode.GRADE_BASED -> {
                if (s.gradeSchemeInput.isBlank()) {
                    patch { it.copy(errorMessage = "Enter a grade scheme name.") }
                    return
                }
                if (s.passingGradeInput.isBlank()) {
                    patch { it.copy(errorMessage = "Enter a passing grade.") }
                    return
                }
                BuiltExamFields(
                    maxScore = 100.0,
                    passMarks = null,
                    gradeScheme = s.gradeSchemeInput.trim(),
                    passingGrade = s.passingGradeInput.trim(),
                    customName = null,
                    customSummary = null,
                )
            }
            AssessmentMode.CUSTOM -> {
                if (s.customSchemeNameInput.isBlank()) {
                    patch { it.copy(errorMessage = "Enter a custom scheme name.") }
                    return
                }
                if (s.customCriteriaInput.isBlank()) {
                    patch { it.copy(errorMessage = "Enter a short criteria / rule summary.") }
                    return
                }
                BuiltExamFields(
                    maxScore = 100.0,
                    passMarks = null,
                    gradeScheme = null,
                    passingGrade = null,
                    customName = s.customSchemeNameInput.trim(),
                    customSummary = s.customCriteriaInput.trim(),
                )
            }
        }

        val exam = Exam(
            id = "exam-${UUID.randomUUID().toString().take(8)}",
            title = s.title.trim(),
            examType = s.examCategory,
            batchId = s.batchId,
            batchLabel = batchLabel,
            subjectName = s.subjectName.trim().ifBlank { "General" },
            maxScore = built.maxScore,
            groupId = s.groupId,
            evaluationType = s.evaluationType,
            scheduleLabel = s.scheduleLabel.trim().ifBlank { "TBA" },
            status = s.status,
            assessmentMode = s.assessmentMode,
            passMarksThreshold = built.passMarks,
            gradeSchemeName = built.gradeScheme,
            passingGradeLabel = built.passingGrade,
            customSchemeName = built.customName,
            customCriteriaSummary = built.customSummary,
        )
        examRepository.addExam(exam)
        onSuccess()
    }

    private data class BuiltExamFields(
        val maxScore: Double,
        val passMarks: Double?,
        val gradeScheme: String?,
        val passingGrade: String?,
        val customName: String?,
        val customSummary: String?,
    )

    private inline fun patch(block: (CreateExamUiState) -> CreateExamUiState) {
        _uiState.update(block)
    }
}
