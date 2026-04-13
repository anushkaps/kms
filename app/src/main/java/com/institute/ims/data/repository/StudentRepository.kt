package com.institute.ims.data.repository

import com.institute.ims.data.model.Batch
import com.institute.ims.data.model.Student
import com.institute.ims.data.model.StudentFilterCriteria

interface StudentRepository {
    fun getBatches(): List<Batch>
    fun getStudents(criteria: StudentFilterCriteria = StudentFilterCriteria()): List<Student>
    fun getStudent(studentId: String): Student?
}
