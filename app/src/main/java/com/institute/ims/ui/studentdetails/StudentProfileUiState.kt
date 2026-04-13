package com.institute.ims.ui.studentdetails

import com.institute.ims.data.model.Student

data class StudentProfileUiState(
    val student: Student? = null,
    val batchDisplay: String? = null,
    val notFound: Boolean = false,
)
