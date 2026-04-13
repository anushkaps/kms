package com.institute.ims.ui.examinations

import com.institute.ims.data.model.Exam
import com.institute.ims.data.model.ExamAnalytics

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
    val selectedTab: ReportCenterTab = ReportCenterTab.QUICK_SUMMARY,
)
