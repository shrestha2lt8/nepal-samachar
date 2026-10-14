package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.NewsArticle
import com.example.ui.components.AdBanner
import com.example.ui.theme.NepalCrimson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    article: NewsArticle,
    isOnline: Boolean = true,
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Hardware & Gesture back navigation handling
    BackHandler {
        onBackClick()
    }

    val context = LocalContext.current
    var fontSizeMultiplier by remember { mutableIntStateOf(1) } // 0: Normal, 1: Large, 2: Extra Large
    var liveSourceImage by remember(article.id) {
        androidx.compose.runtime.mutableStateOf(article.imageUrl?.takeIf { it.startsWith("http") })
    }

    androidx.compose.runtime.LaunchedEffect(article.id, article.originalUrl) {
        if (liveSourceImage.isNullOrBlank() && isOnline && article.originalUrl.startsWith("http")) {
            val client = okhttp3.OkHttpClient.Builder()
                .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .build()
            val extracted = com.example.data.remote.ArticleImageExtractor.extractImageFromUrl(client, article.originalUrl)
            if (!extracted.isNullOrBlank()) {
                liveSourceImage = extracted
            }
        }
    }

    val bodyFontSize = when (fontSizeMultiplier) {
        0 -> 15.sp
        1 -> 18.sp
        else -> 21.sp
    }
    val bodyLineHeight = when (fontSizeMultiplier) {
        0 -> 24.sp
        1 -> 28.sp
        else -> 32.sp
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = article.source.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("reader_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "पछाडि"
                        )
                    }
                },
                actions = {
                    // Font Size toggle
                    IconButton(
                        onClick = {
                            fontSizeMultiplier = (fontSizeMultiplier + 1) % 3
                        },
                        modifier = Modifier.testTag("reader_fontsize_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "अक्षरको आकार"
                        )
                    }

                    // Share action
                    IconButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "${article.title}\n\n${article.originalUrl}\n\n— नेपाल समाचार एप मार्फत")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "समाचार सेयर गर्नुहोस्"))
                        },
                        modifier = Modifier.testTag("reader_share_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "सेयर गर्नुहोस्"
                        )
                    }

                    // Bookmark toggle
                    IconButton(
                        onClick = onBookmarkClick,
                        modifier = Modifier.testTag("reader_bookmark_btn")
                    ) {
                        Icon(
                            imageVector = if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "सुरक्षित गर्नुहोस्",
                            tint = if (article.isBookmarked) NepalCrimson else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Sticky Bottom Bar with Primary Redirect Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reader_bottom_bar"),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Redirect button to the source site
                    Button(
                        onClick = {
                            if (!isOnline) {
                                Toast.makeText(context, "इन्टरनेट छैन: मूल साइट खोल्न इन्टरनेट आवश्यक पर्दछ", Toast.LENGTH_SHORT).show()
                            } else {
                                try {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(article.originalUrl))
                                    context.startActivity(browserIntent)
                                } catch (_: Exception) {}
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("reader_redirect_source_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NepalCrimson
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${article.source.displayName}मा पूरा समाचार पढ्नुहोस्",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Source & Category & Timestamp Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = article.source.brandColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(article.source.brandColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = article.source.displayName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = article.source.brandColor
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = article.category.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                // Offline cached indicator chip
                if (!isOnline) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "अफलाइन पढ्दै",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = article.publishedTimeStr,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Headline
            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Image - Prioritizes real source article image with brand fallback
            val heroImageUrl = liveSourceImage ?: com.example.util.NewsImageUtil.getEffectiveImageUrl(article)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(article.source.brandColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                // Background branding fallback while loading
                Text(
                    text = article.source.displayName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = article.source.brandColor.copy(alpha = 0.5f)
                    )
                )

                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(heroImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(18.dp))

            // Lead summary / callout box
            if (article.summary.isNotBlank() && article.summary != article.title) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = article.summary,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = bodyFontSize,
                            lineHeight = bodyLineHeight
                        ),
                        modifier = Modifier.padding(14.dp)
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Article body content
            val textContent = if (article.content.isNotBlank()) article.content else article.summary
            Text(
                text = textContent,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = bodyFontSize,
                    lineHeight = bodyLineHeight
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Google AdMob Sponsored Banner in Reader View
            AdBanner(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(20.dp))

            // Source Attribution Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "स्रोत जानकारी:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "यो समाचार ${article.source.displayName} (${article.source.enName}) बाट साभार गरिएको हो। विस्तृत सामग्री र टिप्पणीका लागि तलको बटन थिचेर मूल वेबसाइटमा जान सक्नुहुन्छ।",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
