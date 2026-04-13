package com.institute.ims.data.repository

import com.institute.ims.data.model.DashboardModuleCard
import com.institute.ims.data.model.DashboardModuleId
import com.institute.ims.data.model.DashboardStat
import com.institute.ims.data.model.UserRole

/** Static dashboard stats, module cards, and quick-action chip labels. */
object FakeDashboardRepository : DashboardRepository {
    override fun getSummaryStats(): List<DashboardStat> = listOf(
        DashboardStat(
            id = "students",
            label = "Total students",
            value = "10",
            caption = "Current & former",
        ),
        DashboardStat(
            id = "batches",
            label = "Active batches",
            value = "2",
            caption = "CS programmes",
        ),
        DashboardStat(
            id = "exams",
            label = "Upcoming exams",
            value = "4",
            caption = "Next 14 days",
        ),
        DashboardStat(
            id = "reports",
            label = "Reports ready",
            value = "3",
            caption = "Awaiting review",
        ),
    )

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
