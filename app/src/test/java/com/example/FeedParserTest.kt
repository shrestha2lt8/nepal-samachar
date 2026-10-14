package com.example

import com.example.data.model.NewsSource
import com.example.data.remote.FeedParser
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FeedParserTest {

    @Test
    fun testParseSampleRss() {
        val sampleXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
            <channel>
                <title>Online Khabar</title>
                <item>
                    <title>काठमाडौंमा नयाँ योजना घोषणा</title>
                    <link>https://www.onlinekhabar.com/2026/10/test-article</link>
                    <pubDate>Mon, 12 Oct 2026 13:00:06 +0000</pubDate>
                    <category>राजनीति</category>
                    <description>काठमाडौं — नयाँ योजना सुरु भएको छ।</description>
                </item>
            </channel>
            </rss>
        """.trimIndent()

        val articles = FeedParser.parseRss(sampleXml, NewsSource.ONLINEKHABAR)
        assertFalse("Articles list should not be empty", articles.isEmpty())
        val first = articles.first()
        assertTrue(first.title.contains("काठमाडौंमा नयाँ योजना घोषणा"))
        assertTrue(first.content.contains("नयाँ योजना सुरु भएको छ"))
    }

    @Test
    fun testParseOnlinekhabarImage() {
        val sampleXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0" xmlns:content="http://purl.org/rss/1.0/modules/content/">
            <channel>
                <item>
                    <title>सुदूरपश्चिमबाट अम्बिका चलाउने कांग्रेस केन्द्रीय सदस्यमा निर्वाचित</title>
                    <link>https://www.onlinekhabar.com/2026/10/test</link>
                    <description><![CDATA[चलाउनेले तीन हजार ७५५ मत पाएकी छन् ।]]></description>
                    <image>https://www.onlinekhabar.com/wp-content/uploads/2026/10/Amboka-chalaune.jpg</image>
                </item>
            </channel>
            </rss>
        """.trimIndent()
        val articles = FeedParser.parseRss(sampleXml, NewsSource.ONLINEKHABAR)
        assertFalse(articles.isEmpty())
        org.junit.Assert.assertEquals(
            "https://www.onlinekhabar.com/wp-content/uploads/2026/10/Amboka-chalaune.jpg",
            articles.first().imageUrl
        )
    }

    @Test
    fun testParseRatopatiMediaThumbnail() {
        val sampleXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0" xmlns:media="http://search.yahoo.com/mrss/">
            <channel>
                <item>
                    <title>सुदूरपश्चिम बजेट पारित</title>
                    <link>https://www.ratopati.com/story/599387</link>
                    <description><![CDATA[धनगढी । बजेट पारित भएको छ।]]></description>
                    <pubDate>Tue, 13 Oct 2026 12:28:45 +0000</pubDate>
                    <media:thumbnail width="976" height="549" url="https://npcdn.ratopati.com/media/news/sudur-budget.webp"/>
                </item>
            </channel>
            </rss>
        """.trimIndent()
        val articles = FeedParser.parseRss(sampleXml, NewsSource.RATOPATI)
        assertFalse(articles.isEmpty())
        org.junit.Assert.assertEquals(
            "https://npcdn.ratopati.com/media/news/sudur-budget.webp",
            articles.first().imageUrl
        )
    }
}
