package com.institute.ims.data.model

data class Student(
    val id: String,
    val name: String,
    val studentNumber: String,
    val batchId: String,
    val status: StudentStatus,
    val courseLabel: String,
    val category: String,
    val email: String,
    val phone: String,
    val academicYearLabel: String,
    val guardianName: String? = null,
)
