package com.institute.ims.data.model

data class DashboardStat(
    val id: String,
    val label: String,
    val value: String,
    val caption: String? = null,
)
