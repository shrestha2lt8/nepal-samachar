# Nepal News Aggregator (नेपाल समाचार)

A high-performance Android news aggregation app delivering real-time, categorized news from Nepal's premier Nepali-language news portals (OnlineKhabar, Kantipur, Setopati, Ratopati, Baarahkhari, and more) with offline bookmarking, breaking news ticker, clean reader mode, and seamless redirection to original publishers.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following product decisions were confirmed during the planning consultation and are incorporated into the system blueprint:

- **Confirmed Language Scope**: Nepali-language news sources exclusively.
- **Confirmed Core News Portals**: OnlineKhabar, Kantipur, Setopati, Ratopati, Baarahkhari, and additional curated national feeds.
- **Confirmed Key Features**:
  1. Live category tabs (राजनीति / Politics, अर्थ / Economy, समाज / Society, विचार / Opinion, खेलकुद / Sports, मनोरञ्जन / Entertainment, विश्व / World).
  2. Breaking news ticker (ताजा अपडेट / ब्रेकिङ) with high-priority updates.
  3. Clean Reader Mode with customizable typography and Nepali Devanagari font rendering.
  4. Offline Bookmarks saved locally using Room Database.
  5. One-tap "मूल समाचार पढ्नुहोस्" (Read Original Source) button opening Chrome Custom Tabs / external browser directly to the publisher's site.

---

## 1. Overview & Core Concept

- **What It Does**: Aggregates, parses, and harmonizes live news streams from top Nepali news agencies into a unified, lightning-fast mobile reading experience. Articles are organized into standardized categories with instant search, category filtering, offline bookmarking, and publisher source attribution.
- **Target Audience / Persona**: Nepali citizens, diaspora, professionals, and students seeking a single, credible, ad-clutter-free window into national developments without jumping between multiple slow-loading websites.
- **Key Value**: Replaces multiple browser tabs with a clean, low-bandwidth, organized native Android app that highlights essential news summaries and redirects to official sources for full coverage.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Home Feed & Breaking Ticker**: User opens the app to an animated breaking news banner, followed by a filterable category pill carousel and a hero headline card.
2. **Category Browsing & Source Filtering**: Switching categories (e.g. राजनीति, अर्थतन्त्र, खेलकुद) instantly filters the unified feed. Users can also filter by specific publisher (e.g. OnlineKhabar only or Setopati only).
3. **Article Detail & Clean Reader Mode**: Tapping a card opens a reader view with high-contrast Devanagari typography, hero imagery, published time, and reading progress.
4. **Direct Publisher Navigation**: Inside the article view or via quick-actions on the list item, tapping "पूरा समाचार हेर्नुहोस् (Redirect to Source)" launches the original news link directly in an in-app Chrome Custom Tab.
5. **Offline Bookmarks**: Users can tap the bookmark icon on any story to save it locally for offline reading on transit or during connectivity drops.
6. **Search & Source Discovery**: Instant keyword search matching Nepali text across headlines and summaries.

### Visual Identity & Theme
- **Aesthetic Direction**: Modern Nepali Editorial — inspired by crisp broadsheet newspapers infused with sleek Material Design 3. Clean paper surfaces, bold Crimson Red header accents honoring national identity, and deep slate contrast.
- **Color Palette**:
  - `Primary`: Rich Crimson / Rhododendron Red (`#C41E3A` / Dark `#E63946`)
  - `Secondary / Accent`: Himalayan Blue (`#0F4C81` / Dark `#4EA8DE`)
  - `Background`: Warm Editorial Off-White (`#FBFBFD`) / Deep Charcoal Night (`#121316`)
  - `Surface`: Clean Card White (`#FFFFFF`) / Dark Elevated Slate (`#1E1F24`)
  - `Publisher Badges`: Color-coded brand chips (OnlineKhabar Red, Setopati Indigo, Kantipur Crimson, Ratopati Amber).
- **Typography & Hierarchy**:
  - High-clarity Devanagari font rendering with generous line height (`lineHeight = 26.sp`) to ensure Nepali matras and conjuncts render crisply without clipping.
  - Display Headline: Bold 20–22sp with tight leading.
  - Body Text: 16sp with comfortable 1.5x leading for sustained reading comfort.
- **Interactive Feedback & Motion**:
  - Pull-to-refresh with smooth spinner.
  - Ticker auto-scroll with manual pause on touch.
  - Bookmark toggle with subtle scale and spring feedback.
  - Shimmer placeholder skeletons during network fetch.

---

## 3. Key Product Decisions & Trade-Offs

### Decision 1: Aggregation & Live Feed Strategy
- **Chosen Approach**: Direct multi-source XML/RSS parser and HTTP scraper pipeline using OkHttp and native XML pull parsing with predefined fallback feeds and curated sample items if network/CORS conditions prevent fetching.
- **Why**: Many Nepali portals (OnlineKhabar, Ratopati, Setopati) maintain standard RSS feeds and structured meta tags. Parsing these gives instant, authentic Nepali news with guaranteed real URLs, headlines, images, and timestamps.
- **Alternatives Considered**: Headless browser scraping on mobile (too heavy, battery draining) or hardcoded mock data (violates system realism guidelines).

### Decision 2: Data Persistence & Bookmarks
- **Chosen Approach**: Android Room Database (`NewsArticleEntity` + `BookmarkEntity`).
- **Why**: Fully offline-capable, responsive through Kotlin Coroutines Flow, and adheres to Room integration guidelines. Data is stored safely on-device without requiring intrusive logins.

### Decision 3: Publisher Redirection & Source Attribution
- **Chosen Approach**: Prominent publisher badge on every card and a dedicated "मूल स्रोतमा जानुहोस् (Open Source Site)" button using Android Custom Tabs / Intent.
- **Why**: Fulfills the user's explicit requirement ("redirect user to main site when the user clicks in more info"), respects journalist copyright, and gives users full interactive content from the original publisher.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
├───────────────────┬───────────────────┬────────────────────────────────┤
│   HomeScreen &    │   ArticleDetail   │       BookmarksScreen &        │
│   BreakingTicker  │   ReaderMode      │          SearchScreen          │
└─────────┬─────────┴─────────┬─────────┴───────────────┬────────────────┘
          │                   │                         │
          ▼                   ▼                         ▼
┌────────────────────────────────────────────────────────────────────────┐
│                  NewsViewModel (StateFlow & MVI)                       │
├────────────────────────────────────────────────────────────────────────┤
│  - selectedCategory       - activeSourceFilter     - searchQuery       │
│  - breakingHeadlines      - newsFeedState          - bookmarksList     │
└──────────────────┬────────────────────────────────────┬────────────────┘
                   │                                    │
                   ▼                                    ▼
┌──────────────────────────────────────┐  ┌──────────────────────────────┐
│       NewsRepository (Engine)        │  │       Room AppDatabase       │
├──────────────────────────────────────┤  ├──────────────────────────────┤
│  - OnlineKhabarFeedFetcher           │  │  - BookmarkDao (Flow)        │
│  - RatopatiFeedFetcher               │  │  - CachedNewsDao             │
│  - SetopatiFeedFetcher               │  │  - ArticleEntity             │
│  - KantipurFeedFetcher               │  │  - BookmarkEntity            │
│  - BaarahkhariFeedFetcher            │  └──────────────────────────────┘
└──────────────────┬───────────────────┘
                   │
                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│     Network Layer (OkHttp3 + XmlFeedParser + CustomTabsLauncher)       │
└────────────────────────────────────────────────────────────────────────┘
```

### Data Model Entities
- `NewsArticle`:
  - `id: String` (URL-based hash)
  - `title: String` (Nepali title)
  - `summary: String` (Snippet/Description)
  - `content: String` (Full text if available)
  - `sourceName: String` (e.g., "अनलाइनखबर", "सेतोपाटी", "रातोपाटी", "कान्तिपुर", "बाह्रखरी")
  - `sourceKey: NewsSource` (Enum for branding/filtering)
  - `originalUrl: String` (Web link to original article)
  - `imageUrl: String?` (Thumbnail / Hero image)
  - `category: NewsCategory` (राजनीति, अर्थ, समाज, विचार, खेलकुद, मनोरञ्जन, विश्व)
  - `publishedAt: Long` (Epoch millis)
  - `isBreaking: Boolean`
- `BookmarkEntity`: Room-backed entity storing user saved stories with local timestamp.

### Interactive Component & State Mapping
- **Category Filter Bar**: Chips bound to `NewsCategory` enums. Clicking triggers instant filter in `NewsViewModel` with smooth list scroll reset.
- **Source Filter Dropdown**: Allows filtering between all sources or a specific outlet (OnlineKhabar, Setopati, Ratopati, Kantipur, Baarahkhari).
- **Breaking News Marquee**: Auto-advancing horizontal banner linking to top developing stories.
- **Redirect Action**: `LocalUriHandler` or `CustomTabsIntent` opens the authentic publisher URL.
- **Bookmark Button**: Triggers `viewModel.toggleBookmark(article)` with immediate reactive UI update.
