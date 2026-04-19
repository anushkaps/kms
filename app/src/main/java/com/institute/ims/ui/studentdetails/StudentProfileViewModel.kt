package com.institute.ims.ui.studentdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.institute.ims.data.repository.ExamRepository
import com.institute.ims.data.repository.FakeExamRepository
import com.institute.ims.data.repository.FakeStudentRepository
import com.institute.ims.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StudentProfileViewModel(
    private val studentId: String,
    private val repository: StudentRepository = FakeStudentRepository,
    private val examRepository: ExamRepository = FakeExamRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentProfileUiState())
    val uiState: StateFlow<StudentProfileUiState> = _uiState.asStateFlow()

    init {
        val student = repository.getStudent(studentId)
        if (student == null) {
            _uiState.update { it.copy(notFound = true) }
        } else {
            val batch = repository.getBatches().find { it.id == student.batchId }
            val batchDisplay = batch?.let { b -> "${b.name} (${b.code})" }
            val participations = examRepository.getExams().mapNotNull { exam ->
                val row = examRepository
                    .getResultsForExam(exam.id)
                    .firstOrNull { it.studentId == student.id }
                    ?: return@mapNotNull null
                StudentExamParticipation(
                    examId = exam.id,
                    examTitle = exam.title,
                    examType = exam.examType,
                    scheduleLabel = exam.scheduleLabel,
                    score = row.score,
                    gradeLabel = row.gradeLabel,
                )
            }
            _uiState.update {
                it.copy(
                    student = student,
                    batchDisplay = batchDisplay,
                    examParticipations = participations,
                    notFound = false,
                )
            }
        }
    }

    class Factory(
        private val studentId: String,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StudentProfileViewModel(studentId = studentId) as T
        }
    }
}
