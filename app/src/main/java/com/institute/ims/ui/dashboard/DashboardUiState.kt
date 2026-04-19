package com.institute.ims.ui.dashboard

import com.institute.ims.data.model.DashboardCapabilityHighlight
import com.institute.ims.data.model.DashboardModuleCard
import com.institute.ims.data.model.DashboardStat
import com.institute.ims.data.model.NewsItem
import com.institute.ims.data.model.UserRole

data class DashboardUiState(
    val displayName: String = "",
    val role: UserRole? = null,
    val userInitials: String = "",
    /** Large headline under the profile row (time-of-day). */
    val timeOfDayGreeting: String = "Good morning.",
    /** Smaller role-aware line under the headline. */
    val roleContextLine: String = "",
    val instituteSubtitle: String = "Institute Management System",
    val hasUnreadNotifications: Boolean = true,
    val searchQuery: String = "",
    /** When set, Latest news shows only this item until cleared (from hub search). */
    val newsSpotlightId: String? = null,
    val quickChips: List<String> = emptyList(),
    val stats: List<DashboardStat> = emptyList(),
    val modules: List<DashboardModuleCard> = emptyList(),
    val capabilityHighlights: List<DashboardCapabilityHighlight> = emptyList(),
    val news: List<NewsItem> = emptyList(),
    val overviewLine: String = "",
    val regionalSummaryLine: String = "",
    val activeExamCount: Int = 0,
    val unreadNoticeCount: Int = 0,
    val pendingGradeReviewCount: Int = 0,
    val examDraftApprovalCount: Int = 0,
)
