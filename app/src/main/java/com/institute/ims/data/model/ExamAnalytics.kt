package com.institute.ims.data.model

data class ExamAnalytics(
    val averageMarks: Double,
    val highestMarks: Double,
    val lowestMarks: Double,
    val passPercentage: Double?,
    val passedCount: Int?,
    val failedCount: Int?,
    val totalStudents: Int,
    val buckets: List<ScoreBucket>,
    val gradeBreakdown: List<Pair<String, Int>>,
)
