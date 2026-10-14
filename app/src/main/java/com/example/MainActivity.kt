package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.outlined.Info
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainTab
import com.example.ui.components.AboutAppDialog
import com.example.ui.components.RadioMiniPlayer
import com.example.ui.screens.RadioScreen
import com.example.ui.NewsViewModel
import com.example.ui.components.MarketDetailDialog
import com.example.ui.components.SelectedMarketTab
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ReaderScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NepalCrimson
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {

    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Google Mobile Ads SDK asynchronously in background coroutine
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val configuration = com.google.android.gms.ads.RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf(com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR))
                    .build()
                MobileAds.setRequestConfiguration(configuration)
                MobileAds.initialize(applicationContext) {}
            } catch (_: Exception) {}
        }

        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = uiState.isDarkTheme) {
                NepalNewsApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NepalNewsApp(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var activeMarketTab by remember { mutableStateOf<SelectedMarketTab?>(null) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity
    val interstitialAdManager = remember { com.example.util.InterstitialAdManager(context) }

    // Toast notice handler
    LaunchedEffect(uiState.userNotice) {
        uiState.userNotice?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotice()
        }
    }

    // Modal dialog for detailed market rates (Forex, Gold/Silver, NEPSE)
    activeMarketTab?.let { tab ->
        MarketDetailDialog(
            initialTab = tab,
            markets = uiState.markets,
            onDismiss = { activeMarketTab = null }
        )
    }

    // Modal dialog for About App Store showcase
    if (showAboutDialog) {
        AboutAppDialog(
            onDismiss = { showAboutDialog = false }
        )
    }

    // If an article is currently open in Clean Reader Mode, display it full screen
    val readerArticle = uiState.activeReaderArticle
    if (readerArticle != null) {
        ReaderScreen(
            article = readerArticle,
            isOnline = uiState.isOnline,
            onBackClick = { viewModel.closeReader() },
            onBookmarkClick = { viewModel.toggleBookmark(readerArticle) }
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 1.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = NepalCrimson,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Newspaper,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "नेपाल समाचार",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                        // Live Bikram Sambat Date & Auto-Ticking Time
                        Text(
                            text = "${uiState.nepaliDate} • ${uiState.currentTime}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            maxLines = 1,
                            modifier = Modifier.testTag("header_nepali_date_time")
                        )
                    }
                },
                actions = {
                    // Manual Refresh Action Button
                    IconButton(
                        onClick = { viewModel.refreshNews() },
                        modifier = Modifier.testTag("top_refresh_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "ताजा गर्नुहोस्",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Theme Switcher Button
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier.testTag("theme_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (uiState.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (uiState.isDarkTheme) "लाइट थिममा बदल्नुहोस्" else "डार्क थिममा बदल्नुहोस्",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (uiState.activeTab != MainTab.SEARCH) {
                        IconButton(
                            onClick = { viewModel.setActiveTab(MainTab.SEARCH) },
                            modifier = Modifier.testTag("top_search_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "खोज्नुहोस्",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // About App Information Button
                    IconButton(
                        onClick = { showAboutDialog = true },
                        modifier = Modifier.testTag("top_about_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "एप जानकारी",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // Feed Tab
                NavigationBarItem(
                    selected = uiState.activeTab == MainTab.FEED,
                    onClick = { viewModel.setActiveTab(MainTab.FEED) },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Feed,
                            contentDescription = "समाचार"
                        )
                    },
                    label = {
                        Text(
                            text = "समाचार",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NepalCrimson,
                        selectedTextColor = NepalCrimson,
                        indicatorColor = NepalCrimson.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_tab_feed")
                )

                // FM Radio Tab
                NavigationBarItem(
                    selected = uiState.activeTab == MainTab.RADIO,
                    onClick = { viewModel.setActiveTab(MainTab.RADIO) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Radio,
                            contentDescription = "रेडियो"
                        )
                    },
                    label = {
                        Text(
                            text = "रेडियो",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NepalCrimson,
                        selectedTextColor = NepalCrimson,
                        indicatorColor = NepalCrimson.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_tab_radio")
                )

                // Search Tab
                NavigationBarItem(
                    selected = uiState.activeTab == MainTab.SEARCH,
                    onClick = { viewModel.setActiveTab(MainTab.SEARCH) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "खोज"
                        )
                    },
                    label = {
                        Text(
                            text = "खोज",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NepalCrimson,
                        selectedTextColor = NepalCrimson,
                        indicatorColor = NepalCrimson.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_tab_search")
                )

                // Bookmarks Tab
                NavigationBarItem(
                    selected = uiState.activeTab == MainTab.BOOKMARKS,
                    onClick = { viewModel.setActiveTab(MainTab.BOOKMARKS) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (uiState.bookmarks.isNotEmpty()) {
                                    Badge(containerColor = NepalCrimson) {
                                        Text("${uiState.bookmarks.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.activeTab == MainTab.BOOKMARKS) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "सुरक्षित"
                            )
                        }
                    },
                    label = {
                        Text(
                            text = "बुकमार्क",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NepalCrimson,
                        selectedTextColor = NepalCrimson,
                        indicatorColor = NepalCrimson.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_tab_bookmarks")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.activeTab) {
                MainTab.FEED -> HomeScreen(
                    articles = uiState.articles,
                    breakingNews = uiState.breakingNews,
                    selectedCategory = uiState.selectedCategory,
                    selectedSource = uiState.selectedSource,
                    isRefreshing = uiState.isRefreshing,
                    isOnline = uiState.isOnline,
                    cachedCount = uiState.cachedCount,
                    markets = uiState.markets,
                    onCategorySelected = { cat -> viewModel.selectCategory(cat) },
                    onSourceSelected = { src -> viewModel.selectSource(src) },
                    onArticleClick = { art ->
                        interstitialAdManager.onArticleClicked(activity) {
                            viewModel.openReader(art)
                        }
                    },
                    onBookmarkClick = { art -> viewModel.toggleBookmark(art) },
                    onMarketClick = { tab -> activeMarketTab = tab },
                    onRefresh = { viewModel.refreshNews() }
                )

                MainTab.RADIO -> RadioScreen(
                    stations = uiState.radioStations,
                    playbackState = uiState.radioPlaybackState,
                    onStationClick = { station -> viewModel.toggleRadio(station) }
                )

                MainTab.SEARCH -> SearchScreen(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = { q -> viewModel.setSearchQuery(q) },
                    searchResults = uiState.articles,
                    onArticleClick = { art ->
                        interstitialAdManager.onArticleClicked(activity) {
                            viewModel.openReader(art)
                        }
                    },
                    onBookmarkClick = { art -> viewModel.toggleBookmark(art) }
                )

                MainTab.BOOKMARKS -> BookmarksScreen(
                    bookmarks = uiState.bookmarks,
                    onArticleClick = { art ->
                        interstitialAdManager.onArticleClicked(activity) {
                            viewModel.openReader(art)
                        }
                    },
                    onBookmarkClick = { art -> viewModel.toggleBookmark(art) }
                )
            }

            // Persistent floating mini-player bar across tabs (when not on the dedicated Radio tab)
            if (uiState.activeTab != MainTab.RADIO) {
                RadioMiniPlayer(
                    playbackState = uiState.radioPlaybackState,
                    onTogglePlayPause = { station -> viewModel.toggleRadio(station) },
                    onStop = { viewModel.stopRadio() },
                    onExpand = { viewModel.setActiveTab(MainTab.RADIO) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                )
            }
        }
    }
}
