package com.institute.ims.data.model

data class NewsItem(
    val id: String,
    val title: String,
    val body: String,
    val publishedAtEpochMs: Long,
    val tag: String? = null,
)
