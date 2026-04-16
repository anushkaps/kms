package com.institute.ims.data.model

/** How the exam is scored: raw marks, grade bands, or an institute-defined custom rubric. */
enum class AssessmentMode {
    MARKS,
    GRADE_BASED,
    CUSTOM,
}

fun AssessmentMode.uiLabel(): String = when (this) {
    AssessmentMode.MARKS -> "Marks"
    AssessmentMode.GRADE_BASED -> "Grade-based"
    AssessmentMode.CUSTOM -> "Custom"
}

/** Short chip labels so format selectors fit on narrow layouts. */
fun AssessmentMode.shortFormatLabel(): String = when (this) {
    AssessmentMode.MARKS -> "Marks"
    AssessmentMode.GRADE_BASED -> "Grade"
    AssessmentMode.CUSTOM -> "Custom"
}
