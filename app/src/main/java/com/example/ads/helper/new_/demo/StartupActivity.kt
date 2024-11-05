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
import com.example.ads.helper.new_.demo.utils.SELECTED_APP_LANGUAGE_CODE
import com.example.ads.helper.new_.demo.utils.selectedAppLanguageCode
import com.example.app.ads.helper.VasuSplashConfig
import com.example.app.ads.helper.integrity.AppProtector
import com.example.app.ads.helper.interstitialad.InterstitialAdHelper
import com.example.app.ads.helper.openad.AppOpenAdHelper
import com.example.app.ads.helper.purchase.VasuSubscriptionConfig
import com.example.app.ads.helper.purchase.product.AdsManager
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper
import com.example.app.ads.helper.revenuecat.initRevenueCatProductList
import com.example.app.ads.helper.utils.isOnline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StartupActivity : BaseBindingActivity<ActivityStartupBinding>() {

    private var isFirstTime: Boolean = true
    private var isAdLoaded: Boolean = false
    private var isLaunchScreenWithAd: Boolean = false
    private var isLaunchNextScreen: Boolean = false
    private var isOpenSubscriptionScreen: Boolean = false
    private var isAppSafe: Boolean = false

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

        CoroutineScope(Dispatchers.IO).launch {
            ProductPurchaseHelper.setPurchaseListener(object : ProductPurchaseHelper.ProductPurchaseListener {
                override fun onBillingSetupFinished() {
                    Log.e(TAG, "onBillingSetupFinished: ")
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
        }

        mSelectLanguageDialog.show { fLanguageCode ->
            SELECTED_APP_LANGUAGE_CODE = fLanguageCode
            doAfterLanguageSelection()
        }
    }

    private fun doAfterLanguageSelection() {
        AppProtector.with(mActivity)
            .enableDebugMode(true)
            .setAppLanguageCode(selectedAppLanguageCode)
            .packageName(mActivity.packageName)
//            .versionName(BuildConfig.VERSION_NAME)
            .versionName("2.0")
            .needToBlockCheckIntegrity(true)
            .checkIntegrity {
                isAppSafe = true
                Log.e(TAG, "initView: Next Action")
                CoroutineScope(Dispatchers.IO).launch {
                    ProductPurchaseHelper.initBillingClient(fContext = mActivity)
                }
            }



        AdsManager.isShowAds.observe(mActivity) {
            Log.e(TAG, "initView: AdsManager needToShowAds::-> $it")
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
                            Log.e(TAG, "onOpenAdLoad: ")
                            isAdLoaded = true
                            checkAndLaunchScreenWithAd()
                        }
                    })
                } else {
                    InterstitialAdHelper.setOnInterstitialAdLoadListener(fListener = object : InterstitialAdHelper.OnInterstitialAdLoadListener {
                        override fun onAdLoaded() {
                            Log.e(TAG, "onInterstitialAdLoad: ")
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

        if (!isLaunchNextScreen) {
            VasuSplashConfig.showSplashFlow(
                fActivity = mActivity,
                onOpenSubscriptionScreen = {
                    isOpenSubscriptionScreen = true
                    VasuSubscriptionConfig.with(fActivity = mActivity, fAppPackageName = mActivity.packageName, fAppVersionName = BuildConfig.VERSION_NAME)
//                        .enableTestPurchase(true)
                        .setAppLanguageCode(selectedAppLanguageCode)
                        .setNotificationData(fNotificationData = VasuSubscriptionConfig.NotificationData(intentClass = StartupActivity::class.java).apply {
                            this.setNotificationIcon(id = R.drawable.ic_share_blue)
                        })
                        .launchScreen(
                            isFromSplash = true,
                            directShowMorePlanScreen = false,
                            onSubscriptionEvent = {},
                            onScreenFinish = { isUserPurchaseAnyPlan ->
                                checkAndLaunchNextScreen()
                            },
                            onOpeningError = {
                                checkAndLaunchNextScreen()
                            }
                        )
                },
                onNextAction = {
                    checkAndLaunchNextScreen()
                },
            )
        } else {
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
        if (isAppSafe) {
            if (isOnPause) {
                isOnPause = false

                mTimer?.cancelTimer()
                mTimer = null

                if (!isLaunchScreenWithAd && !isLaunchNextScreen && !isOpenSubscriptionScreen) {
                    if (isAdLoaded) {
                        startNextTimer()
                    } else {
                        loadAdWithTimer()
                    }
                } else if (isLaunchScreenWithAd && !isLaunchNextScreen && !isOpenSubscriptionScreen) {
                    Log.e(TAG, "onResume: checkAndLaunchNextScreen")
                    checkAndLaunchNextScreen()
                } else {
                    Log.e(TAG, "onResume: already going on next screen")
                }
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