package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ColorAnnapurna
import com.example.ui.theme.ColorBaarahkhari
import com.example.ui.theme.ColorBizmandu
import com.example.ui.theme.ColorHaatma
import com.example.ui.theme.ColorIctSamachar
import com.example.ui.theme.ColorKantipur
import com.example.ui.theme.ColorKathmanduBulletin
import com.example.ui.theme.ColorNagarik
import com.example.ui.theme.ColorNagarikSewa
import com.example.ui.theme.ColorNayaPatrika
import com.example.ui.theme.ColorNepalPress
import com.example.ui.theme.ColorNepalSays
import com.example.ui.theme.ColorNewsBureau
import com.example.ui.theme.ColorNvKhabar
import com.example.ui.theme.ColorOnlineKhabar
import com.example.ui.theme.ColorOnlinePatrika
import com.example.ui.theme.ColorRatopati
import com.example.ui.theme.ColorSatranMedia
import com.example.ui.theme.ColorSetopati
import com.example.ui.theme.ColorSiriusNepal
import com.example.ui.theme.ColorTechPana
import com.example.ui.theme.ColorWeNepal
import com.example.ui.theme.HimalayanBlue

enum class NewsSource(
    val idNum: Int,
    val displayName: String,
    val enName: String,
    val homeUrl: String,
    val feedUrl: String,
    val brandColor: Color
) {
    ALL(
        idNum = 0,
        displayName = "सबै पोर्टल",
        enName = "All Portals",
        homeUrl = "",
        feedUrl = "",
        brandColor = HimalayanBlue
    ),

    // Prioritized at the very front as requested by user
    RATOPATI(
        idNum = 1,
        displayName = "रातोपाटी",
        enName = "Ratopati",
        homeUrl = "https://ratopati.com",
        feedUrl = "https://ratopati.com/feed",
        brandColor = ColorRatopati
    ),
    SETOPATI(
        idNum = 2,
        displayName = "सेतोपाटी",
        enName = "Setopati",
        homeUrl = "https://setopati.com",
        feedUrl = "https://setopati.com/feed",
        brandColor = ColorSetopati
    ),
    ONLINEKHABAR(
        idNum = 3,
        displayName = "अनलाइनखबर",
        enName = "Onlinekhabar",
        homeUrl = "https://onlinekhabar.com",
        feedUrl = "https://onlinekhabar.com/feed",
        brandColor = ColorOnlineKhabar
    ),
    BAARAHKHARI(
        idNum = 4,
        displayName = "बाह्रखरी",
        enName = "Baarahkhari",
        homeUrl = "https://baahrakhari.com",
        feedUrl = "https://baahrakhari.com/feed",
        brandColor = ColorBaarahkhari
    ),
    NAGARIK_NEWS(
        idNum = 5,
        displayName = "नागरिक न्युज",
        enName = "Nagarik News",
        homeUrl = "https://nagariknetwork.com",
        feedUrl = "https://nagariknetwork.com/feed",
        brandColor = ColorNagarik
    ),

    // Major National Portals
    KANTIPUR(
        idNum = 6,
        displayName = "कान्तिपुर",
        enName = "Kantipur",
        homeUrl = "https://ekantipur.com",
        feedUrl = "https://ekantipur.com/feed",
        brandColor = ColorKantipur
    ),
    ANNAPURNA_POST(
        idNum = 7,
        displayName = "अन्नपूर्ण पोस्ट",
        enName = "Annapurna Post",
        homeUrl = "https://annapurnapost.com",
        feedUrl = "https://annapurnapost.com/feed",
        brandColor = ColorAnnapurna
    ),
    NAYA_PATRIKA(
        idNum = 8,
        displayName = "नयाँ पत्रिका",
        enName = "Naya Patrika",
        homeUrl = "https://nayapatrikadaily.com",
        feedUrl = "https://nayapatrikadaily.com/feed",
        brandColor = ColorNayaPatrika
    ),
    NEPALPRESS(
        idNum = 9,
        displayName = "नेपालप्रेस",
        enName = "NepalPress",
        homeUrl = "https://nepalpress.com",
        feedUrl = "https://nepalpress.com/feed",
        brandColor = ColorNepalPress
    ),
    BIZMANDU(
        idNum = 10,
        displayName = "बिजमान्डू",
        enName = "Bizmandu",
        homeUrl = "https://bizmandu.com",
        feedUrl = "https://bizmandu.com/feed",
        brandColor = ColorBizmandu
    ),
    TECHPANA(
        idNum = 11,
        displayName = "टेकपाना",
        enName = "TechPana",
        homeUrl = "https://techpana.com",
        feedUrl = "https://techpana.com/feed",
        brandColor = ColorTechPana
    ),
    ICT_SAMACHAR(
        idNum = 12,
        displayName = "आईसीटी समाचार",
        enName = "ICT Samachar",
        homeUrl = "https://ictsamachar.com",
        feedUrl = "https://ictsamachar.com/feed",
        brandColor = ColorIctSamachar
    ),
    NEPALSAYS(
        idNum = 13,
        displayName = "NepalSays",
        enName = "NepalSays",
        homeUrl = "https://nepalsays.com",
        feedUrl = "https://nepalsays.com/feed",
        brandColor = ColorNepalSays
    ),
    SIRIUS_NEPAL(
        idNum = 14,
        displayName = "Sirius Nepal",
        enName = "Sirius Nepal",
        homeUrl = "https://siriusnepal.com",
        feedUrl = "https://siriusnepal.com/feed",
        brandColor = ColorSiriusNepal
    ),
    HAATMA(
        idNum = 15,
        displayName = "Haatma",
        enName = "Haatma",
        homeUrl = "https://haatma.com",
        feedUrl = "https://haatma.com/feed",
        brandColor = ColorHaatma
    ),
    NEWS_BUREAU(
        idNum = 16,
        displayName = "News Bureau",
        enName = "News Bureau",
        homeUrl = "https://enewsbureau.com",
        feedUrl = "https://enewsbureau.com/feed",
        brandColor = ColorNewsBureau
    ),
    NV_KHABAR(
        idNum = 17,
        displayName = "NV Khabar",
        enName = "NV Khabar",
        homeUrl = "https://nvkhabar.com",
        feedUrl = "https://nvkhabar.com/feed",
        brandColor = ColorNvKhabar
    ),
    NAGARIK_SEWA(
        idNum = 18,
        displayName = "Nagarik Sewa",
        enName = "Nagarik Sewa",
        homeUrl = "https://nagariksewa.com",
        feedUrl = "https://nagariksewa.com/feed",
        brandColor = ColorNagarikSewa
    ),
    KATHMANDU_BULLETIN(
        idNum = 19,
        displayName = "Kathmandu Bulletin",
        enName = "Kathmandu Bulletin",
        homeUrl = "https://kathmandubulletin.com.np",
        feedUrl = "https://kathmandubulletin.com.np/feed",
        brandColor = ColorKathmanduBulletin
    ),
    ONLINE_PATRIKA(
        idNum = 20,
        displayName = "Online Patrika",
        enName = "Online Patrika",
        homeUrl = "https://onlinepatrika.com",
        feedUrl = "https://onlinepatrika.com/feed",
        brandColor = ColorOnlinePatrika
    ),
    WE_NEPAL(
        idNum = 21,
        displayName = "WeNepal",
        enName = "WeNepal",
        homeUrl = "https://wenepal.com",
        feedUrl = "https://wenepal.com/feed",
        brandColor = ColorWeNepal
    ),
    SATRAN_MEDIA(
        idNum = 22,
        displayName = "Satran Media",
        enName = "Satran Media",
        homeUrl = "https://satranmedia.com",
        feedUrl = "https://satranmedia.com/feed",
        brandColor = ColorSatranMedia
    );

    companion object {
        fun fromSourceName(name: String): NewsSource {
            return entries.firstOrNull {
                it.displayName.equals(name, ignoreCase = true) ||
                it.enName.equals(name, ignoreCase = true) ||
                it.homeUrl.contains(name, ignoreCase = true) ||
                it.name.equals(name, ignoreCase = true)
            } ?: ONLINEKHABAR
        }
    }
}

enum class NewsCategory(
    val displayName: String,
    val enName: String
) {
    LATEST("ताजा / सबै", "Latest"),
    POLITICS("राजनीति", "Politics"),
    ECONOMY("अर्थ / बजार", "Economy"),
    SOCIETY("समाज", "Society"),
    PROVINCES("प्रदेश / स्थानीय", "Provinces"),
    SPORTS("खेलकुद", "Sports"),
    TECH("प्रविधि / ICT", "Tech"),
    EDUCATION("शिक्षा", "Education"),
    HEALTH("स्वास्थ्य", "Health"),
    TOURISM("पर्यटन / हिमाल", "Tourism"),
    EMPLOYMENT("रोजगार / श्रम", "Employment"),
    ENTERTAINMENT("मनोरञ्जन", "Entertainment"),
    OPINION("विचार / स्तम्भ", "Opinion"),
    LIFESTYLE("जीवनशैली", "Lifestyle"),
    WORLD("विश्व", "World");

    companion object {
        fun fromCategoryName(name: String): NewsCategory {
            return entries.firstOrNull {
                it.displayName.equals(name, ignoreCase = true) ||
                it.enName.equals(name, ignoreCase = true) ||
                it.name.equals(name, ignoreCase = true)
            } ?: LATEST
        }
    }
}

data class NewsArticle(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val source: NewsSource,
    val originalUrl: String,
    val imageUrl: String? = null,
    val category: NewsCategory,
    val publishedTimeStr: String,
    val publishedEpoch: Long,
    val isBreaking: Boolean = false,
    val isBookmarked: Boolean = false
)
