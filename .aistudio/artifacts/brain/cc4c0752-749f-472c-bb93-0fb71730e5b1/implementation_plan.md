# Implementation Plan: In-App Article Return Navigation, Real News Images, Live Market Rates, and Play Store Graphics

Provide seamless return navigation when opening full articles, eliminate wrong/mismatched foreign stock images in favor of real publisher photos and authentic branded badges, ensure NEPSE, Gold, and Forex rates update with live data on refresh, and generate high-resolution phone and tablet screenshot assets for Google Play Store publication.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> - **In-App Article Return Navigation**: Instead of launching a raw `Intent.ACTION_VIEW` that ejects the user into an external browser without a way back, the app will use an **in-app Chrome Custom Tab** with brand theme colors (`NepalCrimson`), full gesture swipe-to-dismiss, and an explicit top-left Close/Back arrow (`X`) that immediately returns readers to the Nepal News reader and main feed.
> - **Zero Mismatched Foreign Images**: Remove generic Unsplash stock photos (American sports stadiums, Wall Street skyscrapers, foreign hospitals) that previously caused mismatched photos on local Nepali stories. Prioritize real publisher photos extracted from `og:image` and article tags. When an article lacks an image, render a clean, high-fidelity publisher-branded badge with the portal's official brand color, typography, and newspaper emblem.
> - **Live Financial Data on Refresh**:
>   - **Forex**: Fully dynamic live exchange rates (USD, EUR, GBP, INR, etc.) pulled directly from the Nepal Rastra Bank (NRB) REST API.
>   - **Gold & Silver**: Live Federation of Nepal Gold & Silver Dealers' Association (FENEGOSIDA) prices (छापावाल सुन, तेजाबी, र चाँदी) parsed dynamically without fragile hardcoded price prefix checks.
>   - **NEPSE**: Live stock exchange index and point changes refreshed alongside news feeds.
> - **Play Store Showcase Graphics**: Deliver publication-ready screenshots under `play_store_assets/` covering:
>   - Phone: Home Feed (with AdMob & ticker), News Details Reader, and Live FM Radio Player.
>   - Tablet: 7-inch and 10-inch layouts for Home, Reader, and Radio screens.

---

## 1. Overview & Core Concept

- **What It Does**:
  1. Keeps users inside the Nepal News ecosystem with a dedicated in-app Chrome Custom Tab offering a 1-tap return button.
  2. Ensures every news card and reader view displays either the publisher's authentic photo or an authentic Nepali source badge, never irrelevant foreign stock imagery.
  3. Guarantees that pulling down to refresh updates NEPSE, Gold/Silver, and Foreign Currency rates with the latest market data.
  4. Provides Google Play Store publication screenshot assets in phone and tablet viewports.
- **Target Audience**: Nepali readers requiring accurate live reporting, real-time financial tracking, and continuous FM radio listening.

---

## 2. User Experience & Visual Design

### In-App Source Reader Flow (`ReaderScreen.kt`)
- Tapping `"[स्रोत]मा पूरा समाचार पढ्नुहोस्"` launches an in-app Custom Tab with:
  - Custom toolbar colored in `NepalCrimson` (`#C8102E`).
  - Top-left Back button returning directly to the reader screen.
  - Page title and domain display.
  - Full hardware and gesture back navigation returning straight to Nepal News.

### Accurate Image Hierarchy (`NewsCard.kt`, `ReaderScreen.kt`, `NewsImageUtil.kt`)
- **Primary**: Real image URL scraped from RSS `<enclosure>`, `<media:content>`, or the article's live OpenGraph `<meta property="og:image">`.
- **Fallback**: Elegant, branded source cards (e.g. Kantipur blue, Ratopati crimson, Setopati navy, Bizmandu green) featuring the portal logo/name, category badge, and newspaper iconography. No generic foreign stock photos.

### Live Financial Refresh
- Pulling down to refresh on the Home screen initiates concurrent background jobs:
  - Fresh news ingestion across all verified portals.
  - NRB live foreign exchange rates.
  - FENEGOSIDA gold and silver rates.
  - NEPSE stock market index.
- Ticker bar instantly updates with newly acquired figures.

### Play Store Publication Graphics (`play_store_assets/`)
- **Phone Screenshots (1080 × 1920 / 1080 × 2400)**:
  1. `phone_screenshot_home.png` — Main feed, breaking ticker, market ribbon, category chips, and AdMob banner.
  2. `phone_screenshot_reader.png` — Clean typography, Nepali Bikram Sambat date, font size scaler, and in-app return navigation.
  3. `phone_screenshot_radio.png` — National FM Radio lineup (Radio Nepal, Kantipur, Ujyaalo, Sagarmatha, BBC) with live audio equalizer.
- **Tablet Screenshots (1920 × 1200 / 1920 × 1080)**:
  1. `tablet_screenshot_home.png` — Wide-screen multi-column responsive news layout.
  2. `tablet_screenshot_reader.png` — Centered reading view with optimal line length.
  3. `tablet_screenshot_radio.png` — Responsive grid of live radio stations and controls.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Chrome Custom Tabs vs. Raw Browser vs. Full In-App WebView**
  - *Chosen Approach*: Chrome Custom Tabs (`androidx.browser.customtabs.CustomTabsIntent`).
  - *Why*: Provides the highest rendering fidelity, shared login cookies, automatic malware protection, and lightning-fast loading while maintaining a dedicated in-app toolbar with a prominent Back button that returns to Nepal News.
- **Decision 2: Authentic Publisher Badges vs. Stock Photo Search**
  - *Chosen Approach*: Use publisher-branded card badges for stories without official photos.
  - *Why*: Eliminates context mismatch completely (e.g. foreign stadiums on Nepali cricket stories or foreign politicians on local news).

---

## 4. Technical Architecture & Component Flow

```
┌────────────────────────────────────────────────────────┐
│                      ReaderScreen                      │
│                                                        │
│  User taps "पूरा समाचार पढ्नुहोस्"                      │
│  └──▶ CustomTabsIntent.launchUrl(context, url)         │
│        ├── Toolbar Color: NepalCrimson                 │
│        └── Close Button ──▶ Returns to Nepal News      │
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│                    MarketRepository                    │
│                                                        │
│  refreshMarkets()                                      │
│  ├── fetchLiveNrbForex() ──▶ NRB API (22 currencies)   │
│  ├── fetchLiveGoldRates()──▶ FENEGOSIDA live rates     │
│  └── fetchNepseIndex()   ──▶ Sharesansar live index    │
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│                   Image Resolution                     │
│                                                        │
│  NewsArticle.imageUrl?                                 │
│  ├── Present & Valid ──▶ Coil AsyncImage               │
│  └── Absent / Failed ──▶ Branded Source Card Placeholder│
└────────────────────────────────────────────────────────┘
```

---

## Verification Plan

### Automated Tests
- Run `gradle :app:testDebugUnitTest` to verify repository, parser, and financial market parsing.
- Run `compile_applet` to ensure clean build.

### Manual Verification
- Tap `"[स्रोत]मा पूरा समाचार पढ्नुहोस्"` in Reader view: verify in-app Custom Tab opens with top Back button returning directly to the app.
- Check news cards: verify real news photos render, and articles without photos display clean publisher source cards without mismatched foreign stock imagery.
- Pull down to refresh Home screen: verify NEPSE, gold, and forex rates update with live data.
- Verify all generated Play Store screenshots under `play_store_assets/`.
