package com.example.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(private val context: Context) {

    // Standard Google AdMob test interstitial ad unit ID
    private val adUnitId = "ca-app-pub-3940256099942544/1033173712"

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private var clickCount = 0

    init {
        loadAd()
    }

    fun loadAd() {
        if (isLoading || interstitialAd != null) return
        isLoading = true

        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                adUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        isLoading = false
                        Log.d("InterstitialAdManager", "Interstitial ad loaded successfully")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        interstitialAd = null
                        isLoading = false
                        Log.w("InterstitialAdManager", "Interstitial ad failed to load: ${loadAdError.message}")
                    }
                }
            )
        } catch (e: Throwable) {
            isLoading = false
            Log.w("InterstitialAdManager", "Interstitial load error: ${e.message}")
        }
    }

    /**
     * Handles article click. Displays interstitial ad every 3rd click.
     * Calls `onProceedToReader` immediately if ad is not ready or not on 3rd click.
     */
    fun onArticleClicked(activity: Activity?, onProceedToReader: () -> Unit) {
        clickCount++

        val shouldShowAd = (clickCount % 3 == 0) && (interstitialAd != null) && (activity != null)

        if (shouldShowAd) {
            val ad = interstitialAd
            interstitialAd = null // Consume current ad

            ad?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    loadAd() // Preload for next 3rd click
                    onProceedToReader()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    loadAd()
                    onProceedToReader()
                }
            }

            try {
                ad?.show(activity)
            } catch (e: Throwable) {
                Log.w("InterstitialAdManager", "Failed to show ad: ${e.message}")
                loadAd()
                onProceedToReader()
            }
        } else {
            // Not 3rd click or ad not ready: proceed straight to reader
            if (interstitialAd == null) {
                loadAd()
            }
            onProceedToReader()
        }
    }
}
