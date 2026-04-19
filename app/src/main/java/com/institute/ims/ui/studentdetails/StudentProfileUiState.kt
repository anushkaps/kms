package com.institute.ims.ui.studentdetails

import com.institute.ims.data.model.Student

data class StudentExamParticipation(
    val examId: String,
    val examTitle: String,
    val examType: String,
    val scheduleLabel: String,
    val score: Double,
    val gradeLabel: String?,
)

data class StudentProfileUiState(
    val student: Student? = null,
    val batchDisplay: String? = null,
    val examParticipations: List<StudentExamParticipation> = emptyList(),
    val notFound: Boolean = false,
)
