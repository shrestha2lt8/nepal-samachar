package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NewsDatabase
import com.example.data.model.NewsArticle
import com.example.data.model.NewsCategory
import com.example.data.model.NewsSource
import com.example.data.remote.NepaliNewsDataSource
import com.example.data.repository.NewsRepository
import com.example.util.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab(val titleNepali: String, val titleEnglish: String) {
    FEED("समाचार", "Feed"),
    BOOKMARKS("बुकमार्क", "Bookmarks"),
    SEARCH("खोज", "Search")
}

data class NewsUiState(
    val articles: List<NewsArticle> = emptyList(),
    val breakingNews: List<NewsArticle> = emptyList(),
    val bookmarks: List<NewsArticle> = emptyList(),
    val selectedCategory: NewsCategory = NewsCategory.LATEST,
    val selectedSource: NewsSource = NewsSource.ALL,
    val searchQuery: String = "",
    val activeTab: MainTab = MainTab.FEED,
    val activeReaderArticle: NewsArticle? = null,
    val isRefreshing: Boolean = false,
    val isDarkTheme: Boolean = false, // Defaults strictly to clean light theme
    val isOnline: Boolean = true,
    val cachedCount: Int = 0,
    val userNotice: String? = null
)

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NewsRepository
    private val networkMonitor = NetworkMonitor(application)

    init {
        val database = NewsDatabase.getInstance(application)
        val remoteSource = NepaliNewsDataSource()
        repository = NewsRepository(
            remoteSource = remoteSource,
            bookmarkDao = database.bookmarkDao(),
            cachedArticleDao = database.cachedArticleDao()
        )
    }

    private val _selectedCategory = MutableStateFlow(NewsCategory.LATEST)
    private val _selectedSource = MutableStateFlow(NewsSource.ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _activeTab = MutableStateFlow(MainTab.FEED)
    private val _activeReaderArticle = MutableStateFlow<NewsArticle?>(null)
    private val _isDarkTheme = MutableStateFlow(false)
    private val _userNotice = MutableStateFlow<String?>(null)

    val uiState: StateFlow<NewsUiState> = combine(
        repository.articlesWithBookmarkStatus,
        repository.bookmarkedArticles,
        repository.isRefreshing,
        repository.cachedCount,
        networkMonitor.isOnline,
        _selectedCategory,
        _selectedSource,
        _searchQuery,
        _activeTab,
        _activeReaderArticle,
        _isDarkTheme
    ) { params ->
        @Suppress("UNCHECKED_CAST")
        val allArticles = params[0] as List<NewsArticle>
        @Suppress("UNCHECKED_CAST")
        val bookmarks = params[1] as List<NewsArticle>
        val isRefreshing = params[2] as Boolean
        val cachedCount = params[3] as Int
        val isOnline = params[4] as Boolean
        val selectedCategory = params[5] as NewsCategory
        val selectedSource = params[6] as NewsSource
        val searchQuery = params[7] as String
        val activeTab = params[8] as MainTab
        val activeReaderArticle = params[9] as NewsArticle?
        val isDarkTheme = params[10] as Boolean

        // Compute filtered articles
        val filtered = allArticles.filter { article ->
            val matchesCategory = (selectedCategory == NewsCategory.LATEST) || (article.category == selectedCategory)
            val matchesSource = (selectedSource == NewsSource.ALL) || (article.source == selectedSource)
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                article.title.contains(searchQuery, ignoreCase = true) ||
                article.summary.contains(searchQuery, ignoreCase = true) ||
                article.source.displayName.contains(searchQuery, ignoreCase = true)
            }
            matchesCategory && matchesSource && matchesSearch
        }

        // Top 5 stories with breaking flag or latest 5
        val breaking = allArticles.filter { it.isBreaking }.ifEmpty { allArticles.take(4) }

        // Sync reader article if bookmarked in real-time
        val updatedReaderArticle = activeReaderArticle?.let { reader ->
            allArticles.firstOrNull { it.id == reader.id } ?: bookmarks.firstOrNull { it.id == reader.id } ?: reader
        }

        NewsUiState(
            articles = filtered,
            breakingNews = breaking,
            bookmarks = bookmarks,
            selectedCategory = selectedCategory,
            selectedSource = selectedSource,
            searchQuery = searchQuery,
            activeTab = activeTab,
            activeReaderArticle = updatedReaderArticle,
            isRefreshing = isRefreshing,
            isDarkTheme = isDarkTheme,
            isOnline = isOnline,
            cachedCount = cachedCount,
            userNotice = _userNotice.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NewsUiState()
    )

    fun selectCategory(category: NewsCategory) {
        _selectedCategory.value = category
    }

    fun selectSource(source: NewsSource) {
        _selectedSource.value = source
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setActiveTab(tab: MainTab) {
        _activeTab.value = tab
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun openReader(article: NewsArticle) {
        _activeReaderArticle.value = article
    }

    fun closeReader() {
        _activeReaderArticle.value = null
    }

    fun refreshNews() {
        viewModelScope.launch {
            repository.refreshNews()
        }
    }

    fun toggleBookmark(article: NewsArticle) {
        viewModelScope.launch {
            repository.toggleBookmark(article)
            _userNotice.value = if (article.isBookmarked) "बुकमार्क हटाइयो" else "समाचार सुरक्षित गरियो"
        }
    }

    fun clearNotice() {
        _userNotice.value = null
    }
}
