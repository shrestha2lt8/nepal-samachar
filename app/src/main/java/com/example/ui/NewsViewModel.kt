package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NewsDatabase
import com.example.data.model.FinancialMarketsState
import com.example.data.model.NewsArticle
import com.example.data.model.NewsCategory
import com.example.data.model.NewsSource
import com.example.data.remote.NepaliNewsDataSource
import com.example.data.repository.MarketRepository
import com.example.data.repository.NewsRepository
import com.example.util.NepaliDateUtil
import com.example.util.NetworkMonitor
import com.example.util.NewsDeduplicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

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
    val isDarkTheme: Boolean = false,
    val isOnline: Boolean = true,
    val cachedCount: Int = 0,
    val userNotice: String? = null,
    val markets: FinancialMarketsState = FinancialMarketsState(),
    val nepaliDate: String = NepaliDateUtil.getCurrentNepaliDateBs(),
    val currentTime: String = NepaliDateUtil.getCurrentTimeFormatted(),
    val gregorianDate: String = NepaliDateUtil.getCurrentGregorianDateFormatted()
)

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize all UI filter StateFlows FIRST to prevent null reference races
    private val _selectedCategory = MutableStateFlow(NewsCategory.LATEST)
    private val _selectedSource = MutableStateFlow(NewsSource.ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _activeTab = MutableStateFlow(MainTab.FEED)
    private val _activeReaderArticle = MutableStateFlow<NewsArticle?>(null)
    private val _isDarkTheme = MutableStateFlow(false)
    private val _userNotice = MutableStateFlow<String?>(null)
    private val _currentNepaliDate = MutableStateFlow(NepaliDateUtil.getCurrentNepaliDateBs())
    private val _currentTime = MutableStateFlow(NepaliDateUtil.getCurrentTimeFormatted())

    private val repository: NewsRepository
    private val marketRepository = MarketRepository()
    private val networkMonitor = NetworkMonitor(application)

    init {
        val database = NewsDatabase.getInstance(application)
        val remoteSource = NepaliNewsDataSource()
        repository = NewsRepository(
            remoteSource = remoteSource,
            bookmarkDao = database.bookmarkDao(),
            cachedArticleDao = database.cachedArticleDao()
        )

        // Auto-update clock on every minute change
        viewModelScope.launch {
            while (true) {
                _currentNepaliDate.value = NepaliDateUtil.getCurrentNepaliDateBs()
                _currentTime.value = NepaliDateUtil.getCurrentTimeFormatted()

                val calendar = Calendar.getInstance()
                val seconds = calendar.get(Calendar.SECOND)
                val millis = calendar.get(Calendar.MILLISECOND)
                val delayMillis = ((60 - seconds) * 1000L - millis).coerceAtLeast(1000L)
                delay(delayMillis)
            }
        }
    }

    val uiState: StateFlow<NewsUiState> = combine(
        repository.articlesWithBookmarkStatus,
        repository.bookmarkedArticles,
        repository.isRefreshing,
        repository.cachedCount,
        networkMonitor.isOnline,
        marketRepository.markets,
        _selectedCategory,
        _selectedSource,
        _searchQuery,
        _activeTab,
        _activeReaderArticle,
        _isDarkTheme,
        _currentNepaliDate,
        _currentTime
    ) { params ->
        @Suppress("UNCHECKED_CAST")
        val allArticles = params[0] as List<NewsArticle>
        @Suppress("UNCHECKED_CAST")
        val bookmarks = params[1] as List<NewsArticle>
        val isRefreshing = params[2] as Boolean
        val cachedCount = params[3] as Int
        val isOnline = params[4] as Boolean
        val markets = params[5] as FinancialMarketsState
        val selectedCategory = params[6] as NewsCategory
        val selectedSource = params[7] as NewsSource
        val searchQuery = params[8] as String
        val activeTab = params[9] as MainTab
        val activeReaderArticle = params[10] as NewsArticle?
        val isDarkTheme = params[11] as Boolean
        val nepaliDate = params[12] as String
        val currentTime = params[13] as String

        // Compute filtered articles
        val filtered = allArticles.filter { article ->
            val matchesCategory = (selectedCategory == NewsCategory.LATEST) || (article.category == selectedCategory)
            val matchesSource = (selectedSource == NewsSource.ALL) || (article.source == selectedSource)
            val matchesSearch = searchQuery.isBlank() ||
                article.title.contains(searchQuery, ignoreCase = true) ||
                article.summary.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSource && matchesSearch
        }

        // De-duplicate similar news reporting on the same event across portals in Taja/Sabai
        val deduplicatedArticles = if (selectedCategory == NewsCategory.LATEST && selectedSource == NewsSource.ALL && searchQuery.isBlank()) {
            NewsDeduplicator.deduplicate(filtered)
        } else {
            filtered
        }

        val breakingNews = allArticles.filter { it.isBreaking }.take(5).ifEmpty {
            allArticles.take(3)
        }

        NewsUiState(
            articles = deduplicatedArticles,
            breakingNews = breakingNews,
            bookmarks = bookmarks,
            selectedCategory = selectedCategory,
            selectedSource = selectedSource,
            searchQuery = searchQuery,
            activeTab = activeTab,
            activeReaderArticle = activeReaderArticle,
            isRefreshing = isRefreshing,
            isDarkTheme = isDarkTheme,
            isOnline = isOnline,
            cachedCount = cachedCount,
            userNotice = _userNotice.value,
            markets = markets,
            nepaliDate = nepaliDate,
            currentTime = currentTime,
            gregorianDate = NepaliDateUtil.getCurrentGregorianDateFormatted()
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
            // Update live clock and date immediately
            _currentNepaliDate.value = NepaliDateUtil.getCurrentNepaliDateBs()
            _currentTime.value = NepaliDateUtil.getCurrentTimeFormatted()

            // Concurrently refresh all sections: Live RSS news feeds and Financial Markets
            val marketJob = launch { marketRepository.refreshMarkets() }
            val newsJob = launch { repository.refreshNews() }

            marketJob.join()
            newsJob.join()
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
