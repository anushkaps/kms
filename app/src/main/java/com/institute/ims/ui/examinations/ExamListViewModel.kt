package com.institute.ims.ui.examinations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.institute.ims.data.model.Exam
import com.institute.ims.data.repository.ExamRepository
import com.institute.ims.data.repository.FakeExamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExamListViewModel(
    private val repository: ExamRepository = FakeExamRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamListUiState())
    val uiState: StateFlow<ExamListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeCatalog().collect {
                refreshFromRepository()
            }
        }
        refreshFromRepository()
    }

    fun onGroupFilterChange(groupId: String?) {
        _uiState.update { it.copy(selectedGroupId = groupId) }
        refreshFromRepository()
    }

    private fun refreshFromRepository() {
        _uiState.update { s ->
            val all = repository.getExams()
            val filtered = if (s.selectedGroupId == null) {
                all
            } else {
                all.filter { it.groupId == s.selectedGroupId }
            }
            s.copy(
                groups = repository.getGroups(),
                exams = filtered.sortedWith(
                    compareBy<Exam> { it.status.ordinal }.thenBy { it.title.lowercase() },
                ),
            )
        }
    }
}
