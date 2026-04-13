package com.institute.ims.ui.examinations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.institute.ims.data.repository.ExamRepository
import com.institute.ims.data.repository.FakeExamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExamDetailViewModel(
    private val examId: String,
    private val repository: ExamRepository = FakeExamRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamDetailUiState())
    val uiState: StateFlow<ExamDetailUiState> = _uiState.asStateFlow()

    init {
        val exam = repository.getExam(examId)
        if (exam == null) {
            _uiState.update { it.copy(notFound = true) }
        } else {
            val group = repository.getGroups().find { it.id == exam.groupId }
            val results = repository.getResultsForExam(examId)
            _uiState.update {
                it.copy(
                    exam = exam,
                    groupName = group?.name,
                    results = results,
                    notFound = false,
                )
            }
        }
    }

    class Factory(
        private val examId: String,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ExamDetailViewModel(examId = examId) as T
        }
    }
}
