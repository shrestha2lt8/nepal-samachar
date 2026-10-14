package com.example.data.repository

import android.util.Log
import com.example.data.local.BookmarkDao
import com.example.data.local.BookmarkEntity
import com.example.data.local.CachedArticleDao
import com.example.data.local.CachedArticleEntity
import com.example.data.model.NewsArticle
import com.example.data.remote.NepaliNewsDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsRepository(
    private val remoteSource: NepaliNewsDataSource,
    private val bookmarkDao: BookmarkDao,
    private val cachedArticleDao: CachedArticleDao
) {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: Flow<Boolean> = _isRefreshing

    val cachedCount: Flow<Int> = cachedArticleDao.getCachedCount()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            // Prune any news older than 15 days on startup
            pruneOldNews()

            // Check if local cache has items, if empty pre-populate with seed news
            val existing = cachedArticleDao.getAllCachedArticles().firstOrNull()
            if (existing.isNullOrEmpty()) {
                val seedEntities = remoteSource.getSeedArticles().map {
                    CachedArticleEntity.fromNewsArticle(it)
                }
                cachedArticleDao.insertArticles(seedEntities)
            }
            // Fetch live feeds from Nepali news sources
            refreshNews()
        }
    }

    // Reactive articles stream backed entirely by Room local cache + Bookmarks status
    val articlesWithBookmarkStatus: Flow<List<NewsArticle>> =
        combine(
            cachedArticleDao.getAllCachedArticles(),
            bookmarkDao.getAllBookmarkedIds()
        ) { cachedEntities, bookmarkedIds ->
            val bookmarkedSet = bookmarkedIds.toSet()
            cachedEntities.map { entity ->
                entity.toNewsArticle(isBookmarked = bookmarkedSet.contains(entity.id))
            }
        }

    val bookmarkedArticles: Flow<List<NewsArticle>> =
        bookmarkDao.getAllBookmarks().map { entities ->
            entities.map { it.toNewsArticle() }
        }

    // Auto-prune news older than 15 days (15 * 24 * 60 * 60 * 1000 ms)
    suspend fun pruneOldNews(): Int = withContext(Dispatchers.IO) {
        val fifteenDaysAgo = System.currentTimeMillis() - (15L * 24L * 60L * 60L * 1000L)
        val deletedCount = cachedArticleDao.pruneArticlesOlderThan(fifteenDaysAgo)
        if (deletedCount > 0) {
            Log.d("NewsRepository", "Auto-pruned $deletedCount articles older than 15 days")
        }
        deletedCount
    }

    suspend fun refreshNews(): Result<Int> = withContext(Dispatchers.IO) {
        _isRefreshing.value = true
        try {
            // Auto delete news older than 15 days before updating
            pruneOldNews()

            val freshArticles = remoteSource.fetchAllSources()
            if (freshArticles.isNotEmpty()) {
                val entities = freshArticles.map { CachedArticleEntity.fromNewsArticle(it) }
                cachedArticleDao.insertArticles(entities)
                Log.d("NewsRepository", "Refreshed and stored ${entities.size} live articles")
            }
            Result.success(freshArticles.size)
        } catch (e: Exception) {
            Log.w("NewsRepository", "Refresh error: ${e.message}")
            Result.failure(e)
        } finally {
            _isRefreshing.value = false
        }
    }

    suspend fun toggleBookmark(article: NewsArticle) = withContext(Dispatchers.IO) {
        if (article.isBookmarked) {
            bookmarkDao.deleteBookmarkById(article.id)
        } else {
            val entity = BookmarkEntity.fromNewsArticle(article.copy(isBookmarked = true))
            bookmarkDao.insertBookmark(entity)
        }
    }

    suspend fun removeBookmark(id: String) = withContext(Dispatchers.IO) {
        bookmarkDao.deleteBookmarkById(id)
    }

    suspend fun updateArticleImage(id: String, imageUrl: String) = withContext(Dispatchers.IO) {
        if (imageUrl.isNotBlank()) {
            cachedArticleDao.updateImageUrl(id, imageUrl)
            bookmarkDao.updateImageUrl(id, imageUrl)
        }
    }
}
