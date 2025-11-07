package com.example.app.ads.helper.purchase.twoplan.activity

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.BaseActivity
import com.example.app.ads.helper.base.BaseBindingActivity
import com.example.app.ads.helper.base.utils.disable
import com.example.app.ads.helper.base.utils.enable
import com.example.app.ads.helper.base.utils.getColorRes
import com.example.app.ads.helper.base.utils.gone
import com.example.app.ads.helper.base.utils.invisible
import com.example.app.ads.helper.base.utils.isTiramisuPlus
import com.example.app.ads.helper.base.utils.setPriceStyle
import com.example.app.ads.helper.base.utils.setTrialPeriodStyle
import com.example.app.ads.helper.base.utils.visible
import com.example.app.ads.helper.databinding.ActivityTwoPlanBinding
import com.example.app.ads.helper.databinding.LayoutDynamicPlanItemBinding
import com.example.app.ads.helper.databinding.TwoPlanMainItemBinding
import com.example.app.ads.helper.databinding.TwoPlanTagItemBinding
import com.example.app.ads.helper.launcher.Launcher
import com.example.app.ads.helper.purchase.IS_ENABLE_TEST_PURCHASE
import com.example.app.ads.helper.purchase.IS_FROM_SPLASH
import com.example.app.ads.helper.purchase.SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN
import com.example.app.ads.helper.purchase.SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN_OPEN_AFTER_SPLASH
import com.example.app.ads.helper.purchase.SUBSCRIPTION_DATA_LANGUAGE_CODE
import com.example.app.ads.helper.purchase.SUBSCRIPTION_PRIVACY_POLICY
import com.example.app.ads.helper.purchase.SUBSCRIPTION_TERMS_OF_USE
import com.example.app.ads.helper.purchase.fireSubscriptionEvent
import com.example.app.ads.helper.purchase.product.AdsManager
import com.example.app.ads.helper.purchase.product.PlanOfferType
import com.example.app.ads.helper.purchase.product.ProductInfo
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getBillingPeriodName
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getFullBillingPeriod
import com.example.app.ads.helper.purchase.removeTrailingZeros
import com.example.app.ads.helper.purchase.twoplan.utils.TwoPlanScreenDataModel
import com.example.app.ads.helper.purchase.utils.SubscriptionEventType
import com.example.app.ads.helper.purchase.utils.getEventParamBundle
import com.example.app.ads.helper.remoteconfig.mVasuSubscriptionRemoteConfigModel
import com.example.app.ads.helper.utils.IconPosition
import com.example.app.ads.helper.utils.getLocalizedString
import com.example.app.ads.helper.utils.isAppForeground
import com.example.app.ads.helper.utils.isOnline
import com.example.app.ads.helper.utils.logE
import com.example.app.ads.helper.utils.setCloseIconPosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

// NEW: Data class to cleanly manage all plan information together
data class PlanUIData(
    val productInfo: ProductInfo,
    val planType: Int,
    val planView: View,
    val mainBinding: TwoPlanMainItemBinding,
    val tagBinding: TwoPlanTagItemBinding
)

internal class TwoPlanActivity : BaseBindingActivity<ActivityTwoPlanBinding>() {

    // --- REFACTORED: Use a dynamic list and index for state management ---
    private var isUserPurchaseAnyPlan: Boolean = false
    private var mPlanList = mutableListOf<PlanUIData>()
    private var mSelectedPlanIndex = -1

    private val isFromTimeLine: Boolean
        get() = intent?.getBooleanExtra("isFromTimeLine", false) ?: false

    // --- REFACTORED: Getters for Remote Config values are now cleaner ---
//    private val mSkuType: ArrayList<Int>
//        get() = intent?.getIntegerArrayListExtra("skuType") ?: arrayListOf(1, 2)

    private val mSkuType: ArrayList<Int>
        get() {

            val rawSkuList = intent?.getIntegerArrayListExtra("skuType") ?: arrayListOf()
            val defaultList = arrayListOf(1, 2) // Our safe, default list


            if (rawSkuList.isEmpty()) {
                return defaultList
            }


            val candidateList = ArrayList(rawSkuList.take(4))


            val containsInvalidSku = candidateList.any { sku -> sku !in 0..3 }


            val hasDuplicates = candidateList.size != candidateList.toSet().size


            if (containsInvalidSku || hasDuplicates) {

                Log.w(
                    TAG,
                    "Invalid or duplicate SKUs found in list: $candidateList. Using default list [1, 2]."
                )
                return defaultList
            } else {

                return candidateList
            }
        }

//    private val mDefaultSelectionIndex: Int
//        get() = intent?.getIntExtra("defSkySelection", 0) ?: 0 // Use 0 for first item as default

    //    private val mDefaultSelectionIndex: Int
//        get() {
//
//            val oneBasedSelection = intent?.getIntExtra("defSkySelection", 1) ?: 1
//
//
//            if (oneBasedSelection <= 0) {
//                return 0 // Default to the first item
//            }
//
//
//            return oneBasedSelection - 1
//        }
    private val mDefaultSelectionPlanType: Int
        get() {
            val planType = intent?.getIntExtra("defSkySelection", 1) ?: 1
            return if (planType in 0..3) planType else 1
        }
    private val mPurchaseButtonTextIndex: Int get() = mVasuSubscriptionRemoteConfigModel.purchaseButtonTextIndex

    private val listOfFeatures: ArrayList<String>
        get() = screenDataModel?.listOfFeatures.takeIf { !it.isNullOrEmpty() } ?: arrayListOf(
            getLocalizedString<String>(context = mActivity, resourceId = R.string.remove_water),
            getLocalizedString<String>(context = mActivity, resourceId = R.string.sound_test),
            getLocalizedString<String>(
                context = mActivity,
                resourceId = R.string.unlimited_cleaning
            ),
            getLocalizedString<String>(context = mActivity, resourceId = R.string.clean_headphone),
            getLocalizedString<String>(context = mActivity, resourceId = R.string.custom_modes),
            getLocalizedString<String>(
                context = mActivity,
                resourceId = R.string.ad_free_experience
            )
        )

    private fun getPlanFromIndex(index: Int): ProductInfo? {
        return when (index) {
            0 -> ProductPurchaseHelper.getLifeTimeProductInfo
            1 -> ProductPurchaseHelper.getYearlyProductInfo
            2 -> ProductPurchaseHelper.getMonthlyProductInfo
            3 -> ProductPurchaseHelper.getWeeklyProductInfo
            else -> ProductPurchaseHelper.getYearlyProductInfo
        }
    }

    private val mPlanForgroundSelector: ColorStateList by lazy {
        ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf()),
            intArrayOf(
                mActivity.getColorRes(R.color.default_two_plan_selected_sku_bg_color),
                mActivity.getColorRes(R.color.default_two_plan_unselected_sku_bg_color),
            )
        )
    }

    private val mPlanBgSelector: ColorStateList by lazy {
        ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf()),
            intArrayOf(
                mActivity.getColorRes(R.color.default_two_plan_selected_sku_border_color),
                mActivity.getColorRes(R.color.default_two_plan_selected_sku_border_color),
            )
        )
    }

    private val mPlanTagSelector: ColorStateList by lazy {
        ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf()),
            intArrayOf(
                mActivity.getColorRes(R.color.default_two_plan_selected_sku_border_color),
                mActivity.getColorRes(R.color.default_two_plan_unselected_sku_border_color),
            )
        )
    }

    override fun getScreenLanguageCode(): String =
        SUBSCRIPTION_DATA_LANGUAGE_CODE.takeIf { it.isNotEmpty() } ?: "en"

    override fun setBinding(): ActivityTwoPlanBinding =
        ActivityTwoPlanBinding.inflate(layoutInflater)

    override fun getActivityContext(): BaseActivity = this

    companion object {
        private var screenDataModel: TwoPlanScreenDataModel? = null
        private var onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit = {}
        private var reviewDialogData: Pair<String, String> = Pair("", "")
        private var onBackShowInterAd: (fContext: Activity, () -> Unit) -> Unit =
            { _, next -> next() }

        internal fun launchScreen(
            fActivity: Activity,
            isFromTimeLine: Boolean,
            skuType: ArrayList<Int>,
            defSkySelection: Int,
            screenDataModel: TwoPlanScreenDataModel,
            reviewDialogData: Pair<String, String>,
            onBackShowInterAd: (fContext: Activity, () -> Unit) -> Unit = { _, next -> next() },
            onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit,
        ) {
            Companion.screenDataModel = screenDataModel
            Companion.onScreenFinish = onScreenFinish
            Companion.reviewDialogData = reviewDialogData
            Companion.onBackShowInterAd = onBackShowInterAd

            val lIntent = Intent(fActivity, TwoPlanActivity::class.java).apply {
                putExtra("skuType", skuType)
                putExtra("defSkySelection", defSkySelection)
                putExtra("isFromTimeLine", isFromTimeLine)
            }

            @AnimatorRes @AnimRes val lEnterAnimId: Int = android.R.anim.fade_in
            @AnimatorRes @AnimRes val lExitAnimId: Int = android.R.anim.fade_out

            fActivity.runOnUiThread {
                if (isTiramisuPlus) {
                    val options =
                        ActivityOptions.makeCustomAnimation(fActivity, lEnterAnimId, lExitAnimId)
                    fActivity.startActivity(lIntent, options.toBundle())
                } else {
                    fActivity.startActivity(lIntent)
                    @Suppress("DEPRECATION") fActivity.overridePendingTransition(
                        lEnterAnimId,
                        lExitAnimId
                    )
                }
            }
        }
    }

    override fun setParamBeforeLayoutInit() {
        super.setParamBeforeLayoutInit()
        setEdgeToEdgeLayout()
    }

    override fun onResume() {
        super.onResume()
        mBinding.lySubscribeButton.root.enable
    }

    private fun setBillingListener(fWhere: String): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            logE(TAG, "$fWhere: Set Listener")
            ProductPurchaseHelper.setPurchaseListener(object :
                ProductPurchaseHelper.ProductPurchaseListener {
                override fun onBillingSetupFinished() {
                    if (mPlanList.isEmpty()) {
                        ProductPurchaseHelper.initProductsKeys(fContext = mActivity) {
                            if (mPlanList.isEmpty()) {
                                CoroutineScope(Dispatchers.IO).launch { setAndCreatePlanViews() }
                            }
                        }
                    }
                }

                override fun onPurchasedSuccess() {
                    super.onPurchasedSuccess()
                    if (mSelectedPlanIndex != -1) {
                        val selectedPlan = mPlanList[mSelectedPlanIndex]
                        fireDynamicPurchaseEvent(selectedPlan.productInfo, selectedPlan.planType)
                    }
                    isUserPurchaseAnyPlan = true
                    mActivity.runOnUiThread { mBinding.ivClose.performClick() }
                }
            })
        }
    }

    override fun initView() {
        super.initView()
        fireSubscriptionEvent(fEventType = SubscriptionEventType.VIEW_ALL_PLANS_SCREEN_OPEN)

        runBlocking {
            setBillingListener(fWhere = "initView").join()
            ProductPurchaseHelper.initBillingClient(fContext = mActivity)
        }

//        with(mBinding) {
//            root.layoutDirection = if (isRTLDirectionFromLocale) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
//            root.textDirection = if (isRTLDirectionFromLocale) View.TEXT_DIRECTION_RTL else View.TEXT_DIRECTION_LTR
//            lySubscribeButton.lottieBtnContinue.scaleX = if (isRTLDirectionFromLocale) -1f else 1f
//        }
        mBinding.txtTermsOfUse.isSelected = true
        mBinding.txtPrivacyPolicy.isSelected = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setCloseIconPosition(mBinding.clMain, mBinding.ivClose, IconPosition.LEFT_TO_RIGHT)
        }

        setScreenUI()
        initFeatures()
    }

    override fun initViewListener() {
        super.initViewListener()
        with(mBinding) {
            setClickListener(
                ivClose,
                txtTermsOfUse,
                txtPrivacyPolicy,
                lySubscribeButton.root,
                lyToggle.root
            )
        }
        if (mPlanList.isEmpty()) {
            CoroutineScope(Dispatchers.IO).launch { setAndCreatePlanViews() }
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.iv_close -> customOnBackPressed()
            R.id.ly_toggle -> handleToggleClick()
            R.id.txt_terms_of_use -> {
                if (SUBSCRIPTION_TERMS_OF_USE.isNotEmpty()) {
                    Launcher.openAnyLink(context = mActivity, uri = SUBSCRIPTION_TERMS_OF_USE)
                }
            }

            R.id.txt_privacy_policy -> {
                if (SUBSCRIPTION_PRIVACY_POLICY.isNotEmpty()) {
                    Launcher.openPrivacyPolicy(
                        context = mActivity,
                        fLink = SUBSCRIPTION_PRIVACY_POLICY
                    )
                }
            }

            R.id.ly_subscribe_button -> handlePurchaseClick()
        }
    }

    private suspend fun setAndCreatePlanViews() {

        val planTypesToShow = mSkuType
        val tempPlanData = mutableListOf<Pair<Int, ProductInfo>>()


        withContext(Dispatchers.IO) {
            planTypesToShow.forEach { planType ->
                getPlanFromIndex(planType)?.let { productInfo ->
                    tempPlanData.add(Pair(planType, productInfo))
                }
            }
        }


        withContext(Dispatchers.Main) {
            mPlanList.clear()
            mBinding.planContainer.removeAllViews()


            tempPlanData.forEachIndexed { index, (planType, productInfo) ->
                val planLayoutBinding = LayoutDynamicPlanItemBinding.inflate(
                    layoutInflater,
                    mBinding.planContainer,
                    false
                )


                planLayoutBinding.root.setOnClickListener {

                    if (mSelectedPlanIndex == index) return@setOnClickListener

                    updateSelection(index)
//                    val isToggleOn = mBinding.lyToggle.btnToggle.isSelected
//
//                    if (isToggleOn) {
//
//
//                        val clickedPlanHasFreeTrial =
//                            productInfo.planOfferType == PlanOfferType.FREE_TRIAL
//                        if (clickedPlanHasFreeTrial) {
//
//                            updateSelection(index)
//                        } else {
//
//                        }
//                    } else {
//
//
//                        updateSelection(index)
//                    }
                }


                populatePlanView(planLayoutBinding, productInfo, planType, index)


                mBinding.planContainer.addView(planLayoutBinding.root)


                mPlanList.add(
                    PlanUIData(
                        productInfo,
                        planType,
                        planLayoutBinding.root,
                        TwoPlanMainItemBinding.bind(planLayoutBinding.lyPlanMain.root),
                        TwoPlanTagItemBinding.bind(planLayoutBinding.lyPlanTag.root)
                    )
                )
            }


            val hasAnyFreeTrial =
                mPlanList.any { it.productInfo.planOfferType == PlanOfferType.FREE_TRIAL }
            mBinding.lyToggle.root.visibility = if (hasAnyFreeTrial) View.VISIBLE else View.GONE


//            val initialIndex = if (mDefaultSelectionIndex in mPlanList.indices) {
//                mDefaultSelectionIndex
//            } else {
//                0
//            }
//            updateSelection(initialIndex)


            val initialIndexToSelect =
                mPlanList.indexOfFirst { it.planType == mDefaultSelectionPlanType }

            val finalInitialIndex = if (initialIndexToSelect != -1) {
                initialIndexToSelect
            } else if (mPlanList.isNotEmpty()) {
                0
            } else {
                -1
            }

            if (finalInitialIndex != -1) {
                updateSelection(finalInitialIndex)
            }

        }
    }

    private fun populatePlanView(
        binding: LayoutDynamicPlanItemBinding,
        productInfo: ProductInfo,
        planType: Int,
        index: Int
    ) {
        val main = TwoPlanMainItemBinding.bind(binding.lyPlanMain.root)
        val tag = TwoPlanTagItemBinding.bind(binding.lyPlanTag.root)


        main.ivBackground.backgroundTintList = mPlanBgSelector
        main.ivForeground.backgroundTintList = mPlanForgroundSelector
        tag.ivBackground.imageTintList = mPlanTagSelector


        if (index == 0) {
            tag.root.visible
            tag.txtTag.text =
                getLocalizedString<String>(context = mActivity, resourceId = R.string.best_offer)
        } else {
            tag.root.gone
        }


        when (planType) {
            0 -> {
                main.txtPlanTitle.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.lifetime_access
                )

                main.layReference.gone
            }

            1 -> {
                main.txtPlanTitle.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.yearly_access
                )

                ProductPurchaseHelper.getWeeklyProductInfo?.let { weeklyInfo ->
                    ProductPurchaseHelper.getWeekBaseYearlyDiscount(
                        weekPrice = weeklyInfo.formattedPrice,
                        yearPrice = productInfo.formattedPrice
                    ) { _, discountPrice ->
                        main.txtPlanReferencePrice.text = discountPrice.removeTrailingZeros
                        main.txtPerWeek.text = getLocalizedString<String>(
                            context = mActivity,
                            resourceId = R.string.per_week
                        )
                        main.layReference.visible
                    }
                } ?: main.layReference.gone
            }

            2 -> {
                main.txtPlanTitle.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.monthly_access
                )

                ProductPurchaseHelper.getWeeklyProductInfo?.let { weeklyInfo ->
                    ProductPurchaseHelper.getWeekBaseMonthlyDiscount(
                        weekPrice = weeklyInfo.formattedPrice,
                        monthPrice = productInfo.formattedPrice
                    ) { _, discountPrice ->
                        main.txtPlanReferencePrice.text = discountPrice.removeTrailingZeros
                        main.txtPerWeek.text = getLocalizedString<String>(
                            context = mActivity,
                            resourceId = R.string.per_week
                        )
                        main.layReference.visible
                    }
                } ?: main.layReference.gone
            }

            3 -> {
                main.txtPlanTitle.text = getLocalizedString<String>(
                    context = mActivity,
                    resourceId = R.string.weekly_access
                )

                main.txtPlanReferencePrice.text = productInfo.formattedPrice.removeTrailingZeros
                main.txtPerWeek.text =
                    getLocalizedString<String>(context = mActivity, resourceId = R.string.per_week)
                main.layReference.visible
            }
        }
        updatePlanPrice(productInfo, main, planType)
    }

    private fun updateSelection(newIndex: Int) {
        if (newIndex !in mPlanList.indices) {
            logE(TAG, "updateSelection: Invalid index $newIndex")
            return
        }
        mSelectedPlanIndex = newIndex
        mPlanList.forEachIndexed { index, planData ->
            planData.planView.isSelected = (index == mSelectedPlanIndex)
        }
        val selectedPlanData = mPlanList[mSelectedPlanIndex]
        mBinding.lyToggle.btnToggle.isSelected =
            (selectedPlanData.productInfo.planOfferType == PlanOfferType.FREE_TRIAL)
        updatePurchaseButtonAndFooter(selectedPlanData.productInfo)
    }

    private fun updatePurchaseButtonAndFooter(productInfo: ProductInfo) {
        with(mBinding) {
            when (productInfo.planOfferType) {
                PlanOfferType.FREE_TRIAL -> {
                    txtFreeThenPerPeriod.apply {
                        text = getLocalizedString(
                            context = mActivity,
                            resourceId = R.string.free_then_per_period,
                            formatArgs = arrayOf(
                                productInfo.actualFreeTrialPeriod.getFullBillingPeriod(mActivity),
                                productInfo.formattedPrice.removeTrailingZeros,
                                productInfo.actualBillingPeriod.getBillingPeriodName(mActivity)
                            )
                        ).lowercase()
                        visible
                    }
                    setDefaultButtonText(
                        buttonTextIndex = mPurchaseButtonTextIndex,
                        period = productInfo.actualFreeTrialPeriod.getFullBillingPeriod(mActivity),
                        price = "${productInfo.priceCurrencySymbol}0"
                    )
                }

                else -> {
                    txtFreeThenPerPeriod.invisible
                    setDefaultButtonText(buttonTextIndex = 0, period = "", price = "")
                }
            }
        }
    }

    private fun handleToggleClick() {
        val isTurningOn = !mBinding.lyToggle.btnToggle.isSelected
        val indexToSelect = if (isTurningOn) {
            mPlanList.indexOfFirst { it.productInfo.planOfferType == PlanOfferType.FREE_TRIAL }
        } else {
            mPlanList.indexOfFirst { it.productInfo.planOfferType != PlanOfferType.FREE_TRIAL }
        }
        if (indexToSelect != -1) updateSelection(indexToSelect)
    }

    private fun handlePurchaseClick() {
        if (mSelectedPlanIndex != -1) {
            val selectedSKU = mPlanList[mSelectedPlanIndex].productInfo.id
            mBinding.lySubscribeButton.root.disable
            if (IS_ENABLE_TEST_PURCHASE) {
                ProductPurchaseHelper.fireTestingPurchase(context = mActivity)
            } else {
                ProductPurchaseHelper.purchase(activity = mActivity, productId = selectedSKU)
            }
        }
    }

    private fun fireDynamicPurchaseEvent(productInfo: ProductInfo, planType: Int) {
        val eventBundle = getEventParamBundle(productInfo)
        val isFreeTrial = productInfo.planOfferType == PlanOfferType.FREE_TRIAL
        val eventType = when (planType) {
            0 -> if (isFreeTrial) SubscriptionEventType.LIFE_TIME_FREE_TRAIL_PURCHASE(eventBundle) else SubscriptionEventType.LIFE_TIME_PURCHASE(
                eventBundle
            )

            1 -> if (isFreeTrial) SubscriptionEventType.YEARLY_FREE_TRAIL_SUBSCRIBE(eventBundle) else SubscriptionEventType.YEARLY_SUBSCRIBE(
                eventBundle
            )

            2 -> if (isFreeTrial) SubscriptionEventType.MONTHLY_FREE_TRAIL_SUBSCRIBE(eventBundle) else SubscriptionEventType.MONTHLY_SUBSCRIBE(
                eventBundle
            )

            3 -> if (isFreeTrial) SubscriptionEventType.WEEKLY_FREE_TRAIL_SUBSCRIBE(eventBundle) else SubscriptionEventType.WEEKLY_SUBSCRIBE(
                eventBundle
            )

            else -> SubscriptionEventType.YEARLY_SUBSCRIBE(eventBundle) // Fallback
        }
        fireSubscriptionEvent(fEventType = eventType)
    }

    private fun initFeatures() {
        listOfFeatures.forEachIndexed { index, string ->
            when (index) {
                0 -> mBinding.txtFeature1.text = string
                1 -> mBinding.txtFeature2.text = string
                2 -> mBinding.txtFeature3.text = string
                3 -> mBinding.txtFeature4.text = string
                4 -> mBinding.txtFeature5.text = string
                5 -> mBinding.txtFeature6.text = string
            }
        }
    }

    private fun setScreenUI() {
        with(mBinding) {
            lySubscribeButton.txtBtnContinue.apply {
                isAllCaps = true
                text = StringBuilder().append(
                    getLocalizedString<String>(
                        context = mActivity,
                        resourceId = R.string.start_my_free_trial
                    )
                ).append("0")
            }

            lySubscribeButton.txtBtnContinue.post {
                lySubscribeButton.btnContinue.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        lySubscribeButton.btnContinue.viewTreeObserver.removeOnGlobalLayoutListener(
                            this
                        )

                        val measuredHeight = lySubscribeButton.btnContinue.height
                        val targetParams = lySubscribeButton.btnContinue.layoutParams
                        targetParams.height = measuredHeight
                        lySubscribeButton.btnContinue.layoutParams = targetParams
                    }
                })
            }

            txtUnlockAllFeatures.text =
                getLocalizedString<String>(context = mActivity, resourceId = R.string.best_offer)
            txtChooseYourPlan.text = getLocalizedString<String>(
                context = mActivity,
                resourceId = R.string.choose_your_plan
            )
            textUnloadAll.text = getLocalizedString<String>(
                context = mActivity,
                resourceId = R.string.unlock_all_features
            )
            txtFeatures.text =
                getLocalizedString<String>(context = mActivity, resourceId = R.string.features)
            txtBasic.text =
                getLocalizedString<String>(context = mActivity, resourceId = R.string.basic)
            txtPremium.text = getLocalizedString<String>(
                context = mActivity,
                resourceId = R.string.premium_two_plan
            )
            lyToggle.txtPlanTitle.text = getLocalizedString<String>(
                context = mActivity,
                resourceId = R.string.free_trial_enable
            )
            txtPayNothingNow.text = getLocalizedString<String>(
                context = mActivity,
                resourceId = R.string.pay_nothing_now
            )
            lySecureWithPlayStore.txtSecureWithPlayStore.text = getLocalizedString<String>(
                context = mActivity,
                resourceId = R.string.cancel_anytime_secure_with_play_store
            )
            txtTermsOfUse.text =
                getLocalizedString<String>(context = mActivity, resourceId = R.string.terms_of_use)
            txtPrivacyPolicy.text = getLocalizedString<String>(
                context = mActivity,
                resourceId = R.string.privacy_policy
            )

        }
    }

    private fun setDefaultButtonText(buttonTextIndex: Int, period: String, price: String) {
        mBinding.lySubscribeButton.txtBtnContinue.apply {
            text = getLocalizedString(
                context = mActivity,
                resourceId = when (buttonTextIndex) {
                    2 -> R.string.try_period_for_price
                    1 -> R.string.start_my_free_trial
                    else -> R.string.continue_
                },
                formatArgs = arrayOf(period.lowercase(), price)
            )
            isAllCaps = (buttonTextIndex != 2)
        }
    }

    private fun updatePlanPrice(
        productInfo: ProductInfo?,
        lyPlan: TwoPlanMainItemBinding,
        currentPlanIndex: Int
    ) {
        productInfo ?: return
        when (productInfo.planOfferType) {
            PlanOfferType.FREE_TRIAL -> {
                lyPlan.txtPlanPrice.apply {
                    val trialPeriod =
                        productInfo.actualFreeTrialPeriod.getFullBillingPeriod(mActivity)

                    val fullText = getLocalizedString<String>(
                        context = mActivity,
                        resourceId = R.string.free_day_trial,
                        formatArgs = arrayOf(trialPeriod)
                    )

                    setTrialPeriodStyle(
                        trialPeriod,
                        fullText,
                        R.color.default_two_plan_selected_sku_border_color,
                        14,
                        true
                    )
                    visible
                }
            }

            else -> {
                lyPlan.txtPlanPrice.apply {
                    val price = productInfo.formattedPrice.removeTrailingZeros
                    val period = getLocalizedString<String>(
                        context = mActivity,
                        resourceId = when (currentPlanIndex) {
                            0 -> R.string.lifetime; 1 -> R.string.yearly; 2 -> R.string.monthly; 3 -> R.string.weekly; else -> R.string.weekly
                        }
                    )
                    val fullText = getLocalizedString<String>(
                        context = mActivity,
                        resourceId = R.string.price_for_period_use,
                        formatArgs = arrayOf(price, period)
                    ).lowercase()

                    setPriceStyle(
                        price.lowercase(),
                        fullText,
                        R.color.two_plan_subscription_button_color,
                        14,
                        true
                    )
                    visible
                }
            }
        }
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
        return !isUserPurchaseAnyPlan && isOnline && !isFromTimeLine && mVasuSubscriptionRemoteConfigModel.isShowReviewDialog && !AdsManager(
            mActivity
        ).isReviewDialogOpened
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
            if (isAppForeground && needToShowBackAd()) {
                onBackShowInterAd.invoke(mActivity) { directBack() }
            } else {
                directBack()
            }
            isSystemBackButtonPressed = false
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onScreenFinishing() {
        super.onScreenFinishing(); onScreenFinish.invoke(isUserPurchaseAnyPlan)
    }
}
