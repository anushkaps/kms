package com.institute.ims.utils

import com.institute.ims.data.model.Exam
import com.institute.ims.data.model.ExamAnalytics
import com.institute.ims.data.model.ExamResult
import com.institute.ims.data.model.ScoreBucket
import kotlin.math.roundToInt

/**
 * Pure helpers for exam reports: mean / min / max, pass rate (≥ 40% of [Exam.maxScore] when max is valid),
 * optional %-of-max buckets, and a zero-count bucket list for empty UI previews.
 */
object ExamAnalyticsCalculator {

    const val PASS_MARK_FRACTION: Double = 0.4

    fun compute(exam: Exam, results: List<ExamResult>): ExamAnalytics? {
        if (results.isEmpty()) return null

        val scores = results.map { it.score }
        val average = scores.average()
        val highest = scores.maxOrNull() ?: 0.0
        val lowest = scores.minOrNull() ?: 0.0
        val max = exam.maxScore

        val passThreshold = if (max.isFinite() && max > 0) max * PASS_MARK_FRACTION else null
        val passedCount: Int?
        val failedCount: Int?
        val passPercentage: Double?
        if (passThreshold != null) {
            val passed = results.count { it.score >= passThreshold }
            passedCount = passed
            failedCount = results.size - passed
            passPercentage = (passed * 100.0) / results.size
        } else {
            passedCount = null
            failedCount = null
            passPercentage = null
        }

        val buckets = if (max.isFinite() && max > 0) {
            buildPercentageBuckets(results, max)
        } else {
            emptyList()
        }

        val gradeBreakdown = results
            .groupBy { it.gradeLabel ?: "-" }
            .map { (label, rows) -> label to rows.size }
            .sortedByDescending { it.second }

        return ExamAnalytics(
            averageMarks = average,
            highestMarks = highest,
            lowestMarks = lowest,
            passPercentage = passPercentage,
            passedCount = passedCount,
            failedCount = failedCount,
            totalStudents = results.size,
            buckets = buckets,
            gradeBreakdown = gradeBreakdown,
        )
    }

    /** Same labels as [buildPercentageBuckets]; all counts zero (empty-state preview). */
    fun emptyDistributionPreview(): List<ScoreBucket> = listOf(
        ScoreBucket(label = "0–39%", count = 0),
        ScoreBucket(label = "40–59%", count = 0),
        ScoreBucket(label = "60–79%", count = 0),
        ScoreBucket(label = "80–100%", count = 0),
    )

    /**
     * Buckets by % of max marks: 0–39%, 40–59%, 60–79%, 80–100%.
     */
    private fun buildPercentageBuckets(results: List<ExamResult>, maxScore: Double): List<ScoreBucket> {
        if (!maxScore.isFinite() || maxScore <= 0) return emptyList()
        val counts = intArrayOf(0, 0, 0, 0)
        for (r in results) {
            val pct = ((r.score / maxScore).coerceIn(0.0, 1.0)) * 100.0
            val idx = when {
                pct < 40 -> 0
                pct < 60 -> 1
                pct < 80 -> 2
                else -> 3
            }
            counts[idx]++
        }
        return listOf(
            ScoreBucket(label = "0–39%", count = counts[0]),
            ScoreBucket(label = "40–59%", count = counts[1]),
            ScoreBucket(label = "60–79%", count = counts[2]),
            ScoreBucket(label = "80–100%", count = counts[3]),
        )
    }

    fun formatOneDecimal(value: Double): String {
        val rounded = (value * 10).roundToInt() / 10.0
        return if (rounded == rounded.toLong().toDouble()) {
            rounded.toLong().toString()
        } else {
            rounded.toString()
        }
    }

    fun formatPercent(value: Double): String = "${(value * 10).roundToInt() / 10.0}%"
}
