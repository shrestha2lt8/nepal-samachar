package com.example

import com.example.data.model.NewsArticle
import com.example.data.model.NewsCategory
import com.example.data.model.NewsSource
import com.example.util.NewsDeduplicator
import org.junit.Assert.assertEquals
import org.junit.Test

class NewsDeduplicatorTest {

    @Test
    fun testDeduplicateSimilarHeadlines() {
        val article1 = NewsArticle(
            id = "rato_01",
            title = "रातोपाटी: नेपाल प्रिमियर लिग (NPL) को खेल तालिका सार्वजनिक",
            summary = "नेपाल प्रिमियर लिग एनपिएलको खेल तालिका सार्वजनिक गरिएको छ।",
            content = "विस्तृत विवरण",
            source = NewsSource.RATOPATI,
            originalUrl = "https://www.ratopati.com/story/1",
            category = NewsCategory.SPORTS,
            publishedTimeStr = "१० मिनेट अघि",
            publishedEpoch = 1000L
        )

        val article2 = NewsArticle(
            id = "ok_02",
            title = "अनलाइनखबर: नेपाल प्रिमियर लिग (एनपिएल) को खेल तालिका सार्वजनिक भयो",
            summary = "क्यानले एनपिएलको खेल तालिका सार्वजनिक गरेको छ।",
            content = "विस्तृत विवरण",
            source = NewsSource.ONLINEKHABAR,
            originalUrl = "https://www.onlinekhabar.com/story/2",
            category = NewsCategory.SPORTS,
            publishedTimeStr = "५ मिनेट अघि",
            publishedEpoch = 1200L
        )

        val article3 = NewsArticle(
            id = "seto_03",
            title = "सेतोपाटी: काठमाडौं महानगरपालिकाले ल्यायो नयाँ बजेट",
            summary = "काठमाडौं महानगरको नयाँ बजेट प्रस्तुत भएको छ।",
            content = "विस्तृत विवरण",
            source = NewsSource.SETOPATI,
            originalUrl = "https://www.setopati.com/story/3",
            category = NewsCategory.POLITICS,
            publishedTimeStr = "१ घण्टा अघि",
            publishedEpoch = 500L
        )

        val input = listOf(article1, article2, article3)
        val deduplicated = NewsDeduplicator.deduplicate(input)

        // article1 and article2 are duplicates about NPL schedule, so only 1 should be kept
        // article3 is unique about Kathmandu budget, so it should also be kept
        assertEquals(2, deduplicated.size)
        assertEquals("rato_01", deduplicated[0].id)
        assertEquals("seto_03", deduplicated[1].id)
    }
}
