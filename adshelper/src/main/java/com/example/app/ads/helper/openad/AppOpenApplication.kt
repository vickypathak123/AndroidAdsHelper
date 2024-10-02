@file:Suppress("unused")

package com.example.app.ads.helper.openad

import android.app.Activity
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.os.Process
import android.webkit.WebView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.example.app.ads.helper.activity.InterstitialNativeAdActivity
import com.example.app.ads.helper.base.utils.isPiePlus
import com.example.app.ads.helper.interstitialad.InterstitialAdHelper
import com.example.app.ads.helper.launcher.tabs.CustomTabsHelper
import com.example.app.ads.helper.nativead.NativeAdHelper
import com.example.app.ads.helper.purchase.fourplan.activity.FourPlanActivity
import com.example.app.ads.helper.purchase.product.AdsManager
import com.example.app.ads.helper.purchase.sixbox.activity.ViewAllPlansActivity
import com.example.app.ads.helper.purchase.timeline.activity.TimeLineActivity
import com.example.app.ads.helper.reward.RewardedInterstitialAdHelper
import com.example.app.ads.helper.reward.RewardedVideoAdHelper
import com.example.app.ads.helper.utils.initNetwork
import com.example.app.ads.helper.utils.isAnyAdOpen
import com.example.app.ads.helper.utils.isAppForeground
import com.example.app.ads.helper.utils.isEnableOpenAd
import com.example.app.ads.helper.utils.is_exit_dialog_opened
import com.example.app.ads.helper.utils.logD
import com.example.app.ads.helper.utils.logI
import com.example.app.ads.helper.utils.need_to_block_open_ad_internally
import com.example.app.ads.helper.utils.setTestDeviceIds
import com.example.app.ads.helper.utils.startShowingOpenAdInternally
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Akshay Harsoda
 * @since 28 Nov 2022
 * @updated 25 Jun 2024
 */
abstract class AppOpenApplication : MultiDexApplication(), DefaultLifecycleObserver {

    @Suppress("PrivatePropertyName")
    private val TAG: String = "Admob_${javaClass.simpleName}"

    private val mActivityLifecycleManager: ActivityLifecycleManager by lazy { ActivityLifecycleManager(this@AppOpenApplication) }

    //<editor-fold desc="OnCreate Function">
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super<MultiDexApplication>.onCreate()

        AdsManager(this).updateAdsVisibility()

        initNetwork(fContext = this@AppOpenApplication)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        CustomTabsHelper.initialize(this)

    }
    //</editor-fold>

    /**
     * Override this function in your application class to show the App Open Ad.
     *
     * @param fCurrentActivity Refers to your current activity.
     * @return `true` if you want to show the App Open Ad,
     * @return `false` if you don't want to show the App Open Ad.
     */
    abstract fun onResumeApp(fCurrentActivity: Activity): Boolean

    open fun destroyAllLoadedAd() {
        InterstitialAdHelper.destroy()
        NativeAdHelper.destroy()
        AppOpenAdHelper.destroy()
        RewardedInterstitialAdHelper.destroy()
        RewardedVideoAdHelper.destroy()
    }

    //<editor-fold desc="Init Ads & Set Test Device Id">
    /**
     * Initializing the mobile ads SDK.
     *
     * Helper method to set device IDs, which you can get from logs.
     * Check your logcat output for the test device ID, e.g.,
     * I/Ads: Use RequestConfiguration.Builder.setTestDeviceIds("TEST_DEVICE_ID","TEST_DEVICE_ID")
     *
     * @param fDeviceId Pass multiple "TEST_DEVICE_ID" values.
     */
    open fun initMobileAds(vararg fDeviceId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            setMobileAds(fDeviceId = fDeviceId)
        }
    }

    private fun setMobileAds(vararg fDeviceId: String) {
        if (isPiePlus) {

            getProcessName(applicationContext)?.let { processName ->
                if (processName != packageName) {
                    WebView.setDataDirectorySuffix(processName)
                }
            }


            /*val processName = getProcessName(applicationContext)
            if (processName != null && packageName != processName) {
                WebView.setDataDirectorySuffix(processName)
                MobileAds.initialize(baseContext) {
                    setDeviceIds(fDeviceId = fDeviceId)
                }
            } else {
                MobileAds.initialize(baseContext) {
                    setDeviceIds(fDeviceId = fDeviceId)
                }
            }*/
        }/* else {
            MobileAds.initialize(baseContext) {
                setDeviceIds(fDeviceId = fDeviceId)
            }
        }*/

        MobileAds.initialize(baseContext) {
            setDeviceIds(fDeviceId = fDeviceId)
        }
    }

    private fun getProcessName(fContext: Context?): String? {
//        if (context == null) return null

        fContext?.let { context ->
            context.getSystemService(ActivityManager::class.java)?.let { manager ->
                manager.runningAppProcesses?.let { runningAppProcesses ->
                    for (processInfo in runningAppProcesses) {
                        if (processInfo.pid == Process.myPid()) {
                            return processInfo.processName
                        }
                    }
                }
            }
        }

//        val manager = (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
//        for (processInfo in manager.runningAppProcesses) {
//            if (processInfo.pid == Process.myPid()) {
//                return processInfo.processName
//            }
//        }
        return null
    }

    private fun setDeviceIds(vararg fDeviceId: String) {
        logD(tag = TAG, message = "setDeviceIds: MobileAds Initialization Complete")
        setTestDeviceIds(*fDeviceId)
    }
    //</editor-fold>

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        logI(tag = TAG, message = "onStart: Before Change isAppForeground::$isAppForeground")
        isAppForeground = true
        logI(tag = TAG, message = "onStart: After Change isAppForeground::true")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        logI(tag = TAG, message = "onStop: Before Change isAppForeground::$isAppForeground")
        isAppForeground = false
        logI(tag = TAG, message = "onStop: After Change isAppForeground::false")

//        NativeAdvancedHelper.startAdClickTimer()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        logI(tag = TAG, message = "onResume: ")
        if (isEnableOpenAd) {
            if (isAppForeground) {
                mActivityLifecycleManager.mCurrentActivity?.let { fCurrentActivity ->
                    if (fCurrentActivity !is AdActivity) {
                        logI(tag = TAG, message = "onResume: Current Activity Is Not Ad Activity, isAnyAdOpen::$isAnyAdOpen")
                        if (!isAnyAdOpen) {
//                            if (fCurrentActivity !is InterstitialNativeAdActivity) {
                            if (!checkIsAdsActivity(fCurrentActivity = fCurrentActivity)) {
                                logI(tag = TAG, message = "onResume: Need To Show Open Ad needToBlockOpenAdInternally::$need_to_block_open_ad_internally")
                                if (!need_to_block_open_ad_internally) {
                                    val lDeveloperResumeFlag: Boolean = onResumeApp(fCurrentActivity)
                                    logI(tag = TAG, message = "onResume: Need To Show Open Ad yourResumeFlag::$lDeveloperResumeFlag")
                                    if (lDeveloperResumeFlag) {
                                        mActivityLifecycleManager.showOpenAd()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (need_to_block_open_ad_internally) {
                startShowingOpenAdInternally()
            }
        }
    }

    private fun checkIsAdsActivity(fCurrentActivity: Activity): Boolean {
        val isAdsActivity: Boolean = when (fCurrentActivity) {
            is FourPlanActivity,
            is ViewAllPlansActivity,
            is TimeLineActivity,
            is InterstitialNativeAdActivity -> true

            else -> false
        }

        return if (is_exit_dialog_opened) true else isAdsActivity
    }
}