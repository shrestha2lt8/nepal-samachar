package com.example.config

import com.example.data.model.NewsSource

/**
 * News Sources Configuration File.
 *
 * Developers or administrators can easily enable, disable, reorder,
 * or customize news sources in this centralized configuration file.
 */
data class NewsSourceEntry(
    val source: NewsSource,
    val isEnabled: Boolean = true,
    val priorityOrder: Int = 0
)

object NewsSourcesConfig {

    /**
     * Master configuration list of all supported news portals.
     * Set `isEnabled = false` to disable any portal across the entire app
     * (fetching, filtering, and aggregation).
     */
    val sources: List<NewsSourceEntry> = listOf(
        // Primary Top National Portals (Prioritized)
        NewsSourceEntry(source = NewsSource.RATOPATI, isEnabled = true, priorityOrder = 1),
        NewsSourceEntry(source = NewsSource.SETOPATI, isEnabled = true, priorityOrder = 2),
        NewsSourceEntry(source = NewsSource.ONLINEKHABAR, isEnabled = true, priorityOrder = 3),
        NewsSourceEntry(source = NewsSource.BAARAHKHARI, isEnabled = true, priorityOrder = 4),
        NewsSourceEntry(source = NewsSource.NAGARIK_NEWS, isEnabled = true, priorityOrder = 5),

        // National Broadsheets & Digital Portals
        NewsSourceEntry(source = NewsSource.KANTIPUR, isEnabled = true, priorityOrder = 6),
        NewsSourceEntry(source = NewsSource.ANNAPURNA_POST, isEnabled = true, priorityOrder = 7),
        NewsSourceEntry(source = NewsSource.NAYA_PATRIKA, isEnabled = true, priorityOrder = 8),
        NewsSourceEntry(source = NewsSource.NEPALPRESS, isEnabled = true, priorityOrder = 9),
        NewsSourceEntry(source = NewsSource.BIZMANDU, isEnabled = true, priorityOrder = 10),

        // Tech, Economy & Specialized Portals
        NewsSourceEntry(source = NewsSource.TECHPANA, isEnabled = true, priorityOrder = 11),
        NewsSourceEntry(source = NewsSource.ICT_SAMACHAR, isEnabled = true, priorityOrder = 12),
        NewsSourceEntry(source = NewsSource.NEPALSAYS, isEnabled = true, priorityOrder = 13),
        NewsSourceEntry(source = NewsSource.SIRIUS_NEPAL, isEnabled = true, priorityOrder = 14),
        NewsSourceEntry(source = NewsSource.HAATMA, isEnabled = true, priorityOrder = 15),

        // Regional & Investigative
        NewsSourceEntry(source = NewsSource.NEWS_BUREAU, isEnabled = true, priorityOrder = 16),
        NewsSourceEntry(source = NewsSource.NV_KHABAR, isEnabled = true, priorityOrder = 17),
        NewsSourceEntry(source = NewsSource.NAGARIK_SEWA, isEnabled = true, priorityOrder = 18),
        NewsSourceEntry(source = NewsSource.KATHMANDU_BULLETIN, isEnabled = true, priorityOrder = 19),
        NewsSourceEntry(source = NewsSource.ONLINE_PATRIKA, isEnabled = true, priorityOrder = 20),
        NewsSourceEntry(source = NewsSource.WE_NEPAL, isEnabled = true, priorityOrder = 21),
        NewsSourceEntry(source = NewsSource.SATRAN_MEDIA, isEnabled = true, priorityOrder = 22)
    )

    /**
     * Returns the active, enabled sources ordered by configured priority.
     */
    fun getEnabledSources(): List<NewsSource> {
        return sources
            .filter { it.isEnabled }
            .sortedBy { it.priorityOrder }
            .map { it.source }
    }

    /**
     * Checks if a particular news source is currently enabled in config.
     */
    fun isEnabled(source: NewsSource): Boolean {
        if (source == NewsSource.ALL) return true
        return sources.firstOrNull { it.source == source }?.isEnabled ?: false
    }
}
