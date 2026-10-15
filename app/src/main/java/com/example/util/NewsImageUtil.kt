package com.example.util

import com.example.data.model.NewsArticle

object NewsImageUtil {

    /**
     * Returns the article's real image URL if valid (starts with http/https and is not blank).
     * Returns null if no real publisher image is available, allowing the UI to render
     * an authentic, high-fidelity publisher-branded badge rather than a mismatched foreign stock photo.
     */
    fun getEffectiveImageUrl(article: NewsArticle): String? {
        val raw = article.imageUrl?.trim()
        if (!raw.isNullOrBlank() && (raw.startsWith("http://") || raw.startsWith("https://"))) {
            // Filter out broken tracking pixels or 1x1 spacer gifs
            val lower = raw.lowercase()
            if (!lower.contains("1x1") && !lower.contains("pixel") && !lower.contains("blank.gif")) {
                return raw
            }
        }
        return null
    }
}
