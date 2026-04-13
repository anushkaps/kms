package com.institute.ims.data.repository

import com.institute.ims.data.model.Exam
import com.institute.ims.data.model.ExamGroup
import com.institute.ims.data.model.ExamResult
import kotlinx.coroutines.flow.Flow

interface ExamRepository {
    fun observeCatalog(): Flow<Unit>
    fun getGroups(): List<ExamGroup>
    fun getExams(): List<Exam>
    fun getExam(examId: String): Exam?
    fun getResultsForExam(examId: String): List<ExamResult>
    fun addExam(exam: Exam)
}
