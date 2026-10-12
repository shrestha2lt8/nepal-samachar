package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.NewsArticle
import com.example.data.model.NewsCategory
import com.example.data.model.NewsSource

@Entity(tableName = "cached_articles")
data class CachedArticleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val sourceName: String,
    val originalUrl: String,
    val imageUrl: String?,
    val categoryName: String,
    val publishedTimeStr: String,
    val publishedEpoch: Long,
    val isBreaking: Boolean,
    val cachedAt: Long = System.currentTimeMillis()
) {
    fun toNewsArticle(isBookmarked: Boolean = false): NewsArticle {
        return NewsArticle(
            id = id,
            title = title,
            summary = summary,
            content = content,
            source = NewsSource.fromSourceName(sourceName),
            originalUrl = originalUrl,
            imageUrl = imageUrl,
            category = NewsCategory.fromCategoryName(categoryName),
            publishedTimeStr = publishedTimeStr,
            publishedEpoch = publishedEpoch,
            isBreaking = isBreaking,
            isBookmarked = isBookmarked
        )
    }

    companion object {
        fun fromNewsArticle(article: NewsArticle): CachedArticleEntity {
            return CachedArticleEntity(
                id = article.id,
                title = article.title,
                summary = article.summary,
                content = article.content,
                sourceName = article.source.name,
                originalUrl = article.originalUrl,
                imageUrl = article.imageUrl,
                categoryName = article.category.name,
                publishedTimeStr = article.publishedTimeStr,
                publishedEpoch = article.publishedEpoch,
                isBreaking = article.isBreaking,
                cachedAt = System.currentTimeMillis()
            )
        }
    }
}
