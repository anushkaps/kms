package com.institute.ims.ui.studentdetails

import com.institute.ims.data.model.Batch
import com.institute.ims.data.model.Student
import com.institute.ims.data.model.StudentStatus

data class StudentListUiState(
    val searchQuery: String = "",
    val batchIdFilter: String? = null,
    val statusFilter: StudentStatus? = null,
    val courseLabelFilter: String? = null,
    val categoryFilter: String? = null,
    val advancedPanelExpanded: Boolean = false,
    val batches: List<Batch> = emptyList(),
    val students: List<Student> = emptyList(),
    val courseLabels: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
)
