# Implementation Plan: App Store Visual Realignment, AdMob Cadence, and Live Nepali FM Radio

Align the Google Play Store graphics with the user's exact uploaded app screenshot (`image.png`), adjust the AdMob advertisement insertion logic to render an ad after every 3 news articles in the feed, and build a live Nepali FM Radio streaming player with a dedicated bottom navigation tab and persistent mini-player.

---

## User Review Required

> [!IMPORTANT]
> **AdMob Placement Cadence**: An AdMob banner ad will be inserted after every 3 news items (after items 3, 6, 9...). It will preserve the clean card container with the `"📢 विज्ञापन (Ad) नेपाल समाचार साझेदार"` header shown in your screenshot.
> **FM Radio Streaming Sources**: Top national stations included: Radio Nepal (100 MHz), Radio Kantipur (96.1 MHz), Ujyaalo 90 Network (90.0 MHz), Radio Sagarmatha (102.4 MHz), BBC Nepali Service, Image FM (97.9 MHz), and Hits FM (91.2 MHz).

---

## Proposed Changes

### 1. Store Graphics Realignment with Current App Screenshot
- **Source of Truth**: Use the user's uploaded `image.png` (displaying the clean white top bar, red square app logo, `⚡ ताजा` breaking badge, live compact NEPSE & gold ticker, and cyan `[↗ मूल स्रोतमा पढ्नुहोस्]` action buttons).
- **Process Store Phone Screenshot (`play_store_assets/phone_screenshot_1.png`)**:
  - Convert and format `image.png` to 1080 × 1920 px (9:16 aspect ratio) for Google Play Store phone listing.
  - Sync `app/src/main/res/drawable/img_store_screenshot.png` so the in-app `AboutAppDialog` displays the exact current app interface.
- **Update Feature Graphic (`play_store_assets/feature_graphic_1024x500.png`)**:
  - Update the 1024 × 500 px Google Play Feature Graphic banner using the real screenshot on the phone mockup, maintaining the red/white brand harmony and official typography.
  - Update `app/src/main/res/drawable/img_feature_graphic.png` for in-app consistency.

### 2. AdMob Ad Placement Every 3 News Articles (`HomeScreen.kt`)
- Update `HomeScreen.kt` LazyColumn items:
  - Calculate index positions to place an AdMob banner ad after every 3 news cards (`(index + 1) % 3 == 0`).
  - Maintain the existing visual style from your screenshot: `"📢 विज्ञापन (Ad) नेपाल समाचार साझेदार"` label with the AdMob banner container.
  - Ensure stable list keys so pull-to-refresh, source filtering, and category switches do not reload ads unnecessarily or cause scroll stutter.

### 3. Live Nepali FM Radio Streaming Feature
- **Radio Data Layer (`RadioStation.kt` & `RadioRepository.kt`)**:
  - Define model `RadioStation(id, nameNepali, frequency, streamUrl, logoUrl, location, description)`.
  - Curate active HTTP/HLS streaming endpoints for top national stations:
    - **Radio Nepal (रेडियो नेपाल)** — 100 MHz (Singhadurbar, Kathmandu)
    - **Radio Kantipur (रेडियो कान्तिपुर)** — 96.1 MHz
    - **Ujyaalo 90 Network (उज्यालो ९० नेटवर्क)** — 90.0 MHz
    - **Radio Sagarmatha (रेडियो सगरमाथा)** — 102.4 MHz
    - **BBC Nepali Service (बीबीसी नेपाली)**
    - **Image FM (इमेज एफएम)** — 97.9 MHz
    - **Hits FM (हिट्स एफएम)** — 91.2 MHz
- **Audio Playback Manager (`RadioPlaybackManager.kt`)**:
  - Handle audio streaming, buffering, errors, play/pause state, and audio focus using Android Media3/ExoPlayer.
  - Provide a reactive `StateFlow<RadioPlaybackState>` (Idle, Buffering, Playing, Paused, Error).
- **Dedicated Radio Screen (`RadioScreen.kt`)**:
  - Accessible via the new **रेडियो (Radio)** tab in the bottom navigation bar.
  - Shows station cards with frequency badges, live playing wave/equalizer animation, station metadata, and instant play/pause.
- **Persistent Floating Mini-Player Bar (`RadioMiniPlayer.kt`)**:
  - Positioned right above the bottom navigation bar across all tabs (`MainTab.FEED`, `MainTab.SEARCH`, `MainTab.BOOKMARKS`).
  - Displays the currently playing station name, frequency, animated playing indicator, and quick Play/Pause/Stop controls so users can listen to radio while reading news articles.
- **Bottom Navigation Update (`MainActivity.kt`, `NewsViewModel.kt`, `MainTab.kt`)**:
  - Add `MainTab.RADIO` to `MainTab.kt`.
  - Update bottom navigation bar to include 4 tabs:
    1. **समाचार** (Feed)
    2. **रेडियो** (FM Radio with `Icons.Default.Radio`)
    3. **खोज** (Search)
    4. **बुकमार्क** (Bookmarks)

---

## Verification Plan

### Automated Tests
- Run unit test suite: `gradle :app:testDebugUnitTest` to verify repository, feed parsing, and model integrity.
- Verify Robolectric tests for tab switching and bookmark toggling.

### Manual Verification
- Verify that `phone_screenshot_1.png` and `feature_graphic_1024x500.png` in `play_store_assets/` match the attached screenshot.
- Verify news feed displays an ad banner after article 3, 6, 9, etc.
- Verify FM Radio streams connect and play audio, toggle between stations, and persist in the mini-player while navigating across tabs.
- Final applet compilation check via `compile_applet`.
