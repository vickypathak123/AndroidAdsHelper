package com.example.ads.helper.new_.demo

import android.util.Log
import android.view.View
import com.example.ads.helper.new_.demo.activitys.MainActivity
import com.example.ads.helper.new_.demo.base.BaseActivity
import com.example.ads.helper.new_.demo.base.BaseBindingActivity
import com.example.ads.helper.new_.demo.base.shared_prefs.getBoolean
import com.example.ads.helper.new_.demo.databinding.ActivityStartupBinding
import com.example.ads.helper.new_.demo.utils.AppTimer
import com.example.ads.helper.new_.demo.utils.REVENUE_CAT_ID
import com.example.app.ads.helper.VasuSplashConfig
import com.example.app.ads.helper.interstitialad.InterstitialAdHelper
import com.example.app.ads.helper.isOnline
import com.example.app.ads.helper.logE
import com.example.app.ads.helper.openad.AppOpenAdHelper
import com.example.app.ads.helper.purchase.VasuSubscriptionConfig
import com.example.app.ads.helper.purchase.product.AdsManager
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper
import com.example.app.ads.helper.purchase.utils.MorePlanScreenType
import com.example.app.ads.helper.revenuecat.initRevenueCatProductList
import com.safetynet.integritycheck.integrity.AppProtector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StartupActivity : BaseBindingActivity<ActivityStartupBinding>() {

    private var isFirstTime: Boolean = true
    private var isAdLoaded: Boolean = false
    private var isLaunchScreenWithAd: Boolean = false
    private var isLaunchNextScreen: Boolean = false

    override fun getActivityContext(): BaseActivity {
        return this@StartupActivity
    }

    override fun setBinding(): ActivityStartupBinding {
        return ActivityStartupBinding.inflate(layoutInflater)
    }


    override fun onNoInternetDialogShow() {
        super.onNoInternetDialogShow()
        mTimer?.cancelTimer()
        mTimer = null
    }

    override fun onNoInternetDialogDismiss() {
        super.onNoInternetDialogDismiss()
        initView()
    }

    @Suppress("DEPRECATION")
    override fun initView() {
        super.initView()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        AppProtector.with(mActivity)
            .checkIntegrity {

            }

        CoroutineScope(Dispatchers.IO).launch {
            ProductPurchaseHelper.setPurchaseListener(object : ProductPurchaseHelper.ProductPurchaseListener {
                override fun onBillingSetupFinished() {
                    val action: () -> Unit = {
                        CoroutineScope(Dispatchers.Main).launch {
                            mActivity.runOnUiThread {
                                loadAdWithTimer()
                            }
                        }
                    }

                    if (REVENUE_CAT_ID.isNotEmpty()) {
                        initRevenueCatProductList(fContext = mActivity, onInitializationComplete = action)
                    } else {
                        ProductPurchaseHelper.initProductsKeys(fContext = mActivity, onInitializationComplete = action)
                    }
                }
            })

            ProductPurchaseHelper.initBillingClient(fContext = mActivity)
        }

        AdsManager.isShowAds.observe(mActivity) {
            logE(TAG, "initView: AdsManager needToShowAds::-> $it")
        }
    }

    private fun loadAdWithTimer() {
        if (isOnline) {
            startTimer(millisInFuture = 6000, countDownInterval = 1000)

            if (isFirstTime) {
                isFirstTime = false
                if (this.getBoolean(IS_OPEN_ADS_ENABLE, true)) {
                    AppOpenAdHelper.setOnAppOpenAdLoadListener(fListener = object : AppOpenAdHelper.OnAppOpenAdLoadListener {
                        override fun onAdLoaded() {
                            Log.e(TAG, "Admob_ onOpenAdLoad: ")
                            isAdLoaded = true
                            checkAndLaunchScreenWithAd()
                        }
                    })
                } else {
                    InterstitialAdHelper.setOnInterstitialAdLoadListener(fListener = object : InterstitialAdHelper.OnInterstitialAdLoadListener {
                        override fun onAdLoaded() {
                            Log.e(TAG, "Admob_Inte onInterstitialAdLoad: ")
                            isAdLoaded = true
                            checkAndLaunchScreenWithAd()
                        }
                    })
                }
            }
        } else {
            startTimer(millisInFuture = 1000, countDownInterval = 1000)
        }
    }


    private fun startTimer(millisInFuture: Long, countDownInterval: Long) {
        mTimer?.cancelTimer()
        mTimer = AppTimer(
            millisInFuture = millisInFuture,
            countDownInterval = countDownInterval,
            onFinish = {
                checkAndLaunchScreenWithAd()
            }
        )

        mTimer?.start()
    }

    private fun checkAndLaunchScreenWithAd() {
        if (!isOnPause) {
            if (!isLaunchScreenWithAd) {
                launchScreenWithAd()
            }
        }
    }

    private fun launchScreenWithAd() {
        mTimer?.cancelTimer()
        mTimer = null
        isLaunchScreenWithAd = true

        /*if (this.getBoolean(IS_OPEN_ADS_ENABLE, true)) {
            if (!isLaunchNextScreen) {
                Log.e(TAG, "openActivityWithAd: Call With or With-Out Open Ad")
                mActivity.showAppOpenAd {
                    checkAndLaunchNextScreen()
                }
            }
        } else {
            if (!isLaunchNextScreen) {
                mActivity.showInterstitialAd { _, _ ->
                    Log.e(TAG, "openActivityWithAd: Call With or With-Out Interstitial Ad")
                    checkAndLaunchNextScreen()
                }
            }
        }*/

        if (!isLaunchNextScreen) {
            VasuSplashConfig.showSplashFlow(
                fActivity = mActivity,
                onOpenSubscriptionScreen = {
                    Log.e(TAG, "launchScreenWithAd: onOpenSubscriptionScreen")
                    VasuSubscriptionConfig.with(fActivity = mActivity)
                        .enableTestPurchase(true)
                        .setNotificationData(fNotificationData = VasuSubscriptionConfig.NotificationData(intentClass = StartupActivity::class.java).apply {
                            this.setNotificationIcon(id = R.drawable.ic_share_blue)
                        })
                        .launchScreen(
//                            morePlanScreenType = MorePlanScreenType.FOUR_PLAN_SCREEN,
                            isFromSplash = true,
//                            showCloseAdForTimeLineScreen = true,
//                            showCloseAdForViewAllPlanScreenOpenAfterSplash = true,
//                            showCloseAdForViewAllPlanScreen = true,
                            directShowMorePlanScreen = false,
                            onSubscriptionEvent = {},
                            onScreenFinish = {
                                Log.e(TAG, "launchScreenWithAd: $it, onScreenFinish")
                                checkAndLaunchNextScreen()
                            }
                        )
                },
                onNextAction = {
                    Log.e(TAG, "launchScreenWithAd: onNextAction")
                    checkAndLaunchNextScreen()
                }
            )
        } else {
            Log.e(TAG, "launchScreenWithAd: else")
            checkAndLaunchNextScreen()
        }
    }

    private fun startNextTimer() {
        startTimer(millisInFuture = 500, countDownInterval = 100)
    }

    private fun checkAndLaunchNextScreen() {
        if (!isLaunchNextScreen) {
            launchNextScreen()
        }
    }

    private fun launchNextScreen() {
        isLaunchNextScreen = true
        launchActivity(fIntent = getActivityIntent<MainActivity>(), isAdsShowing = false, isNeedToFinish = true)
    }
    //</editor-fold>

    override fun onResume() {
        if (isOnPause) {
            isOnPause = false

            mTimer?.cancelTimer()
            mTimer = null

            if (!isLaunchScreenWithAd && !isLaunchNextScreen) {
                if (isAdLoaded) {
                    startNextTimer()
                } else {
                    loadAdWithTimer()
                }
            } else if (isLaunchScreenWithAd && !isLaunchNextScreen) {
                checkAndLaunchNextScreen()
            } else {
                Log.e(TAG, "onResume: already going on next screen")
            }
        }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        isOnPause = true
        mTimer?.cancelTimer()
    }

    override fun customOnBackPressed() {

    }
}