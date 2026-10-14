package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.example.data.model.RadioStation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class RadioPlaybackState {
    object Idle : RadioPlaybackState()
    data class Buffering(val station: RadioStation) : RadioPlaybackState()
    data class Playing(val station: RadioStation) : RadioPlaybackState()
    data class Paused(val station: RadioStation) : RadioPlaybackState()
    data class Error(val station: RadioStation, val message: String) : RadioPlaybackState()
}

class RadioPlaybackManager(private val context: Context) {

    private val TAG = "RadioPlaybackManager"
    private var mediaPlayer: MediaPlayer? = null
    private var currentStation: RadioStation? = null

    private val _playbackState = MutableStateFlow<RadioPlaybackState>(RadioPlaybackState.Idle)
    val playbackState: StateFlow<RadioPlaybackState> = _playbackState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    fun play(station: RadioStation) {
        scope.launch {
            try {
                // If the same station is already paused, simply resume
                if (currentStation?.id == station.id && _playbackState.value is RadioPlaybackState.Paused) {
                    resume()
                    return@launch
                }

                // If already playing this station, do nothing
                if (currentStation?.id == station.id && _playbackState.value is RadioPlaybackState.Playing) {
                    return@launch
                }

                currentStation = station
                _playbackState.value = RadioPlaybackState.Buffering(station)

                releasePlayer()

                withContext(Dispatchers.IO) {
                    val player = MediaPlayer().apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                        )
                        setDataSource(station.streamUrl)
                        setOnPreparedListener { mp ->
                            mp.start()
                            scope.launch {
                                _playbackState.value = RadioPlaybackState.Playing(station)
                            }
                        }
                        setOnErrorListener { _, what, extra ->
                            Log.e(TAG, "Playback error: what=$what, extra=$extra")
                            scope.launch {
                                _playbackState.value = RadioPlaybackState.Error(
                                    station,
                                    "प्रसारण सम्पर्क हुन सकेन। कृपया पछि प्रयास गर्नुहोस्।"
                                )
                            }
                            true
                        }
                        setOnCompletionListener {
                            scope.launch {
                                _playbackState.value = RadioPlaybackState.Idle
                            }
                        }
                        prepareAsync()
                    }
                    mediaPlayer = player
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting station ${station.nameNepali}: ${e.message}")
                _playbackState.value = RadioPlaybackState.Error(
                    station,
                    "प्रसारण खोल्न असमर्थ: ${e.message}"
                )
            }
        }
    }

    fun pause() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    currentStation?.let {
                        _playbackState.value = RadioPlaybackState.Paused(it)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing playback: ${e.message}")
        }
    }

    fun resume() {
        try {
            mediaPlayer?.let { player ->
                player.start()
                currentStation?.let {
                    _playbackState.value = RadioPlaybackState.Playing(it)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resuming playback: ${e.message}")
            currentStation?.let { play(it) }
        }
    }

    fun togglePlayPause(station: RadioStation) {
        val currentState = _playbackState.value
        when {
            currentStation?.id == station.id && currentState is RadioPlaybackState.Playing -> {
                pause()
            }
            currentStation?.id == station.id && currentState is RadioPlaybackState.Paused -> {
                resume()
            }
            else -> {
                play(station)
            }
        }
    }

    fun stop() {
        releasePlayer()
        currentStation = null
        _playbackState.value = RadioPlaybackState.Idle
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                reset()
                release()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing player: ${e.message}")
        } finally {
            mediaPlayer = null
        }
    }

    fun release() {
        stop()
    }
}
