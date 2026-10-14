# Gemini AI Context & Data Schemas Reference

This file provides compressed data schemas and API contracts for Gemini AI models working with this project. Use this reference instead of inspecting entire Kotlin source files to minimize token usage.

---

## 1. Key Data Schemas

### NewsArticle
```kotlin
data class NewsArticle(
    val id: String,                  // Unique hash of originalUrl
    val title: String,               // Nepali news title
    val summary: String,             // Plain text excerpt (HTML tags stripped)
    val content: String?,            // Full article text if available
    val source: NewsSource,          // NewsSource enum
    val category: NewsCategory,      // NewsCategory enum
    val originalUrl: String,         // Web link to publisher
    val publishedAt: Date,           // Published timestamp
    val imageUrl: String? = null,    // Primary photo URL (og:image or RSS)
    val isBreaking: Boolean = false, // Editorial breaking news tag
    val isBookmarked: Boolean = false// User saved offline flag
)
```

### RadioStation
```kotlin
data class RadioStation(
    val id: String,                  // e.g. "radio_nepal", "kantipur_fm"
    val nameNepali: String,          // e.g. "रेडियो नेपाल"
    val nameEnglish: String,         // e.g. "Radio Nepal"
    val frequency: String,           // e.g. "१०० MHz"
    val streamUrl: String,           // Live audio endpoint (HLS/MP3)
    val location: String,            // e.g. "सिंहदरबार, काठमाडौं"
    val tagline: String,             // Broadcast motto
    val brandColorHex: Long          // Brand accent hex (e.g. 0xFFC8102EL)
)
```

### FinancialMarketsState
```kotlin
data class FinancialMarketsState(
    val nepseIndex: Double = 2742.88,
    val nepseChange: Double = 14.32,
    val goldFinePerTola: Long = 160500L,     // 24K Hallmark Gold (NPR)
    val goldTejabiPerTola: Long = 159800L,   // 22K Gold (NPR)
    val silverPerTola: Long = 1945L,         // Silver (NPR)
    val usdNprRate: Double = 134.82,
    val lastUpdated: String = ""
)
```

### NewsCategory
- `LATEST ("ताजा समाचार")`: All fresh breaking stories
- `POLITICS ("राजनीति")`: National governance and election updates
- `ECONOMY ("अर्थतन्त्र")`: Business, banks, inflation, budgets
- `SOCIETY ("समाज")`: Social affairs, education, culture
- `SPORTS ("खेलकुद")`: Football, cricket, martial arts
- `ENTERTAINMENT ("मनोरञ्जन")`: Nepali cinema, art, music
- `WORLD ("विश्व")`: Global geopolitics and international relations
- `TECH ("प्रविधि")`: Gadgets, cybersecurity, startups

---

## 2. Audio Playback State Flow

```
Idle ──▶ Buffering(station) ──▶ Playing(station) ──▶ Paused(station) ──▶ Idle
  │              │                     │
  └──────────────┴─────────────────────┴───▶ Error(station, message)
```
- Managed in: `RadioPlaybackManager.kt`
- Consumed in: `MainActivity.kt`, `RadioScreen.kt`, and `RadioMiniPlayer.kt`

---

## 3. Ad Placement Logic
```kotlin
// LazyColumn itemsIndexed inside HomeScreen.kt:
itemsIndexed(items = articles, key = { _, article -> article.id }) { index, article ->
    NewsCard(article = article, ...)
    if ((index + 1) % 3 == 0) {
        AdBanner(modifier = Modifier.padding(vertical = 4.dp))
    }
}
```

---

## 4. Web Scraping & Image Fallback Chain
1. RSS `<image>`, `<media:thumbnail>`, or `<enclosure>` tag (parsed by `FeedParser.kt`).
2. Live webpage scraping for `<meta property="og:image">` (handled asynchronously by `ArticleImageExtractor.kt` on background IO thread).
3. `NewsImageUtil.getEffectiveImageUrl(article)` returns curated high-res category photography if external image is unavailable.
