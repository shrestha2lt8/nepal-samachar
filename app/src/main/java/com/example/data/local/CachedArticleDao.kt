package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedArticleDao {
    @Query("SELECT * FROM cached_articles ORDER BY publishedEpoch DESC")
    fun getAllCachedArticles(): Flow<List<CachedArticleEntity>>

    @Query("SELECT * FROM cached_articles WHERE categoryName = :categoryName ORDER BY publishedEpoch DESC")
    fun getCachedArticlesByCategory(categoryName: String): Flow<List<CachedArticleEntity>>

    @Query("SELECT * FROM cached_articles WHERE id = :id LIMIT 1")
    suspend fun getArticleById(id: String): CachedArticleEntity?

    @Query("SELECT COUNT(*) FROM cached_articles")
    fun getCachedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<CachedArticleEntity>)

    // Prune articles older than 15 days, NEVER deleting bookmarked articles even if older than 15 days
    @Query("DELETE FROM cached_articles WHERE publishedEpoch < :cutoffTimestamp AND id NOT IN (SELECT id FROM bookmarks)")
    suspend fun pruneArticlesOlderThan(cutoffTimestamp: Long): Int

    @Query("DELETE FROM cached_articles WHERE cachedAt < :expiryTimestamp")
    suspend fun pruneOldArticles(expiryTimestamp: Long)

    @Query("UPDATE cached_articles SET imageUrl = :imageUrl WHERE id = :id")
    suspend fun updateImageUrl(id: String, imageUrl: String)

    @Query("DELETE FROM cached_articles")
    suspend fun clearAll()
}
