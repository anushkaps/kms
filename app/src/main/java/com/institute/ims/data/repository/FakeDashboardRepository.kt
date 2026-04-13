package com.institute.ims.data.repository

import com.institute.ims.data.model.DashboardModuleCard
import com.institute.ims.data.model.DashboardModuleId
import com.institute.ims.data.model.DashboardStat
import com.institute.ims.data.model.UserRole

/** Static dashboard stats, module cards, and quick-action chip labels. */
object FakeDashboardRepository : DashboardRepository {
    override fun getSummaryStats(role: UserRole): List<DashboardStat> = when (role) {
        UserRole.ADMIN -> listOf(
            DashboardStat(
                id = "pending_approval",
                label = "Pending faculty approval",
                value = "2",
                caption = "Sign-offs waiting",
            ),
            DashboardStat(
                id = "published_reports",
                label = "Published reports",
                value = "12",
                caption = "This term",
            ),
        )
        UserRole.FACULTY -> listOf(
            DashboardStat(
                id = "grading_queue",
                label = "Grading queue",
                value = "1",
                caption = "Draft papers",
            ),
            DashboardStat(
                id = "upcoming_exams",
                label = "Upcoming exams",
                value = "2",
                caption = "Next 7 days",
            ),
        )
    }

    override fun getModuleCards(): List<DashboardModuleCard> = listOf(
        DashboardModuleCard(
            id = DashboardModuleId.STUDENTS,
            title = "Student Details",
            description = "Browse batches, search students, and open profiles.",
        ),
        DashboardModuleCard(
            id = DashboardModuleId.EXAMS,
            title = "Examinations",
            description = "Manage exam groups, results, and evaluation types.",
        ),
    )

    override fun getQuickChipLabels(): List<String> = listOf(
        "Students",
        "Exams",
        "News",
    )

    override fun getOverviewLine(role: UserRole): String = when (role) {
        UserRole.ADMIN -> "Today: 2 timetable updates and 1 pending faculty approval."
        UserRole.FACULTY -> "Today: you have 1 grading queue item and 2 class reminders."
    }
}
