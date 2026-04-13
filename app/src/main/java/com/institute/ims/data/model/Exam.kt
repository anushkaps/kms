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
    val isCustomType: Boolean,
)
