package com.institute.ims.utils

import com.institute.ims.data.model.EvaluationType

object ExamGradeFormatter {
    fun label(score: Double, maxScore: Double, evaluationType: EvaluationType): String {
        if (maxScore <= 0) return "-"
        val pct = (score / maxScore).coerceIn(0.0, 1.0) * 100.0
        return unifiedLetterGrade(pct)
    }

    private fun unifiedLetterGrade(pct: Double): String = when {
        pct >= 90 -> "A"
        pct >= 80 -> "A-"
        pct >= 70 -> "B"
        pct >= 60 -> "B-"
        pct >= 50 -> "C"
        pct >= 40 -> "C-"
        else -> "F"
    }
}
