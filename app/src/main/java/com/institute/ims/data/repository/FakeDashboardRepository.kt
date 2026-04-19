package com.institute.ims.data.repository

import com.institute.ims.data.model.DashboardCapabilityAction
import com.institute.ims.data.model.DashboardCapabilityHighlight
import com.institute.ims.data.model.DashboardModuleCard
import com.institute.ims.data.model.DashboardModuleId
import com.institute.ims.data.model.DashboardStat
import com.institute.ims.data.model.ExamStatus
import com.institute.ims.data.model.StudentFilterCriteria
import com.institute.ims.data.model.StudentStatus
import com.institute.ims.data.model.UserRole
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Dashboard stats and module copy derived from the same seed repositories as the rest of the app. */
object FakeDashboardRepository : DashboardRepository {

    private fun currentStudentCount(): Int =
        FakeStudentRepository.getStudents(StudentFilterCriteria(status = StudentStatus.CURRENT)).size

    private fun batchCount(): Int = FakeStudentRepository.getBatches().size

    private fun activeExamCount(): Int =
        FakeExamRepository.getExams().count { it.status != ExamStatus.COMPLETED }

    private fun draftExamCount(): Int =
        FakeExamRepository.getExams().count { it.status == ExamStatus.DRAFT }

    private fun publishedExamCount(): Int =
        FakeExamRepository.getExams().count { it.status == ExamStatus.PUBLISHED }

    override fun getSummaryStats(role: UserRole): List<DashboardStat> {
        val students = currentStudentCount()
        val exams = activeExamCount()
        val batches = batchCount()
        val drafts = draftExamCount()
        val published = publishedExamCount()
        return when (role) {
            UserRole.ADMIN -> listOf(
                DashboardStat(
                    id = "students",
                    label = "Students",
                    value = students.toString(),
                    caption = "Enrolled",
                ),
                DashboardStat(
                    id = "exams",
                    label = "Active exams",
                    value = exams.toString(),
                    caption = "Running",
                ),
                DashboardStat(
                    id = "reports",
                    label = "Reports due",
                    value = drafts.toString(),
                    caption = "Pending",
                ),
                DashboardStat(
                    id = "batches",
                    label = "Batches",
                    value = batches.toString(),
                    caption = "Active",
                ),
            )
            UserRole.FACULTY -> listOf(
                DashboardStat(
                    id = "grading_queue",
                    label = "Grading queue",
                    value = drafts.toString(),
                    caption = "Draft papers",
                ),
                DashboardStat(
                    id = "upcoming_exams",
                    label = "Upcoming exams",
                    value = published.toString(),
                    caption = "Next 7 days",
                ),
            )
        }
    }

    override fun getModuleCards(): List<DashboardModuleCard> {
        val students = currentStudentCount()
        val exams = activeExamCount()
        return listOf(
            DashboardModuleCard(
                id = DashboardModuleId.STUDENTS,
                title = "Students",
                description = "$students enrolled",
            ),
            DashboardModuleCard(
                id = DashboardModuleId.EXAMS,
                title = "Exams",
                description = "$exams active",
            ),
        )
    }

    override fun getQuickChipLabels(): List<String> = listOf(
        "Students",
        "Exams",
        "News",
    )

    override fun getOverviewLine(role: UserRole): String {
        val today = LocalDate.now(ZoneId.systemDefault())
        val fmt = DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.getDefault())
        return today.format(fmt)
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
