package com.example.app.ads.helper.activity

import android.app.Activity
import android.content.Intent
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.BaseActivity
import com.example.app.ads.helper.base.BaseBindingActivity
import com.example.app.ads.helper.base.utils.beVisibleIf
import com.example.app.ads.helper.databinding.ActivityInterstitialNativeAdBinding
import com.example.app.ads.helper.utils.isAnyAdOpen
import com.example.app.ads.helper.utils.logI
import com.example.app.ads.helper.nativead.NativeAdInterstitialType
import com.example.app.ads.helper.nativead.NativeAdView


fun showFullScreenNativeAdActivity(fActivity: Activity, onInterstitialNativeAdClosed: () -> Unit) {
    InterstitialNativeAdActivity.lunchFullScreenAd(fActivity = fActivity, onInterstitialNativeAdClosed = onInterstitialNativeAdClosed)
}

internal class InterstitialNativeAdActivity : BaseBindingActivity<ActivityInterstitialNativeAdBinding>() {

    override fun setBinding(): ActivityInterstitialNativeAdBinding {
        return ActivityInterstitialNativeAdBinding.inflate(layoutInflater)
    }

    override fun getActivityContext(): BaseActivity {
        return this@InterstitialNativeAdActivity
    }

    companion object {
        private var onThisAdClosed: () -> Unit = {}
        fun lunchFullScreenAd(fActivity: Activity, onInterstitialNativeAdClosed: () -> Unit) {
            onThisAdClosed = onInterstitialNativeAdClosed
            Intent(fActivity, InterstitialNativeAdActivity::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }.also { fActivity.startActivity(it) }
        }
    }

    override fun setParamBeforeLayoutInit() {
        super.setParamBeforeLayoutInit()
        setTheme(R.style.theme_interstitial_native_ad_activity)
        window?.let { lWindow ->
            lWindow.decorView.let { lDecorView ->
                WindowInsetsControllerCompat(lWindow, lDecorView).apply {
                    this.isAppearanceLightStatusBars = true // true or false as desired.
                    this.isAppearanceLightNavigationBars = true
                    this.hide(WindowInsetsCompat.Type.systemBars())
                }
            }
        }
    }

    override fun initView() {
        super.initView()
        isAnyAdOpen = true
        loadAds()
    }

    private fun loadAds() {
        with(mBinding) {
            interstitialNativeAdView.let { view ->
                view.setOnNativeAdViewListener(fListener = object : NativeAdView.OnNativeAdViewListener {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        isAnyAdOpen = true
                        logI(TAG, "onAdLoaded: ")
                        mActivity.runOnUiThread {
                            ivCloseAd.beVisibleIf(view.getNativeAdInterstitialType() == NativeAdInterstitialType.WEBSITE)
                        }
                    }

                    override fun onAdCustomClosed() {
                        super.onAdCustomClosed()
                        logI(TAG, "onAdCustomClosed: ")
                        isAnyAdOpen = true
                        mActivity.runOnUiThread {
                            ivCloseAd.performClick()
                        }
                    }

                    override fun onAdFailed() {
                        super.onAdFailed()
                        logI(TAG, "onAdFailed: ")
                        isAnyAdOpen = true
                        mActivity.runOnUiThread {
                            ivCloseAd.performClick()
                        }
                    }
                })
            }
        }

    }

    override fun initViewListener() {
        super.initViewListener()
        mBinding.ivCloseAd.setOnClickListener {
            finishTask()
        }
    }

    private fun finishTask() {
        onThisAdClosed.invoke()
        isAnyAdOpen = false
        finishAfterTransition()
    }

    override fun customOnBackPressed() {

    }
}