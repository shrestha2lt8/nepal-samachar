package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinancialMarketsState
import com.example.data.model.NewsArticle
import com.example.ui.theme.AmberGold
import com.example.ui.theme.HimalayanBlue
import com.example.ui.theme.NepalCrimson

@Composable
fun CompactTickerRibbon(
    breakingNews: List<NewsArticle>,
    markets: FinancialMarketsState,
    isRefreshing: Boolean,
    onBreakingClick: (NewsArticle) -> Unit,
    onMarketClick: (SelectedMarketTab) -> Unit,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Infinite rotation for spinning sync icon when refreshing
    val infiniteTransition = rememberInfiniteTransition(label = "refresh_spin")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(34.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 10.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Sync / Refresh Chip
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onRefreshClick)
                    .testTag("ribbon_refresh_btn"),
                shape = RoundedCornerShape(6.dp),
                color = if (isRefreshing) NepalCrimson.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "ताजा गर्नुहोस्",
                        tint = if (isRefreshing) NepalCrimson else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(13.dp)
                            .rotate(if (isRefreshing) rotationAngle else 0f)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isRefreshing) "अपडेट हुँदै..." else "ताजा",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRefreshing) NepalCrimson else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            // 2. Breaking News Chip (if available)
            val topBreaking = breakingNews.firstOrNull()
            if (topBreaking != null) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onBreakingClick(topBreaking) }
                        .testTag("ribbon_breaking_chip"),
                    shape = RoundedCornerShape(6.dp),
                    color = NepalCrimson.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = NepalCrimson,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "ब्रेकिङ: ${topBreaking.title}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 3. NEPSE Mini Pill
            RibbonMarketPill(
                icon = Icons.Default.ShowChart,
                label = "नेप्से",
                value = "${markets.nepse.index} (${markets.nepse.change})",
                accentColor = if (markets.nepse.isPositive) Color(0xFF16A34A) else Color(0xFFDC2626),
                onClick = { onMarketClick(SelectedMarketTab.NEPSE) },
                testTag = "ribbon_nepse_pill"
            )

            // 4. Gold Mini Pill
            RibbonMarketPill(
                icon = Icons.Default.MonetizationOn,
                label = "सुन",
                value = "रु ${markets.gold.fineGoldPerTola}",
                accentColor = AmberGold,
                onClick = { onMarketClick(SelectedMarketTab.GOLD) },
                testTag = "ribbon_gold_pill"
            )

            // 5. USD Forex Mini Pill
            val usd = markets.forexList.firstOrNull { it.currencyCode == "USD" }
            RibbonMarketPill(
                icon = Icons.Default.CurrencyExchange,
                label = "USD",
                value = "रु ${usd?.sellRate ?: "१३५.०५"}",
                accentColor = HimalayanBlue,
                onClick = { onMarketClick(SelectedMarketTab.FOREX) },
                testTag = "ribbon_forex_pill"
            )
        }
    }
}

@Composable
private fun RibbonMarketPill(
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            )
        }
    }
}
