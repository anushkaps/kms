package com.institute.ims.data.repository

import com.institute.ims.data.model.AssessmentMode
import com.institute.ims.data.model.EvaluationType
import com.institute.ims.data.model.Exam
import com.institute.ims.data.model.ExamGroup
import com.institute.ims.data.model.ExamResult
import com.institute.ims.data.model.ExamStatus
import com.institute.ims.utils.ExamGradeFormatter
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** In-memory exams, groups, and results; supports [addExam] for the create-exam flow. */
object FakeExamRepository : ExamRepository {

    private val groups = mutableListOf(
        ExamGroup(id = "grp-spring", name = "Spring Session Assessments"),
        ExamGroup(id = "grp-finals", name = "End-Term Finals"),
    )

    private val exams = mutableListOf(
        Exam(
            id = "exam-001",
            title = "Data Structures - Mid-term",
            examType = "Mid-term",
            batchId = "batch-2024",
            batchLabel = "Computer Science 2024 (CS-24)",
            subjectName = "Data Structures & Algorithms",
            maxScore = 50.0,
            groupId = "grp-spring",
            evaluationType = EvaluationType.GPA,
            scheduleLabel = "12 Apr 2026 · 10:00",
            status = ExamStatus.PUBLISHED,
            assessmentMode = AssessmentMode.MARKS,
        ),
        Exam(
            id = "exam-002",
            title = "Operating Systems - Pop Quiz",
            examType = "Quiz",
            batchId = "batch-2023",
            batchLabel = "Computer Science 2023 (CS-23)",
            subjectName = "Operating Systems",
            maxScore = 20.0,
            groupId = "grp-spring",
            evaluationType = EvaluationType.CCE,
            scheduleLabel = "18 Apr 2026 · 09:30",
            status = ExamStatus.DRAFT,
            assessmentMode = AssessmentMode.MARKS,
        ),
        Exam(
            id = "exam-003",
            title = "Design & Analysis of Algorithms - Final",
            examType = "Final",
            batchId = "batch-2024",
            batchLabel = "Computer Science 2024 (CS-24)",
            subjectName = "Design & Analysis of Algorithms",
            maxScore = 100.0,
            groupId = "grp-finals",
            evaluationType = EvaluationType.CWA,
            scheduleLabel = "02 May 2026 · 14:00",
            status = ExamStatus.COMPLETED,
            assessmentMode = AssessmentMode.GRADE_BASED,
            gradeSchemeName = "Letter bands (O–F)",
            passingGradeLabel = "C",
        ),
        Exam(
            id = "exam-004",
            title = "Data Systems - Viva",
            examType = "Viva",
            batchId = "batch-2024",
            batchLabel = "Computer Science 2024 (CS-24)",
            subjectName = "Data Systems",
            maxScore = 30.0,
            groupId = "grp-spring",
            evaluationType = EvaluationType.CWA,
            scheduleLabel = "22 Apr 2026 · Slots TBA",
            status = ExamStatus.PUBLISHED,
            assessmentMode = AssessmentMode.CUSTOM,
            customSchemeName = "Viva rubric",
            customCriteriaSummary = "Oral defence criteria; qualitative merit bands.",
        ),
    )

    private val results = mutableListOf<ExamResult>()

    private val catalogSignal = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    init {
        fun ex(id: String) = exams.first { it.id == id }
        results.addAll(
            listOf(
                resultRow(ex("exam-001"), "stu-001", "Ananya Iyer", "CS24-001", 44.0),
                resultRow(ex("exam-001"), "stu-002", "Rahul Verma", "CS24-014", 38.5),
                resultRow(ex("exam-001"), "stu-009", "Aisha Patel", "CS24-019", 41.0),
                resultRow(ex("exam-003"), "stu-001", "Ananya Iyer", "CS24-001", 86.0),
                resultRow(ex("exam-003"), "stu-002", "Rahul Verma", "CS24-014", 79.0),
                resultRow(ex("exam-003"), "stu-004", "Jordan Smith", "CS24-031", 91.0),
                resultRow(ex("exam-003"), "stu-009", "Aisha Patel", "CS24-019", 88.5),
                resultRow(ex("exam-004"), "stu-001", "Ananya Iyer", "CS24-001", 28.0),
                resultRow(ex("exam-004"), "stu-002", "Rahul Verma", "CS24-014", 25.5),
                resultRow(ex("exam-004"), "stu-003", "Mei Lin Chen", "CS24-022", 27.0),
                resultRow(ex("exam-004"), "stu-004", "Jordan Smith", "CS24-031", 24.5),
                resultRow(ex("exam-004"), "stu-005", "Priya Nair", "CS23-108", 29.0),
                resultRow(ex("exam-004"), "stu-006", "Vikram Singh", "CS23-045", 22.0),
                resultRow(ex("exam-004"), "stu-007", "Sara Khan", "CS23-067", 26.5),
                resultRow(ex("exam-004"), "stu-009", "Aisha Patel", "CS24-019", 30.0),
            ),
        )
    }

    private fun resultRow(
        exam: Exam,
        studentId: String,
        studentName: String,
        studentNumber: String,
        score: Double,
    ): ExamResult {
        val grade = ExamGradeFormatter.label(score, exam.maxScore, exam.evaluationType)
        return ExamResult(
            examId = exam.id,
            studentId = studentId,
            studentName = studentName,
            studentNumber = studentNumber,
            score = score,
            gradeLabel = grade,
        )
    }

    override fun observeCatalog(): Flow<Unit> = catalogSignal.asSharedFlow()

    override fun getGroups(): List<ExamGroup> = groups.toList()

    override fun getExams(): List<Exam> = exams.toList()

    override fun getExam(examId: String): Exam? = exams.find { it.id == examId }

    override fun getResultsForExam(examId: String): List<ExamResult> {
        val exam = getExam(examId) ?: return emptyList()
        if (exam.status == ExamStatus.DRAFT) return emptyList()
        return results
            .filter { it.examId == examId }
            .map { row ->
                row.copy(
                    gradeLabel = ExamGradeFormatter.label(
                        score = row.score,
                        maxScore = exam.maxScore,
                        evaluationType = exam.evaluationType,
                    ),
                )
            }
            .sortedBy { it.studentName.lowercase() }
    }

    override fun addExam(exam: Exam) {
        exams.add(exam)
        catalogSignal.tryEmit(Unit)
    }

    override fun publishExam(examId: String): Exam? {
        val index = exams.indexOfFirst { it.id == examId }
        if (index == -1) return null
        val updated = exams[index].publish()
        exams[index] = updated
        catalogSignal.tryEmit(Unit)
        return updated
    }
}
