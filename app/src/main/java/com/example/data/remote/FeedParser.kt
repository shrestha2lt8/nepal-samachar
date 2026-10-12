package com.example.data.remote

import android.util.Xml
import com.example.data.model.NewsArticle
import com.example.data.model.NewsCategory
import com.example.data.model.NewsSource
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern

object FeedParser {

    private val imgPattern = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>", Pattern.CASE_INSENSITIVE)

    fun parseRss(xmlContent: String, source: NewsSource): List<NewsArticle> {
        val articles = mutableListOf<NewsArticle>()
        try {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(StringReader(xmlContent))

            var eventType = parser.eventType
            var inItem = false
            var currentTitle = ""
            var currentLink = ""
            var currentDescription = ""
            var currentPubDate = ""
            var currentImageUrl: String? = null
            var currentCategoryStr = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name?.lowercase(Locale.ROOT) ?: ""

                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (tagName == "item" || tagName == "entry") {
                            inItem = true
                            currentTitle = ""
                            currentLink = ""
                            currentDescription = ""
                            currentPubDate = ""
                            currentImageUrl = null
                            currentCategoryStr = ""
                        } else if (inItem) {
                            when (tagName) {
                                "title" -> currentTitle = parser.nextText()
                                "link" -> {
                                    val href = parser.getAttributeValue(null, "href")
                                    currentLink = if (!href.isNullOrBlank()) href else parser.nextText()
                                }
                                "description", "summary" -> {
                                    val rawDesc = parser.nextText()
                                    currentDescription = stripHtml(rawDesc)
                                    if (currentImageUrl == null) {
                                        currentImageUrl = extractImageFromHtml(rawDesc)
                                    }
                                }
                                "content:encoded", "content" -> {
                                    val rawContent = parser.nextText()
                                    if (currentDescription.isBlank()) {
                                        currentDescription = stripHtml(rawContent)
                                    }
                                    if (currentImageUrl == null) {
                                        currentImageUrl = extractImageFromHtml(rawContent)
                                    }
                                }
                                "pubdate", "published", "updated" -> {
                                    currentPubDate = parser.nextText()
                                }
                                "category" -> {
                                    val cat = parser.nextText()
                                    if (cat.isNotBlank()) currentCategoryStr = cat
                                }
                                "enclosure" -> {
                                    val type = parser.getAttributeValue(null, "type") ?: ""
                                    val url = parser.getAttributeValue(null, "url")
                                    if (url != null && (type.startsWith("image") || url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".webp"))) {
                                        currentImageUrl = url
                                    }
                                }
                                "media:content", "media:thumbnail" -> {
                                    val url = parser.getAttributeValue(null, "url")
                                    if (!url.isNullOrBlank()) {
                                        currentImageUrl = url
                                    }
                                }
                            }
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if (tagName == "item" || tagName == "entry") {
                            if (currentTitle.isNotBlank()) {
                                val cleanTitle = stripHtml(currentTitle).trim()
                                val cleanUrl = currentLink.trim()
                                val id = if (cleanUrl.isNotBlank()) cleanUrl.hashCode().toString() else cleanTitle.hashCode().toString()
                                val category = inferCategory(currentCategoryStr, cleanTitle, currentDescription)
                                val pubMillis = parsePubDate(currentPubDate)
                                val timeAgo = formatTimeAgo(pubMillis)

                                articles.add(
                                    NewsArticle(
                                        id = id,
                                        title = cleanTitle,
                                        summary = if (currentDescription.isNotBlank()) currentDescription.take(280) else cleanTitle,
                                        content = currentDescription,
                                        source = source,
                                        originalUrl = if (cleanUrl.isNotBlank()) cleanUrl else source.homeUrl,
                                        imageUrl = currentImageUrl,
                                        category = category,
                                        publishedTimeStr = timeAgo,
                                        publishedEpoch = pubMillis,
                                        isBreaking = articles.isEmpty() // top story can be breaking
                                    )
                                )
                            }
                            inItem = false
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return articles
    }

    private fun stripHtml(html: String): String {
        return html
            .replace(Regex("<[^>]*>"), " ")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun extractImageFromHtml(html: String): String? {
        val matcher = imgPattern.matcher(html)
        if (matcher.find()) {
            return matcher.group(1)
        }
        return null
    }

    private fun inferCategory(rawCategory: String, title: String, description: String): NewsCategory {
        val text = "$rawCategory $title $description".lowercase(Locale.ROOT)
        return when {
            text.contains("खेलकुद") || text.contains("खेल") || text.contains("क्रिकेट") || text.contains("फुटबल") || text.contains("स्पोर्ट्स") || text.contains("sports") -> NewsCategory.SPORTS
            text.contains("अर्थ") || text.contains("बजार") || text.contains("बैंक") || text.contains("शेयर") || text.contains("नेप्से") || text.contains("धितोपत्र") || text.contains("व्यापार") || text.contains("economy") || text.contains("business") -> NewsCategory.ECONOMY
            text.contains("राजनीति") || text.contains("प्रधानमन्त्री") || text.contains("सरकार") || text.contains("संसद") || text.contains("मन्त्री") || text.contains("कांग्रेस") || text.contains("एमाले") || text.contains("माओवादी") || text.contains("politics") -> NewsCategory.POLITICS
            text.contains("प्रदेश") || text.contains("कोशी") || text.contains("मधेश") || text.contains("बागमती") || text.contains("गण्डकी") || text.contains("लुम्बिनी") || text.contains("कर्णाली") || text.contains("सुदूरपश्चिम") || text.contains("स्थानीय तह") -> NewsCategory.PROVINCES
            text.contains("शिक्षा") || text.contains("विश्वविद्यालय") || text.contains("त्रिवि") || text.contains("परीक्षा") || text.contains("विद्यार्थी") || text.contains("विद्यालय") || text.contains("कलेज") -> NewsCategory.EDUCATION
            text.contains("स्वास्थ्य") || text.contains("अस्पताल") || text.contains("औषधि") || text.contains("रोग") || text.contains("उपचार") || text.contains("चिकित्सक") -> NewsCategory.HEALTH
            text.contains("पर्यटन") || text.contains("हिमाल") || text.contains("पदमार्ग") || text.contains("सगरमाथा") || text.contains("होटल") || text.contains("उड्डयन") -> NewsCategory.TOURISM
            text.contains("रोजगार") || text.contains("श्रम") || text.contains("वैदेशिक") || text.contains("कोरिया") || text.contains("खाडी") || text.contains("जागिर") -> NewsCategory.EMPLOYMENT
            text.contains("प्रविधि") || text.contains("कम्प्युटर") || text.contains("एआई") || text.contains("मोबाइल") || text.contains("इन्टरनेट") || text.contains("साइबर") || text.contains("tech") || text.contains("ict") -> NewsCategory.TECH
            text.contains("मनोरञ्जन") || text.contains("चलचित्र") || text.contains("फिल्म") || text.contains("गीत") || text.contains("कलाकार") || text.contains("सिनेमा") || text.contains("entertainment") -> NewsCategory.ENTERTAINMENT
            text.contains("विचार") || text.contains("स्तम्भ") || text.contains("विश्लेषण") || text.contains("टिप्पणी") || text.contains("opinion") -> NewsCategory.OPINION
            text.contains("जीवनशैली") || text.contains("साहित्य") || text.contains("कविता") || text.contains("संस्कृति") || text.contains("खानपान") -> NewsCategory.LIFESTYLE
            text.contains("विश्व") || text.contains("अन्तर्राष्ट्रिय") || text.contains("भारत") || text.contains("चीन") || text.contains("अमेरिका") || text.contains("विदेश") || text.contains("world") -> NewsCategory.WORLD
            text.contains("समाज") || text.contains("घटना") || text.contains("दुर्घटना") || text.contains("प्रहरी") || text.contains("अदालत") || text.contains("society") -> NewsCategory.SOCIETY
            else -> NewsCategory.LATEST
        }
    }

    private fun parsePubDate(pubDateStr: String): Long {
        if (pubDateStr.isBlank()) return System.currentTimeMillis()
        val formats = listOf(
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH),
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH),
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        )
        for (format in formats) {
            try {
                val date = format.parse(pubDateStr)
                if (date != null) return date.time
            } catch (_: Exception) {}
        }
        return System.currentTimeMillis()
    }

    fun formatTimeAgo(epochMillis: Long): String {
        val diff = System.currentTimeMillis() - epochMillis
        if (diff < 0) return "भर्खरै"
        val minutes = diff / (1000 * 60)
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 2 -> "भर्खरै"
            minutes < 60 -> "${toNepaliNumber(minutes)} मिनेट अघि"
            hours < 24 -> "${toNepaliNumber(hours)} घण्टा अघि"
            days == 1L -> "हिजो"
            days < 30 -> "${toNepaliNumber(days)} दिन अघि"
            else -> "१ महिना अघि"
        }
    }

    fun toNepaliNumber(num: Long): String {
        val devanagariDigits = charArrayOf('०', '१', '२', '३', '४', '५', '६', '७', '८', '९')
        return num.toString().map { ch ->
            if (ch in '0'..'9') devanagariDigits[ch - '0'] else ch
        }.joinToString("")
    }
}
