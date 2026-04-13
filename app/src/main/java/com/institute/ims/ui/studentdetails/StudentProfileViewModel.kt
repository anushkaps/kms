package com.institute.ims.ui.studentdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.institute.ims.data.repository.FakeStudentRepository
import com.institute.ims.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StudentProfileViewModel(
    private val studentId: String,
    private val repository: StudentRepository = FakeStudentRepository,
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
            _uiState.update {
                it.copy(
                    student = student,
                    batchDisplay = batchDisplay,
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
