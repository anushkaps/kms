package com.institute.ims.ui.examinations

import com.institute.ims.data.model.Exam
import com.institute.ims.data.model.ExamAnalytics

data class ReportUiState(
    val notFound: Boolean = false,
    val exam: Exam? = null,
    val groupName: String? = null,
    val analytics: ExamAnalytics? = null,
)
