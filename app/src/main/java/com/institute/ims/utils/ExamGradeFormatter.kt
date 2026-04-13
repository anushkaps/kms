package com.institute.ims.utils

import com.institute.ims.data.model.EvaluationType

object ExamGradeFormatter {
    fun label(score: Double, maxScore: Double, evaluationType: EvaluationType): String {
        if (maxScore <= 0) return "—"
        val pct = (score / maxScore).coerceIn(0.0, 1.0) * 100.0
        return when (evaluationType) {
            EvaluationType.GPA -> letterGrade(pct)
            EvaluationType.CCE -> competencyBand(pct)
            EvaluationType.CWA -> weightedLetter(pct)
        }
    }

    private fun letterGrade(pct: Double): String = when {
        pct >= 93 -> "A"
        pct >= 85 -> "B+"
        pct >= 78 -> "B"
        pct >= 70 -> "C+"
        pct >= 60 -> "C"
        else -> "F"
    }

    private fun competencyBand(pct: Double): String = when {
        pct >= 85 -> "Advanced"
        pct >= 70 -> "Proficient"
        pct >= 55 -> "Developing"
        else -> "Emerging"
    }

    private fun weightedLetter(pct: Double): String = when {
        pct >= 90 -> "O"
        pct >= 80 -> "A"
        pct >= 70 -> "B"
        pct >= 60 -> "C"
        else -> "D"
    }
}
