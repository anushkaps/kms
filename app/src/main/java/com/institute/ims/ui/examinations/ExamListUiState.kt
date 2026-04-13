package com.institute.ims.ui.examinations

import com.institute.ims.data.model.Exam
import com.institute.ims.data.model.ExamGroup

data class ExamListUiState(
    val groups: List<ExamGroup> = emptyList(),
    val exams: List<Exam> = emptyList(),
    val selectedGroupId: String? = null,
)
