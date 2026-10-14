package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RadioStation
import com.example.ui.theme.NepalCrimson
import com.example.util.RadioPlaybackState

@Composable
fun RadioMiniPlayer(
    playbackState: RadioPlaybackState,
    onTogglePlayPause: (RadioStation) -> Unit,
    onStop: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVisible = playbackState !is RadioPlaybackState.Idle
    val activeStation = when (playbackState) {
        is RadioPlaybackState.Buffering -> playbackState.station
        is RadioPlaybackState.Playing -> playbackState.station
        is RadioPlaybackState.Paused -> playbackState.station
        is RadioPlaybackState.Error -> playbackState.station
        is RadioPlaybackState.Idle -> null
    }

    AnimatedVisibility(
        visible = isVisible && activeStation != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        if (activeStation != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clickable { onExpand() }
                    .testTag("radio_mini_player"),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    NepalCrimson.copy(alpha = 0.25f)
                ),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Station Icon Badge
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(activeStation.brandColorHex).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radio,
                            contentDescription = activeStation.nameNepali,
                            tint = Color(activeStation.brandColorHex),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Station info
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeStation.nameNepali,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = activeStation.frequency,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NepalCrimson,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        val statusText = when (playbackState) {
                            is RadioPlaybackState.Buffering -> "प्रसारण खुल्दै छ..."
                            is RadioPlaybackState.Playing -> "प्रत्यक्ष प्रसारण (Live)"
                            is RadioPlaybackState.Paused -> "रोकिराखिएको छ (Paused)"
                            is RadioPlaybackState.Error -> "प्रसारणमा अवरोध"
                            is RadioPlaybackState.Idle -> ""
                        }

                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (playbackState is RadioPlaybackState.Playing) NepalCrimson else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            ),
                            maxLines = 1
                        )
                    }

                    // Play/Pause Action
                    Box(
                        modifier = Modifier.size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (playbackState is RadioPlaybackState.Buffering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp,
                                color = NepalCrimson
                            )
                        } else {
                            IconButton(
                                onClick = { onTogglePlayPause(activeStation) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(NepalCrimson, CircleShape)
                                    .testTag("mini_player_toggle_btn")
                            ) {
                                Icon(
                                    imageVector = if (playbackState is RadioPlaybackState.Playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (playbackState is RadioPlaybackState.Playing) "रोक्नुहोस्" else "बजाउनुहोस्",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Close Button
                    IconButton(
                        onClick = onStop,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("mini_player_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "बन्द गर्नुहोस्",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
