package com.example.ads.helper.new_.demo.newdemo.activity

import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.ads.helper.new_.demo.R
import com.example.ads.helper.new_.demo.StartupActivity
import com.example.ads.helper.new_.demo.base.BaseActivity
import com.example.ads.helper.new_.demo.base.BaseBindingActivity
import com.example.ads.helper.new_.demo.base.utils.toEditable
import com.example.ads.helper.new_.demo.databinding.ActivityRemoteConfigBinding
import com.example.ads.helper.new_.demo.newdemo.utils.AppRemoteConfigModel
import com.example.ads.helper.new_.demo.newdemo.utils.GoogleAdsModel
import com.example.ads.helper.new_.demo.newdemo.utils.PlayIntegrityModel
import com.example.ads.helper.new_.demo.newdemo.utils.VasuSubscriptionConfigModel
import com.example.ads.helper.new_.demo.newdemo.utils.remoteConfigModel
import com.example.ads.helper.new_.demo.newdemo.utils.setOfflineRemoteConfig
import com.example.ads.helper.new_.demo.newdemo.utils.setRemoteConfigJson
import com.example.ads.helper.new_.demo.utils.SELECTED_APP_LANGUAGE_CODE
import com.example.app.ads.helper.VasuAdsConfig
import com.example.app.ads.helper.purchase.utils.MorePlanScreenType

class RemoteConfigActivity : BaseBindingActivity<ActivityRemoteConfigBinding>() {

    override fun setBinding(): ActivityRemoteConfigBinding {
        return ActivityRemoteConfigBinding.inflate(layoutInflater)
    }

    override fun getActivityContext(): BaseActivity {
        return this@RemoteConfigActivity
    }

    override fun initView() {
        super.initView()

        with(mBinding) {
            val screenItems = listOf(MorePlanScreenType.FOUR_PLAN_SCREEN.value, MorePlanScreenType.SIX_BOX_SCREEN.value,MorePlanScreenType.WEEKLY_SCREEN.value)
            val screenAdapter = ArrayAdapter(mActivity, R.layout.list_item, screenItems)
            (tilMorePlanScreenType.editText as? AutoCompleteTextView)?.setAdapter(screenAdapter)

            val languageItems = resources.getStringArray(R.array.subscription_screen_language)
            val languageAdapter = ArrayAdapter(mActivity, R.layout.list_item, languageItems)
            (tilAppLanguage.editText as? AutoCompleteTextView)?.setAdapter(languageAdapter)
        }

        mActivity.setOfflineRemoteConfig {
            with(mActivity.remoteConfigModel) {
                Log.d(TAG, "initView: <<--Akshay-->> $this")
                with(mBinding) {
                    switchNeedToShowBannerAd.isChecked = googleAds.isNeedToShowBannerAd
                    switchNeedToShowInterstitialAd.isChecked = googleAds.isNeedToShowInterstitialAd
                    switchNeedToShowNativeAd.isChecked = googleAds.isNeedToShowNativeAd
                    switchNeedToShowAppOpenAd.isChecked = googleAds.isNeedToShowAppOpenAd
                    switchNeedToShowRewardedInterstitialAd.isChecked = googleAds.isNeedToShowRewardedInterstitialAd
                    switchNeedToShowRewardedVideoAd.isChecked = googleAds.isNeedToShowRewardedVideoAd

                    etInitialSubscriptionOpenFlow.text = vasuSubscriptionConfig.initialSubscriptionOpenFlow.joinToString(",").toEditable
                    etPurchaseButtonTextIndex.text = vasuSubscriptionConfig.purchaseButtonTextIndex.toString().toEditable
                    switchInitialSubscriptionTimeLineCloseAd.isChecked = vasuSubscriptionConfig.initialSubscriptionTimeLineCloseAd
                    switchInitialSubscriptionMorePlanCloseAd.isChecked = vasuSubscriptionConfig.initialSubscriptionMorePlanCloseAd
                    switchInAppSubscriptionAdClose.isChecked = vasuSubscriptionConfig.inAppSubscriptionAdClose
                    etMorePlanScreenType.setText(vasuSubscriptionConfig.morePlanScreenType, false)
                    etLifeTimePlanDiscountPercentage.text = vasuSubscriptionConfig.lifeTimePlanDiscountPercentage.toString().toEditable
                    etRattingBarSliderTiming.text = vasuSubscriptionConfig.rattingBarSliderTiming.toString().toEditable
                    etWeeklyCloseTiming.text = vasuSubscriptionConfig.subscriptionCloseIconShowTime.toString().toEditable

                    switchErrorHide.isChecked = playIntegrity.errorHide
                    etVerdictsResponseCodes.text = playIntegrity.verdictsResponseCodes.joinToString(", ").toEditable
                }
            }
        }
    }

    override fun initViewListener() {
        super.initViewListener()
        setClickListener(mBinding.btnNext)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        with(mBinding) {
            when (v) {
                btnNext -> {
                    val lDataModel = AppRemoteConfigModel(
                        googleAds = GoogleAdsModel(
                            isNeedToShowBannerAd = switchNeedToShowBannerAd.isChecked,
                            isNeedToShowInterstitialAd = switchNeedToShowInterstitialAd.isChecked,
                            isNeedToShowNativeAd = switchNeedToShowNativeAd.isChecked,
                            isNeedToShowAppOpenAd = switchNeedToShowAppOpenAd.isChecked,
                            isNeedToShowRewardedInterstitialAd = switchNeedToShowRewardedInterstitialAd.isChecked,
                            isNeedToShowRewardedVideoAd = switchNeedToShowRewardedVideoAd.isChecked,
                        ),
                        vasuSubscriptionConfig = VasuSubscriptionConfigModel(
                            initialSubscriptionOpenFlow = ArrayList(etInitialSubscriptionOpenFlow.text?.split(",")?.mapNotNull { it.trim().toIntOrNull() } ?: arrayListOf()),
                            purchaseButtonTextIndex = etPurchaseButtonTextIndex.text?.toString()?.trim()?.toIntOrNull() ?: 0,
                            initialSubscriptionTimeLineCloseAd = switchInitialSubscriptionTimeLineCloseAd.isChecked,
                            initialSubscriptionMorePlanCloseAd = switchInitialSubscriptionMorePlanCloseAd.isChecked,
                            inAppSubscriptionAdClose = switchInAppSubscriptionAdClose.isChecked,
                            morePlanScreenType = etMorePlanScreenType.text?.toString()?.trim() ?: MorePlanScreenType.SIX_BOX_SCREEN.value,
                            lifeTimePlanDiscountPercentage = etLifeTimePlanDiscountPercentage.text?.toString()?.trim()?.toIntOrNull() ?: 90,
                            rattingBarSliderTiming = etRattingBarSliderTiming.text?.toString()?.trim()?.toIntOrNull() ?: 5000,
                            subscriptionCloseIconShowTime = etWeeklyCloseTiming.text?.toString()?.trim()?.toLongOrNull() ?: 3000L
                        ),
                        playIntegrity = PlayIntegrityModel(
                            errorHide = switchErrorHide.isChecked,
                            verdictsResponseCodes = ArrayList(etVerdictsResponseCodes.text?.split(", ")?.map { it.trim() } ?: arrayListOf()),
                        ),
                    )

                    Log.i(TAG, "onClick: <<--Akshay-->> $lDataModel")

                    mActivity.setRemoteConfigJson(
                        fDataModel = lDataModel,
                        action = {
                            SELECTED_APP_LANGUAGE_CODE = mactAppLanguage.text?.toString()?.substringAfter("(")?.substringBefore(")")?.takeIf { it.isNotEmpty() } ?: "en"
                            initAdsRemoteConfigFlag()
                            launchActivity(
                                fIntent = getActivityIntent<StartupActivity>(),
                                isAdsShowing = false,
                                isNeedToFinish = true
                            )
                        }
                    )
                }

                else -> {}
            }
        }
    }

    private fun initAdsRemoteConfigFlag() {
        with(remoteConfigModel.googleAds) {
            VasuAdsConfig.with(mActivity)
                .enableAppOpenAdFromRemoteConfig(fIsEnable = isNeedToShowAppOpenAd)
                .enableBannerAdFromRemoteConfig(fIsEnable = isNeedToShowBannerAd)
                .enableInterstitialAdFromRemoteConfig(fIsEnable = isNeedToShowInterstitialAd)
                .enableNativeAdFromRemoteConfig(fIsEnable = isNeedToShowNativeAd)
                .enableRewardedInterstitialAdFromRemoteConfig(fIsEnable = isNeedToShowRewardedInterstitialAd)
                .enableRewardedVideoAdFromRemoteConfig(fIsEnable = isNeedToShowRewardedVideoAd)
                .initializeRemoteConfig()
        }
    }
}