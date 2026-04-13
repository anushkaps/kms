package com.institute.ims.ui.studentdetails

import androidx.lifecycle.ViewModel
import com.institute.ims.data.model.StudentFilterCriteria
import com.institute.ims.data.model.StudentStatus
import com.institute.ims.data.repository.FakeStudentRepository
import com.institute.ims.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StudentListViewModel(
    private val repository: StudentRepository = FakeStudentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentListUiState())
    val uiState: StateFlow<StudentListUiState> = _uiState.asStateFlow()

    init {
        val batches = repository.getBatches()
        val all = repository.getStudents(StudentFilterCriteria())
        _uiState.update {
            it.copy(
                batches = batches,
                courseLabels = all.map { s -> s.courseLabel }.distinct().sorted(),
                categories = all.map { s -> s.category }.distinct().sorted(),
            )
        }
        refreshStudents()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        refreshStudents()
    }

    fun onBatchFilterChange(batchId: String?) {
        _uiState.update { it.copy(batchIdFilter = batchId) }
        refreshStudents()
    }

    fun onStatusFilterChange(status: StudentStatus?) {
        _uiState.update { it.copy(statusFilter = status) }
        refreshStudents()
    }

    fun onCourseFilterChange(courseLabel: String?) {
        _uiState.update { it.copy(courseLabelFilter = courseLabel) }
        refreshStudents()
    }

    fun onCategoryFilterChange(category: String?) {
        _uiState.update { it.copy(categoryFilter = category) }
        refreshStudents()
    }

    fun onToggleAdvancedPanel() {
        _uiState.update { it.copy(advancedPanelExpanded = !it.advancedPanelExpanded) }
    }

    fun onClearAdvancedFilters() {
        _uiState.update {
            it.copy(
                courseLabelFilter = null,
                categoryFilter = null,
            )
        }
        refreshStudents()
    }

    fun onResetAllFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                batchIdFilter = null,
                statusFilter = null,
                courseLabelFilter = null,
                categoryFilter = null,
            )
        }
        refreshStudents()
    }

    private fun refreshStudents() {
        _uiState.update { s ->
            val criteria = StudentFilterCriteria(
                query = s.searchQuery,
                batchId = s.batchIdFilter,
                status = s.statusFilter,
                courseLabel = s.courseLabelFilter,
                category = s.categoryFilter,
            )
            s.copy(students = repository.getStudents(criteria))
        }
    }
}
