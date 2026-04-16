package com.institute.ims.data.repository

import com.institute.ims.data.model.DashboardCapabilityAction
import com.institute.ims.data.model.DashboardCapabilityHighlight
import com.institute.ims.data.model.DashboardModuleCard
import com.institute.ims.data.model.DashboardModuleId
import com.institute.ims.data.model.DashboardStat
import com.institute.ims.data.model.UserRole

/** Static dashboard stats, module cards, and quick-action chip labels. */
object FakeDashboardRepository : DashboardRepository {
    override fun getSummaryStats(role: UserRole): List<DashboardStat> = when (role) {
        UserRole.ADMIN -> listOf(
            DashboardStat(
                id = "students",
                label = "Students",
                value = "847",
                caption = "Enrolled",
            ),
            DashboardStat(
                id = "exams",
                label = "Active exams",
                value = "12",
                caption = "Running",
            ),
            DashboardStat(
                id = "reports",
                label = "Reports due",
                value = "3",
                caption = "Pending",
            ),
            DashboardStat(
                id = "batches",
                label = "Batches",
                value = "6",
                caption = "Active",
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
            title = "Students",
            description = "847 enrolled",
        ),
        DashboardModuleCard(
            id = DashboardModuleId.EXAMS,
            title = "Exams",
            description = "12 active",
        ),
    )

    override fun getQuickChipLabels(): List<String> = listOf(
        "Students",
        "Exams",
        "News",
    )

    override fun getOverviewLine(role: UserRole): String = when (role) {
        UserRole.ADMIN -> "Mon, 14 Apr 2026"
        UserRole.FACULTY -> "Today: you have 1 grading queue item and 2 class reminders."
    }

    override fun getCapabilityHighlights(): List<DashboardCapabilityHighlight> = listOf(
        DashboardCapabilityHighlight(
            id = "cap-nav",
            title = "Search, ease of use, and campus news",
            lines = listOf(
                "Iconic hub with an innovative search bar: jump to Students, Exams, people, papers, or news in one place.",
                "User-friendly layout for staff with basic computer skills: clear labels, large targets, predictable flow.",
                "Shallow learning curve: start from this dashboard, then open a module card or a search result.",
                "Latest institute news is listed on the hub after you sign in (scroll to Latest news).",
            ),
        ),
        DashboardCapabilityHighlight(
            id = "cap-locale-general",
            title = "Language and basic configuration",
            lines = listOf(
                "Language, country, currency, and time zone: use the Language & region card above, or search for \"language\" / \"currency\".",
                "General institute settings (default grading scales, automatic unique ID rules, numbering schemes) are configured in a full IMS deployment; here you can inspect how student numbers and grade models appear in live screens.",
            ),
            actionLabel = "Open language & region",
            action = DashboardCapabilityAction.OPEN_REGIONAL,
        ),
        DashboardCapabilityHighlight(
            id = "cap-grading-ids",
            title = "Grading systems and unique student IDs",
            lines = listOf(
                "Grading: GPA, CCE, and CWA-style evaluation paths with reports and analytics under Examinations.",
                "Automatic unique IDs: every learner carries a stable student number in the directory (seed data simulates production IDs).",
            ),
            actionLabel = "Open Examinations",
            action = DashboardCapabilityAction.OPEN_EXAMS,
        ),
        DashboardCapabilityHighlight(
            id = "cap-catalog",
            title = "Courses, batches, subjects, electives, and transfers",
            lines = listOf(
                "Manage programmes, batches, subjects (including electives), and batch-to-batch transfers in a complete IMS.",
                "This prototype: browse batches, programmes, and cohort context under Student Details; transfers are described as part of that same academic structure.",
            ),
            actionLabel = "Open Student Details",
            action = DashboardCapabilityAction.OPEN_STUDENTS,
        ),
        DashboardCapabilityHighlight(
            id = "cap-admission-sms",
            title = "Admission forms and SMS alerts",
            lines = listOf(
                "Customizable admission forms for intake workflows.",
                "SMS module for single-student or group alerts (reminders, closures, exam notices).",
                "Not wired to a real SMS gateway in this build; open the info sheet for how the module fits a production stack.",
            ),
            actionLabel = "About admissions & SMS",
            action = DashboardCapabilityAction.OPEN_ADMISSION_SMS_INFO,
        ),
        DashboardCapabilityHighlight(
            id = "cap-lifecycle",
            title = "Student categories and graduation",
            lines = listOf(
                "Student categories (e.g. undergraduate, exchange) appear in the directory and filters.",
                "Graduation and alumni: use the Former status filter for completed cohorts; ceremonies and certificates are part of the full IMS product.",
            ),
            actionLabel = "Open Student Details",
            action = DashboardCapabilityAction.OPEN_STUDENTS,
        ),
    )
}
