package com.institute.ims.ui.examinations

import com.institute.ims.data.model.Exam
import com.institute.ims.data.model.ExamAnalytics
import com.institute.ims.data.model.ExamResult

enum class ReportCenterTab {
    QUICK_SUMMARY,
    PERFORMANCE_OVERVIEW,
    RESULT_DISTRIBUTION,
}

data class ReportUiState(
    val notFound: Boolean = false,
    val exam: Exam? = null,
    val groupName: String? = null,
    val analytics: ExamAnalytics? = null,
    val topResults: List<ExamResult> = emptyList(),
    val selectedTab: ReportCenterTab = ReportCenterTab.QUICK_SUMMARY,
)
