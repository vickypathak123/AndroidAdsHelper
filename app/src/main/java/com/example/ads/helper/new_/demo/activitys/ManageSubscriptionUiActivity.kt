package com.example.ads.helper.new_.demo.activitys

import android.content.res.ColorStateList
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.CompoundButton
import com.example.ads.helper.new_.demo.BuildConfig
import com.example.ads.helper.new_.demo.R
import com.example.ads.helper.new_.demo.StartupActivity
import com.example.ads.helper.new_.demo.base.BaseActivity
import com.example.ads.helper.new_.demo.base.BaseBindingActivity
import com.example.ads.helper.new_.demo.base.utils.beVisibleIf
import com.example.ads.helper.new_.demo.base.utils.getDrawableRes
import com.example.ads.helper.new_.demo.base.utils.makeText
import com.example.ads.helper.new_.demo.databinding.ActivityManageSubscriptionUiBinding
import com.example.app.ads.helper.purchase.VasuSubscriptionConfig
import com.example.app.ads.helper.purchase.fourplan.utils.FourPlanRattingItem
import com.example.app.ads.helper.purchase.fourplan.utils.FourPlanRattingItemType
import com.example.app.ads.helper.purchase.fourplan.utils.FourPlanUserItem
import com.example.app.ads.helper.purchase.utils.MorePlanScreenType

class ManageSubscriptionUiActivity : BaseBindingActivity<ActivityManageSubscriptionUiBinding>(), CompoundButton.OnCheckedChangeListener {

    override fun setBinding(): ActivityManageSubscriptionUiBinding = ActivityManageSubscriptionUiBinding.inflate(layoutInflater)

    override fun getActivityContext(): BaseActivity = this@ManageSubscriptionUiActivity

    override fun initView() {
        super.initView()
        with(mBinding) {
            with(layoutHeader) {
                ivHeaderBack.setImageDrawable(mActivity.getDrawableRes(R.drawable.ic_new_header_back))
                txtHeaderTitle.text = "Subscription UI Update"
            }

            lyTimeLineMainColor.switchItem.text = "Time Line Main Color :-"
            lyTimeLineHeaderColor.switchItem.text = "Time Line Header Color :-"
            lyTimeLineCloseIconColor.switchItem.text = "Time Line Close Icon Color :-"
            lyTimeLineTrackInactiveColor.switchItem.text = "Time Line Track Inactive Color :-"
            lyTimeLineHintTextColor.switchItem.text = "Time Line Hint Text Color :-"
            lyTimeLineInstantAccessHintTextColor.switchItem.text = "Time Line Instant Access Hint Text Color :-"
            lyTimeLineSecureWithPlayStoreTextColor.switchItem.text = "Time Line Secure With Play Store Text Color :-"
            lyTimeLineSecureWithPlayStoreBackgroundColor.switchItem.text = "Time Line Secure With Play Store Background Color :-"
            lyTimeLineButtonContinueTextColor.switchItem.text = "Time Line Button Continue Text Color :-"

            lyViewAllPlansHeaderColor.switchItem.text = "View All Plans Header Color :-"
            lyViewAllPlansSubHeaderColor.switchItem.text = "View All Plans Sub Header Color :-"
            lyViewAllPlansCloseIconColor.switchItem.text = "View All Plans Close Icon Color :-"
            lyViewAllPlansRatingColor.switchItem.text = "View All Plans Rating Color :-"
            lyViewAllPlansRatingPlaceHolderColor.switchItem.text = "View All Plans Rating Place Holder Color :-"
            lyViewAllPlansRatingIndicatorColor.switchItem.text = "View All Plans Rating Indicator Color :-"
            lyViewAllPlansUnselectedItemDataColor.switchItem.text = "View All Plans Unselected Item Data Color :-"
            lyViewAllPlansSelectedItemDataColor.switchItem.text = "View All Plans Selected Item Data Color :-"
            lyViewAllPlansPayNothingNowColor.switchItem.text = "View All Plans Pay Nothing Now Color :-"
            lyViewAllPlansSecureWithPlayStoreTextColor.switchItem.text = "View All Plans Secure With Play Store Text Color :-"
            lyViewAllPlansSecureWithPlayStoreBackgroundColor.switchItem.text = "View All Plans Secure With Play Store Background Color :-"
            lyViewAllPlansItemBoxBackgroundColor.switchItem.text = "View All Plans Item Box Background Color :-"
            lyViewAllPlansSelectedSkuBackgroundColor.switchItem.text = "View All Plans Selected SKU Background Color :-"
            lyViewAllPlansUnselectedSkuBackgroundColor.switchItem.text = "View All Plans Unselected SKU Background Color :-"
        }
    }

    private fun setCheckedChangeListener(vararg fViews: CompoundButton) {
        for (lView in fViews) {
            lView.setOnCheckedChangeListener(this@ManageSubscriptionUiActivity)
            lView.isChecked = true
            lView.isChecked = false
        }
    }

    override fun initViewListener() {
        super.initViewListener()
        with(mBinding) {
            setClickListener(layoutHeader.ivHeaderBack, btnCancel, btnUpdate)
            setCheckedChangeListener(
                switchPrivacyPolicy,
                switchTermsOfUse,
                switchTimeLineScreenUi,
                switchViewAllPlansScreenUi,

                switchTimeLineInstantAccessAnimation,
                switchTimeLineSliderAnimation,
                lyTimeLineMainColor.switchItem,
                lyTimeLineHeaderColor.switchItem,
                lyTimeLineCloseIconColor.switchItem,
                lyTimeLineTrackInactiveColor.switchItem,
                lyTimeLineHintTextColor.switchItem,
                lyTimeLineInstantAccessHintTextColor.switchItem,
                lyTimeLineSecureWithPlayStoreTextColor.switchItem,
                lyTimeLineSecureWithPlayStoreBackgroundColor.switchItem,
                lyTimeLineButtonContinueTextColor.switchItem,

                lyViewAllPlansHeaderColor.switchItem,
                lyViewAllPlansSubHeaderColor.switchItem,
                lyViewAllPlansCloseIconColor.switchItem,
                lyViewAllPlansRatingColor.switchItem,
                lyViewAllPlansRatingPlaceHolderColor.switchItem,
                lyViewAllPlansRatingIndicatorColor.switchItem,
                lyViewAllPlansUnselectedItemDataColor.switchItem,
                lyViewAllPlansSelectedItemDataColor.switchItem,
                lyViewAllPlansPayNothingNowColor.switchItem,
                lyViewAllPlansSecureWithPlayStoreTextColor.switchItem,
                lyViewAllPlansSecureWithPlayStoreBackgroundColor.switchItem,
                lyViewAllPlansItemBoxBackgroundColor.switchItem,
                lyViewAllPlansSelectedSkuBackgroundColor.switchItem,
                lyViewAllPlansUnselectedSkuBackgroundColor.switchItem,

                )

        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        with(mBinding) {
            when (v) {
                btnCancel,
                layoutHeader.ivHeaderBack -> customOnBackPressed()

                btnUpdate -> {
                    VasuSubscriptionConfig.with(fActivity = mActivity, fAppVersionName = BuildConfig.VERSION_NAME)
//                        .enableTestPurchase(true)
                        .setAppLanguageCode(fCode = spLanguage.selectedItem.toString().substringAfter("(").substringBefore(")").takeIf { it.isNotEmpty() } ?: "en")
                        .setPrivacyPolicy(fLink = etPrivacyPolicy.text.toString().trim().takeIf { it.isNotEmpty() && switchPrivacyPolicy.isChecked } ?: "https://www.freeprivacypolicy.com/blog/privacy-policy-url/")
                        .setTermsOfUse(fLink = etTermsOfUse.text.toString().trim().takeIf { it.isNotEmpty() && switchTermsOfUse.isChecked } ?: "https://policies.google.com/privacy?hl=en-US")
                        .setNotificationData(fNotificationData = VasuSubscriptionConfig.NotificationData(intentClass = StartupActivity::class.java).apply {
                            this.setNotificationIcon(id = R.drawable.ic_share_blue)
//                            this.setNotificationId(id = 100)
//                            this.setNotificationChannelId(channelId = "channelId = ")
//                            this.setNotificationChannelName(channelName = "channelName = ")
                        })
                        .setTimeLineScreenData { fTimeLineScreenData ->
                            with(fTimeLineScreenData) {
//                                this.setInstantAccessHint(R.string.dialog_title, R.string.no, R.string.yes, R.string.app_name, R.string.banner_ads)
//                                this.setInstantAccessLottieFile(fLottieFile = R.raw.blast_gift)
                                this.setWithInstantAccessAnimation(isAnimated = switchTimeLineInstantAccessAnimation.isChecked)
                                this.setWithSliderAnimation(isAnimated = switchTimeLineSliderAnimation.isChecked)
                                this.mainColor(fColors = ColorStateList.valueOf(lyTimeLineMainColor.colorSlider.selectedColor).takeIf { lyTimeLineMainColor.switchItem.isChecked })
                                this.headerColor(fColors = ColorStateList.valueOf(lyTimeLineHeaderColor.colorSlider.selectedColor).takeIf { lyTimeLineHeaderColor.switchItem.isChecked })
                                this.closeIconColor(fColors = ColorStateList.valueOf(lyTimeLineCloseIconColor.colorSlider.selectedColor).takeIf { lyTimeLineCloseIconColor.switchItem.isChecked })
                                this.trackInactiveColor(fColors = ColorStateList.valueOf(lyTimeLineTrackInactiveColor.colorSlider.selectedColor).takeIf { lyTimeLineTrackInactiveColor.switchItem.isChecked })
                                this.hintTextColor(fColors = ColorStateList.valueOf(lyTimeLineHintTextColor.colorSlider.selectedColor).takeIf { lyTimeLineHintTextColor.switchItem.isChecked })
                                this.instantAccessHintTextColor(fColors = ColorStateList.valueOf(lyTimeLineInstantAccessHintTextColor.colorSlider.selectedColor).takeIf { lyTimeLineInstantAccessHintTextColor.switchItem.isChecked })
                                this.secureWithPlayStoreTextColor(fColors = ColorStateList.valueOf(lyTimeLineSecureWithPlayStoreTextColor.colorSlider.selectedColor).takeIf { lyTimeLineSecureWithPlayStoreTextColor.switchItem.isChecked })
                                this.secureWithPlayStoreBackgroundColor(fColors = ColorStateList.valueOf(lyTimeLineSecureWithPlayStoreBackgroundColor.colorSlider.selectedColor).takeIf { lyTimeLineSecureWithPlayStoreBackgroundColor.switchItem.isChecked })
                                this.buttonContinueTextColor(fColors = ColorStateList.valueOf(lyTimeLineButtonContinueTextColor.colorSlider.selectedColor).takeIf { lyTimeLineButtonContinueTextColor.switchItem.isChecked })
                            }
                        }
                        .setViewAllPlansScreenData { fViewAllPlansScreenData ->
                            with(fViewAllPlansScreenData) {
//                                this.setPurchaseButtonTextIndex(index = listOf(0, 1, 2).random())
//                                this.setBoxItems(BoxItem(),BoxItem(),BoxItem(),BoxItem(),BoxItem(),BoxItem())
//                                this.setRattingItems(RattingItem(),RattingItem(),RattingItem(),RattingItem(),RattingItem())
//                                this.setYearPlanIconSelector(fSelectorItem = SelectorDrawableItem())
//                                this.setLifTimePlanIconSelector(fSelectorItem = SelectorDrawableItem())
//                                this.setMonthPlanIconSelector(fSelectorItem = SelectorDrawableItem())
//                                this.setPlanSelector(fSelectorItem = SelectorDrawableItem())
//                                this.setPlanHeaderSelector(fSelectorItem = SelectorDrawableItem())
//                                this.setPlanBackgroundSelector(fSelectorItem = SelectorDrawableItem())
//                                this.setPlanHeaderTextColorSelector(fSelectorItem = SelectorColorItem())
//                                this.setPlanTitleTextColorSelector(fSelectorItem = SelectorColorItem())
//                                this.setPlanTrialPeriodTextColorSelector(fSelectorItem = SelectorColorItem())
//                                this.setPlanPriceTextColorSelector(fSelectorItem = SelectorColorItem())
                                this.headerColor(fColors = ColorStateList.valueOf(lyViewAllPlansHeaderColor.colorSlider.selectedColor).takeIf { lyViewAllPlansHeaderColor.switchItem.isChecked })
                                this.subHeaderColor(fColors = ColorStateList.valueOf(lyViewAllPlansSubHeaderColor.colorSlider.selectedColor).takeIf { lyViewAllPlansSubHeaderColor.switchItem.isChecked })
                                this.closeIconColor(fColors = ColorStateList.valueOf(lyViewAllPlansCloseIconColor.colorSlider.selectedColor).takeIf { lyViewAllPlansCloseIconColor.switchItem.isChecked })
                                this.ratingColor(fColors = ColorStateList.valueOf(lyViewAllPlansRatingColor.colorSlider.selectedColor).takeIf { lyViewAllPlansRatingColor.switchItem.isChecked })
                                this.ratingPlaceHolderColor(fColors = ColorStateList.valueOf(lyViewAllPlansRatingPlaceHolderColor.colorSlider.selectedColor).takeIf { lyViewAllPlansRatingPlaceHolderColor.switchItem.isChecked })
                                this.ratingIndicatorColor(fColors = ColorStateList.valueOf(lyViewAllPlansRatingIndicatorColor.colorSlider.selectedColor).takeIf { lyViewAllPlansRatingIndicatorColor.switchItem.isChecked })
                                this.unselectedItemDataColor(fColors = ColorStateList.valueOf(lyViewAllPlansUnselectedItemDataColor.colorSlider.selectedColor).takeIf { lyViewAllPlansUnselectedItemDataColor.switchItem.isChecked })
                                this.selectedItemDataColor(fColors = ColorStateList.valueOf(lyViewAllPlansSelectedItemDataColor.colorSlider.selectedColor).takeIf { lyViewAllPlansSelectedItemDataColor.switchItem.isChecked })
                                this.payNothingNowColor(fColors = ColorStateList.valueOf(lyViewAllPlansPayNothingNowColor.colorSlider.selectedColor).takeIf { lyViewAllPlansPayNothingNowColor.switchItem.isChecked })
                                this.secureWithPlayStoreTextColor(fColors = ColorStateList.valueOf(lyViewAllPlansSecureWithPlayStoreTextColor.colorSlider.selectedColor).takeIf { lyViewAllPlansSecureWithPlayStoreTextColor.switchItem.isChecked })
                                this.secureWithPlayStoreBackgroundColor(fColors = ColorStateList.valueOf(lyViewAllPlansSecureWithPlayStoreBackgroundColor.colorSlider.selectedColor).takeIf { lyViewAllPlansSecureWithPlayStoreBackgroundColor.switchItem.isChecked })
                                this.itemBoxBackgroundColor(fColors = ColorStateList.valueOf(lyViewAllPlansItemBoxBackgroundColor.colorSlider.selectedColor).takeIf { lyViewAllPlansItemBoxBackgroundColor.switchItem.isChecked })
                                this.selectedSkuBackgroundColor(fColors = ColorStateList.valueOf(lyViewAllPlansSelectedSkuBackgroundColor.colorSlider.selectedColor).takeIf { lyViewAllPlansSelectedSkuBackgroundColor.switchItem.isChecked })
                                this.unselectedSkuBackgroundColor(fColors = ColorStateList.valueOf(lyViewAllPlansUnselectedSkuBackgroundColor.colorSlider.selectedColor).takeIf { lyViewAllPlansUnselectedSkuBackgroundColor.switchItem.isChecked })
                            }
                        }
                        .setFourPlanScreenData { fFourPlanScreenData ->
                            with(fFourPlanScreenData) {
//                                this.setPurchaseButtonTextIndex(index = listOf(0, 1, 2).random())
//                                this.setLifeTimePlanDiscountPercentage(discountPercentage = 80)

                                this.setBoxItems(
                                    FourPlanUserItem(
                                        backgroundDrawable = com.example.app.ads.helper.R.drawable.bg_cloud_backup,
                                        itemIcon = com.example.app.ads.helper.R.drawable.aa_cloud_backup,
                                        itemName = com.example.app.ads.helper.R.string.rating_user,
                                    ),
                                    FourPlanUserItem(
                                        backgroundDrawable = com.example.app.ads.helper.R.drawable.bg_cloud_backup,
                                        itemIcon = com.example.app.ads.helper.R.drawable.aa_cloud_backup,
                                        itemName = com.example.app.ads.helper.R.string.rating_user,
                                    ),
                                    FourPlanUserItem(
                                        backgroundDrawable = com.example.app.ads.helper.R.drawable.bg_cloud_backup,
                                        itemIcon = com.example.app.ads.helper.R.drawable.aa_cloud_backup,
                                        itemName = com.example.app.ads.helper.R.string.rating_user,
                                    ),
                                    FourPlanUserItem(
                                        backgroundDrawable = com.example.app.ads.helper.R.drawable.bg_cloud_backup,
                                        itemIcon = com.example.app.ads.helper.R.drawable.aa_cloud_backup,
                                        itemName = com.example.app.ads.helper.R.string.rating_user,
                                    ),
                                    FourPlanUserItem(
                                        backgroundDrawable = com.example.app.ads.helper.R.drawable.bg_cloud_backup,
                                        itemIcon = com.example.app.ads.helper.R.drawable.aa_cloud_backup,
                                        itemName = com.example.app.ads.helper.R.string.rating_user,
                                    ),
                                    FourPlanUserItem(
                                        backgroundDrawable = com.example.app.ads.helper.R.drawable.bg_cloud_backup,
                                        itemIcon = com.example.app.ads.helper.R.drawable.aa_cloud_backup,
                                        itemName = com.example.app.ads.helper.R.string.rating_user,
                                    )
                                )

                                this.setRattingItems(
                                    FourPlanRattingItem(
                                        ratingCount = 4.5f,
                                        ratingHeader = com.example.app.ads.helper.R.string.rating_header,
                                        ratingSubHeader = com.example.app.ads.helper.R.string.rating_sub_header,
                                        itemType = FourPlanRattingItemType.APP_RATING,
                                    ),
                                    FourPlanRattingItem(
                                        satisfiedCustomerCount = 200000,
                                        satisfiedCustomerDrawable = com.example.app.ads.helper.R.drawable.aa_test_image_girl,
                                        itemType = FourPlanRattingItemType.SATISFIED_CUSTOMER,
                                    ),
                                    FourPlanRattingItem(
                                        reviewTitle = com.example.app.ads.helper.R.string.rating_header,
                                        reviewSubTitle = com.example.app.ads.helper.R.string.rating_sub_header,
                                        reviewGivenBy = com.example.app.ads.helper.R.string.rating_user,
                                        reviewGivenByTextGravity = Gravity.CENTER,
                                        itemType = FourPlanRattingItemType.REVIEW,
                                    ),
                                    FourPlanRattingItem(
                                        reviewTitle = com.example.app.ads.helper.R.string.rating_header,
                                        reviewSubTitle = com.example.app.ads.helper.R.string.rating_sub_header,
                                        reviewGivenBy = com.example.app.ads.helper.R.string.rating_user,
                                        reviewGivenByTextGravity = Gravity.START,
                                        itemType = FourPlanRattingItemType.REVIEW,
                                    ),
                                    FourPlanRattingItem(
                                        reviewTitle = com.example.app.ads.helper.R.string.rating_header,
                                        reviewSubTitle = com.example.app.ads.helper.R.string.rating_sub_header,
                                        reviewGivenBy = com.example.app.ads.helper.R.string.rating_user,
                                        reviewGivenByTextGravity = Gravity.END,
                                        itemType = FourPlanRattingItemType.REVIEW,
                                    )
                                )
                            }
                        }
                        .launchScreen(
                            fPlanScreenType = MorePlanScreenType.fromName(value = spScreenType.selectedItem.toString().takeIf { it.isNotEmpty() } ?: "six_box_screen"),
                            isFromSplash = switchIsFromSplash.isChecked,
//                            showCloseAdForTimeLineScreen = switchCloseAdForTimeLineScreen.isChecked,
//                            showCloseAdForViewAllPlanScreenOpenAfterSplash = switchCloseAdForViewAllPlansScreenAfterSplash.isChecked,
//                            showCloseAdForViewAllPlanScreen = switchCloseAdForViewAllPlansScreen.isChecked,
                            directShowMorePlanScreen = switchShowOnlyViewAllPlans.isChecked,
                            onSubscriptionEvent = { eventType ->
                                Log.e(TAG, "onClick: Admob_ Akshay_ eventType::-> $eventType")
                            },
                            onScreenFinish = { isUserPurchaseAnyPlan ->
                                Log.e(TAG, "onClick: Admob_ Akshay_ Screen Finished isUserPurchaseAnyPlan::-> $isUserPurchaseAnyPlan")
                            },
                            onOpeningError = {
                                mActivity.makeText("Error")
                                Log.e(TAG, "onClick: Admob_ Akshay_ Opening Error")
                            }
                        )
                }
            }
        }
    }

    override fun onCheckedChanged(v: CompoundButton, isChecked: Boolean) {
        with(mBinding) {
            when (v) {
                switchPrivacyPolicy -> etPrivacyPolicy.beVisibleIf(isChecked)
                switchTermsOfUse -> etTermsOfUse.beVisibleIf(isChecked)
                switchTimeLineScreenUi -> {
                    clTimeLineScreenUi.beVisibleIf(isChecked)

                    if (!isChecked) {
                        switchTimeLineInstantAccessAnimation.isChecked = false
                        switchTimeLineSliderAnimation.isChecked = false
                        lyTimeLineMainColor.switchItem.isChecked = false
                        lyTimeLineHeaderColor.switchItem.isChecked = false
                        lyTimeLineCloseIconColor.switchItem.isChecked = false
                        lyTimeLineTrackInactiveColor.switchItem.isChecked = false
                        lyTimeLineHintTextColor.switchItem.isChecked = false
                        lyTimeLineInstantAccessHintTextColor.switchItem.isChecked = false
                        lyTimeLineSecureWithPlayStoreTextColor.switchItem.isChecked = false
                        lyTimeLineSecureWithPlayStoreBackgroundColor.switchItem.isChecked = false
                        lyTimeLineButtonContinueTextColor.switchItem.isChecked = false
                    } else {
                    }
                }

                switchViewAllPlansScreenUi -> {
                    clViewAllPlansScreenUi.beVisibleIf(isChecked)

                    if (!isChecked) {
                        lyViewAllPlansHeaderColor.switchItem.isChecked = false
                        lyViewAllPlansSubHeaderColor.switchItem.isChecked = false
                        lyViewAllPlansCloseIconColor.switchItem.isChecked = false
                        lyViewAllPlansRatingColor.switchItem.isChecked = false
                        lyViewAllPlansRatingPlaceHolderColor.switchItem.isChecked = false
                        lyViewAllPlansRatingIndicatorColor.switchItem.isChecked = false
                        lyViewAllPlansUnselectedItemDataColor.switchItem.isChecked = false
                        lyViewAllPlansSelectedItemDataColor.switchItem.isChecked = false
                        lyViewAllPlansPayNothingNowColor.switchItem.isChecked = false
                        lyViewAllPlansSecureWithPlayStoreTextColor.switchItem.isChecked = false
                        lyViewAllPlansSecureWithPlayStoreBackgroundColor.switchItem.isChecked = false
                        lyViewAllPlansItemBoxBackgroundColor.switchItem.isChecked = false
                        lyViewAllPlansSelectedSkuBackgroundColor.switchItem.isChecked = false
                        lyViewAllPlansUnselectedSkuBackgroundColor.switchItem.isChecked = false
                    } else {
                    }
                }

                lyTimeLineMainColor.switchItem -> lyTimeLineMainColor.colorSlider.beVisibleIf(isChecked)
                lyTimeLineHeaderColor.switchItem -> lyTimeLineHeaderColor.colorSlider.beVisibleIf(isChecked)
                lyTimeLineCloseIconColor.switchItem -> lyTimeLineCloseIconColor.colorSlider.beVisibleIf(isChecked)
                lyTimeLineTrackInactiveColor.switchItem -> lyTimeLineTrackInactiveColor.colorSlider.beVisibleIf(isChecked)
                lyTimeLineHintTextColor.switchItem -> lyTimeLineHintTextColor.colorSlider.beVisibleIf(isChecked)
                lyTimeLineInstantAccessHintTextColor.switchItem -> lyTimeLineInstantAccessHintTextColor.colorSlider.beVisibleIf(isChecked)
                lyTimeLineSecureWithPlayStoreTextColor.switchItem -> lyTimeLineSecureWithPlayStoreTextColor.colorSlider.beVisibleIf(isChecked)
                lyTimeLineSecureWithPlayStoreBackgroundColor.switchItem -> lyTimeLineSecureWithPlayStoreBackgroundColor.colorSlider.beVisibleIf(isChecked)
                lyTimeLineButtonContinueTextColor.switchItem -> lyTimeLineButtonContinueTextColor.colorSlider.beVisibleIf(isChecked)

                lyViewAllPlansHeaderColor.switchItem -> lyViewAllPlansHeaderColor.colorSlider.beVisibleIf(isChecked)
                lyViewAllPlansSubHeaderColor.switchItem -> lyViewAllPlansSubHeaderColor.colorSlider.beVisibleIf(isChecked)
                lyViewAllPlansCloseIconColor.switchItem -> lyViewAllPlansCloseIconColor.colorSlider.beVisibleIf(isChecked)
                lyViewAllPlansRatingColor.switchItem -> lyViewAllPlansRatingColor.colorSlider.beVisibleIf(isChecked)
                lyViewAllPlansRatingPlaceHolderColor.switchItem -> lyViewAllPlansRatingPlaceHolderColor.colorSlider.beVisibleIf(isChecked)
                lyViewAllPlansRatingIndicatorColor.switchItem -> lyViewAllPlansRatingIndicatorColor.colorSlider.beVisibleIf(isChecked)
                lyViewAllPlansUnselectedItemDataColor.switchItem -> lyViewAllPlansUnselectedItemDataColor.colorSlider.beVisibleIf(isChecked)
                lyViewAllPlansSelectedItemDataColor.switchItem -> lyViewAllPlansSelectedItemDataColor.colorSlider.beVisibleIf(isChecked)
                lyViewAllPlansPayNothingNowColor.switchItem -> lyViewAllPlansPayNothingNowColor.colorSlider.beVisibleIf(isChecked)
                lyViewAllPlansSecureWithPlayStoreTextColor.switchItem -> lyViewAllPlansSecureWithPlayStoreTextColor.colorSlider.beVisibleIf(isChecked)
                lyViewAllPlansSecureWithPlayStoreBackgroundColor.switchItem -> lyViewAllPlansSecureWithPlayStoreBackgroundColor.colorSlider.beVisibleIf(isChecked)
                lyViewAllPlansItemBoxBackgroundColor.switchItem -> lyViewAllPlansItemBoxBackgroundColor.colorSlider.beVisibleIf(isChecked)
                lyViewAllPlansSelectedSkuBackgroundColor.switchItem -> lyViewAllPlansSelectedSkuBackgroundColor.colorSlider.beVisibleIf(isChecked)
                lyViewAllPlansUnselectedSkuBackgroundColor.switchItem -> lyViewAllPlansUnselectedSkuBackgroundColor.colorSlider.beVisibleIf(isChecked)
                else -> {}
            }
        }
    }
}