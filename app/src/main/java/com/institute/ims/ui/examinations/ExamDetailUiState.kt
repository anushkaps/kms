package com.institute.ims.ui.examinations

import com.institute.ims.data.model.Exam
import com.institute.ims.data.model.ExamResult

data class ExamDetailUiState(
    val exam: Exam? = null,
    val groupName: String? = null,
    val results: List<ExamResult> = emptyList(),
    val notFound: Boolean = false,
)
