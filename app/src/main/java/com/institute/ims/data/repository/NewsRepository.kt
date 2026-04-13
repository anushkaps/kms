package com.institute.ims.data.repository

import com.institute.ims.data.model.NewsItem

interface NewsRepository {
    fun getNews(): List<NewsItem>
}
