package com.myisolutions.income.tax.calculator.pakistan

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.*

class MainApplication : Application(), Application.ActivityLifecycleCallbacks,
    LifecycleEventObserver
{
    private lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null

    override fun onCreate()
    {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        MobileAds.initialize(this) {}
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenAdManager = AppOpenAdManager()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event)
    {
        if (event == Lifecycle.Event.ON_START)
        {
            currentActivity?.let {
                appOpenAdManager.showAdIfAvailable(it)
            }
        }
    }

    /** Interface definition for a callback to be invoked when an app open ad is complete. */
    interface OnShowAdCompleteListener
    {
        fun onShowAdComplete()
    }

    private inner class AppOpenAdManager
    {
        private val test = "ca-app-pub-3940256099942544/9257395921"
        private val live = "ca-app-pub-1035415808955400/8613369064"
        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false
        var isShowingAd = false

        /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
        private var loadTime: Long = 0

        /** Show the ad if one isn't already showing. */
        fun showAdIfAvailable(activity: Activity)
        {
            showAdIfAvailable(activity, object : OnShowAdCompleteListener
            {
                override fun onShowAdComplete()
                {
                    // Empty because the user will go back to the activity that shows the ad.
                }
            })
        }

        /** Shows the ad if one isn't already showing. */
        fun showAdIfAvailable(
            activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener
        )
        {
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd)
            {
                Log.d("LOG_TAG", "The app open ad is already showing.")
                return
            }
            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable())
            {
                Log.d("LOG_TAG", "The app open ad is not ready yet.")
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
                return
            }

            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback()
            {
                override fun onAdDismissedFullScreenContent()
                {
                    // Called when full screen content is dismissed.
                    // Set the reference to null so isAdAvailable() returns false.
                    Log.d("LOG_TAG", "Ad dismissed fullscreen content.")
                    appOpenAd = null
                    isShowingAd = false

                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError)
                {
                    // Called when fullscreen content failed to show.
                    // Set the reference to null so isAdAvailable() returns false.
                    Log.d("LOG_TAG", adError.message)
                    appOpenAd = null
                    isShowingAd = false

                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                }

                override fun onAdShowedFullScreenContent()
                {
                    // Called when fullscreen content is shown.
                    Log.d("LOG_TAG", "Ad showed fullscreen content.")
                }
            }
            isShowingAd = true
            appOpenAd?.show(activity)
        }

        /** Request an ad. */
        fun loadAd(activity: Activity)
        {
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable())
            {
                return
            }

            isLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(activity,
                live,
                request,
                object : AppOpenAd.AppOpenAdLoadCallback()
                {
                    override fun onAdLoaded(ad: AppOpenAd)
                    {
                        // Called when an app open ad has loaded.
                        Log.d("LOG_TAG", "Ad was loaded.")
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                        appOpenAd?.show(activity)
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError)
                    {
                        // Called when an app open ad has failed to load.
                        Log.d("LOG_TAG", loadAdError.message)
                        isLoadingAd = false;
                    }
                })
        }

        /** Check if ad exists and can be shown. */
        private fun isAdAvailable(): Boolean
        {
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
        }

        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean
        {
            val dateDifference: Long = Date().time - loadTime
            val numMilliSecondsPerHour: Long = 3600000
            return dateDifference < numMilliSecondsPerHour * numHours
        }
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?)
    {
    }

    override fun onActivityStarted(activity: Activity)
    {
        if (!appOpenAdManager.isShowingAd)
        {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(p0: Activity)
    {
    }

    override fun onActivityPaused(p0: Activity)
    {
    }

    override fun onActivityStopped(p0: Activity)
    {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle)
    {
    }

    override fun onActivityDestroyed(p0: Activity)
    {
    }
}