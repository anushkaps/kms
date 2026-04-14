package com.institute.ims.data.repository

import com.institute.ims.data.model.NewsItem

/** Small seeded news list filtered on the Dashboard. */
object FakeNewsRepository : NewsRepository {
    private val items = listOf(
        NewsItem(
            id = "news-1",
            title = "Semester registration window opens Monday",
            body = "All CS batches may complete registration online. Late submissions require admin approval.",
            publishedAtEpochMs = 1_712_000_000_000L,
            tag = "Registrar",
        ),
        NewsItem(
            id = "news-2",
            title = "Mid-term assessment schedule published",
            body = "Faculty are asked to confirm room allocations by Friday. Students will see slots in the examinations module.",
            publishedAtEpochMs = 1_712_086_400_000L,
            tag = "Academic",
        ),
        NewsItem(
            id = "news-3",
            title = "Campus network maintenance - brief outage",
            body = "IT will perform upgrades Sunday 02:00–04:00. IMS remains available on mobile data.",
            publishedAtEpochMs = 1_712_172_800_000L,
            tag = "IT",
        ),
    )

    override fun getNews(): List<NewsItem> = items
}
