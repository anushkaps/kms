package com.institute.ims.ui.dashboard

/** One row in the dashboard hub search dropdown; actions are handled in the screen via navigation callbacks. */
data class DashboardNavSuggestion(
    val id: String,
    val title: String,
    val subtitle: String,
    val action: DashboardNavAction,
)

sealed class DashboardNavAction {
    data object OpenStudentDirectory : DashboardNavAction()
    data object OpenExamList : DashboardNavAction()
    data class OpenNews(val query: String = "") : DashboardNavAction()
    data object OpenRegionalSettings : DashboardNavAction()
    data class OpenStudentProfile(val studentId: String) : DashboardNavAction()
    data class OpenExamDetail(val examId: String) : DashboardNavAction()
}
