package com.example.data.remote

import android.util.Log
import com.example.data.model.NewsArticle
import com.example.data.model.NewsCategory
import com.example.data.model.NewsSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class NepaliNewsDataSource {

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    suspend fun fetchFeed(source: NewsSource): List<NewsArticle> = withContext(Dispatchers.IO) {
        if (source.feedUrl.isBlank()) return@withContext emptyList()
        try {
            val request = Request.Builder()
                .url(source.feedUrl)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) NepalNewsAggregator/1.0")
                .header("Accept", "application/rss+xml, application/xml, text/xml, application/atom+xml, */*")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val xml = response.body?.string() ?: ""
                    if (xml.isNotBlank()) {
                        val parsed = FeedParser.parseRss(xml, source)
                        if (parsed.isNotEmpty()) {
                            Log.d("NepaliNewsDataSource", "Successfully fetched ${parsed.size} articles from ${source.displayName}")
                            return@withContext parsed
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("NepaliNewsDataSource", "Live fetch notice for ${source.displayName}: ${e.message}")
        }
        emptyList()
    }

    suspend fun fetchAllSources(): List<NewsArticle> = withContext(Dispatchers.IO) {
        // Prioritizing Ratopati, Setopati, Onlinekhabar, Baarahkhari, Nagarik News at the top
        val prioritizedSources = listOf(
            NewsSource.RATOPATI,
            NewsSource.SETOPATI,
            NewsSource.ONLINEKHABAR,
            NewsSource.BAARAHKHARI,
            NewsSource.NAGARIK_NEWS,
            NewsSource.KANTIPUR,
            NewsSource.ANNAPURNA_POST,
            NewsSource.NAYA_PATRIKA,
            NewsSource.NEPALPRESS,
            NewsSource.BIZMANDU,
            NewsSource.TECHPANA,
            NewsSource.ICT_SAMACHAR,
            NewsSource.NEPALSAYS,
            NewsSource.SIRIUS_NEPAL,
            NewsSource.HAATMA,
            NewsSource.NEWS_BUREAU,
            NewsSource.NV_KHABAR,
            NewsSource.NAGARIK_SEWA,
            NewsSource.KATHMANDU_BULLETIN,
            NewsSource.ONLINE_PATRIKA,
            NewsSource.WE_NEPAL,
            NewsSource.SATRAN_MEDIA
        )

        // Pull feeds concurrently using async
        val fetchedArticles = coroutineScope {
            prioritizedSources.map { source ->
                async { fetchFeed(source) }
            }.awaitAll().flatten()
        }

        val combined = (fetchedArticles + getSeedArticles()).distinctBy { it.id }
        combined.sortedByDescending { it.publishedEpoch }
    }

    fun getSeedArticles(): List<NewsArticle> {
        val now = System.currentTimeMillis()
        return listOf(
            // 1. Ratopati (ratopati.com)
            NewsArticle(
                id = "rato_01",
                title = "रातोपाटी: नेपाल प्रिमियर लिग (NPL) को तालिका सार्वजनिक, त्रिवि मैदानमा भव्य उद्घाटन हुने",
                summary = "अन्तर्राष्ट्रिय स्टार खेलाडीहरूको सहभागिता रहने लिगको तयारी अन्तिम चरणमा, टिकट अनलाइनबाट खुला।",
                content = "काठमाडौं — क्यानका अनुसार खेलकुद पर्यटनलाई विश्वव्यापी बनाउन यस पटकको लिग ऐतिहासिक हुनेछ। नेपाली क्रिकेट समर्थकहरूको व्यापक उत्साह देखिएको छ।",
                source = NewsSource.RATOPATI,
                originalUrl = "https://ratopati.com",
                imageUrl = "https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?w=800&auto=format&fit=crop&q=60",
                category = NewsCategory.SPORTS,
                publishedTimeStr = "५ मिनेट अघि",
                publishedEpoch = now - 5 * 60 * 1000,
                isBreaking = true
            ),

            // 2. Setopati (setopati.com)
            NewsArticle(
                id = "seto_02",
                title = "सेतोपाटी: विदेशी मुद्रा सञ्चिति हालसम्मकै उच्च, रेमिट्यान्स आप्रवाहमा नयाँ रेकर्ड कायम",
                summary = "नेपाल राष्ट्र बैंकको पछिल्लो प्रतिवेदन अनुसार बाह्य क्षेत्र स्थायित्व बलियो, आयात धान्न सक्ने क्षमता १४ महिनाभन्दा बढी।",
                content = "काठमाडौं — सेतोपाटी अनुसार बैंकिङ प्रणालीमा अधिक तरलता रहेकाले कर्जाको ब्याजदर एकल अंकमा झरेको छ, जसले उत्पादनमूलक लगानीमा सुधार ल्याउने अपेक्षा गरिएको छ।",
                source = NewsSource.SETOPATI,
                originalUrl = "https://setopati.com",
                imageUrl = "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=60",
                category = NewsCategory.ECONOMY,
                publishedTimeStr = "१० मिनेट अघि",
                publishedEpoch = now - 10 * 60 * 1000,
                isBreaking = false
            ),

            // 3. Onlinekhabar (onlinekhabar.com)
            NewsArticle(
                id = "ok_03",
                title = "अनलाइनखबर: संसद्को हिउँदे अधिवेशनमा राष्ट्रिय सरोकारका मुख्य विधेयकहरूमाथि सघन छलफल",
                summary = "विद्यालय शिक्षा, नागरिकता र अख्तियार सम्बन्धी महत्त्वपूर्ण विधेयकहरूलाई प्राथमिकताका साथ अघि बढाउने सहमति।",
                content = "काठमाडौं — अनलाइनखबरका अनुसार संसदीय कार्यव्यवस्था परामर्श समितिले विधायिकी कार्यलाई तीव्र बनाउने सहमति जुटाएको छ।",
                source = NewsSource.ONLINEKHABAR,
                originalUrl = "https://onlinekhabar.com",
                imageUrl = "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=800&auto=format&fit=crop&q=60",
                category = NewsCategory.POLITICS,
                publishedTimeStr = "१५ मिनेट अघि",
                publishedEpoch = now - 15 * 60 * 1000,
                isBreaking = true
            ),

            // 4. Baarahkhari (baahrakhari.com)
            NewsArticle(
                id = "baarah_04",
                title = "बाह्रखरी: मुग्लिङ-पोखरा चार लेन सडक विस्तार द्रुत गतिमा, प्रमुख खण्डहरूमा कालोपत्रे सम्पन्न",
                summary = "राष्ट्रिय गौरवको आयोजना अन्तर्गत पुल निर्माण र स्तरोन्नतिले गति लिँदै, आगामी वर्षभित्र पूर्ण सञ्चालनमा आउने।",
                content = "तनहुँ — बाह्रखरीका अनुसार यात्रुहरूलाई लामो समयदेखि सास्ती दिइरहेको मुग्लिङ-पोखरा खण्डमा काम तीव्र भएको छ। स्थानीय तथा पर्यटकहरू हर्षित छन्।",
                source = NewsSource.BAARAHKHARI,
                originalUrl = "https://baahrakhari.com",
                imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=800&auto=format&fit=crop&q=60",
                category = NewsCategory.SOCIETY,
                publishedTimeStr = "२० मिनेट अघि",
                publishedEpoch = now - 20 * 60 * 1000,
                isBreaking = false
            ),

            // 5. Nagarik News (nagariknetwork.com)
            NewsArticle(
                id = "nagarik_05",
                title = "नागरिक न्युज: कोशी र गण्डकी प्रदेशमा कृषि आधुनिकीकरणका लागि विशेष बजेट विनियोजन",
                summary = "पहाडी फलफूल तथा तरकारी खेती प्रवर्द्धन गर्न शीतभण्डार र बजार केन्द्र स्थापना गरिने।",
                content = "विराटनगर / पोखरा — नागरिक न्युज अनुसार प्रदेश सरकारहरूले वास्तविक किसानको लागत घटाउन सिँचाइ र आधुनिक कृषि औजारमा ५० प्रतिशत अनुदान घोषणा गरेका छन्।",
                source = NewsSource.NAGARIK_NEWS,
                originalUrl = "https://nagariknetwork.com",
                imageUrl = "https://images.unsplash.com/photo-1500937386664-56d1dfef3854?w=800&auto=format&fit=crop&q=60",
                category = NewsCategory.PROVINCES,
                publishedTimeStr = "२५ मिनेट अघि",
                publishedEpoch = now - 25 * 60 * 1000,
                isBreaking = false
            ),

            // New Categories Articles
            // Education Category
            NewsArticle(
                id = "edu_06",
                title = "त्रिविको परीक्षा प्रणालीलाई पूर्ण डिजिटल बनाउने तयारी, समयमै नतिजा प्रकाशन हुने",
                summary = "त्रिभुवन विश्वविद्यालयले अनलाइन फारम, डिजिटल प्रवेशपत्र र ओएमआर सिट जाँच प्रणाली अनिवार्य गर्ने निर्णय गर्यो।",
                content = "काठमाडौं — विश्वविद्यालय सुधार कार्यदलले वार्षिक शैक्षिक क्यालेन्डर कडाइका साथ पालना गराउने र नतिजा प्रकाशन ढिलाइ अन्त्य गर्ने कार्ययोजना पेश गरेको छ।",
                source = NewsSource.KANTIPUR,
                originalUrl = "https://ekantipur.com",
                imageUrl = "https://images.unsplash.com/photo-1455849318743-b2233052fcff?w=800&auto=format&fit=crop&q=60",
                category = NewsCategory.EDUCATION,
                publishedTimeStr = "३० मिनेट अघि",
                publishedEpoch = now - 30 * 60 * 1000,
                isBreaking = false
            ),

            // Health Category
            NewsArticle(
                id = "health_07",
                title = "स्वास्थ्य बिमा कार्यक्रमको दायरा विस्तार, सबै जिल्ला अस्पतालहरूमा विशेषज्ञ सेवा सुरु",
                summary = "नागरिकलाई सर्वसुलभ स्वास्थ्य सेवा दिन औषधि सूची र बिमा रकममा वृद्धि गर्ने मन्त्रिपरिषद्को निर्णय।",
                content = "काठमाडौं — स्वास्थ्य मन्त्रालयले ग्रामीण क्षेत्रका आधारभूत अस्पतालहरूमा भिडियो एक्सरे र ल्याब सेवा विस्तार गर्ने कार्य तीव्र बनाएको छ।",
                source = NewsSource.ANNAPURNA_POST,
                originalUrl = "https://annapurnapost.com",
                imageUrl = "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=800&auto=format&fit=crop&q=60",
                category = NewsCategory.HEALTH,
                publishedTimeStr = "३५ मिनेट अघि",
                publishedEpoch = now - 35 * 60 * 1000,
                isBreaking = false
            ),

            // Tourism Category
            NewsArticle(
                id = "tour_08",
                title = "सगरमाथा र अन्नपूर्ण क्षेत्रमा पदयात्री पर्यटकको चाप बढ्यो, स्थानीय व्यवसायी उत्साहित",
                summary = "शरद ऋतुको मौसम अनुकूल भएसँगै हिमाली भेगका होमस्टे र होटलहरू भरिभराउ, हवाई उडानमा वृद्धि।",
                content = "सोलुखुम्बु / कास्की — पर्यटन विभागका अनुसार यस वर्ष विदेशी पर्यटकको आगमन संख्याले नयाँ कीर्तिमान बनाएको छ।",
                source = NewsSource.SIRIUS_NEPAL,
                originalUrl = "https://siriusnepal.com",
                imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop&q=60",
                category = NewsCategory.TOURISM,
                publishedTimeStr = "४० मिनेट अघि",
                publishedEpoch = now - 40 * 60 * 1000,
                isBreaking = false
            ),

            // Employment Category
            NewsArticle(
                id = "emp_09",
                title = "वैदेशिक रोजगारीमा जाने श्रमिकहरूका लागि नि:शुल्क सीपमूलक तालिम र वित्तीय साक्षरता",
                summary = "दक्ष जनशक्ति पठाउन श्रम मन्त्रालयले सातै प्रदेशमा प्राविधिक तालिम केन्द्रहरू सञ्चालनमा ल्यायो।",
                content = "काठमाडौं — सुरक्षित र मर्यादित वैदेशिक रोजगारी सुनिश्चित गर्न श्रमिकहरूको बिमा रकम र श्रम स्वीकृति प्रक्रियालाई पूर्ण स्वचालित बनाइएको छ।",
                source = NewsSource.NEWS_BUREAU,
                originalUrl = "https://enewsbureau.com",
                imageUrl = "https://images.unsplash.com/photo-1521791136064-7986c2920216?w=800&auto=format&fit=crop&q=60",
                category = NewsCategory.EMPLOYMENT,
                publishedTimeStr = "४५ मिनेट अघि",
                publishedEpoch = now - 45 * 60 * 1000,
                isBreaking = false
            ),

            // Tech Category
            NewsArticle(
                id = "tech_10",
                title = "टेकपाना: नेपालमै निर्मित डिजिटल वालेट र खुला एपीआई प्रणालीले ई-कमर्समा ल्याएको क्रान्ति",
                summary = "क्यूआर भुक्तानी प्रणाली अन्तर्राष्ट्रिय स्तरमा जोडिएसँगै सीमापार वित्तीय कारोबार सहज बन्दै।",
                content = "काठमाडौं — टेकपाना अनुसार स्वदेशी फिनटेक कम्पनीहरूको कारोबार संख्या मासिक अर्बौं रुपैयाँ पुगेको छ।",
                source = NewsSource.TECHPANA,
                originalUrl = "https://techpana.com",
                imageUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&auto=format&fit=crop&q=60",
                category = NewsCategory.TECH,
                publishedTimeStr = "५० मिनेट अघि",
                publishedEpoch = now - 50 * 60 * 1000,
                isBreaking = false
            ),

            // Provinces Category
            NewsArticle(
                id = "prov_11",
                title = "कर्णाली र सुदूरपश्चिममा सडक करिडोर निर्माण तीव्र, सीमा नाकाहरूलाई जोड्ने योजना",
                summary = "भेरी र कर्णाली करिडोरले चीन र भारतबीचको अन्तरदेशीय व्यापार सहज बनाउने विश्वास।",
                content = "सुर्खेत — स्थानीय उत्पादनलाई राष्ट्रिय बजारसँग जोड्न प्रदेश सरकारले सडक पूर्वाधारलाई प्रमुख प्राथमिकता दिएको छ।",
                source = NewsSource.NAYA_PATRIKA,
                originalUrl = "https://nayapatrikadaily.com",
                imageUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800&auto=format&fit=crop&q=60",
                category = NewsCategory.PROVINCES,
                publishedTimeStr = "५५ मिनेट अघि",
                publishedEpoch = now - 55 * 60 * 1000,
                isBreaking = false
            ),

            // Lifestyle / Literature Category
            NewsArticle(
                id = "life_12",
                title = "नेपाली साहित्य र लोक संस्कृतिको संरक्षणमा नयाँ पुस्ताका लेखक तथा सर्जकहरूको योगदान",
                summary = "काठमाडौंमा सम्पन्न राष्ट्रिय पुस्तक मेलामा मौलिक उपन्यास र इतिहाससम्बन्धी कृतिहरूको उच्च बिक्री।",
                content = "काठमाडौं — सत्रान मिडियाका अनुसार नेपाली भाषा, मैथिली, नेवारी र भोजपुरी साहित्यको प्रवर्द्धनका लागि अनुवाद कार्यक्रमलाई तीव्रता दिइने भएको छ।",
                source = NewsSource.SATRAN_MEDIA,
                originalUrl = "https://satranmedia.com",
                imageUrl = "https://images.unsplash.com/photo-1457369804613-52c61a468e7d?w=800&auto=format&fit=crop&q=60",
                category = NewsCategory.LIFESTYLE,
                publishedTimeStr = "१ घण्टा अघि",
                publishedEpoch = now - 60 * 60 * 1000,
                isBreaking = false
            )
        )
    }
}
