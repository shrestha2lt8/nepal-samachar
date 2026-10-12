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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.NewsArticle
import com.example.data.model.NewsCategory
import com.example.data.model.NewsSource
import com.example.ui.components.AdBanner
import com.example.ui.components.BreakingNewsTicker
import com.example.ui.components.CategoryFilterBar
import com.example.ui.components.HeroNewsCard
import com.example.ui.components.NewsCard
import com.example.ui.components.SourceFilterRow
import com.example.ui.theme.AmberGold
import com.example.ui.theme.NepalCrimson

@Composable
fun HomeScreen(
    articles: List<NewsArticle>,
    breakingNews: List<NewsArticle>,
    selectedCategory: NewsCategory,
    selectedSource: NewsSource,
    isRefreshing: Boolean,
    isOnline: Boolean,
    cachedCount: Int,
    onCategorySelected: (NewsCategory) -> Unit,
    onSourceSelected: (NewsSource) -> Unit,
    onArticleClick: (NewsArticle) -> Unit,
    onBookmarkClick: (NewsArticle) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Offline Status Banner
        AnimatedVisibility(
            visible = !isOnline,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("offline_status_banner"),
                shape = RoundedCornerShape(10.dp),
                color = AmberGold.copy(alpha = 0.14f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "अफलाइन",
                        tint = AmberGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "अफलाइन मोड सक्रिय छ",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "इन्टरनेट छैन तर $cachedCount वटा सुरक्षित समाचारहरू अफलाइन पढ्न उपलब्ध छन्।",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }

        // Ticker banner
        if (breakingNews.isNotEmpty()) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                BreakingNewsTicker(
                    breakingNews = breakingNews,
                    onItemClick = onArticleClick
                )
            }
        }

        // Category filter chips
        CategoryFilterBar(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected
        )

        // Source filter chips
        SourceFilterRow(
            selectedSource = selectedSource,
            onSourceSelected = onSourceSelected
        )

        // Refresh indicator or feed count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${selectedCategory.displayName} • ${selectedSource.displayName}",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = NepalCrimson
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("refresh_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "ताजा गर्नुहोस्",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (articles.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
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
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "यस वर्गमा कुनै समाचार भेटिएन",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "कृपया अर्को समाचार स्रोत वा वर्ग छान्नुहोस् वा ताजा गर्नुहोस्।",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            onCategorySelected(NewsCategory.LATEST)
                            onSourceSelected(NewsSource.ALL)
                            onRefresh()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("reset_filters_btn")
                    ) {
                        Text("सबै समाचार हेर्नुहोस्")
                    }
                }
            }
        } else {
            // News Feed List with Inline Google Ads
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("news_feed_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(
                    items = articles,
                    key = { _, article -> article.id }
                ) { index, article ->
                    if (index == 0 && (selectedCategory == NewsCategory.LATEST || selectedCategory == NewsCategory.POLITICS)) {
                        HeroNewsCard(
                            article = article,
                            onCardClick = { onArticleClick(article) },
                            onBookmarkClick = { onBookmarkClick(article) }
                        )
                    } else {
                        NewsCard(
                            article = article,
                            onCardClick = { onArticleClick(article) },
                            onBookmarkClick = { onBookmarkClick(article) }
                        )
                    }

                    // Sponsored Google AdMob Banner after article 2
                    if (index == 2) {
                        Spacer(modifier = Modifier.height(4.dp))
                        AdBanner(modifier = Modifier.padding(vertical = 4.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}
