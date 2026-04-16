package com.institute.ims.ui.examinations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.institute.ims.data.repository.ExamRepository
import com.institute.ims.data.repository.FakeExamRepository
import com.institute.ims.utils.ExamAnalyticsCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Resolves one exam by id and derives analytics from repository results (or null if none). */
class ReportViewModel(
    private val examId: String,
    private val repository: ExamRepository = FakeExamRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        val exam = repository.getExam(examId)
        if (exam == null) {
            _uiState.update { it.copy(notFound = true) }
        } else {
            val group = repository.getGroups().find { it.id == exam.groupId }
            val results = repository.getResultsForExam(examId)
            val analytics = ExamAnalyticsCalculator.compute(exam, results)
            val topResults = results.sortedByDescending { it.score }.take(3)
            _uiState.update {
                it.copy(
                    exam = exam,
                    groupName = group?.name,
                    analytics = analytics,
                    topResults = topResults,
                    notFound = false,
                )
            }
        }
    }

    fun onReportTabChange(tab: ReportCenterTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    class Factory(
        private val examId: String,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReportViewModel(examId = examId) as T
        }
    }
}
