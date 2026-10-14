package com.example.util

import com.example.data.model.NewsArticle
import com.example.data.model.NewsCategory
import kotlin.math.abs

object NewsImageUtil {

    // High quality, fast loading, curated imagery for categories
    private val sportsImages = listOf(
        "https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1531415074868-036b107e775a?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1517649763962-0c623266ddc0?w=800&auto=format&fit=crop&q=80"
    )

    private val economyImages = listOf(
        "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=800&auto=format&fit=crop&q=80"
    )

    private val politicsImages = listOf(
        "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1529107386315-e1a2ed48a620?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1577962917302-cd874c4e31d2?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1541872705-1f73c6400ec9?w=800&auto=format&fit=crop&q=80"
    )

    private val tourismImages = listOf(
        "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1585409677983-0f6c41ca0c3b?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=800&auto=format&fit=crop&q=80"
    )

    private val techImages = listOf(
        "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=800&auto=format&fit=crop&q=80"
    )

    private val educationImages = listOf(
        "https://images.unsplash.com/photo-1455849318743-b2233052fcff?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=800&auto=format&fit=crop&q=80"
    )

    private val healthImages = listOf(
        "https://images.unsplash.com/photo-1505751172876-fa1923c5c528?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?w=800&auto=format&fit=crop&q=80"
    )

    private val entertainmentImages = listOf(
        "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=800&auto=format&fit=crop&q=80"
    )

    private val societyImages = listOf(
        "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1469571486292-0ba58a3f068b?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?w=800&auto=format&fit=crop&q=80"
    )

    private val provincesImages = listOf(
        "https://images.unsplash.com/photo-1500937386664-56d1dfef3854?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1472214103451-9374bd1c798e?w=800&auto=format&fit=crop&q=80"
    )

    private val defaultGeneralImages = listOf(
        "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1495020689067-958852a7765e?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1588681664899-f142ff2dc9b1?w=800&auto=format&fit=crop&q=80"
    )

    fun getFallbackImageUrl(category: NewsCategory, seed: Int): String {
        val list = when (category) {
            NewsCategory.SPORTS -> sportsImages
            NewsCategory.ECONOMY -> economyImages
            NewsCategory.POLITICS -> politicsImages
            NewsCategory.TOURISM -> tourismImages
            NewsCategory.TECH -> techImages
            NewsCategory.EDUCATION -> educationImages
            NewsCategory.HEALTH -> healthImages
            NewsCategory.ENTERTAINMENT -> entertainmentImages
            NewsCategory.SOCIETY -> societyImages
            NewsCategory.PROVINCES -> provincesImages
            NewsCategory.OPINION -> politicsImages
            NewsCategory.LIFESTYLE -> entertainmentImages
            NewsCategory.EMPLOYMENT -> economyImages
            NewsCategory.WORLD -> defaultGeneralImages
            NewsCategory.LATEST -> defaultGeneralImages
        }
        val index = abs(seed) % list.size
        return list[index]
    }

    /**
     * Returns a guaranteed valid, non-blank image URL for any article.
     * Uses the article's image if present and valid; otherwise returns a curated
     * high-res category-specific image.
     */
    fun getEffectiveImageUrl(article: NewsArticle): String {
        val raw = article.imageUrl?.trim()
        if (!raw.isNullOrBlank() && (raw.startsWith("http://") || raw.startsWith("https://"))) {
            return raw
        }
        return getFallbackImageUrl(article.category, article.id.hashCode())
    }
}
