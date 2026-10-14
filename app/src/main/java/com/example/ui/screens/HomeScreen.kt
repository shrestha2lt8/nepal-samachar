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
import androidx.compose.runtime.getValue
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
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.NepalCrimson

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
                if (isRefreshing || cachedCount == 0) {
                    // Engaging First-Launch / Loading Skeleton
                    NewsLoadingSkeleton()
                } else {
                    // Empty State for filtered results
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

@Composable
fun NewsLoadingSkeleton(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val alpha by transition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.72f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("news_loading_skeleton_list"),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Status Messaging Card
        item(key = "loading_status_card") {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("loading_status_banner"),
                shape = RoundedCornerShape(12.dp),
                color = NepalCrimson.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, NepalCrimson.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp,
                        color = NepalCrimson
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ताजा समाचार संकलन गरिँदै छ...",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "नेपालका २०+ शीर्ष अनलाइन पोर्टलहरूबाट प्रत्यक्ष सामग्री प्राप्त हुँदैछ...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // 4 Animated Skeleton News Cards
        items(count = 4) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Text Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Source chip placeholder
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.4f))
                        )

                        // Title line 1
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.6f))
                        )

                        // Title line 2
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.5f))
                        )

                        // Bottom date metadata
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.3f))
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Right Thumbnail placeholder
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.35f))
                    )
                }
            }
        }
    }
}

