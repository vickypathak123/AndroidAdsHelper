package com.example.app.ads.helper.purchase.fourplan.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.BaseActivity
import com.example.app.ads.helper.base.BaseBindingActivity
import com.example.app.ads.helper.base.utils.beVisibleIf
import com.example.app.ads.helper.base.utils.disable
import com.example.app.ads.helper.base.utils.enable
import com.example.app.ads.helper.base.utils.getColorRes
import com.example.app.ads.helper.base.utils.getDimensionRes
import com.example.app.ads.helper.base.utils.getDrawableRes
import com.example.app.ads.helper.base.utils.getFontRes
import com.example.app.ads.helper.base.utils.gone
import com.example.app.ads.helper.base.utils.invisible
import com.example.app.ads.helper.base.utils.isTiramisuPlus
import com.example.app.ads.helper.base.utils.setTextSizeDimension
import com.example.app.ads.helper.base.utils.visible
import com.example.app.ads.helper.databinding.ActivityFourPlanBinding
import com.example.app.ads.helper.databinding.FourPlanRatingIndicatorBinding
import com.example.app.ads.helper.utils.getLocalizedString
import com.example.app.ads.helper.utils.isEnglishLanguage
import com.example.app.ads.helper.utils.isRTLDirectionFromLocale
import com.example.app.ads.helper.launcher.Launcher
import com.example.app.ads.helper.utils.logE
import com.example.app.ads.helper.purchase.IS_ENABLE_TEST_PURCHASE
import com.example.app.ads.helper.purchase.IS_FROM_SPLASH
import com.example.app.ads.helper.purchase.SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN
import com.example.app.ads.helper.purchase.SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN_OPEN_AFTER_SPLASH
import com.example.app.ads.helper.purchase.SUBSCRIPTION_DATA_LANGUAGE_CODE
import com.example.app.ads.helper.purchase.SUBSCRIPTION_PRIVACY_POLICY
import com.example.app.ads.helper.purchase.SUBSCRIPTION_TERMS_OF_USE
import com.example.app.ads.helper.purchase.fireSubscriptionEvent
import com.example.app.ads.helper.purchase.fourplan.adapter.FourPlanRatingAdapter
import com.example.app.ads.helper.purchase.fourplan.adapter.FourPlanUserItemAdapter
import com.example.app.ads.helper.purchase.fourplan.utils.FourPlanRattingItem
import com.example.app.ads.helper.purchase.fourplan.utils.FourPlanRattingItemType
import com.example.app.ads.helper.purchase.fourplan.utils.FourPlanScreenDataModel
import com.example.app.ads.helper.purchase.fourplan.utils.FourPlanUserItem
import com.example.app.ads.helper.purchase.product.AdsManager
import com.example.app.ads.helper.purchase.product.PlanOfferType
import com.example.app.ads.helper.purchase.product.ProductInfo
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getBillingPeriodName
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getFullBillingPeriod
import com.example.app.ads.helper.purchase.removeTrailingZeros
import com.example.app.ads.helper.purchase.timeline.activity.TimeLineActivity
import com.example.app.ads.helper.purchase.timeline.activity.TimeLineActivity.Companion
import com.example.app.ads.helper.purchase.utils.AdTimer
import com.example.app.ads.helper.purchase.utils.SubscriptionEventType
import com.example.app.ads.helper.remoteconfig.mVasuSubscriptionRemoteConfigModel
import com.example.app.ads.helper.utils.isOnline
import com.example.app.ads.helper.utils.toCamelCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


internal class FourPlanActivity : BaseBindingActivity<ActivityFourPlanBinding>() {

    private val mHandler = Handler(Looper.getMainLooper())
    private val speedScroll = 0
    private val mRunnable = object : Runnable {
        var count = 0
        override fun run() {
            if (count == mBinding.rvUserItem.adapter?.itemCount) count = 0
            if (count < (mBinding.rvUserItem.adapter?.itemCount ?: -1)) {
                mBinding.rvUserItem.smoothScrollToPosition(++count)
                mHandler.postDelayed(this, speedScroll.toLong())
            }
        }
    }

    private var mTimer: AdTimer? = null

    private var isLifeTimePrizeSated: Boolean = false
    private var isYearlyPrizeSated: Boolean = false
    private var isMonthlyPrizeSated: Boolean = false
    private var isWeeklyPrizeSated: Boolean = false
    private var isUserPurchaseAnyPlan: Boolean = false

    private var mLifetimePlanProductInfo: ProductInfo? = null
    private var mYearlyPlanProductInfo: ProductInfo? = null
    private var mWeeklyPlanProductInfo: ProductInfo? = null
    private var mMonthlyPlanProductInfo: ProductInfo? = null

    /**
     * Index == 2 ::-> Try 3 days for $0
     * Index == 1 ::-> START MY FREE TRIAL
     * Index == else ::-> CONTINUE
     */
    private val mPurchaseButtonTextIndex: Int get() = mVasuSubscriptionRemoteConfigModel.purchaseButtonTextIndex

    private val isFromTimeLine: Boolean get() = intent?.getBooleanExtra("isFromTimeLine", false) ?: false

    private val listOfBoxItem: ArrayList<FourPlanUserItem>
        get() = screenDataModel?.listOfBoxItem.takeIf { !it.isNullOrEmpty() } ?: arrayListOf(
            FourPlanUserItem(backgroundDrawable = R.drawable.bg_cloud_backup, itemIcon = R.drawable.aa_cloud_backup, itemName = R.string.rating_user),
            FourPlanUserItem(backgroundDrawable = R.drawable.bg_cloud_backup, itemIcon = R.drawable.aa_cloud_backup, itemName = R.string.rating_user),
            FourPlanUserItem(backgroundDrawable = R.drawable.bg_cloud_backup, itemIcon = R.drawable.aa_cloud_backup, itemName = R.string.rating_user),
            FourPlanUserItem(backgroundDrawable = R.drawable.bg_cloud_backup, itemIcon = R.drawable.aa_cloud_backup, itemName = R.string.rating_user),
            FourPlanUserItem(backgroundDrawable = R.drawable.bg_cloud_backup, itemIcon = R.drawable.aa_cloud_backup, itemName = R.string.rating_user),
            FourPlanUserItem(backgroundDrawable = R.drawable.bg_cloud_backup, itemIcon = R.drawable.aa_cloud_backup, itemName = R.string.rating_user),
        )

    private val listOfRattingItem: ArrayList<FourPlanRattingItem>
        get() = screenDataModel?.listOfRattingItem.takeIf { !it.isNullOrEmpty() } ?: arrayListOf(
            FourPlanRattingItem(
                ratingCount = 4.5f,
                ratingHeader = R.string.rating_header,
                ratingSubHeader = R.string.rating_sub_header,
                itemType = FourPlanRattingItemType.APP_RATING,
            ),
            FourPlanRattingItem(
                satisfiedCustomerCount = 200000,
                satisfiedCustomerDrawable = R.drawable.aa_test_image_girl,
                itemType = FourPlanRattingItemType.SATISFIED_CUSTOMER,
            ),
            FourPlanRattingItem(
                reviewTitle = R.string.rating_header,
                reviewSubTitle = R.string.rating_sub_header,
                reviewGivenBy = R.string.rating_user,
                reviewGivenByTextGravity = Gravity.CENTER,
                itemType = FourPlanRattingItemType.REVIEW,
            ),
            FourPlanRattingItem(
                reviewTitle = R.string.rating_header,
                reviewSubTitle = R.string.rating_sub_header,
                reviewGivenBy = R.string.rating_user,
                reviewGivenByTextGravity = Gravity.START,
                itemType = FourPlanRattingItemType.REVIEW,
            ),
            FourPlanRattingItem(
                reviewTitle = R.string.rating_header,
                reviewSubTitle = R.string.rating_sub_header,
                reviewGivenBy = R.string.rating_user,
                reviewGivenByTextGravity = Gravity.END,
                itemType = FourPlanRattingItemType.REVIEW,
            ),
            FourPlanRattingItem(
                reviewTitle = R.string.rating_header,
                reviewSubTitle = R.string.rating_sub_header,
                reviewGivenBy = R.string.rating_user,
                reviewGivenByTextGravity = Gravity.CENTER,
                itemType = FourPlanRattingItemType.REVIEW,
            ),
        )

    private val listOfRattingIndicatorView: ArrayList<FourPlanRatingIndicatorBinding> = ArrayList()

    private val mLifetimePlanSelector: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf()
                ),
                intArrayOf(
                    mActivity.getColorRes(R.color.default_four_plan_selected_life_time_sku_border_color),
                    mActivity.getColorRes(R.color.default_four_plan_unselected_sku_border_color),
                )
            )
        }

    private val mYearlyPlanSelector: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf()
                ),
                intArrayOf(
                    mActivity.getColorRes(R.color.default_four_plan_selected_yearly_sku_border_color),
                    mActivity.getColorRes(R.color.default_four_plan_unselected_sku_border_color),
                )
            )
        }

    private val mMonthlyPlanSelector: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf()
                ),
                intArrayOf(
                    mActivity.getColorRes(R.color.default_four_plan_selected_monthly_sku_border_color),
                    mActivity.getColorRes(R.color.default_four_plan_unselected_sku_border_color),
                )
            )
        }

    private val mWeeklyPlanSelector: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf()
                ),
                intArrayOf(
                    mActivity.getColorRes(R.color.default_four_plan_selected_weekly_sku_border_color),
                    mActivity.getColorRes(R.color.default_four_plan_unselected_sku_border_color),
                )
            )
        }

    companion object {
        private var screenDataModel: FourPlanScreenDataModel? = null
        private var onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit = {}
        private var reviewDialogData: Pair<String, String> = Pair("", "")

        internal fun launchScreen(
            fActivity: Activity,
            isFromTimeLine: Boolean,
            screenDataModel: FourPlanScreenDataModel,
            reviewDialogData: Pair<String, String>,
            onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit,
        ) {
            Companion.screenDataModel = screenDataModel
            Companion.onScreenFinish = onScreenFinish
            Companion.reviewDialogData = reviewDialogData

            val lIntent = Intent(fActivity, FourPlanActivity::class.java)
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

    override fun getScreenLanguageCode(): String {
        return SUBSCRIPTION_DATA_LANGUAGE_CODE.takeIf { it.isNotEmpty() } ?: "en"
    }

    override fun setBinding(): ActivityFourPlanBinding = ActivityFourPlanBinding.inflate(layoutInflater)

    override fun getActivityContext(): BaseActivity = this@FourPlanActivity

    override fun setParamBeforeLayoutInit() {
        super.setParamBeforeLayoutInit()
        setEdgeToEdgeLayout()
    }

    override fun onResume() {
        super.onResume()
        mBinding.lySubscribeButton.root.enable
        mHandler.postDelayed(mRunnable, speedScroll.toLong())
        if (isOnPause) {
            isOnPause = false
            setBillingListener(fWhere = "onResume")
        }
    }

    private fun setBillingListener(fWhere: String): Job {
        val job: Job = CoroutineScope(Dispatchers.IO).launch {
            logE(TAG, "$fWhere: Set Listener")
            ProductPurchaseHelper.setPurchaseListener(object : ProductPurchaseHelper.ProductPurchaseListener {
                override fun onBillingSetupFinished() {
                    if (!isYearlyPrizeSated || !isWeeklyPrizeSated || !isMonthlyPrizeSated || !isLifeTimePrizeSated) {
                        ProductPurchaseHelper.initProductsKeys(fContext = mActivity) {
                            if (!isYearlyPrizeSated || !isWeeklyPrizeSated || !isMonthlyPrizeSated || !isLifeTimePrizeSated) {
                                setProductData()
                            }
                        }
                    }
                }

                override fun onPurchasedSuccess() {
                    super.onPurchasedSuccess()
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
                                    }
                                }

                                mBinding.lyWeeklyPlan.root.isSelected -> {
                                    mWeeklyPlanProductInfo?.let { productInfo ->
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

//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

//        getEdgeToEdgeMargin()

        fireSubscriptionEvent(fEventType = SubscriptionEventType.VIEW_ALL_PLANS_SCREEN_OPEN)

        val job: Job = setBillingListener(fWhere = "initView")
        runBlocking {
            job.join()
            CoroutineScope(Dispatchers.IO).launch {
                ProductPurchaseHelper.initBillingClient(fContext = mActivity)
            }
        }

        with(mBinding) {
            root.layoutDirection = View.LAYOUT_DIRECTION_RTL.takeIf { isRTLDirectionFromLocale } ?: View.LAYOUT_DIRECTION_LTR
            root.textDirection = View.LAYOUT_DIRECTION_RTL.takeIf { isRTLDirectionFromLocale } ?: View.LAYOUT_DIRECTION_LTR

            lySubscribeButton.lottieBtnContinue.scaleX = (-1f).takeIf { isRTLDirectionFromLocale } ?: 1f
        }

        setScreenUI()
        initBoxItemView()
        setRatingViewPager()
    }

    override fun initViewListener() {
        super.initViewListener()
        with(mBinding) {
            setClickListener(
                ivClose,
                txtPayNothingNow,
                lyLifetimePlan.root,
                lyYearlyPlan.root,
                lyMonthlyPlan.root,
                lyWeeklyPlan.root,
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

                lyLifetimePlan.root,
                lyYearlyPlan.root,
                lyMonthlyPlan.root,
                lyWeeklyPlan.root -> {
                    lyLifetimePlan.root.isSelected = (v == lyLifetimePlan.root)
                    lyYearlyPlan.root.isSelected = (v == lyYearlyPlan.root)
                    lyMonthlyPlan.root.isSelected = (v == lyMonthlyPlan.root)
                    lyWeeklyPlan.root.isSelected = (v == lyWeeklyPlan.root)

                    val lProductInfo: ProductInfo? = when {
                        lyYearlyPlan.root.isSelected -> mYearlyPlanProductInfo
                        lyLifetimePlan.root.isSelected -> mLifetimePlanProductInfo
                        lyMonthlyPlan.root.isSelected -> mMonthlyPlanProductInfo
                        lyWeeklyPlan.root.isSelected -> mWeeklyPlanProductInfo
                        else -> null
                    }

                    logE(TAG, "onClick: ${lProductInfo?.id}")

                    lProductInfo?.let { productInfo ->
                        when (productInfo.planOfferType) {
                            PlanOfferType.FREE_TRIAL -> {
                                txtFreeThenPerPeriod.apply {
                                    this.text = getLocalizedString<String>(
                                        context = mActivity,
                                        resourceId = R.string.free_then_per_period,
                                        formatArgs = arrayOf(
                                            productInfo.actualFreeTrialPeriod.getFullBillingPeriod(context = mActivity),
                                            productInfo.formattedPrice.removeTrailingZeros,
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
                                                    productInfo.formattedPrice.removeTrailingZeros,
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
                                            productInfo.formattedPrice.removeTrailingZeros,
                                            getLocalizedString<String>(
                                                context = mActivity,
                                                resourceId = R.string.lifetime.takeIf {
                                                    lyLifetimePlan.root.isSelected
                                                } ?: R.string.yearly.takeIf {
                                                    lyYearlyPlan.root.isSelected
                                                } ?: R.string.monthly.takeIf {
                                                    lyMonthlyPlan.root.isSelected
                                                } ?: R.string.weekly.takeIf {
                                                    lyWeeklyPlan.root.isSelected
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
                            toolbarColor = Color.parseColor("#09203F"),
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
                            toolbarColor = Color.parseColor("#09203F"),
                            isNightMode = false
                        )
                    } else {
                    }
                }

                lySubscribeButton.root -> {
                    val selectedSKU: String = when {
                        lyYearlyPlan.root.isSelected -> mYearlyPlanProductInfo?.id ?: ""
                        lyLifetimePlan.root.isSelected -> mLifetimePlanProductInfo?.id ?: ""
                        lyMonthlyPlan.root.isSelected -> mMonthlyPlanProductInfo?.id ?: ""
                        lyWeeklyPlan.root.isSelected -> mWeeklyPlanProductInfo?.id ?: ""
                        else -> ""
                    }

                    if (selectedSKU.isNotEmpty()) {
                        lySubscribeButton.root.disable
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

    @SuppressLint("ClickableViewAccessibility")
    private fun initBoxItemView() {
        with(mBinding.rvUserItem) {

            this.layoutManager = object : LinearLayoutManager(mActivity, HORIZONTAL, false) {
                override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
                    val smoothScroller: LinearSmoothScroller =
                        object : LinearSmoothScroller(mActivity) {
//                            private val SPEED = 4f // Change this value (default=25f)
                            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                                return (4f / displayMetrics.densityDpi)
                            }
                        }
                    smoothScroller.targetPosition = position
                    startSmoothScroll(smoothScroller)
                }
            }

            this.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mHandler.removeCallbacks(mRunnable)
                    }

                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> {
                        mHandler.postDelayed(mRunnable, speedScroll.toLong())
                    }
                }
                false
            }

            FourPlanUserItemAdapter(
                itemList = listOfBoxItem
            ).also { this.adapter = it }

            this.setHasFixedSize(true)
        }
        mHandler.postDelayed(mRunnable, speedScroll.toLong())
    }

    private fun setRatingViewPager() {
        val listSize: Int = (listOfRattingItem.size + 2)
        with(mBinding) {
            FourPlanRatingAdapter(
                originalList = listOfRattingItem,

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
                        listOfRattingIndicatorView.forEachIndexed { index, view ->
                            view.root.isSelected = (index == dotPosition)
                        }
                    }
                })
            }

            dotsIndicator.apply {
                this.removeAllViews()
                listOfRattingIndicatorView.clear()

                for (item in listOfRattingItem) {
                    val lBinding = FourPlanRatingIndicatorBinding.inflate(layoutInflater, this, false)

                    lBinding.root.isSelected = false
                    this.addView(lBinding.root)
                    listOfRattingIndicatorView.add(lBinding)
                }
            }

            startAutoSwipeViewPagerTimer()
        }
    }

    private fun startAutoSwipeViewPagerTimer() {
        mTimer?.cancelTimer()
        mTimer = null

        AdTimer(
            millisInFuture = mVasuSubscriptionRemoteConfigModel.rattingBarSliderTiming,
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

    private fun setScreenUI() {
        with(mBinding) {
            txtUnlockAllFeatures.apply {
                this.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.unlock_all_features,
                )

                this.isSelected = !isEnglishLanguage
            }

            txtPayNothingNow.apply {
                this.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.pay_nothing_now,
                )
            }

            lySubscribeButton.apply {
                this.ivBackground.apply {
                    this.background = mActivity.getDrawableRes(R.drawable.bg_subscribe_button_four_plan)
                }
                this.txtBtnContinue.apply {
                    this.setTextSizeDimension(com.intuit.ssp.R.dimen._15ssp)
                    this.typeface = mActivity.getFontRes(R.font.ads_plus_jakarta_sans_extra_bold)
                }
            }

            lySecureWithPlayStore.apply {
                this.ivSecureWithPlayStore.setImageDrawable(mActivity.getDrawableRes(R.drawable.ic_secure_with_play_store_four_plan))

                this.txtSecureWithPlayStore.apply {
                    this.typeface = mActivity.getFontRes(R.font.ads_plus_jakarta_sans_regular)
                    this.text = getLocalizedString<String>(
                        context = mActivity,
                        resourceId = R.string.cancel_anytime_secure_with_play_store,
                    )
                    this.isSelected = true
                    this.setTextColor(Color.parseColor("#24272C"))
                }

                this.ivSecureWithPlayStoreBg.setBackgroundColor(Color.parseColor("#F5F5F5"))
            }

            txtTermsOfUse.apply {
                this.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.terms_of_use,
                )
            }

            txtPrivacyPolicy.apply {
                this.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.privacy_policy,
                )
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
                        period.lowercase(),
                        price
                    )
                )
                this.isAllCaps = (buttonTextIndex != 2)

                this.setEdgeToEdgeTopPadding(fTopPadding = mActivity.getDimensionRes(com.intuit.ssp.R.dimen._11ssp).toInt(), isAddDefaultPadding = false)
                this.setEdgeToEdgeBottomPadding(fBottomPadding = mActivity.getDimensionRes(com.intuit.ssp.R.dimen._11ssp).toInt(), isAddDefaultPadding = false)
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

    //<editor-fold desc="get & set all plan data with UI">
    private fun setProductData() {
        CoroutineScope(Dispatchers.IO).launch {
            ProductPurchaseHelper.getLifeTimeProductInfo?.let { productInfo ->
                mLifetimePlanProductInfo = productInfo
                setLifeTimeProductData(productInfo = productInfo)
            }

            ProductPurchaseHelper.getYearlyProductInfo?.let { yearlyProductInfo ->
                mYearlyPlanProductInfo = yearlyProductInfo
//                ProductPurchaseHelper.getMonthlyProductInfo?.let { monthlyProductInfo ->
                ProductPurchaseHelper.getWeeklyProductInfo?.let { weeklyProductInfo ->
//                    ProductPurchaseHelper.getMonthBaseYearlyDiscount(
                    ProductPurchaseHelper.getWeekBaseYearlyDiscount(
                        weekPrice = weeklyProductInfo.formattedPrice,
                        yearPrice = yearlyProductInfo.formattedPrice,
                        onDiscountCalculated = { discountPercentage, discountPrice ->
                            setYearlyProductData(productInfo = yearlyProductInfo, discountPercentage = discountPercentage, discountPrice = discountPrice)
                        }
                    )
                }
            }

            ProductPurchaseHelper.getMonthlyProductInfo?.let { monthlyProductInfo ->
                mMonthlyPlanProductInfo = monthlyProductInfo
                ProductPurchaseHelper.getWeeklyProductInfo?.let { weeklyProductInfo ->
                    ProductPurchaseHelper.getWeekBaseMonthlyDiscount(
                        weekPrice = weeklyProductInfo.formattedPrice,
                        monthPrice = monthlyProductInfo.formattedPrice,
                        onDiscountCalculated = { discountPercentage, discountPrice ->
                            setMonthlyProductData(productInfo = monthlyProductInfo, discountPercentage = discountPercentage, discountPrice = discountPrice)
                        }
                    )
                }
            }

            ProductPurchaseHelper.getWeeklyProductInfo?.let { weeklyProductInfo ->
                mWeeklyPlanProductInfo = weeklyProductInfo
                setWeeklyProductData(productInfo = weeklyProductInfo)
            }
        }
    }

    private fun setLifeTimeProductData(productInfo: ProductInfo) {
        CoroutineScope(Dispatchers.Main).launch {
            mActivity.runOnUiThread {
                with(mBinding) {
                    lyLifetimePlanTag.apply {
                        this.txtTag.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.best_value).toCamelCase
                        this.ivBackground.setBackgroundColor(mLifetimePlanSelector.getColorForState(intArrayOf(android.R.attr.state_selected), mLifetimePlanSelector.defaultColor))
                    }
                    lyLifetimePlan.apply {
                        this.ivBackground.backgroundTintList = mLifetimePlanSelector
                        this.lyTagHint.apply {
                            this.txtTag.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.best_value).toCamelCase
                            this.ivBackground.setBackgroundColor(mMonthlyPlanSelector.getColorForState(intArrayOf(android.R.attr.state_selected), mMonthlyPlanSelector.defaultColor))
                            this.root.invisible
                        }

                        this.txtPlanTitle.apply {
                            this.text = getLocalizedString<String>(
                                context = mActivity,
                                resourceId = R.string.lifetime,
                            )
                        }

                        this.txtPlanReferencePrice.apply {
                            this.text = getLocalizedString<String>(
                                context = mActivity,
                                resourceId = R.string.pay_once_enjoy_forever,
                            )
                        }

                        this.txtPlanPrice.apply {
                            this.text = productInfo.formattedPrice.removeTrailingZeros
                        }

                        this.txtPlanDiscount.apply {
                            this.text = getLocalizedString<String>(
                                context = mActivity,
                                resourceId = R.string.percentage_off,
                                formatArgs = arrayOf("${mVasuSubscriptionRemoteConfigModel.lifeTimePlanDiscountPercentage}")
                            )
                            this.setTextColor(mLifetimePlanSelector.getColorForState(intArrayOf(android.R.attr.state_selected), mLifetimePlanSelector.defaultColor))
                        }
                    }
                }
                isLifeTimePrizeSated = true
            }
        }
    }

    private fun setYearlyProductData(productInfo: ProductInfo, discountPercentage: Int, discountPrice: String) {
        CoroutineScope(Dispatchers.Main).launch {
            mActivity.runOnUiThread {
                with(mBinding) {
                    lyYearlyPlanTag.apply {
                        this.txtTag.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.most_popular).toCamelCase
                        this.ivBackground.setBackgroundColor(mYearlyPlanSelector.getColorForState(intArrayOf(android.R.attr.state_selected), mYearlyPlanSelector.defaultColor))
                    }
                    lyYearlyPlan.apply {
                        this.ivBackground.backgroundTintList = mYearlyPlanSelector
                        this.lyTagHint.apply {
                            this.txtTag.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.most_popular).toCamelCase
                            this.ivBackground.setBackgroundColor(mMonthlyPlanSelector.getColorForState(intArrayOf(android.R.attr.state_selected), mMonthlyPlanSelector.defaultColor))
                            this.root.invisible
                        }

                        this.txtPlanTitle.apply {
                            this.text = getLocalizedString<String>(
                                context = mActivity,
                                resourceId = R.string.yearly,
                            )
                        }

                        this.txtPlanReferencePrice.apply {
                            this.text = getLocalizedString<String>(
                                context = mActivity,
                                resourceId = R.string.price_per_period,
                                formatArgs = arrayOf(
                                    discountPrice.removeTrailingZeros,
                                    getLocalizedString<String>(
                                        context = mActivity,
//                                        resourceId = R.string.period_month,
                                        resourceId = R.string.period_week,
                                    )
                                ),
                            ).lowercase()
                        }

                        this.txtPlanPrice.apply {
                            this.text = productInfo.formattedPrice.removeTrailingZeros
                        }

                        this.txtPlanDiscount.apply {
                            this.text = getLocalizedString<String>(
                                context = mActivity,
                                resourceId = R.string.percentage_off,
                                formatArgs = arrayOf("$discountPercentage")
                            )
                            this.setTextColor(mYearlyPlanSelector.getColorForState(intArrayOf(android.R.attr.state_selected), mYearlyPlanSelector.defaultColor))
                        }
                    }
                }
                isYearlyPrizeSated = true
            }
        }
    }

    private fun setMonthlyProductData(productInfo: ProductInfo, discountPercentage: Int, discountPrice: String) {
        CoroutineScope(Dispatchers.Main).launch {
            mActivity.runOnUiThread {
                with(mBinding) {
                    lyMonthlyPlanTag.apply {
                        this.txtTag.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.recommend).toCamelCase
                        this.ivBackground.setBackgroundColor(mMonthlyPlanSelector.getColorForState(intArrayOf(android.R.attr.state_selected), mMonthlyPlanSelector.defaultColor))
                    }
                    lyMonthlyPlan.apply {
                        this.ivBackground.backgroundTintList = mMonthlyPlanSelector
                        this.lyTagHint.apply {
                            this.txtTag.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.recommend).toCamelCase
                            this.ivBackground.setBackgroundColor(mMonthlyPlanSelector.getColorForState(intArrayOf(android.R.attr.state_selected), mMonthlyPlanSelector.defaultColor))
                            this.root.invisible
                        }

                        this.txtPlanTitle.apply {
                            this.text = getLocalizedString<String>(
                                context = mActivity,
                                resourceId = R.string.monthly,
                            )
                        }

                        this.txtPlanReferencePrice.apply {
                            this.text = getLocalizedString<String>(
                                context = mActivity,
                                resourceId = R.string.price_per_period,
                                formatArgs = arrayOf(
                                    discountPrice.removeTrailingZeros,
                                    getLocalizedString<String>(
                                        context = mActivity,
                                        resourceId = R.string.period_week,
                                    )
                                ),
                            ).lowercase()
                        }

                        this.txtPlanPrice.apply {
                            this.text = (productInfo.offerFormattedPrice.takeIf { productInfo.planOfferType == PlanOfferType.INTRO_OFFER } ?: productInfo.formattedPrice).removeTrailingZeros
                        }

                        this.txtPlanDiscount.apply {
                            this.text = getLocalizedString<String>(
                                context = mActivity,
                                resourceId = R.string.percentage_off,
                                formatArgs = arrayOf("$discountPercentage")
                            )
                            this.setTextColor(mMonthlyPlanSelector.getColorForState(intArrayOf(android.R.attr.state_selected), mMonthlyPlanSelector.defaultColor))
                        }
                    }
                }
                isMonthlyPrizeSated = true
            }
        }
    }

    private fun setWeeklyProductData(productInfo: ProductInfo) {
        CoroutineScope(Dispatchers.Main).launch {
            mActivity.runOnUiThread {
                with(mBinding) {
                    lyWeeklyPlan.apply {
                        this.ivBackground.backgroundTintList = mWeeklyPlanSelector
                        this.txtPlanDiscount.gone
                        this.lyTagHint.root.gone

                        this.txtPlanTitle.apply {
                            (this.layoutParams as ConstraintLayout.LayoutParams).apply {
                                this.verticalBias = 0.5f
                            }.also { this.layoutParams = it }

                            this.text = getLocalizedString<String>(
                                context = mActivity,
                                resourceId = R.string.weekly,
                            )
                        }

//                        this.txtPlanReferencePrice.apply {
//                            this.text = getLocalizedString<String>(
//                                context = mActivity,
//                                resourceId = R.string.enjoy_forever,
//                            )
//                        }

                        this.txtPlanReferencePrice.apply {
                            this.text = getLocalizedString<String>(
                                context = mActivity,
                                resourceId = R.string.price_per_period,
                                formatArgs = arrayOf(
                                    productInfo.formattedPrice.removeTrailingZeros,
                                    getLocalizedString<String>(
                                        context = mActivity,
                                        resourceId = R.string.period_week,
                                    )
                                ),
                            ).lowercase()
                        }

                        this.txtPlanPrice.apply {
                            this.text = productInfo.formattedPrice.removeTrailingZeros
                        }
                    }

                    lyYearlyPlan.root.performClick()
                }
                isWeeklyPrizeSated = true
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Override Functions">
    override fun onScreenFinishing() {
        super.onScreenFinishing()
        onScreenFinish.invoke(isUserPurchaseAnyPlan)
    }

    override fun needToShowBackAd(): Boolean {
        var isShowAd = false
        if (!isFromTimeLine) {
            if (mBinding.ivClose.isPressed || isSystemBackButtonPressed || isFromReviewDialog) {
                if (IS_FROM_SPLASH && SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN_OPEN_AFTER_SPLASH) {
                    isShowAd = true
                } else if (!IS_FROM_SPLASH && SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN) {
                    isShowAd = true
                }
            }
        }
        return isShowAd
    }

    override fun needToShowReviewDialog(): Boolean {
//        return (!isFromTimeLine) && IS_FROM_SPLASH && (!AdsManager(context = mActivity).isReviewDialogOpened)
        return !isUserPurchaseAnyPlan && isOnline && (!isFromTimeLine) && (!AdsManager(context = mActivity).isReviewDialogOpened)
    }

    private var isFromReviewDialog: Boolean = false

    override fun showReviewDialog(onNextAction: () -> Unit) {
        super.showReviewDialog(onNextAction)
        mReviewDialog.show(
            fPackageName = reviewDialogData.first,
            fVersionName = reviewDialogData.second,
            fLanguageCode = SUBSCRIPTION_DATA_LANGUAGE_CODE,
            onDismiss = {
                isFromReviewDialog = true
                onNextAction.invoke()
            },
        )
    }

    override fun customOnBackPressed() {
        if (needToShowReviewDialog()) {
            super.customOnBackPressed()
        } else {
            if (mBinding.ivClose.isPressed || isSystemBackButtonPressed || isFromReviewDialog) {
                fireSubscriptionEvent(fEventType = SubscriptionEventType.VIEW_ALL_PLANS_SCREEN_CLOSE)
            }
            super.customOnBackPressed()
            isSystemBackButtonPressed = false
        }
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacks(mRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimer?.cancelTimer()
        mTimer = null
        mHandler.removeCallbacks(mRunnable)
    }
    //</editor-fold>
}