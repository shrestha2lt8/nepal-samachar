# GEMINI AI Model Context & Token Optimization Guide

> **Target Audience:** Gemini AI models, coding agents, and automated tools analyzing this codebase.  
> **Purpose:** Provide an ultra-compact, token-efficient blueprint of the Nepal News Android project so Gemini models can generate accurate modifications, debug issues, or integrate features without consuming excessive context window tokens.

---

## 1. Quick Project Summary

- **App Name:** Nepal News (नेपाल समाचार)
- **Platform:** Android (Min SDK: 26, Target/Compile SDK: 35)
- **Framework:** Jetpack Compose + Material 3 (M3)
- **Language:** Kotlin 100%
- **Architecture:** MVVM (Model-View-ViewModel) with Kotlin Coroutines & StateFlow
- **Persistence:** Room Database (Offline cache & Bookmarks)
- **Networking:** OkHttp 4 + Android XmlPullParser (live RSS parsing & web scraping)
- **Media Playback:** Android `MediaPlayer` via `RadioPlaybackManager` for live HLS/MP3 streams
- **Monetization:** Google Mobile Ads (AdMob) Banner & Interstitial SDK
- **Image Loading:** Coil Compose with auto-fallback to source web scraping (`ArticleImageExtractor`)

---

## 2. Directory Structure & Key Files

```
├── app/src/main/
│   ├── AndroidManifest.xml                  # Permissions (INTERNET), cleartext traffic, AdMob app ID
│   └── java/com/example/
│       ├── MainActivity.kt                  # Root Activity, Bottom Navigation (4 tabs), Scaffold, MiniPlayer
│       ├── data/
│       │   ├── model/
│       │   │   ├── NewsArticle.kt           # Primary article data class with bookmark state
│       │   │   ├── NewsCategory.kt          # Categories (LATEST, POLITICS, ECONOMY, SPORTS, TECH, etc.)
│       │   │   ├── NewsSource.kt            # 20+ Nepali news portals (Onlinekhabar, Setopati, Ratopati, etc.)
│       │   │   └── RadioStation.kt          # Radio station model & verified live streaming URLs
│       │   ├── local/
│       │   │   ├── NewsDatabase.kt          # Room DB singleton (CachedArticle & Bookmark entities)
│       │   │   ├── BookmarkDao.kt           # Bookmark persistence DAO
│       │   │   └── CachedArticleDao.kt      # Offline article cache DAO
│       │   ├── remote/
│       │   │   ├── NepaliNewsDataSource.kt  # Multi-portal RSS aggregator
│       │   │   ├── FeedParser.kt            # Robust XML/RSS parser with tag & attribute extractors
│       │   │   └── ArticleImageExtractor.kt # Rapid og:image scraper from live article URLs
│       │   └── repository/
│       │       ├── NewsRepository.kt        # Combines remote + local DB with Flow reactive updates
│       │       └── MarketRepository.kt      # Live NEPSE index, gold/silver rates, currency exchange
│       ├── ui/
│       │   ├── NewsViewModel.kt             # Central ViewModel: UI state, radio playback, filter states
│       │   ├── components/
│       │   │   ├── NewsCard.kt              # Standard news card with rounded thumbnail & actions
│       │   │   ├── AdBanner.kt              # AdMob banner card with sponsored header
│       │   │   ├── RadioMiniPlayer.kt       # Persistent floating audio player bar above bottom nav
│       │   │   ├── MarketsTickerBar.kt      # Horizontal scrolling NEPSE & Gold price ticker
│       │   │   └── AboutAppDialog.kt        # App info and store preview modal
│       │   ├── screens/
│       │   │   ├── HomeScreen.kt            # Main feed with AdMob ad placed every 3 news cards
│       │   │   ├── RadioScreen.kt           # Dedicated live FM radio screen with equalizer animations
│       │   │   ├── ReaderScreen.kt          # Clean article reader screen with full hero image
│       │   │   ├── SearchScreen.kt          # Instant search filter across title & summary
│       │   │   └── BookmarksScreen.kt       # Saved offline articles list
│       │   └── theme/
│       │       ├── Color.kt                 # Brand colors (NepalCrimson = #C8102E)
│       │       ├── Theme.kt                 # M3 dynamic / custom light and dark color schemes
│       │       └── Type.kt                  # Typography scale
│       └── util/
│           ├── RadioPlaybackManager.kt      # Audio streaming state machine (Idle, Buffering, Playing, Paused)
│           ├── NepaliDateUtil.kt            # Live Bikram Sambat (BS) date & nepali clock formatter
│           ├── NewsImageUtil.kt             # High-res image fallback resolver
│           └── NewsDeduplicator.kt          # Deduplication of identical reporting across portals
```

---

## 3. Core Architectural Rules for Gemini Models

1. **State Management**:
   - Single source of truth via `NewsViewModel.uiState: StateFlow<NewsUiState>`.
   - Use `collectAsStateWithLifecycle()` in Compose screens.
   - Do **not** create separate ad-hoc state holders for shared data.

2. **AdMob Placement Rule**:
   - In news lists, display an `AdBanner` after every 3 articles (`(index + 1) % 3 == 0`).
   - Use stable keys in `LazyColumn` (`key = { _, article -> article.id }`) to avoid ad re-renders.

3. **Live FM Radio Playback**:
   - `RadioPlaybackManager` manages `MediaPlayer` on background threads with state emitted via `RadioPlaybackState`.
   - Never block the UI thread during stream resolution or audio preparation.
   - The persistent floating `RadioMiniPlayer` renders across tabs when active.

4. **Image Resolution Strategy**:
   - RSS feeds often lack `<enclosure>` or `<media:content>`.
   - `ArticleImageExtractor` fetches the real article's `og:image` from the web page header (16KB stream) and updates the Room cache.
   - Always fall back to `NewsImageUtil.getEffectiveImageUrl()` to prevent empty image boxes.

5. **Token Saving Tips for Prompting**:
   - Reference component and model names directly rather than requesting full file contents.
   - Use `docs/TOKEN_OPTIMIZED_PROMPTS.md` for zero-shot prompt engineering.
   - Use `docs/GEMINI_MODAL_TEMPLATES.md` when invoking Gemini API for in-app features.
