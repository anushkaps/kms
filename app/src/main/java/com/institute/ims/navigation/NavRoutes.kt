package com.institute.ims.navigation

object NavRoutes {
    const val Splash = "splash"
    const val RoleSelect = "role_select"
    const val Dashboard = "dashboard/{userId}"
    const val StudentList = "student_list"
    const val StudentProfile = "student_profile/{studentId}"
    const val ExamList = "exam_list"
    const val CreateExam = "create_exam"
    const val ExamDetail = "exam_detail/{examId}"
    const val Report = "report/{examId}"
    const val RegionalSettings = "regional_settings"
    const val CapabilityInfo = "capability_info/{stubId}"

    fun dashboard(userId: String): String = "dashboard/${encode(userId)}"

    fun capabilityInfo(stubId: String): String = "capability_info/${encode(stubId)}"

    fun studentProfile(studentId: String): String = "student_profile/${encode(studentId)}"

    fun examDetail(examId: String): String = "exam_detail/${encode(examId)}"

    fun report(examId: String): String = "report/${encode(examId)}"

    private fun encode(segment: String): String = android.net.Uri.encode(segment)
}
