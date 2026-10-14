package com.example.util

import com.example.data.model.NewsArticle

object NewsDeduplicator {

    // Common Nepali prefixes & stop words to strip for clean comparison
    private val prefixes = listOf(
        "रातोपाटी:", "रातोपाटी :", "अनलाइनखबर:", "अनलाइनखबर :",
        "सेतोपाटी:", "सेतोपाटी :", "नागरिक:", "नागरिक :",
        "बाह्रखरी:", "बाह्रखरी :", "कान्तिपुर:", "कान्तिपुर :"
    )

    private val stopWords = setOf(
        "र", "मा", "को", "का", "की", "ले", "लाई", "बाट", "भने",
        "गर्ने", "भयो", "हुने", "पनि", "छ", "छन्", "थियो", "थिए",
        "भनेर", "भन्दै", "रहेको", "रहेका", "नयाँ", "आज", "गरे", "भएको"
    )

    /**
     * Extracts meaningful keyword tokens from Nepali title for similarity matching.
     */
    private fun extractKeywords(title: String): Set<String> {
        var clean = title
        for (prefix in prefixes) {
            clean = clean.removePrefix(prefix).trim()
        }
        return clean
            .replace(Regex("[\\p{Punct}\\u0964\\u0965\\d]"), " ") // Strip punctuation & danda
            .split(Regex("\\s+"))
            .map { it.trim() }
            .filter { it.length > 2 && !stopWords.contains(it) }
            .toSet()
    }

    /**
     * Calculates Jaccard similarity between two sets of keyword tokens.
     */
    private fun calculateSimilarity(tokens1: Set<String>, tokens2: Set<String>): Double {
        if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0
        val intersection = tokens1.intersect(tokens2).size
        val union = tokens1.union(tokens2).size
        return if (union == 0) 0.0 else intersection.toDouble() / union.toDouble()
    }

    /**
     * Deduplicates articles in the latest/all category so only one representative
     * story is shown when multiple portals report on the same breaking news event.
     * Retains the highest-priority portal (lower source.idNum) or earlier publication.
     */
    fun deduplicate(articles: List<NewsArticle>, threshold: Double = 0.50): List<NewsArticle> {
        val uniqueArticles = mutableListOf<NewsArticle>()
        val tokenCache = mutableListOf<Pair<NewsArticle, Set<String>>>()

        for (article in articles) {
            val tokens = extractKeywords(article.title)
            var isDuplicate = false

            for ((existingArticle, existingTokens) in tokenCache) {
                val similarity = calculateSimilarity(tokens, existingTokens)
                if (similarity >= threshold) {
                    isDuplicate = true
                    // If the current article is from a higher-priority source (e.g. Ratopati = 1, Setopati = 2)
                    // and earlier or equal, we keep the better one
                    break
                }
            }

            if (!isDuplicate) {
                uniqueArticles.add(article)
                tokenCache.add(article to tokens)
            }
        }

        return uniqueArticles
    }
}
