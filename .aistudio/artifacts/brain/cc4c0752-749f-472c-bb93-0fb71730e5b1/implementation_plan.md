# Implementation Plan: Portal News Availability, First-Launch Skeleton Experience, and Reader Action Insets

Address empty portal news feeds by auditing and updating feed endpoints/fallback web scrapers, replace the first-load "यस वर्गमा कुनै समाचार भेटिएन" empty state with an engaging animated shimmer loader and Nepali loading messaging, and lift the Reader screen's primary red action button above the device's navigation bar using proper `navigationBarsPadding`.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> - **Portal Lineup Strategy**: Portals with active, verified live feeds (Ratopati, Setopati, Onlinekhabar, Baarahkhari, Nagarik News, Bizmandu, Sirius Nepal, Haatma, NV Khabar, Nagarik Sewa) will remain front-and-center. For major portals without standard RSS endpoints (such as Kantipur and Annapurna Post), we will deploy direct HTML article extractors so users see live reporting. Dead or unreachable domains will be gracefully retired to prevent empty portal feeds.
> - **First-Launch UX**: When opening the app or switching filters while fetching, the UI will display a polished animated skeleton shimmer with `"ताजा समाचार संकलन गरिँदै छ..."` rather than showing the confusing `"यस वर्गमा कुनै समाचार भेटिएन"` ("No news found") banner.
> - **Reader Action Insets**: In `ReaderScreen.kt`, the sticky bottom surface holding the primary red button will have `.navigationBarsPadding()` applied to guarantee full clearance above 3-button navigation bars (Back, Home, Recents) and gesture navigation pills on all Android devices.

---

## 1. Overview & Core Concept

- **What It Does**:
  1. Ensures every selected news portal consistently returns real news stories.
  2. Replaces premature "not found" zero-states on first load with animated news skeleton cards and informative Nepali status messaging.
  3. Fixes edge-to-edge window insets in the article detail reader so the primary red redirect button is 100% visible and easily tappable above the phone's system navigation buttons.
- **Target Audience**: Nepali readers accessing live national news on diverse Android devices with varying system navigation bars (gesture bar vs. 3-button bar).

---

## 2. User Experience & Visual Design

### First-Launch & Loading Experience (`HomeScreen.kt`)
- When the app is launched and database cache is initializing or remote feeds are loading:
  - Display 4 animated placeholder news card skeletons with subtle pulse/shimmer animation matching Material 3 surface tones.
  - Display a branded status header:
    - `"ताजा समाचार संकलन गरिँदै छ..."` (Gathering latest news...)
    - Subtitle: `"नेपालका २०+ शीर्ष समाचार पोर्टलहरूबाट प्रत्यक्ष सामग्री प्राप्त गर्दै..."`
  - Prevents the alarming "No news found" empty state from flashing before feeds have finished fetching.
- True Empty State: Only rendered when `isRefreshing == false` and the filter yields zero matches, with a one-tap `"सबै समाचार हेर्नुहोस्"` button.

### Reader Screen Bottom Navigation Inset (`ReaderScreen.kt`)
- In `ReaderScreen.kt`, add `.navigationBarsPadding()` to the sticky bottom surface / row.
- Ensure the red button (`NepalCrimson`) maintains its full 48dp touch target, rounded corners (12dp), and comfortable 12dp vertical padding safely above the Android system buttons.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Direct HTML Scrapers for Portals Lacking Working RSS**
  - *Chosen Approach*: Deploy robust HTML article listing extractors for portals whose default `/feed` returns HTTP 404 (e.g. Kantipur, Annapurna Post, Naya Patrika).
  - *Why*: Several leading Nepali news organizations don't maintain standard WordPress RSS feeds; direct HTML extraction guarantees users can read stories from their favorite portals without encountering blank screens.
- **Decision 2: Clear Distinction Between Initial Loading vs. Empty Filter**
  - *Chosen Approach*: Check `articles.isEmpty() && isRefreshing` (or `initialLoad`) in `HomeScreen.kt` to trigger the skeleton shimmer rather than the empty state container.
  - *Why*: Dramatically improves perceived performance and avoids user confusion during the 1-2 seconds of initial network ingestion.

---

## 4. Technical Architecture & Component Flow

```
┌────────────────────────────────────────────────────────┐
│                      HomeScreen                        │
│                                                        │
│  articles.isEmpty() && isRefreshing?                   │
│  ├── YES ──▶ NewsSkeletonShimmer (4 cards + badge)     │
│  └── NO  ──▶ LazyColumn with NewsCards & AdBanners     │
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│                     ReaderScreen                       │
│                                                        │
│  Scaffold(bottomBar = {                                │
│    Surface(modifier = Modifier.navigationBarsPadding())│
│      ├── Red Action Button (Safely above Back/Home)    │
│  })                                                    │
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│                NepaliNewsDataSource                    │
│                                                        │
│  Verified RSS Feeds ──▶ FeedParser                     │
│  Non-RSS Portals   ──▶ PortalHtmlScraper               │
│                    ──▶ NewsRepository ──▶ Room Cache   │
└────────────────────────────────────────────────────────┘
```

---

## Verification Plan

### Automated Tests
- Run `gradle :app:testDebugUnitTest` to verify parser, scraper, and repository tests pass.
- Verify Robolectric tests for first-load state rendering.

### Manual Verification
- Launch app with empty cache: verify animated skeleton shimmer and Nepali loading text appear instead of "यस वर्गमा कुनै समाचार भेटिएन".
- Open every news portal tab: verify news cards populate without blank screens.
- Open `ReaderScreen`: verify the red action button is fully visible above the system navigation bar with ample clearance.
- Final applet compilation check via `compile_applet`.
