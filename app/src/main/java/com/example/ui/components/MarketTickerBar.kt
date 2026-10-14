package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinancialMarketsState
import com.example.ui.theme.AmberGold
import com.example.ui.theme.HimalayanBlue
import com.example.ui.theme.NepalCrimson

enum class SelectedMarketTab {
    NEPSE,
    GOLD,
    FOREX
}

@Composable
fun MarketTickerBar(
    markets: FinancialMarketsState,
    onMarketClick: (SelectedMarketTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. NEPSE Stock Market Card
        MarketMiniCard(
            title = "NEPSE नेप्से",
            mainMetric = markets.nepse.index,
            secondaryMetric = "${markets.nepse.change} (${markets.nepse.percentChange})",
            caption = "कारोबार: ${markets.nepse.turnover}",
            icon = Icons.Default.ShowChart,
            accentColor = if (markets.nepse.isPositive) Color(0xFF16A34A) else Color(0xFFDC2626),
            testTag = "nepse_market_card",
            onClick = { onMarketClick(SelectedMarketTab.NEPSE) }
        )

        // 2. Gold & Silver Card
        MarketMiniCard(
            title = "सुन तथा चाँदी",
            mainMetric = "रु ${markets.gold.fineGoldPerTola}",
            secondaryMetric = "चाँदी: रु ${markets.gold.silverPerTola}",
            caption = "छापावाल प्रति तोला",
            icon = Icons.Default.MonetizationOn,
            accentColor = AmberGold,
            testTag = "gold_market_card",
            onClick = { onMarketClick(SelectedMarketTab.GOLD) }
        )

        // 3. Forex Card
        val usd = markets.forexList.firstOrNull { it.currencyCode == "USD" }
        MarketMiniCard(
            title = "विदेशी मुद्रा (NRB)",
            mainMetric = "$ १ = रु ${usd?.sellRate ?: "१३५.०५"}",
            secondaryMetric = "९ देशको विनिमय दर",
            caption = "बिक्री दर (Nepal Rastra Bank)",
            icon = Icons.Default.CurrencyExchange,
            accentColor = HimalayanBlue,
            testTag = "forex_market_card",
            onClick = { onMarketClick(SelectedMarketTab.FOREX) }
        )
    }
}

@Composable
private fun MarketMiniCard(
    title: String,
    mainMetric: String,
    secondaryMetric: String,
    caption: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = mainMetric,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                text = secondaryMetric,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = accentColor
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                ),
                maxLines = 1
            )
        }
    }
}
