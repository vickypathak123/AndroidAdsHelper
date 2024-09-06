package com.example.app.ads.helper.purchase.sixbox.activity

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.FontRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.BaseActivity
import com.example.app.ads.helper.base.BaseBindingActivity
import com.example.app.ads.helper.base.utils.beVisibleIf
import com.example.app.ads.helper.base.utils.getColorRes
import com.example.app.ads.helper.base.utils.getDimensionRes
import com.example.app.ads.helper.base.utils.getDrawableRes
import com.example.app.ads.helper.base.utils.getFontRes
import com.example.app.ads.helper.base.utils.gone
import com.example.app.ads.helper.base.utils.isTiramisuPlus
import com.example.app.ads.helper.base.utils.sdpToPx
import com.example.app.ads.helper.base.utils.setTextSizeDimension
import com.example.app.ads.helper.base.utils.visible
import com.example.app.ads.helper.databinding.ActivityViewAllPlansBinding
import com.example.app.ads.helper.databinding.LayoutSubscribeItemBoxBinding
import com.example.app.ads.helper.databinding.LayoutSubscribeSkuItemBinding
import com.example.app.ads.helper.getLocalizedString
import com.example.app.ads.helper.isRTLDirectionFromLocale
import com.example.app.ads.helper.launcher.Launcher
import com.example.app.ads.helper.purchase.IS_ENABLE_TEST_PURCHASE
import com.example.app.ads.helper.purchase.IS_FROM_SPLASH
import com.example.app.ads.helper.purchase.SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN
import com.example.app.ads.helper.purchase.SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN_OPEN_AFTER_SPLASH
import com.example.app.ads.helper.purchase.SUBSCRIPTION_PRIVACY_POLICY
import com.example.app.ads.helper.purchase.SUBSCRIPTION_TERMS_OF_USE
import com.example.app.ads.helper.purchase.fireSubscriptionEvent
import com.example.app.ads.helper.purchase.product.PlanOfferType
import com.example.app.ads.helper.purchase.product.ProductInfo
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getBillingPeriodName
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getFullBillingPeriod
import com.example.app.ads.helper.purchase.sixbox.utils.BoxItem
import com.example.app.ads.helper.purchase.sixbox.utils.InfiniteRecyclerAdapter
import com.example.app.ads.helper.purchase.sixbox.utils.RattingItem
import com.example.app.ads.helper.purchase.sixbox.utils.ViewAllPlansScreenDataModel
import com.example.app.ads.helper.purchase.utils.AdTimer
import com.example.app.ads.helper.purchase.utils.SubscriptionEventType
import com.example.app.ads.helper.remoteconfig.mVasuSubscriptionConfigModel
import com.zhpan.indicator.enums.IndicatorOrientation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class ViewAllPlansActivity : BaseBindingActivity<ActivityViewAllPlansBinding>() {

    private var isLifeTimePrizeSated: Boolean = false
    private var isAnyPlanPrizeSated: Boolean = false

    private var mLifetimePlanProductInfo: ProductInfo? = null
    private var mYearlyPlanProductInfo: ProductInfo? = null
    private var mWeeklyPlanProductInfo: ProductInfo? = null
    private var mMonthlyPlanProductInfo: ProductInfo? = null

    /**
     * Index == 2 ::-> Try 3 days for $0
     * Index == 1 ::-> START MY FREE TRIAL
     * Index == else ::-> CONTINUE
     */
    private val mPurchaseButtonTextIndex: Int get() = mVasuSubscriptionConfigModel.purchaseButtonTextIndex

    private val listOfBoxItem: ArrayList<BoxItem>
        get() = screenDataModel?.listOfBoxItem.takeIf { !it.isNullOrEmpty() } ?: arrayListOf(
            BoxItem(
                backgroundColor = Color.parseColor("#F2F8FF"),
                foregroundColor = Color.parseColor("#1C74FF"),
                itemName = R.string.instant_access_hint_1,
                itemIcon = R.drawable.aa_item_1
            ),
            BoxItem(
                backgroundColor = Color.parseColor("#F3EDFF"),
                foregroundColor = Color.parseColor("#7523FF"),
                itemName = R.string.instant_access_hint_2,
                itemIcon = R.drawable.aa_item_2
            ),
            BoxItem(
                backgroundColor = Color.parseColor("#FFEDED"),
                foregroundColor = Color.parseColor("#FF4343"),
                itemName = R.string.instant_access_hint_3,
                itemIcon = R.drawable.aa_item_3
            ),
            BoxItem(
                backgroundColor = Color.parseColor("#FFEEF8"),
                foregroundColor = Color.parseColor("#FF4AB2"),
                itemName = R.string.instant_access_hint_4,
                itemIcon = R.drawable.aa_item_4
            ),
            BoxItem(
                backgroundColor = Color.parseColor("#FFF5EF"),
                foregroundColor = Color.parseColor("#FF7A28"),
                itemName = R.string.instant_access_hint_5,
                itemIcon = R.drawable.aa_item_5
            ),
            BoxItem(
                backgroundColor = Color.parseColor("#E2FFF0"),
                foregroundColor = Color.parseColor("#12CC6A"),
                itemName = R.string.instant_access_hint_6,
                itemIcon = R.drawable.aa_item_6
            ),
        )

    private val listOfRattingItem: ArrayList<RattingItem>
        get() = screenDataModel?.listOfRattingItem.takeIf { !it.isNullOrEmpty() } ?: arrayListOf(
            RattingItem(
                title = R.string.rating_header,
                subTitle = R.string.rating_sub_header,
                givenBy = R.string.rating_user,
                rattingStar = 0.5f,
                givenByTextGravity = Gravity.START
            ),
            RattingItem(
                title = R.string.rating_header,
                subTitle = R.string.rating_sub_header,
                givenBy = R.string.rating_user,
                rattingStar = 1.5f,
                givenByTextGravity = Gravity.END
            ),
            RattingItem(
                title = R.string.rating_header,
                subTitle = R.string.rating_sub_header,
                givenBy = R.string.rating_user,
                rattingStar = 2.5f,
                givenByTextGravity = Gravity.CENTER
            ),
            RattingItem(
                title = R.string.rating_header,
                subTitle = R.string.rating_sub_header,
                givenBy = R.string.rating_user,
                rattingStar = 3.5f,
                givenByTextGravity = Gravity.START
            ),
            RattingItem(
                title = R.string.rating_header,
                subTitle = R.string.rating_sub_header,
                givenBy = R.string.rating_user,
                rattingStar = 4.5f,
                givenByTextGravity = Gravity.END
            ),
            RattingItem(
                title = R.string.rating_header,
                subTitle = R.string.rating_sub_header,
                givenBy = R.string.rating_user,
                rattingStar = 5.5f,
                givenByTextGravity = Gravity.CENTER
            ),
        )

    private var mTimer: AdTimer? = null

    //    @get:ColorRes
    private val headerColor: ColorStateList get() = screenDataModel?.headerColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_view_more_plan_header_color))

    //    @get:ColorRes
    private val subHeaderColor: ColorStateList get() = screenDataModel?.subHeaderColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_view_more_plan_sub_header_color))

    //    @get:ColorRes
    private val closeIconColor: ColorStateList get() = screenDataModel?.closeIconColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_view_more_plan_close_icon_color))

    //    @get:ColorRes
    private val ratingColor: ColorStateList get() = screenDataModel?.ratingColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_view_more_plan_rating_color))

    //    @get:ColorRes
    private val ratingPlaceHolderColor: ColorStateList get() = screenDataModel?.ratingPlaceHolderColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_view_more_plan_rating_place_holder_color))

    //    @get:ColorRes
    private val ratingIndicatorColor: ColorStateList get() = screenDataModel?.ratingIndicatorColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_view_more_plan_rating_indicator_color))

    //    @get:ColorRes
    private val unselectedItemDataColor: ColorStateList get() = screenDataModel?.unselectedItemDataColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_view_more_plan_unselected_item_data_color))

    //    @get:ColorRes
    private val selectedItemDataColor: ColorStateList get() = screenDataModel?.selectedItemDataColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_view_more_plan_selected_item_data_color))

    //    @get:ColorRes
    private val payNothingNowColor: ColorStateList get() = screenDataModel?.payNothingNowColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_view_more_plan_pay_nothing_now_color))

    //    @get:ColorRes
    private val secureWithPlayStoreTextColor: ColorStateList get() = screenDataModel?.secureWithPlayStoreTextColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_view_more_plan_secure_with_play_store_text_color))

    //    @get:ColorRes
    private val secureWithPlayStoreBackgroundColor: ColorStateList get() = screenDataModel?.secureWithPlayStoreBackgroundColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_secure_with_play_store_background_color))

    //    @get:ColorRes
    private val itemBoxBackgroundColor: ColorStateList get() = screenDataModel?.itemBoxBackgroundColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_view_more_plan_item_box_background_color))

    //    @get:ColorRes
    private val selectedSkuBackgroundColor: ColorStateList get() = screenDataModel?.selectedSkuBackgroundColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_view_more_plan_selected_sku_background_color))

    //    @get:ColorRes
    private val unselectedSkuBackgroundColor: ColorStateList get() = screenDataModel?.unselectedSkuBackgroundColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_view_more_plan_unselected_sku_background_color))

    @FontRes
    private val planExtraBoldFontFamily: Int = R.font.plus_jakarta_sans_extra_bold

    @FontRes
    private val planBoldFontFamily: Int = R.font.plus_jakarta_sans_bold

    @FontRes
    private val planSemiBoldFontFamily: Int = R.font.plus_jakarta_sans_semi_bold

    @FontRes
    private val planMediumFontFamily: Int = R.font.plus_jakarta_sans_medium

    //<editor-fold desc="Plan Item Selectors">
    private val mYearPlanIconSelector: StateListDrawable
        get() {
            return StateListDrawable()
                .apply {
                    this.addState(
                        intArrayOf(android.R.attr.state_selected),
                        if (screenDataModel?.yearPlanIconSelector?.selectedDrawableRes != null && screenDataModel?.yearPlanIconSelector?.selectedDrawableRes != -1) {
                            mActivity.getDrawableRes(screenDataModel?.yearPlanIconSelector?.selectedDrawableRes!!)
                        } else if (screenDataModel?.yearPlanIconSelector?.selectedColorRes != null && screenDataModel?.yearPlanIconSelector?.selectedColorRes != -1) {
                            ColorDrawable(screenDataModel?.yearPlanIconSelector?.selectedColorRes!!)
                        } else {
                            mActivity.getDrawableRes(R.drawable.ic_yearly_selected)
                        }
                    )
                    this.addState(
                        intArrayOf(),
                        if (screenDataModel?.yearPlanIconSelector?.defaultDrawableRes != null && screenDataModel?.yearPlanIconSelector?.defaultDrawableRes != -1) {
                            mActivity.getDrawableRes(screenDataModel?.yearPlanIconSelector?.defaultDrawableRes!!)
                        } else if (screenDataModel?.yearPlanIconSelector?.defaultColorRes != null && screenDataModel?.yearPlanIconSelector?.defaultColorRes != -1) {
                            ColorDrawable(screenDataModel?.yearPlanIconSelector?.defaultColorRes!!)
                        } else {
                            mActivity.getDrawableRes(R.drawable.ic_yearly)
                        }
                    )
                }
        }

    private val mLifTimePlanIconSelector: StateListDrawable
        get() {
            return StateListDrawable()
                .apply {
                    this.addState(
                        intArrayOf(android.R.attr.state_selected),
                        if (screenDataModel?.lifTimePlanIconSelector?.selectedDrawableRes != null && screenDataModel?.lifTimePlanIconSelector?.selectedDrawableRes != -1) {
                            mActivity.getDrawableRes(screenDataModel?.lifTimePlanIconSelector?.selectedDrawableRes!!)
                        } else if (screenDataModel?.lifTimePlanIconSelector?.selectedColorRes != null && screenDataModel?.lifTimePlanIconSelector?.selectedColorRes != -1) {
                            ColorDrawable(screenDataModel?.lifTimePlanIconSelector?.selectedColorRes!!)
                        } else {
                            mActivity.getDrawableRes(R.drawable.ic_lifetime_selected)
                        }
                    )
                    this.addState(
                        intArrayOf(),
                        if (screenDataModel?.lifTimePlanIconSelector?.defaultDrawableRes != null && screenDataModel?.lifTimePlanIconSelector?.defaultDrawableRes != -1) {
                            mActivity.getDrawableRes(screenDataModel?.lifTimePlanIconSelector?.defaultDrawableRes!!)
                        } else if (screenDataModel?.lifTimePlanIconSelector?.defaultColorRes != null && screenDataModel?.lifTimePlanIconSelector?.defaultColorRes != -1) {
                            ColorDrawable(screenDataModel?.lifTimePlanIconSelector?.defaultColorRes!!)
                        } else {
                            mActivity.getDrawableRes(R.drawable.ic_lifetime)
                        }
                    )
                }
        }

    private val mMonthPlanIconSelector: StateListDrawable
        get() {
            return StateListDrawable()
                .apply {
                    this.addState(
                        intArrayOf(android.R.attr.state_selected),
                        if (screenDataModel?.monthPlanIconSelector?.selectedDrawableRes != null && screenDataModel?.monthPlanIconSelector?.selectedDrawableRes != -1) {
                            mActivity.getDrawableRes(screenDataModel?.monthPlanIconSelector?.selectedDrawableRes!!)
                        } else if (screenDataModel?.monthPlanIconSelector?.selectedColorRes != null && screenDataModel?.monthPlanIconSelector?.selectedColorRes != -1) {
                            ColorDrawable(screenDataModel?.monthPlanIconSelector?.selectedColorRes!!)
                        } else {
                            mActivity.getDrawableRes(R.drawable.ic_monthly_selected)
                        }
                    )
                    this.addState(
                        intArrayOf(),
                        if (screenDataModel?.monthPlanIconSelector?.defaultDrawableRes != null && screenDataModel?.monthPlanIconSelector?.defaultDrawableRes != -1) {
                            mActivity.getDrawableRes(screenDataModel?.monthPlanIconSelector?.defaultDrawableRes!!)
                        } else if (screenDataModel?.monthPlanIconSelector?.defaultColorRes != null && screenDataModel?.monthPlanIconSelector?.defaultColorRes != -1) {
                            ColorDrawable(screenDataModel?.monthPlanIconSelector?.defaultColorRes!!)
                        } else {
                            mActivity.getDrawableRes(R.drawable.ic_monthly)
                        }
                    )
                }
        }

    private val mPlanSelector: StateListDrawable
        get() {
            return StateListDrawable()
                .apply {
                    this.addState(
                        intArrayOf(android.R.attr.state_selected),
                        if (screenDataModel?.planSelector?.selectedDrawableRes != null && screenDataModel?.planSelector?.selectedDrawableRes != -1) {
                            mActivity.getDrawableRes(screenDataModel?.planSelector?.selectedDrawableRes!!)
                        } else if (screenDataModel?.planSelector?.selectedColorRes != null && screenDataModel?.planSelector?.selectedColorRes != -1) {
                            ColorDrawable(screenDataModel?.planSelector?.selectedColorRes!!)
                        } else {
                            mActivity.getDrawableRes(R.drawable.bg_subscribe_selected_sku)
                        }
                    )
                    this.addState(
                        intArrayOf(),
                        if (screenDataModel?.planSelector?.defaultDrawableRes != null && screenDataModel?.planSelector?.defaultDrawableRes != -1) {
                            mActivity.getDrawableRes(screenDataModel?.planSelector?.defaultDrawableRes!!)
                        } else if (screenDataModel?.planSelector?.defaultColorRes != null && screenDataModel?.planSelector?.defaultColorRes != -1) {
                            ColorDrawable(screenDataModel?.planSelector?.defaultColorRes!!)
                        } else {
                            ColorDrawable(ratingPlaceHolderColor.defaultColor)
                        }
                    )
                }
        }

    private val mPlanHeaderSelector: StateListDrawable
        get() {
            return StateListDrawable()
                .apply {
                    this.addState(
                        intArrayOf(android.R.attr.state_selected),
                        if (screenDataModel?.planHeaderSelector?.selectedDrawableRes != null && screenDataModel?.planHeaderSelector?.selectedDrawableRes != -1) {
                            mActivity.getDrawableRes(screenDataModel?.planHeaderSelector?.selectedDrawableRes!!)
                        } else if (screenDataModel?.planHeaderSelector?.selectedColorRes != null && screenDataModel?.planHeaderSelector?.selectedColorRes != -1) {
                            ColorDrawable(screenDataModel?.planHeaderSelector?.selectedColorRes!!)
                        } else {
                            ColorDrawable(Color.TRANSPARENT)
                        }
                    )
                    this.addState(
                        intArrayOf(),
                        if (screenDataModel?.planHeaderSelector?.defaultDrawableRes != null && screenDataModel?.planHeaderSelector?.defaultDrawableRes != -1) {
                            mActivity.getDrawableRes(screenDataModel?.planHeaderSelector?.defaultDrawableRes!!)
                        } else if (screenDataModel?.planHeaderSelector?.defaultColorRes != null && screenDataModel?.planHeaderSelector?.defaultColorRes != -1) {
                            ColorDrawable(screenDataModel?.planHeaderSelector?.defaultColorRes!!)
                        } else {
                            ColorDrawable(unselectedSkuBackgroundColor.defaultColor)
                        }
                    )
                }
        }

    private val mPlanBackgroundSelector: StateListDrawable
        get() {
            return StateListDrawable()
                .apply {
                    this.addState(
                        intArrayOf(android.R.attr.state_selected),
                        if (screenDataModel?.planBackgroundSelector?.selectedDrawableRes != null && screenDataModel?.planBackgroundSelector?.selectedDrawableRes != -1) {
                            mActivity.getDrawableRes(screenDataModel?.planBackgroundSelector?.selectedDrawableRes!!)
                        } else if (screenDataModel?.planBackgroundSelector?.selectedColorRes != null && screenDataModel?.planBackgroundSelector?.selectedColorRes != -1) {
                            ColorDrawable(screenDataModel?.planBackgroundSelector?.selectedColorRes!!)
                        } else {
                            ColorDrawable(selectedSkuBackgroundColor.defaultColor)
                        }
                    )
                    this.addState(
                        intArrayOf(),
                        if (screenDataModel?.planBackgroundSelector?.defaultDrawableRes != null && screenDataModel?.planBackgroundSelector?.defaultDrawableRes != -1) {
                            mActivity.getDrawableRes(screenDataModel?.planBackgroundSelector?.defaultDrawableRes!!)
                        } else if (screenDataModel?.planBackgroundSelector?.defaultColorRes != null && screenDataModel?.planBackgroundSelector?.defaultColorRes != -1) {
                            ColorDrawable(screenDataModel?.planBackgroundSelector?.defaultColorRes!!)
                        } else {
                            ColorDrawable(unselectedSkuBackgroundColor.defaultColor)
                        }
                    )
                }
        }

    private val mPlanHeaderTextColorSelector: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf()
                ),
                intArrayOf(
                    mActivity.getColorRes(screenDataModel?.planHeaderTextColorSelector?.selectedColorRes?.takeIf { it != -1 } ?: android.R.color.white),
                    screenDataModel?.planHeaderTextColorSelector?.defaultColorRes?.takeIf { it != -1 }?.let {
                        mActivity.getColorRes(it)
                    } ?: unselectedItemDataColor.defaultColor
                )
            )
        }

    private val mPlanTitleTextColorSelector: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf()
                ),
                intArrayOf(
                    screenDataModel?.planTitleTextColorSelector?.selectedColorRes?.takeIf { it != -1 }?.let {
                        mActivity.getColorRes(it)
                    } ?: subHeaderColor.defaultColor,
                    screenDataModel?.planTitleTextColorSelector?.defaultColorRes?.takeIf { it != -1 }?.let {
                        mActivity.getColorRes(it)
                    } ?: unselectedItemDataColor.defaultColor
                )
            )
        }

    private val mPlanTrialPeriodTextColorSelector: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf()
                ),
                intArrayOf(
                    screenDataModel?.planTrialPeriodTextColorSelector?.selectedColorRes?.takeIf { it != -1 }?.let {
                        mActivity.getColorRes(it)
                    } ?: selectedItemDataColor.defaultColor,
                    screenDataModel?.planTrialPeriodTextColorSelector?.defaultColorRes?.takeIf { it != -1 }?.let {
                        mActivity.getColorRes(it)
                    } ?: unselectedItemDataColor.defaultColor
                )
            )
        }

    private val mPlanPriceTextColorSelector: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf()
                ),
                intArrayOf(
                    screenDataModel?.planPriceTextColorSelector?.selectedColorRes?.takeIf { it != -1 }?.let {
                        mActivity.getColorRes(it)
                    } ?: subHeaderColor.defaultColor,
                    screenDataModel?.planPriceTextColorSelector?.defaultColorRes?.takeIf { it != -1 }?.let {
                        mActivity.getColorRes(it)
                    } ?: subHeaderColor.defaultColor
                )
            )
        }
    //</editor-fold>

    private val isFromTimeLine: Boolean get() = intent?.getBooleanExtra("isFromTimeLine", false) ?: false

    companion object {
        private var screenDataModel: ViewAllPlansScreenDataModel? = null

        private var onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit = {}

//        var animationDuration: Float = 500.0f
//        var animationName: String = "DecelerateInterpolator"


        internal fun launchScreen(
            fActivity: Activity,
            isFromTimeLine: Boolean,
            screenDataModel: ViewAllPlansScreenDataModel,
            onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit,
        ) {
            Companion.screenDataModel = screenDataModel
            Companion.onScreenFinish = onScreenFinish

            val lIntent = Intent(fActivity, ViewAllPlansActivity::class.java)
            lIntent.putExtra("isFromTimeLine", isFromTimeLine)

            @AnimatorRes @AnimRes val lEnterAnimId: Int = android.R.anim.fade_in
            @AnimatorRes @AnimRes val lExitAnimId: Int = android.R.anim.fade_out

            fActivity.runOnUiThread {
                if (isTiramisuPlus) {
                    val options = ActivityOptions.makeCustomAnimation(fActivity, lEnterAnimId, lExitAnimId)
                    fActivity.startActivity(lIntent, options.toBundle())
                } else {
                    fActivity.startActivity(lIntent)
                    @Suppress("DEPRECATION")
                    fActivity.overridePendingTransition(lEnterAnimId, lExitAnimId)
                }
            }
        }
    }

    override fun setBinding(): ActivityViewAllPlansBinding = ActivityViewAllPlansBinding.inflate(layoutInflater)

    override fun getActivityContext(): BaseActivity = this@ViewAllPlansActivity

    override fun setParamBeforeLayoutInit() {
        super.setParamBeforeLayoutInit()
        setEdgeToEdgeLayout()
    }

    private var isUserPurchaseAnyPlan: Boolean = false

    override fun onResume() {
        super.onResume()
        if (isOnPause) {
            isOnPause = false
            setBillingListener(fWhere = "onResume")
        }
    }

    private fun setBillingListener(fWhere: String): Job {
        val job: Job = CoroutineScope(Dispatchers.IO).launch {
            Log.e(TAG, "$fWhere: Set Listener")
            ProductPurchaseHelper.setPurchaseListener(object : ProductPurchaseHelper.ProductPurchaseListener {
                override fun onBillingSetupFinished() {
                    if (!isAnyPlanPrizeSated || !isLifeTimePrizeSated) {
                        ProductPurchaseHelper.initProductsKeys(fContext = mActivity) {
                            if (!isAnyPlanPrizeSated || !isLifeTimePrizeSated) {
                                setProductData()
                            }
                        }
                    }
                }

                override fun onPurchasedSuccess() {
                    super.onPurchasedSuccess()
                    Log.e(TAG, "onPurchasedSuccess: Akshay_")
                    CoroutineScope(Dispatchers.Main).launch {
                        mActivity.runOnUiThread {
                            when {
                                mBinding.lyYearlyPlan.root.isSelected -> {
                                    mYearlyPlanProductInfo?.let { productInfo ->
                                        fireSubscriptionEvent(fEventType = SubscriptionEventType.YEARLY_FREE_TRAIL_SUBSCRIBE.takeIf { productInfo.planOfferType == PlanOfferType.FREE_TRIAL } ?: SubscriptionEventType.YEARLY_SUBSCRIBE)
                                    }
                                }

                                mBinding.lyLifetimePlan.root.isSelected -> {
                                    mLifetimePlanProductInfo?.let { productInfo ->
                                        fireSubscriptionEvent(fEventType = SubscriptionEventType.LIFE_TIME_FREE_TRAIL_PURCHASE.takeIf { productInfo.planOfferType == PlanOfferType.FREE_TRIAL } ?: SubscriptionEventType.LIFE_TIME_PURCHASE)
                                    }
                                }

                                mBinding.lyMonthlyPlan.root.isSelected -> {
                                    mMonthlyPlanProductInfo?.let { productInfo ->
                                        fireSubscriptionEvent(fEventType = SubscriptionEventType.MONTHLY_FREE_TRAIL_SUBSCRIBE.takeIf { productInfo.planOfferType == PlanOfferType.FREE_TRIAL } ?: SubscriptionEventType.MONTHLY_SUBSCRIBE)
                                    } ?: mWeeklyPlanProductInfo?.let { productInfo ->
                                        fireSubscriptionEvent(fEventType = SubscriptionEventType.WEEKLY_FREE_TRAIL_SUBSCRIBE.takeIf { productInfo.planOfferType == PlanOfferType.FREE_TRIAL } ?: SubscriptionEventType.WEEKLY_SUBSCRIBE)
                                    }
                                }

                                else -> {}
                            }



                            isUserPurchaseAnyPlan = true
                            mBinding.ivClose.performClick()
                        }
                    }
                }
            })
        }
        return job
    }

    override fun initView() {
        super.initView()
        getEdgeToEdgeMargin()

        fireSubscriptionEvent(fEventType = SubscriptionEventType.VIEW_ALL_PLANS_SCREEN_OPEN)

        val job: Job = setBillingListener(fWhere = "initView")
        runBlocking {
            job.join()
            CoroutineScope(Dispatchers.IO).launch {
                Log.e(TAG, "initView: InitBilling")
                ProductPurchaseHelper.initBillingClient(fContext = mActivity)
            }
        }

        with(mBinding) {
            root.layoutDirection = View.LAYOUT_DIRECTION_RTL.takeIf { isRTLDirectionFromLocale } ?: View.LAYOUT_DIRECTION_LTR
            root.textDirection = View.LAYOUT_DIRECTION_RTL.takeIf { isRTLDirectionFromLocale } ?: View.LAYOUT_DIRECTION_LTR

            lySubscribeButton.lottieBtnContinue.scaleX = (-1f).takeIf { isRTLDirectionFromLocale } ?: 1f

            setScreenUI()

            setBoxItem(lyBoxItem = lyItem1, boxItem = listOfBoxItem[0].apply {
                backgroundColor = backgroundColor.takeIf { it != 0 } ?: Color.parseColor("#F2F8FF")
                foregroundColor = foregroundColor.takeIf { it != 0 } ?: Color.parseColor("#1C74FF")
            })
            setBoxItem(lyBoxItem = lyItem2, boxItem = listOfBoxItem[1].apply {
                backgroundColor = backgroundColor.takeIf { it != 0 } ?: Color.parseColor("#F3EDFF")
                foregroundColor = foregroundColor.takeIf { it != 0 } ?: Color.parseColor("#7523FF")
            })
            setBoxItem(lyBoxItem = lyItem3, boxItem = listOfBoxItem[2].apply {
                backgroundColor = backgroundColor.takeIf { it != 0 } ?: Color.parseColor("#FFEDED")
                foregroundColor = foregroundColor.takeIf { it != 0 } ?: Color.parseColor("#FF4343")
            })
            setBoxItem(lyBoxItem = lyItem4, boxItem = listOfBoxItem[3].apply {
                backgroundColor = backgroundColor.takeIf { it != 0 } ?: Color.parseColor("#FFEEF8")
                foregroundColor = foregroundColor.takeIf { it != 0 } ?: Color.parseColor("#FF4AB2")
            })
            setBoxItem(lyBoxItem = lyItem5, boxItem = listOfBoxItem[4].apply {
                backgroundColor = backgroundColor.takeIf { it != 0 } ?: Color.parseColor("#FFF5EF")
                foregroundColor = foregroundColor.takeIf { it != 0 } ?: Color.parseColor("#FF7A28")
            })
            setBoxItem(lyBoxItem = lyItem6, boxItem = listOfBoxItem[5].apply {
                backgroundColor = backgroundColor.takeIf { it != 0 } ?: Color.parseColor("#E2FFF0")
                foregroundColor = foregroundColor.takeIf { it != 0 } ?: Color.parseColor("#12CC6A")
            })

            setRatingViewPager()

            lyYearlyPlan.apply {
                ivPlanIcon.setImageDrawable(mYearPlanIconSelector)
                setPlanUI(fBinding = this)
            }
            lyLifetimePlan.apply {
                ivPlanIcon.setImageDrawable(mLifTimePlanIconSelector)
                setPlanUI(fBinding = this)
            }
            lyMonthlyPlan.apply {
                ivPlanIcon.setImageDrawable(mMonthPlanIconSelector)
                setPlanUI(fBinding = this)
            }
        }
    }

    override fun initViewListener() {
        super.initViewListener()
        with(mBinding) {
            setClickListener(
                ivClose,
                txtPayNothingNow,
                lyYearlyPlan.root,
                lyLifetimePlan.root,
                lyMonthlyPlan.root,
                txtTermsOfUse,
                txtPrivacyPolicy,
                lySubscribeButton.root,
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            setProductData()
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        with(mBinding) {
            when (v) {
                txtPayNothingNow,
                ivClose -> customOnBackPressed()

                lyYearlyPlan.root,
                lyLifetimePlan.root,
                lyMonthlyPlan.root -> {
                    updateSelectedPlanUI(fBinding = lyYearlyPlan, isPlanSelected = (v == lyYearlyPlan.root))
                    updateSelectedPlanUI(fBinding = lyLifetimePlan, isPlanSelected = (v == lyLifetimePlan.root))
                    updateSelectedPlanUI(fBinding = lyMonthlyPlan, isPlanSelected = (v == lyMonthlyPlan.root))

                    val lProductInfo: ProductInfo? = when {
                        lyYearlyPlan.root.isSelected -> mYearlyPlanProductInfo
                        lyLifetimePlan.root.isSelected -> mLifetimePlanProductInfo
                        lyMonthlyPlan.root.isSelected -> mMonthlyPlanProductInfo ?: mWeeklyPlanProductInfo
                        else -> null
                    }

                    Log.e(TAG, "onClick: ${lProductInfo?.id}")

                    lProductInfo?.let { productInfo ->
                        when (productInfo.planOfferType) {
                            PlanOfferType.FREE_TRIAL -> {
                                txtFreeThenPerPeriod.apply {
                                    this.text = getLocalizedString<String>(
                                        context = mActivity,
                                        resourceId = R.string.free_then_per_period,
                                        formatArgs = arrayOf(
                                            productInfo.actualFreeTrialPeriod.getFullBillingPeriod(context = mActivity),
                                            productInfo.formattedPrice,
                                            productInfo.actualBillingPeriod.getBillingPeriodName(context = mActivity)
                                        )
                                    ).lowercase()
                                    this.visible
                                }
                                txtStartWithAFreeTrial.apply {
                                    this.text = getLocalizedString<String>(
                                        context = mActivity,
                                        resourceId = R.string.payment_is_charged_after_period_cancel_anytime,
                                        formatArgs = arrayOf(
                                            productInfo.actualFreeTrialPeriod.getFullBillingPeriod(context = mActivity).lowercase()
                                        )
                                    )
                                    this.visible
                                }

                                setDefaultButtonText(
                                    buttonTextIndex = mPurchaseButtonTextIndex,
                                    period = productInfo.actualFreeTrialPeriod.getFullBillingPeriod(context = mActivity),
                                    price = "${productInfo.priceCurrencySymbol}0",
                                    isNeedToUpdateTopHeader = false
                                )
                            }

                            PlanOfferType.INTRO_OFFER -> {
                                txtFreeThenPerPeriod.apply {
                                    this.text = getLocalizedString<String>(
                                        context = mActivity,
                                        resourceId = R.string.price_upto_period_then_price_slash_period,
                                        formatArgs = arrayOf(
                                            productInfo.offerFormattedPrice,
                                            productInfo.offerActualBillingPeriod.getFullBillingPeriod(context = mActivity),
                                            getLocalizedString<String>(
                                                context = mActivity,
                                                resourceId = R.string.price_slash_period,
                                                formatArgs = arrayOf(
                                                    productInfo.formattedPrice,
                                                    productInfo.offerActualBillingPeriod.getBillingPeriodName(context = mActivity)
                                                )
                                            )
                                        )
                                    ).lowercase()
                                    this.visible
                                }

                                setDefaultButtonText(buttonTextIndex = 0, period = "", price = "")
                            }

                            else -> {
                                txtFreeThenPerPeriod.apply {
                                    this.text = getLocalizedString<String>(
                                        context = mActivity,
                                        resourceId = R.string.price_for_period_use,
                                        formatArgs = arrayOf(
                                            productInfo.formattedPrice,
                                            getLocalizedString<String>(
                                                context = mActivity,
                                                resourceId = R.string.lifetime.takeIf {
                                                    lyLifetimePlan.root.isSelected
                                                } ?: R.string.yearly.takeIf {
                                                    lyYearlyPlan.root.isSelected
                                                } ?: R.string.monthly.takeIf {
                                                    lyMonthlyPlan.root.isSelected && mMonthlyPlanProductInfo != null
                                                } ?: R.string.weekly
                                            )
                                        )
                                    ).lowercase()
                                    this.visible
                                }

                                setDefaultButtonText(buttonTextIndex = 0, period = "", price = "")
                            }
                        }
                    } ?: kotlin.run {
                        setDefaultButtonText(buttonTextIndex = 0, period = "", price = "")
                    }
                }

                txtTermsOfUse -> {
                    if (SUBSCRIPTION_TERMS_OF_USE.isNotEmpty()) {
                        Launcher.openAnyLink(
                            context = mActivity,
                            uri = SUBSCRIPTION_TERMS_OF_USE,
                            toolbarColor = headerColor.defaultColor,
                            isNightMode = false
                        )
                    } else {
                    }
                }

                txtPrivacyPolicy -> {
                    if (SUBSCRIPTION_PRIVACY_POLICY.isNotEmpty()) {
                        Launcher.openPrivacyPolicy(
                            context = mActivity,
                            fLink = SUBSCRIPTION_PRIVACY_POLICY,
                            toolbarColor = headerColor.defaultColor,
                            isNightMode = false
                        )
                    } else {
                    }
                }

                lySubscribeButton.root -> {
                    val selectedSKU: String = when {
                        lyYearlyPlan.root.isSelected -> mYearlyPlanProductInfo?.id ?: ""
                        lyLifetimePlan.root.isSelected -> mLifetimePlanProductInfo?.id ?: ""
                        lyMonthlyPlan.root.isSelected -> mMonthlyPlanProductInfo?.id ?: mWeeklyPlanProductInfo?.id ?: ""
                        else -> ""
                    }

                    if (selectedSKU.isNotEmpty()) {
                        if (IS_ENABLE_TEST_PURCHASE) {
                            ProductPurchaseHelper.fireTestingPurchase(context = mActivity)
                        } else {
                            ProductPurchaseHelper.purchase(activity = mActivity, productId = selectedSKU)
                        }
                    } else {
                    }
                }

                else -> {}
            }
        }
    }

    private fun setDefaultButtonText(buttonTextIndex: Int, period: String, price: String, isNeedToUpdateTopHeader: Boolean = true) {
        with(mBinding) {
            txtPayNothingNow.beVisibleIf(!isNeedToUpdateTopHeader)
            lySubscribeButton.txtBtnContinue.apply {
                this.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.try_period_for_price.takeIf {
                        buttonTextIndex == 2
                    } ?: R.string.start_my_free_trial.takeIf {
                        buttonTextIndex == 1
                    } ?: R.string.continue_,
                    formatArgs = arrayOf(
                        period,
                        price
                    )
                )
                this.isAllCaps = (buttonTextIndex != 2)

                this.setEdgeToEdgeTopPadding(fTopPadding = mActivity.getDimensionRes(com.intuit.ssp.R.dimen._11ssp).toInt(), isAddDefaultPadding = false)
                this.setEdgeToEdgeBottomPadding(fBottomPadding = mActivity.getDimensionRes(com.intuit.ssp.R.dimen._9ssp).toInt(), isAddDefaultPadding = false)
            }

            if (isNeedToUpdateTopHeader) {
                txtStartWithAFreeTrial.apply {
                    this.text = getLocalizedString<String>(
                        context = mActivity,
                        resourceId = R.string.lifetime_purchase.takeIf { lyLifetimePlan.root.isSelected } ?: R.string.subscription_will_auto_renew_cancel_anytime,
                    )
                    this.visible
                }
            }
        }
    }

    private fun setScreenUI() {
        with(mBinding) {
            txtUnlockAllFeatures.apply {
                this.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.unlock_all_features,
                )
                this.setTextColor(headerColor)
            }
            txtStartWithAFreeTrial.apply {
                this.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.payment_is_charged_after_period_cancel_anytime,
                )
                this.setTextColor(subHeaderColor)
            }
            txtFreeThenPerPeriod.apply {
                this.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.free_then_per_period,
                ).lowercase()
                this.setTextColor(closeIconColor)
            }
            txtPayNothingNow.apply {
                this.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.pay_nothing_now,
                )
                this.setTextColor(payNothingNowColor)
            }
            lySecureWithPlayStore.ivSecureWithPlayStore.apply {
                this.setColorFilter(secureWithPlayStoreTextColor.defaultColor, android.graphics.PorterDuff.Mode.SRC_IN)
            }
            lySecureWithPlayStore.txtSecureWithPlayStore.apply {
                this.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.cancel_anytime_secure_with_play_store,
                )
                this.setTextColor(secureWithPlayStoreTextColor)
                this.isSelected = true
            }
            lySecureWithPlayStore.ivSecureWithPlayStoreBg.apply {
                this.setBackgroundColor(secureWithPlayStoreBackgroundColor.defaultColor)
            }
            txtTermsOfUse.apply {
                this.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.terms_of_use,
                )
                this.setTextColor(closeIconColor)
            }
            txtPrivacyPolicy.apply {
                this.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.privacy_policy,
                )
                this.setTextColor(closeIconColor)
            }
            ivClose.apply {
                this.setColorFilter(closeIconColor.defaultColor, android.graphics.PorterDuff.Mode.SRC_IN)
            }
        }
    }

    private fun setBoxItem(lyBoxItem: LayoutSubscribeItemBoxBinding, boxItem: BoxItem) {
        with(lyBoxItem) {
            with(boxItem) {
                root.setCardBackgroundColor(itemBoxBackgroundColor)
                backgroundColor.let { color ->
                    ivBackground.setBackgroundColor(color)
                    ivBackground.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
                }
                foregroundColor.let { color ->
                    ivBackgroundBottom.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
                }
                ivIcon.setImageDrawable(mActivity.getDrawableRes(itemIcon))
                txtName.apply {
                    this.text = getLocalizedString<String>(
                        context = mActivity,
                        resourceId = itemName,
                    )
                    this.setTextColor(subHeaderColor)
                }
            }
        }
    }

    private fun setRatingViewPager() {
        val listSize: Int = (listOfRattingItem.size + 2)
        with(mBinding) {

            InfiniteRecyclerAdapter(
                originalList = listOfRattingItem,
                ratingHeaderColor = subHeaderColor,
                ratingSubHeaderColor = closeIconColor,
                ratingProgressTintColor = ratingColor,
                ratingProgressBackgroundTintColor = ratingPlaceHolderColor

            ).also { infiniteViewPager.adapter = it }

            infiniteViewPager.apply {
                this.setCurrentItem(1, false)
                this.offscreenPageLimit = listSize // For Make Parent Height of Max Height of Child View

                this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrollStateChanged(state: Int) {
                        super.onPageScrollStateChanged(state)
                        if (state == ViewPager2.SCROLL_STATE_IDLE) {
                            when (this@apply.currentItem) {
                                (listSize - 1) -> this@apply.setCurrentItem(1, false)
                                0 -> this@apply.setCurrentItem((listOfRattingItem.size), false)
                            }
                        }
                    }

                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                        val dotPosition = (listOfRattingItem.size - 1).takeIf { position < 1 } ?: (0).takeIf { position > listOfRattingItem.size } ?: (position - 1)
                        dotsIndicator.onPageScrolled(dotPosition, positionOffset, positionOffsetPixels)
                    }
                })
            }

            dotsIndicator.apply {
                setPageSize(listOfRattingItem.size)
                setCheckedColor(ratingIndicatorColor.defaultColor)
                setNormalColor(ratingPlaceHolderColor.defaultColor)
                setOrientation(IndicatorOrientation.INDICATOR_RTL.takeIf { isRTLDirectionFromLocale } ?: IndicatorOrientation.INDICATOR_HORIZONTAL)
                notifyDataChanged()
            }

            startAutoSwipeViewPagerTimer()
        }
    }

    private fun startAutoSwipeViewPagerTimer() {
        mTimer?.cancelTimer()
        mTimer = null

        AdTimer(
            millisInFuture = 2000,
            countDownInterval = 1000,
            onTick = {
            },
            onFinish = {
                CoroutineScope(Dispatchers.Main).launch {
                    mActivity.runOnUiThread {
                        mBinding.infiniteViewPager.post {
                            mBinding.infiniteViewPager.setCurrentItem(mBinding.infiniteViewPager.currentItem + 1, true)
                            startAutoSwipeViewPagerTimer()
                        }
                    }
                }
            }
        ).also { mTimer = it }

        mTimer?.start()
    }

    private fun setPlanUI(fBinding: LayoutSubscribeSkuItemBinding) {
        with(fBinding) {
            mBinding.lySubscribeButton.ivBackground.background = mPlanSelector
            mBinding.lySubscribeButton.ivBackground.isSelected = true

            ivGradiantBackground.background = mPlanSelector
            ivTopPricePercentageBackground.background = mPlanHeaderSelector
            ivTopBackground.background = mPlanBackgroundSelector
            ivBottomPriceBackground.background = mPlanBackgroundSelector
            txtPlanPricePercentage.setTextColor(mPlanHeaderTextColorSelector)
            txtPlanTitle.setTextColor(mPlanTitleTextColorSelector)
            txtPlanTrialPeriod.setTextColor(mPlanTrialPeriodTextColorSelector)
            txtPlanReferencePrice.setTextColor(mPlanTrialPeriodTextColorSelector)
            txtPlanPrice.setTextColor(mPlanPriceTextColorSelector)
        }
    }

    private fun updateSelectedPlanUI(fBinding: LayoutSubscribeSkuItemBinding, isPlanSelected: Boolean = false) {
        mActivity.runOnUiThread {
            with(fBinding) {
                txtPlanPricePercentage.apply {
                    this.typeface = mActivity.getFontRes(planExtraBoldFontFamily.takeIf { isPlanSelected } ?: planExtraBoldFontFamily)
                    this.setTextSizeDimension(com.intuit.ssp.R.dimen._10ssp.takeIf { isPlanSelected } ?: com.intuit.ssp.R.dimen._9ssp)
                }

                txtPlanTitle.apply {
                    this.typeface = mActivity.getFontRes(planExtraBoldFontFamily.takeIf { isPlanSelected } ?: planBoldFontFamily)
                    this.setTextSizeDimension(com.intuit.ssp.R.dimen._13ssp.takeIf { isPlanSelected } ?: com.intuit.ssp.R.dimen._12ssp)
                }

                txtPlanTrialPeriod.apply {
                    this.typeface = mActivity.getFontRes(planSemiBoldFontFamily.takeIf { isPlanSelected } ?: planMediumFontFamily)
                    this.setTextSizeDimension(com.intuit.ssp.R.dimen._11ssp.takeIf { isPlanSelected } ?: com.intuit.ssp.R.dimen._11ssp)
                }

                txtPlanReferencePrice.apply {
                    this.typeface = mActivity.getFontRes(planSemiBoldFontFamily.takeIf { isPlanSelected } ?: planSemiBoldFontFamily)
                    this.setTextSizeDimension(com.intuit.ssp.R.dimen._9ssp.takeIf { isPlanSelected } ?: com.intuit.ssp.R.dimen._8ssp)
                }

                txtPlanPrice.apply {
                    this.typeface = mActivity.getFontRes(planExtraBoldFontFamily.takeIf { isPlanSelected } ?: planSemiBoldFontFamily)
                    this.setTextSizeDimension(com.intuit.ssp.R.dimen._12ssp.takeIf { isPlanSelected } ?: com.intuit.ssp.R.dimen._11ssp)
                    this.setEdgeToEdgeTopPadding(fTopPadding = mActivity.getDimensionRes(com.intuit.ssp.R.dimen._8ssp.takeIf { isPlanSelected } ?: com.intuit.ssp.R.dimen._4ssp).toInt(), isAddDefaultPadding = false)
                    this.setEdgeToEdgeBottomPadding(fBottomPadding = mActivity.getDimensionRes(com.intuit.ssp.R.dimen._9ssp.takeIf { isPlanSelected } ?: com.intuit.ssp.R.dimen._5ssp).toInt(), isAddDefaultPadding = false)
                }

                ivPlanIcon.apply {
                    this.layoutParams.width = mActivity.sdpToPx(com.intuit.sdp.R.dimen._30sdp.takeIf { isPlanSelected } ?: com.intuit.sdp.R.dimen._23sdp)
                    this.requestLayout()
                }

                ivGradiantBackground.isSelected = isPlanSelected
                ivTopPricePercentageBackground.isSelected = isPlanSelected
                ivTopBackground.isSelected = isPlanSelected
                ivBottomPriceBackground.isSelected = isPlanSelected
                ivPlanIcon.isSelected = isPlanSelected
                txtPlanPricePercentage.isSelected = isPlanSelected
                txtPlanTitle.isSelected = isPlanSelected
                txtPlanTrialPeriod.isSelected = isPlanSelected
                txtPlanReferencePrice.isSelected = isPlanSelected
                txtPlanPrice.isSelected = isPlanSelected

                fBinding.root.isSelected = isPlanSelected.takeIf { !isPlanSelected } ?: fBinding.root.isSelected

                if (fBinding.root.parent is ConstraintLayout) {
                    val parent: ConstraintLayout = fBinding.root.parent as ConstraintLayout
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(parent)
                    constraintSet.setDimensionRatio(fBinding.root.id, "328.4:500".takeIf { isPlanSelected } ?: "300:400")
                    val transition = ChangeBounds().apply {
                        duration = 100 // 1000ms = 1 second
                        interpolator = OvershootInterpolator()

                        addTarget(fBinding.root)
                    }
                    TransitionManager.beginDelayedTransition(parent, transition)
                    constraintSet.applyTo(parent)

                    mBinding.root.post {
                        mBinding.infiniteViewPager.requestLayout()
                    }
                }

                if (isPlanSelected && !fBinding.root.isSelected) {
                    fBinding.root.isSelected = true
                }
            }
        }
    }

    private fun setProductData() {
        CoroutineScope(Dispatchers.IO).launch {
            ProductPurchaseHelper.getLifeTimeProductInfo?.let { productInfo ->
                mLifetimePlanProductInfo = productInfo
                CoroutineScope(Dispatchers.Main).launch {
                    mActivity.runOnUiThread {
                        with(mBinding.lyLifetimePlan) {
                            txtPlanPricePercentage.apply {
                                this.text = getLocalizedString<String>(
                                    context = mActivity,
                                    resourceId = R.string.best_value,
                                )
                            }
                            txtPlanTitle.apply {
                                this.text = getLocalizedString<String>(
                                    context = mActivity,
                                    resourceId = R.string.lifetime,
                                )
                            }
                            txtPlanTrialPeriod.apply {
                                this.text = getLocalizedString<String>(
                                    context = mActivity,
                                    resourceId = R.string.purchase_1,
                                )
                            }
                            txtPlanReferencePrice.apply {
                                this.text = getLocalizedString<String>(
                                    context = mActivity,
                                    resourceId = R.string.price_slash_period,
                                )
                                this.gone
                            }
                            txtPlanPrice.apply {
                                this.text = productInfo.formattedPrice
                            }

                            isLifeTimePrizeSated = true
                        }
                    }
                }
            }

            ProductPurchaseHelper.getMonthlyProductInfo?.let { monthlyProductInfo ->
                ProductPurchaseHelper.getYearlyProductInfo?.let { yearlyProductInfo ->
                    mMonthlyPlanProductInfo = monthlyProductInfo
                    mYearlyPlanProductInfo = yearlyProductInfo
                    setAnyPlanBaseYearlyProductInfoUI(
                        firstPlanProductInfo = monthlyProductInfo,
                        yearlyProductInfo = yearlyProductInfo,
                        isMonthlySKU = true
                    )
                }
            } ?: kotlin.run {
                ProductPurchaseHelper.getWeeklyProductInfo?.let { weeklyProductInfo ->
                    ProductPurchaseHelper.getYearlyProductInfo?.let { yearlyProductInfo ->
                        mWeeklyPlanProductInfo = weeklyProductInfo
                        mYearlyPlanProductInfo = yearlyProductInfo
                        setAnyPlanBaseYearlyProductInfoUI(
                            firstPlanProductInfo = weeklyProductInfo,
                            yearlyProductInfo = yearlyProductInfo,
                            isMonthlySKU = false
                        )
                    }
                }
            }
        }
    }

    private fun setAnyPlanBaseYearlyProductInfoUI(firstPlanProductInfo: ProductInfo, yearlyProductInfo: ProductInfo, isMonthlySKU: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            mActivity.runOnUiThread {
                with(mBinding.lyMonthlyPlan) {
                    txtPlanPricePercentage.apply {
                        this.text = getLocalizedString<String>(
                            context = mActivity,
                            resourceId = R.string.most_popular,
                        )
                    }
                    txtPlanTitle.apply {
                        this.text = getLocalizedString<String>(
                            context = mActivity,
                            resourceId = R.string.monthly.takeIf { isMonthlySKU } ?: R.string.weekly,
                        )
                    }
                    txtPlanTrialPeriod.apply {
                        when (firstPlanProductInfo.planOfferType) {
                            PlanOfferType.FREE_TRIAL -> {
                                this.text = getLocalizedString<String>(
                                    context = mActivity,
                                    resourceId = R.string.period_free,
                                    formatArgs = arrayOf(
                                        firstPlanProductInfo.actualFreeTrialPeriod.getFullBillingPeriod(context = mActivity)
                                    )
                                )
                            }

                            PlanOfferType.INTRO_OFFER -> {
                                this.text = getLocalizedString<String>(
                                    context = mActivity,
                                    resourceId = R.string.upto_period,
                                    formatArgs = arrayOf(
                                        firstPlanProductInfo.offerActualBillingPeriod.getFullBillingPeriod(context = mActivity)
                                    )
                                )
                            }

                            else -> {}
                        }
                        this.beVisibleIf(firstPlanProductInfo.planOfferType != PlanOfferType.BASE_PLAN)
                    }
                    txtPlanReferencePrice.apply {
                        this.text = getLocalizedString<String>(
                            context = mActivity,
                            resourceId = R.string.price_slash_period,
                        )
                        this.gone
                    }
                    txtPlanPrice.apply {
                        this.text = firstPlanProductInfo.offerFormattedPrice.takeIf { firstPlanProductInfo.planOfferType == PlanOfferType.INTRO_OFFER } ?: firstPlanProductInfo.formattedPrice
                    }
                }

                with(mBinding.lyYearlyPlan) {
                    if (isMonthlySKU) {
                        ProductPurchaseHelper.getMonthBaseYearlyDiscount(
                            monthPrice = firstPlanProductInfo.formattedPrice,
                            yearPrice = yearlyProductInfo.formattedPrice,
                            onDiscountCalculated = { discountPercentage, discountPrice ->
                                txtPlanPricePercentage.apply {
                                    this.text = getLocalizedString<String>(
                                        context = mActivity,
                                        resourceId = R.string.save_percentage,
                                        formatArgs = arrayOf(discountPercentage.toString())
                                    )
                                }

                                txtPlanReferencePrice.apply {
                                    this.text = getLocalizedString<String>(
                                        context = mActivity,
                                        resourceId = R.string.price_slash_period,
                                        formatArgs = arrayOf(
                                            discountPrice,
                                            getLocalizedString<String>(
                                                context = mActivity,
                                                resourceId = R.string.period_month,
                                            )
                                        )
                                    )
                                    this.visible
                                }
                            }
                        )
                    } else {
                        ProductPurchaseHelper.getWeekBaseYearlyDiscount(
                            weekPrice = firstPlanProductInfo.formattedPrice,
                            yearPrice = yearlyProductInfo.formattedPrice,
                            onDiscountCalculated = { discountPercentage, discountPrice ->
                                txtPlanPricePercentage.apply {
                                    this.text = getLocalizedString<String>(
                                        context = mActivity,
                                        resourceId = R.string.save_percentage,
                                        formatArgs = arrayOf(discountPercentage.toString())
                                    )
                                }

                                txtPlanReferencePrice.apply {
                                    this.text = getLocalizedString<String>(
                                        context = mActivity,
                                        resourceId = R.string.price_slash_period,
                                        formatArgs = arrayOf(
                                            discountPrice,
                                            getLocalizedString<String>(
                                                context = mActivity,
                                                resourceId = R.string.period_month,
                                            )
                                        )
                                    )
                                    this.visible
                                }
                            }
                        )
                    }


                    txtPlanTitle.apply {
                        this.text = getLocalizedString<String>(
                            context = mActivity,
                            resourceId = R.string.yearly,
                        )
                    }
                    txtPlanTrialPeriod.apply {
                        when (yearlyProductInfo.planOfferType) {
                            PlanOfferType.FREE_TRIAL -> {
                                this.text = getLocalizedString<String>(
                                    context = mActivity,
                                    resourceId = R.string.period_free,
                                    formatArgs = arrayOf(
                                        yearlyProductInfo.actualFreeTrialPeriod.getFullBillingPeriod(context = mActivity)
                                    )
                                )
                            }

                            PlanOfferType.INTRO_OFFER -> {
                                this.text = getLocalizedString<String>(
                                    context = mActivity,
                                    resourceId = R.string.upto_period,
                                    formatArgs = arrayOf(
                                        yearlyProductInfo.offerActualBillingPeriod.getFullBillingPeriod(context = mActivity)
                                    )
                                )
                            }

                            else -> {}
                        }
                        this.beVisibleIf(yearlyProductInfo.planOfferType != PlanOfferType.BASE_PLAN)
                    }

                    txtPlanPrice.apply {
                        this.text = yearlyProductInfo.formattedPrice
                    }
                }

                isAnyPlanPrizeSated = true

                mBinding.lyYearlyPlan.root.performClick()
            }
        }
    }

    override fun onScreenFinishing() {
        super.onScreenFinishing()
        onScreenFinish.invoke(isUserPurchaseAnyPlan)
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimer?.cancelTimer()
        mTimer = null
    }

    override fun needToShowBackAd(): Boolean {
        var isShowAd = false
        if (!isFromTimeLine) {
            if (mBinding.ivClose.isPressed || isSystemBackButtonPressed) {
                if (IS_FROM_SPLASH && SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN_OPEN_AFTER_SPLASH) {
                    isShowAd = true
                } else if (!IS_FROM_SPLASH && SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN) {
                    isShowAd = true
                }
            }
        }
        return isShowAd
    }

    override fun customOnBackPressed() {
        if (mBinding.ivClose.isPressed || isSystemBackButtonPressed) {
            fireSubscriptionEvent(fEventType = SubscriptionEventType.VIEW_ALL_PLANS_SCREEN_CLOSE)
        }
        super.customOnBackPressed()
        isSystemBackButtonPressed = false
    }
}