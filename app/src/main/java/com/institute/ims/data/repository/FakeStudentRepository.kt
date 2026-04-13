package com.institute.ims.data.repository

import com.institute.ims.data.model.Batch
import com.institute.ims.data.model.Student
import com.institute.ims.data.model.StudentFilterCriteria
import com.institute.ims.data.model.StudentStatus

/** Fixed seed students and batches; filtering and search run in memory. */
object FakeStudentRepository : StudentRepository {
    private val batches = listOf(
        Batch(id = "batch-2024", name = "Computer Science 2024", code = "CS-24"),
        Batch(id = "batch-2023", name = "Computer Science 2023", code = "CS-23"),
    )

    private val students = listOf(
        Student(
            id = "stu-001",
            name = "Ananya Iyer",
            studentNumber = "CS24-001",
            batchId = "batch-2024",
            status = StudentStatus.CURRENT,
            courseLabel = "B.Sc. Computer Science",
            category = "Undergraduate",
            email = "ananya.iyer@student.inst.edu",
            phone = "+91 98765 43201",
            academicYearLabel = "Year 2",
            guardianName = "R. Iyer",
        ),
        Student(
            id = "stu-002",
            name = "Rahul Verma",
            studentNumber = "CS24-014",
            batchId = "batch-2024",
            status = StudentStatus.CURRENT,
            courseLabel = "B.Sc. Computer Science",
            category = "Undergraduate",
            email = "rahul.verma@student.inst.edu",
            phone = "+91 98765 43214",
            academicYearLabel = "Year 2",
            guardianName = "S. Verma",
        ),
        Student(
            id = "stu-003",
            name = "Mei Lin Chen",
            studentNumber = "CS24-022",
            batchId = "batch-2024",
            status = StudentStatus.CURRENT,
            courseLabel = "B.Sc. Computer Science",
            category = "Exchange",
            email = "mei.chen@student.inst.edu",
            phone = "+65 9123 4400",
            academicYearLabel = "Year 1",
            guardianName = null,
        ),
        Student(
            id = "stu-004",
            name = "Jordan Smith",
            studentNumber = "CS24-031",
            batchId = "batch-2024",
            status = StudentStatus.CURRENT,
            courseLabel = "B.Sc. Data Science",
            category = "Undergraduate",
            email = "jordan.smith@student.inst.edu",
            phone = "+1 415 555 0198",
            academicYearLabel = "Year 1",
            guardianName = "A. Smith",
        ),
        Student(
            id = "stu-005",
            name = "Priya Nair",
            studentNumber = "CS23-108",
            batchId = "batch-2023",
            status = StudentStatus.CURRENT,
            courseLabel = "B.Sc. Computer Science",
            category = "Undergraduate",
            email = "priya.nair@student.inst.edu",
            phone = "+91 98765 44108",
            academicYearLabel = "Year 3",
            guardianName = "L. Nair",
        ),
        Student(
            id = "stu-006",
            name = "Vikram Singh",
            studentNumber = "CS23-045",
            batchId = "batch-2023",
            status = StudentStatus.FORMER,
            courseLabel = "B.Sc. Computer Science",
            category = "Undergraduate",
            email = "vikram.singh.alumni@inst.edu",
            phone = "+91 98765 44045",
            academicYearLabel = "Graduated 2025",
            guardianName = "K. Singh",
        ),
        Student(
            id = "stu-007",
            name = "Sara Khan",
            studentNumber = "CS23-067",
            batchId = "batch-2023",
            status = StudentStatus.FORMER,
            courseLabel = "B.Sc. Data Science",
            category = "Undergraduate",
            email = "sara.khan.alumni@inst.edu",
            phone = "+91 98765 44067",
            academicYearLabel = "Graduated 2025",
            guardianName = "H. Khan",
        ),
        Student(
            id = "stu-008",
            name = "Leo Martinez",
            studentNumber = "CS24-008",
            batchId = "batch-2024",
            status = StudentStatus.FORMER,
            courseLabel = "B.Sc. Computer Science",
            category = "Undergraduate",
            email = "leo.martinez@personal.dev",
            phone = "+34 600 112 233",
            academicYearLabel = "Withdrawn",
            guardianName = "M. Martinez",
        ),
        Student(
            id = "stu-009",
            name = "Aisha Patel",
            studentNumber = "CS24-019",
            batchId = "batch-2024",
            status = StudentStatus.CURRENT,
            courseLabel = "B.Sc. Data Science",
            category = "Undergraduate",
            email = "aisha.patel@student.inst.edu",
            phone = "+91 98765 44019",
            academicYearLabel = "Year 2",
            guardianName = "N. Patel",
        ),
        Student(
            id = "stu-010",
            name = "Chris Wong",
            studentNumber = "CS23-091",
            batchId = "batch-2023",
            status = StudentStatus.FORMER,
            courseLabel = "B.Sc. Computer Science",
            category = "Undergraduate",
            email = "chris.wong.alumni@inst.edu",
            phone = "+852 6123 8891",
            academicYearLabel = "Graduated 2024",
            guardianName = "J. Wong",
        ),
    )

    override fun getBatches(): List<Batch> = batches

    override fun getStudents(criteria: StudentFilterCriteria): List<Student> =
        students
            .asSequence()
            .filter { matches(it, criteria) }
            .sortedWith(compareBy({ it.name.lowercase() }, { it.studentNumber }))
            .toList()

    override fun getStudent(studentId: String): Student? = students.find { it.id == studentId }

    private fun matches(student: Student, c: StudentFilterCriteria): Boolean {
        if (c.batchId != null && student.batchId != c.batchId) return false
        if (c.status != null && student.status != c.status) return false
        if (c.courseLabel != null && student.courseLabel != c.courseLabel) return false
        if (c.category != null && !student.category.equals(c.category, ignoreCase = true)) return false
        val q = c.query.trim()
        if (q.isNotEmpty()) {
            val haystack = listOf(
                student.name,
                student.studentNumber,
                student.id,
                student.email,
            )
            if (haystack.none { it.contains(q, ignoreCase = true) }) return false
        }
        return true
    }
}
