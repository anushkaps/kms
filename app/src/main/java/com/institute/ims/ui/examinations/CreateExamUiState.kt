package com.institute.ims.ui.examinations

import com.institute.ims.data.model.Batch
import com.institute.ims.data.model.EvaluationType
import com.institute.ims.data.model.ExamGroup
import com.institute.ims.data.model.ExamStatus

data class CreateExamUiState(
    val title: String = "",
    val examType: String = "Mid-term",
    val batchId: String = "",
    val subjectName: String = "",
    val maxMarksInput: String = "100",
    val groupId: String = "",
    val evaluationType: EvaluationType = EvaluationType.GPA,
    val scheduleLabel: String = "",
    val status: ExamStatus = ExamStatus.DRAFT,
    val batches: List<Batch> = emptyList(),
    val groups: List<ExamGroup> = emptyList(),
    val examTypes: List<String> = listOf("Mid-term", "Final", "Quiz", "Viva", "Assignment", "Custom"),
    val errorMessage: String? = null,
)
