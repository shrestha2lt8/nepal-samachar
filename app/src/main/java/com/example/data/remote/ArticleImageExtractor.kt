package com.example.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI
import java.util.regex.Pattern

object ArticleImageExtractor {

    private const val TAG = "ArticleImageExtractor"

    private val ogImagePattern1 = Pattern.compile(
        "<meta[^>]+(?:property|name)\\s*=\\s*['\"](?:og:image|twitter:image|twitter:image:src)['\"][^>]+content\\s*=\\s*['\"]([^'\"]+)['\"]",
        Pattern.CASE_INSENSITIVE
    )

    private val ogImagePattern2 = Pattern.compile(
        "<meta[^>]+content\\s*=\\s*['\"]([^'\"]+)['\"][^>]+(?:property|name)\\s*=\\s*['\"](?:og:image|twitter:image|twitter:image:src)['\"]",
        Pattern.CASE_INSENSITIVE
    )

    private val linkImagePattern = Pattern.compile(
        "<link[^>]+rel\\s*=\\s*['\"]image_src['\"][^>]+href\\s*=\\s*['\"]([^'\"]+)['\"]",
        Pattern.CASE_INSENSITIVE
    )

    private val imgTagPattern = Pattern.compile(
        "<img[^>]+(?:class\\s*=\\s*['\"][^'\"]*(?:featured|post-thumb|detail|main|news-image|hero)[^'\"]*['\"][^>]+src|src)\\s*=\\s*['\"]([^'\"]+)['\"]",
        Pattern.CASE_INSENSITIVE
    )

    /**
     * Connects to the original news story webpage and extracts the actual article image
     * from OpenGraph meta tags (<meta property="og:image" ...>), Twitter cards, or main article photo.
     */
    suspend fun extractImageFromUrl(client: OkHttpClient, articleUrl: String): String? = withContext(Dispatchers.IO) {
        if (articleUrl.isBlank() || !articleUrl.startsWith("http")) return@withContext null

        try {
            val request = Request.Builder()
                .url(articleUrl)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) NepalNewsAggregator/1.0")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.9,ne;q=0.8")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null

                val source = response.body?.source() ?: return@withContext null
                val buffer = okio.Buffer()
                // Read up to 64KB which comfortably includes HTML <head> and top featured images
                var bytesRead = 0L
                val maxBytes = 65536L
                while (bytesRead < maxBytes && !source.exhausted()) {
                    val read = source.read(buffer, 8192)
                    if (read == -1L) break
                    bytesRead += read
                }

                val html = buffer.readUtf8()
                val foundUrl = findImageUrlInHtml(html)
                if (!foundUrl.isNullOrBlank()) {
                    val resolved = resolveUrl(articleUrl, foundUrl)
                    Log.d(TAG, "Extracted real article image for $articleUrl -> $resolved")
                    return@withContext resolved
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to extract image for $articleUrl: ${e.message}")
        }
        null
    }

    private fun findImageUrlInHtml(html: String): String? {
        val m1 = ogImagePattern1.matcher(html)
        if (m1.find()) {
            val url = m1.group(1)?.trim()
            if (isValidImageUrl(url)) return url
        }

        val m2 = ogImagePattern2.matcher(html)
        if (m2.find()) {
            val url = m2.group(1)?.trim()
            if (isValidImageUrl(url)) return url
        }

        val m3 = linkImagePattern.matcher(html)
        if (m3.find()) {
            val url = m3.group(1)?.trim()
            if (isValidImageUrl(url)) return url
        }

        val m4 = imgTagPattern.matcher(html)
        if (m4.find()) {
            val url = m4.group(1)?.trim()
            if (isValidImageUrl(url)) return url
        }

        return null
    }

    private fun isValidImageUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val clean = url.lowercase()
        // Skip common tiny tracking pixels, avatars, or logos
        if (clean.contains("logo") || clean.contains("icon") || clean.contains("avatar") || clean.contains("1x1")) {
            return false
        }
        return clean.endsWith(".jpg") || clean.endsWith(".jpeg") || clean.endsWith(".png") ||
               clean.endsWith(".webp") || clean.contains("/uploads/") || clean.contains("/media/") ||
               clean.contains("/images/") || clean.contains("/posts/") || clean.contains("/news/")
    }

    private fun resolveUrl(baseUrl: String, candidateUrl: String): String {
        return try {
            val baseUri = URI(baseUrl)
            baseUri.resolve(candidateUrl).toString()
        } catch (_: Exception) {
            if (candidateUrl.startsWith("//")) {
                "https:$candidateUrl"
            } else if (candidateUrl.startsWith("/")) {
                val host = URI(baseUrl).host
                "https://$host$candidateUrl"
            } else {
                candidateUrl
            }
        }
    }
}
