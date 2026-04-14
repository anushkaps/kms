package com.institute.ims.data.model

/** Maps full IMS marketing bullets to dashboard copy; [action] is optional deep-link. */
data class DashboardCapabilityHighlight(
    val id: String,
    val title: String,
    val lines: List<String>,
    val actionLabel: String? = null,
    val action: DashboardCapabilityAction? = null,
)

enum class DashboardCapabilityAction {
    OPEN_STUDENTS,
    OPEN_EXAMS,
    OPEN_REGIONAL,
    OPEN_ADMISSION_SMS_INFO,
}
