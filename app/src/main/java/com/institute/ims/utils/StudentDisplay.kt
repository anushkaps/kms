package com.institute.ims.utils

/** Short labels so batch/status/category fit on one row without ugly truncation. */
fun studentCategoryShortLabel(category: String): String = when (category.lowercase()) {
    "undergraduate" -> "UG"
    "postgraduate" -> "PG"
    "exchange" -> "Exch"
    "alumni" -> "Alumni"
    "doctoral" -> "PhD"
    else ->
        if (category.length > 12) category.take(12).trim() + "…" else category
}
