package com.institute.ims.data.model

data class StudentFilterCriteria(
    val query: String = "",
    val batchId: String? = null,
    val status: StudentStatus? = null,
    val courseLabel: String? = null,
    val category: String? = null,
)
