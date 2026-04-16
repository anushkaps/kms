package com.institute.ims.data.model

data class Exam(
    val id: String,
    val title: String,
    val examType: String,
    val batchId: String,
    val batchLabel: String,
    val subjectName: String,
    val maxScore: Double,
    val groupId: String,
    val evaluationType: EvaluationType,
    val scheduleLabel: String,
    val status: ExamStatus,
    val assessmentMode: AssessmentMode,
    /** When [assessmentMode] is [AssessmentMode.MARKS], explicit pass line on max scale. */
    val passMarksThreshold: Double? = null,
    val gradeSchemeName: String? = null,
    val passingGradeLabel: String? = null,
    val customSchemeName: String? = null,
    val customCriteriaSummary: String? = null,
)
