package com.institute.ims.ui.examinations

import com.institute.ims.data.model.AssessmentMode
import com.institute.ims.data.model.Batch
import com.institute.ims.data.model.EvaluationType
import com.institute.ims.data.model.ExamGroup
import com.institute.ims.data.model.ExamStatus

data class CreateExamUiState(
    val title: String = "",
    val examCategory: String = "Mid-term",
    val assessmentMode: AssessmentMode = AssessmentMode.MARKS,
    val batchId: String = "",
    val subjectName: String = "",
    val maxMarksInput: String = "100",
    val passThresholdInput: String = "40",
    val gradeSchemeInput: String = "",
    val passingGradeInput: String = "",
    val customSchemeNameInput: String = "",
    val customCriteriaInput: String = "",
    val groupId: String = "",
    val evaluationType: EvaluationType = EvaluationType.GPA,
    val scheduleLabel: String = "",
    val status: ExamStatus = ExamStatus.DRAFT,
    val batches: List<Batch> = emptyList(),
    val groups: List<ExamGroup> = emptyList(),
    val examCategories: List<String> = listOf("Mid-term", "Final", "Quiz", "Viva", "Assignment"),
    val errorMessage: String? = null,
)
