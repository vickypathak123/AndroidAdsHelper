package com.example.ads.helper.new_.demo

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.example.ads.helper.new_.demo.base.shared_prefs.getBoolean
import com.example.ads.helper.new_.demo.base.utils.getStringRes
import com.example.ads.helper.new_.demo.utils.LIFE_TIME_SKU
import com.example.ads.helper.new_.demo.utils.MONTHLY_SKU
import com.example.ads.helper.new_.demo.utils.REVENUE_CAT_ID
import com.example.ads.helper.new_.demo.utils.WEEKLY_SKU
import com.example.ads.helper.new_.demo.utils.YEARLY_SKU
import com.example.app.ads.helper.VasuAdsConfig
import com.example.app.ads.helper.openad.AppOpenApplication
import com.example.app.ads.helper.remoteconfig.initSubscriptionRemoteConfig
import com.example.app.ads.helper.revenuecat.initRevenueCat

class AppApplication : AppOpenApplication() {

    private val TAG = javaClass.simpleName

    private val isAdEnable: Boolean = true

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

//        this.save(IS_OPEN_ADS_ENABLE, true)

        Log.e(TAG, "onCreate: IS_OPEN_ADS_ENABLE::${this.getBoolean(IS_OPEN_ADS_ENABLE, true)}")

        VasuAdsConfig.with(this)
            .enableOpenAd(fIsEnable = true)
            .enableDebugMode(fIsEnable = true)
            .enablePurchaseHistoryLog(fIsEnable = false)
            .needToTakeAllTestAdID(fIsTakeAll = true)
            .needToBlockInterstitialAd(fIsBlock = false)
            .setLifeTimeProductKey(LIFE_TIME_SKU)
            .setSubscriptionKey(WEEKLY_SKU, MONTHLY_SKU, YEARLY_SKU)
            .setAdmobOpenAdId(getStringRes(com.example.app.ads.helper.R.string.test_admob_open_ad_id))
            .setAdmobBannerAdId(getStringRes(com.example.app.ads.helper.R.string.test_admob_banner_ad_id))
            .setAdmobRewardVideoAdId(getStringRes(com.example.app.ads.helper.R.string.test_admob_reward_video_ad_id))
            .setAdmobInterstitialAdId(getStringRes(com.example.app.ads.helper.R.string.test_admob_interstitial_ad_id))
            .setAdmobNativeAdvancedAdId(getStringRes(com.example.app.ads.helper.R.string.test_admob_native_advanced_ad_id))
            .setAdmobRewardInterstitialAdId(getStringRes(com.example.app.ads.helper.R.string.test_admob_reward_interstitial_ad_id))
            .setAdmobSplashBannerAdId(getStringRes(com.example.app.ads.helper.R.string.test_admob_adaptive_banner_ad_id))
            .initialize()

        initMobileAds()

        if (REVENUE_CAT_ID.isNotEmpty()) {
            initRevenueCat(fContext = this, revenueCatID = REVENUE_CAT_ID)
        }

        destroyAllLoadedAd()
    }

    override fun onResumeApp(fCurrentActivity: Activity): Boolean {
        return true
    }

//    override fun onResumeApp(fCurrentActivity: Activity): Boolean {
//        Log.e(TAG, "onResumeApp: fCurrentActivity::${fCurrentActivity.localClassName}")
////        val isNeedToShowAd: Boolean = when {
////            fCurrentActivity is SplashActivity -> {
////                Log.d(TAG, "onResumeApp: fCurrentActivity is SplashActivity")
////                false
////            }
////
////            fCurrentActivity is SecondActivity -> {
////                Log.d(TAG, "onResumeApp: fCurrentActivity is ExitActivity")
////                false
////            }
////
////            this@AppApplication.isNeedToLoadAd -> {
////                true
////            }
////
////            else -> {
////                false
////            }
////        }
////
////        return isNeedToShowAd
//
//        return false
//    }
}