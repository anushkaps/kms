package com.institute.ims.data.model

data class ExamResult(
    val examId: String,
    val studentId: String,
    val studentName: String,
    val studentNumber: String,
    val score: Double,
    val gradeLabel: String? = null,
)
