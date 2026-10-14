package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.FinancialMarketsState
import com.example.data.model.NewsArticle
import com.example.data.model.NewsCategory
import com.example.data.model.NewsSource
import com.example.ui.components.AdBanner
import com.example.ui.components.BreakingNewsTicker
import com.example.ui.components.CompactFilterRow
import com.example.ui.components.MarketsRibbon
import com.example.ui.components.NewsCard
import com.example.ui.components.SelectedMarketTab
import com.example.ui.theme.AmberGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    articles: List<NewsArticle>,
    breakingNews: List<NewsArticle>,
    selectedCategory: NewsCategory,
    selectedSource: NewsSource,
    isRefreshing: Boolean,
    isOnline: Boolean,
    cachedCount: Int,
    markets: FinancialMarketsState,
    onCategorySelected: (NewsCategory) -> Unit,
    onSourceSelected: (NewsSource) -> Unit,
    onArticleClick: (NewsArticle) -> Unit,
    onBookmarkClick: (NewsArticle) -> Unit,
    onMarketClick: (SelectedMarketTab) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Offline Status Banner (Compact)
        AnimatedVisibility(
            visible = !isOnline,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp)
                    .testTag("offline_status_banner"),
                shape = RoundedCornerShape(8.dp),
                color = AmberGold.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "अफलाइन",
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "अफलाइन: $cachedCount वटा सुरक्षित समाचार उपलब्ध छन्",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // LINE 1: Dedicated Breaking News Ticker (Separate Line)
        if (breakingNews.isNotEmpty()) {
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)) {
                BreakingNewsTicker(
                    breakingNews = breakingNews,
                    onItemClick = onArticleClick
                )
            }
        }

        // LINE 2: Dedicated Financial Markets Ribbon (Separate Line: NEPSE, Gold, NRB Forex, Sync Button)
        MarketsRibbon(
            markets = markets,
            isRefreshing = isRefreshing,
            onMarketClick = onMarketClick,
            onRefreshClick = onRefresh,
            modifier = Modifier.testTag("markets_ribbon")
        )

        // LINE 3: Compact Filter Row (Source Dropdown + Category Micro-Chips)
        CompactFilterRow(
            selectedCategory = selectedCategory,
            selectedSource = selectedSource,
            onCategorySelected = onCategorySelected,
            onSourceSelected = onSourceSelected,
            modifier = Modifier.testTag("compact_filter_row")
        )

        // Feed Container with Pull to Refresh
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .testTag("pull_to_refresh_box")
        ) {
            if (articles.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "यस वर्गमा कुनै समाचार भेटिएन",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "कृपया स्वाइप गरेर ताजा गर्नुहोस् वा अर्को समाचार स्रोत छान्नुहोस्।",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                onCategorySelected(NewsCategory.LATEST)
                                onSourceSelected(NewsSource.ALL)
                                onRefresh()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("reset_filters_btn")
                        ) {
                            Text("सबै समाचार हेर्नुहोस्")
                        }
                    }
                }
            } else {
                // Spacious News Feed List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("news_feed_list"),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        items = articles,
                        key = { _, article -> article.id }
                    ) { index, article ->
                        NewsCard(
                            article = article,
                            onCardClick = { onArticleClick(article) },
                            onBookmarkClick = { onBookmarkClick(article) }
                        )

                        // Sponsored Ad Banner after every 3 news articles
                        if ((index + 1) % 3 == 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            AdBanner(modifier = Modifier.padding(vertical = 4.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}
