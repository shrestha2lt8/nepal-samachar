package com.example.data.repository

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
            // Check if local cache has items, if empty pre-populate with rich seed news
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

    suspend fun refreshNews(): Result<Unit> = withContext(Dispatchers.IO) {
        _isRefreshing.value = true
        try {
            val freshArticles = remoteSource.fetchAllSources()
            if (freshArticles.isNotEmpty()) {
                val entities = freshArticles.map { CachedArticleEntity.fromNewsArticle(it) }
                cachedArticleDao.insertArticles(entities)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
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
}
