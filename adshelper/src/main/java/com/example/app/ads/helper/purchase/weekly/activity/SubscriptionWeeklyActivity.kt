package com.example.app.ads.helper.purchase.weekly.activity

import android.app.Activity
import android.app.ActivityOptions
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.BaseActivity
import com.example.app.ads.helper.base.BaseBindingActivity
import com.example.app.ads.helper.base.utils.disable
import com.example.app.ads.helper.base.utils.enable
import com.example.app.ads.helper.base.utils.gone
import com.example.app.ads.helper.base.utils.isTiramisuPlus
import com.example.app.ads.helper.base.utils.visible
import com.example.app.ads.helper.databinding.ActivitySubscriptionWeeklyBinding
import com.example.app.ads.helper.purchase.IS_ENABLE_TEST_PURCHASE
import com.example.app.ads.helper.purchase.IS_FROM_SPLASH
import com.example.app.ads.helper.purchase.SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN
import com.example.app.ads.helper.purchase.SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN_OPEN_AFTER_SPLASH
import com.example.app.ads.helper.purchase.SUBSCRIPTION_DATA_LANGUAGE_CODE
import com.example.app.ads.helper.purchase.fireSubscriptionEvent
import com.example.app.ads.helper.purchase.product.AdsManager
import com.example.app.ads.helper.purchase.product.PlanOfferType
import com.example.app.ads.helper.purchase.product.ProductInfo
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper
import com.example.app.ads.helper.purchase.utils.SubscriptionEventType
import com.example.app.ads.helper.purchase.utils.getEventParamBundle
import com.example.app.ads.helper.purchase.weekly.utill.widget.SubscriptionButtonBouncing
import com.example.app.ads.helper.purchase.weekly.utill.WeeklyPlanScreenDataModel
import com.example.app.ads.helper.purchase.weekly.utill.WeeklyPlanUserItem
import com.example.app.ads.helper.remoteconfig.mVasuSubscriptionRemoteConfigModel
import com.example.app.ads.helper.utils.IconPosition
import com.example.app.ads.helper.utils.getFormattedTwoLineString
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
import java.util.Timer
import java.util.TimerTask


internal class SubscriptionWeeklyActivity :
    BaseBindingActivity<ActivitySubscriptionWeeklyBinding>() {


    override fun getActivityContext(): BaseActivity = this@SubscriptionWeeklyActivity

    private var isWeeklyPrizeSated: Boolean = false
    private var isUserPurchaseAnyPlan: Boolean = false
    private var mWeeklyPlanProductInfo: ProductInfo? = null

    var from = ""
    private val isFromTimeLine: Boolean
        get() = intent?.getBooleanExtra("isFromTimeLine", false) ?: false

    private val listOfBoxItem: ArrayList<WeeklyPlanUserItem>
        get() = screenDataModel?.listOfPoints.takeIf { !it.isNullOrEmpty() } ?: arrayListOf(
            WeeklyPlanUserItem(itemName = R.string.rating_user),
            WeeklyPlanUserItem(itemName = R.string.rating_user),
            WeeklyPlanUserItem(itemName = R.string.rating_user),
            WeeklyPlanUserItem(itemName = R.string.rating_user),
            WeeklyPlanUserItem(itemName = R.string.rating_user),
            WeeklyPlanUserItem(itemName = R.string.rating_user)
        )

    private val backgroundDrawable: Int?
        get() = screenDataModel?.backgroundDrawable

    private val weeklyScreenCloseIconTime: Long?
        get() = screenDataModel?.weeklyScreenCloseIconTime

    companion object {
        private var screenDataModel: WeeklyPlanScreenDataModel? = null
        private var onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit = {}
        private var reviewDialogData: Pair<String, String> = Pair("", "")
        private var onBackShowInterAd: (fContext: Activity, () -> Unit) -> Unit =
            { _, next -> next() }

        internal fun launchScreen(
            fActivity: Activity,
            isFromTimeLine: Boolean,
            screenDataModel: WeeklyPlanScreenDataModel,
            reviewDialogData: Pair<String, String>,
            onBackShowInterAd: (fContext: Activity, () -> Unit) -> Unit = { _, next -> next() },
            onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit,
        ) {
            Companion.screenDataModel = screenDataModel
            Companion.onScreenFinish = onScreenFinish
            Companion.reviewDialogData = reviewDialogData
            Companion.onBackShowInterAd = onBackShowInterAd

            val lIntent = Intent(fActivity, SubscriptionWeeklyActivity::class.java)
            lIntent.putExtra("isFromTimeLine", isFromTimeLine)

            @AnimatorRes @AnimRes val lEnterAnimId: Int = android.R.anim.fade_in
            @AnimatorRes @AnimRes val lExitAnimId: Int = android.R.anim.fade_out

            fActivity.runOnUiThread {
                if (isTiramisuPlus) {
                    val options =
                        ActivityOptions.makeCustomAnimation(fActivity, lEnterAnimId, lExitAnimId)
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

    override fun setParamBeforeLayoutInit() {
        super.setParamBeforeLayoutInit()
        setEdgeToEdgeLayout()
    }

    override fun initView() {
        super.initView()
        mBinding.apply {
            tvTitle.text = getFormattedTwoLineString(context = mActivity, resourceId = R.string.try_free_nwithout_ads)
            tvFreeTrial.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.after_free_trial_period_s_per_week)
            tvFreeTrialPeriod.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.s_free_trial)
            tvNoPaymentRequired.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.no_payment_required_now)
            tvStart.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.label_start)
            tvPlaySetting.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.desc_google_play_settings)
        }

        fireSubscriptionEvent(fEventType = SubscriptionEventType.WEEKLY_CLICK_OPEN)
        val job: Job = setBillingListener(fWhere = "initView")
        runBlocking {
            job.join()
            CoroutineScope(Dispatchers.IO).launch {
                ProductPurchaseHelper.initBillingClient(fContext = mActivity)
            }
        }

        Glide.with(this)
            .load(backgroundDrawable?: R.color.NewLightColor)
            .into(mBinding.ivBgMain)

        Log.e(TAG, "initView:  backgroundDrawable $backgroundDrawable screenDataModel ==== $screenDataModel  listofPoints ==== $listOfBoxItem")


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setCloseIconPosition(mBinding.clMain, mBinding.ivSubClose, IconPosition.LEFT_TO_RIGHT)
        }
        setCloseButton()


        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            private var isFirstTime = true
            override fun run() {
                if (!isFirstTime) {
                    setBouncingAnimation()
                }
                handler.postDelayed(this, 3000)
                isFirstTime = false
            }
        }
        handler.post(runnable)


        with(mBinding) {
            tvFeature1.isSelected = true
            tvFeature2.isSelected = true
            tvFeature3.isSelected = true
            tvFeature4.isSelected = true
            tvFeature5.isSelected = true
            tvFeature6.isSelected = true
            tvFreeTrialPeriod.isSelected = true

        }

        setButtonAnimation()

        mBinding.tvPlaySetting.setClickableText(
            getLocalizedString<String>(context = mActivity, resourceId = R.string.desc_google_play_settings),
            getLocalizedString<String>(context = mActivity, resourceId = R.string.google_play_settings),
            "#90BEFF".toColorInt()
        ) {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "https://play.google.com/store/account/subscriptions".toUri()
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                Log.e("TAG", anfe.toString())
            }
        }


        initAllPoints()

    }

    private fun initAllPoints() {

        with(mBinding) {

            tvFeature1.text = getLocalizedString<String>(
                context = tvFeature1.context,
                resourceId = listOfBoxItem[0].itemName,
            )
            tvFeature2 . text = getLocalizedString < String >(
                context = tvFeature1.context,
                resourceId = listOfBoxItem[1].itemName,
            )
            tvFeature3 . text = getLocalizedString < String >(
                context = tvFeature1.context,
                resourceId = listOfBoxItem[2].itemName,
            )
            tvFeature4 . text = getLocalizedString < String >(
                context = tvFeature1.context,
                resourceId = listOfBoxItem[3].itemName,
            )
            tvFeature5 . text = getLocalizedString < String >(
                context = tvFeature1.context,
                resourceId = listOfBoxItem[4].itemName,
            )
            tvFeature6 . text = getLocalizedString < String >(
                context = tvFeature1.context,
                resourceId = listOfBoxItem[5].itemName,
            )
        }

    }


    override fun setBinding() = ActivitySubscriptionWeeklyBinding.inflate(layoutInflater)


    private fun setButtonAnimation() {
        val handler = Handler(Looper.getMainLooper())

        val runnable = object : Runnable {
            private var isFirstTime = true
            override fun run() {
                if (!isFirstTime) {
                    val myAnim =
                        AnimationUtils.loadAnimation(
                            this@SubscriptionWeeklyActivity,
                            R.anim.zoomin_subscription
                        )
                    val interpolator = SubscriptionButtonBouncing(0.15, 15.0)
                    myAnim.interpolator = interpolator
                    mBinding.flStart.startAnimation(myAnim)

                    myAnim.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}
                        override fun onAnimationRepeat(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) {
                        }
                    })
                }
                handler.postDelayed(this, 3000)
                isFirstTime = false
            }
        }
        handler.post(runnable)
    }

    fun TextView.setClickableText(
        text: String,
        clickableText: String,
        @ColorInt clickableColor: Int,
        clickListener: () -> Unit
    ) {

        val spannableString = SpannableString(text)

        val startingPosition: Int = text.indexOf(clickableText)

        if (startingPosition > -1) {
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    clickListener()
                }

                override fun updateDrawState(textPaint: TextPaint) {
                    super.updateDrawState(textPaint)
                    textPaint.isUnderlineText = true
                }
            }

            val endingPosition: Int = startingPosition + clickableText.length
            spannableString.setSpan(
                clickableSpan, startingPosition,
                endingPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableString.setSpan(
                ForegroundColorSpan(clickableColor), startingPosition,
                endingPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }

        setText(spannableString)
    }

    override fun onScreenFinishing() {
        super.onScreenFinishing()
        onScreenFinish.invoke(isUserPurchaseAnyPlan)
    }

    override fun needToShowBackAd(): Boolean {
        var isShowAd = false
        if (!isFromTimeLine) {
            if (mBinding.ivSubClose.isPressed || isSystemBackButtonPressed || isFromReviewDialog) {
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

        return !isUserPurchaseAnyPlan && isOnline /*&& (!isFromTimeLine)*/ && mVasuSubscriptionRemoteConfigModel.isShowReviewDialog &&  (!AdsManager(context = mActivity).isReviewDialogOpened)
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

    private fun setBouncingAnimation() {
        val myAnim = AnimationUtils.loadAnimation(this, R.anim.zoomin_subscription)
        val interpolator = SubscriptionButtonBouncing(0.15, 15.0)
        myAnim.interpolator = interpolator

        myAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
//                mBinding.buttonDone.startAnimation(myAnim)
            }
        })
    }

    override fun initViewListener() {
        super.initViewListener()
        with(mBinding) {
            setClickListener(
                ivSubClose,
                flStart
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            setProductData()
        }
    }

//    override fun customOnBackPressed() {
//        if (needToShowReviewDialog()) {
//            super.customOnBackPressed()
//        } else {
//            if (mBinding.ivSubClose.isPressed || isSystemBackButtonPressed || isFromReviewDialog) {
//                fireSubscriptionEvent(fEventType = SubscriptionEventType.WEEKLY_PLANS_SCREEN_CLOSE)
//            }
//            super.customOnBackPressed()
//            isSystemBackButtonPressed = false
//        }
//    }

    override fun customOnBackPressed() {
        if (needToShowReviewDialog()) {
            super.customOnBackPressed()
        } else {
            if (mBinding.ivSubClose.visibility != View.VISIBLE)
                return

            if (mBinding.ivSubClose.isPressed || isSystemBackButtonPressed || isFromReviewDialog) {
                fireSubscriptionEvent(fEventType = SubscriptionEventType.WEEKLY_PLANS_SCREEN_CLOSE)
            }

//            super.customOnBackPressed()
            if (isAppForeground && needToShowBackAd()) {
                logE("CRASH_CHECK_1", "8")
                onBackShowInterAd.invoke(mActivity) {
                    directBack()
                }

            } else {
                logE("CRASH_CHECK_1", "9")
                directBack()
            }
            isSystemBackButtonPressed = false
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        with(mBinding) {
            when (v) {
                ivSubClose -> customOnBackPressed()

                flStart -> {

                    logE(TAG, "onClick: ${mWeeklyPlanProductInfo?.id}")
                    if (mWeeklyPlanProductInfo?.id?.isNotEmpty() == true) {
                        flStart.disable
                        if (IS_ENABLE_TEST_PURCHASE) {
                            ProductPurchaseHelper.fireTestingPurchase(context = mActivity)
                        } else {
                            ProductPurchaseHelper.purchase(
                                activity = mActivity,
                                productId = mWeeklyPlanProductInfo?.id ?: ""
                            )
                        }
                    }
                }


                else -> {}
            }
        }
    }

//    private fun setWeeklyProductData(productInfo: ProductInfo) {
//        CoroutineScope(Dispatchers.Main).launch {
//            mActivity.runOnUiThread {
//                val offer = productInfo.planOfferType
//
//                if (offer != null) {
//
//                    when (productInfo.planOfferType) {
//
//                        PlanOfferType.FREE_TRIAL -> {
//
//                            Log.e("Subscription", "WEEK => OfferType.FreeTrial ====== $productInfo")
//
////                            val period =
////                                getBillingPeriod(offer.pricingPhases.first().billingPeriod.iso8601)
//
//                            mBinding.tvFreeTrial.visible
//                            mBinding.tvNoPaymentRequired.visible
//
////                            mBinding.tvFreeTrial.text = String.format(
////                                getString(R.string.after_free_trial_period_s_per_week),
////                                productInfo.formattedPrice
////                            )
//                            mBinding.tvFreeTrial.text = getLocalizedString(mActivity, resourceId = R.string.after_free_trial_period_s_per_week , formatArgs = arrayOf(productInfo.formattedPrice))
//
//
////                            mBinding.tvFreeTrialPeriod.text = String.format(
////                                getString(R.string.s_free_trial),
////                                productInfo.freeTrialPeriod
////                            )
//                            mBinding.tvFreeTrialPeriod.text = getLocalizedString(mActivity, resourceId = R.string.s_free_trial , formatArgs = arrayOf(productInfo.freeTrialPeriod))
//                        }
//
//                        PlanOfferType.INTRO_OFFER -> {
//
//                        }
//
//                        else -> {
//
//                            Log.e("Subscription", "WEEK => else")
//
//                            mBinding.tvFreeTrial.gone
//                            mBinding.tvNoPaymentRequired.gone
//
//                            val regularPrice = productInfo.formattedPrice
//
////                            mBinding.tvFreeTrialPeriod.text = String.format(
////                                getString(R.string.s_per_week),
////                                regularPrice
////                            )
//                            mBinding.tvFreeTrialPeriod.text = getLocalizedString(mActivity, resourceId = R.string.s_per_week , formatArgs = arrayOf(regularPrice))
//
//                        }
//                    }
//
//                }
//                isWeeklyPrizeSated = true
//            }
//        }
//    }

    private fun setWeeklyProductData(productInfo: ProductInfo) {
        CoroutineScope(Dispatchers.Main).launch {
            mActivity.runOnUiThread {
                val offer = productInfo.planOfferType

                if (offer != null) {

                    when (productInfo.planOfferType) {

                        PlanOfferType.FREE_TRIAL -> {

                            Log.e("Subscription", "WEEK => OfferType.FreeTrial ====== $productInfo")

//                            val period =
//                                getBillingPeriod(offer.pricingPhases.first().billingPeriod.iso8601)

                            mBinding.tvFreeTrial.visible
                            mBinding.tvNoPaymentRequired.visible

//                            mBinding.tvFreeTrial.text = String.format(
//                                getString(R.string.after_free_trial_period_s_per_week),
//                                productInfo.formattedPrice
//                            )

                            val subType: Int =
                                mVasuSubscriptionRemoteConfigModel.weeklyScreenPlanType
                            val stringResId = when (subType) {
                                0 -> R.string.after_free_trial_period_s_per_lifetime
                                1 -> R.string.after_free_trial_period_s_per_yearly
                                2 -> R.string.after_free_trial_period_s_per_monthly
                                3 -> R.string.after_free_trial_period_s_per_week
                                else -> R.string.after_free_trial_period_s_per_yearly // fallback
                            }
                            mBinding.tvFreeTrial.text = getLocalizedString(mActivity, resourceId = stringResId, formatArgs = arrayOf(productInfo.formattedPrice))


//                            mBinding.tvFreeTrialPeriod.text = String.format(
//                                getString(R.string.s_free_trial),
//                                productInfo.freeTrialPeriod
//                            )
                            mBinding.tvFreeTrialPeriod.text = getLocalizedString(mActivity, resourceId = R.string.s_free_trial , formatArgs = arrayOf(productInfo.freeTrialPeriod))
                        }

                        PlanOfferType.INTRO_OFFER -> {

                        }

                        else -> {

                            Log.e("Subscription", "WEEK => else")

                            mBinding.tvFreeTrial.gone
                            mBinding.tvNoPaymentRequired.gone

                            val regularPrice = productInfo.formattedPrice

//                            mBinding.tvFreeTrialPeriod.text = String.format(
//                                getString(R.string.s_per_week),
//                                regularPrice
//                            )
                            val subType: Int =
                                mVasuSubscriptionRemoteConfigModel.weeklyScreenPlanType
                            val stringResId = when (subType) {
                                0 -> R.string.s_per_lifetime
                                1 -> R.string.s_per_yearly
                                2 -> R.string.s_per_monthly
                                3 -> R.string.s_per_week
                                else -> R.string.s_per_yearly // fallback
                            }
                            mBinding.tvFreeTrialPeriod.text = getLocalizedString(mActivity, resourceId = stringResId , formatArgs = arrayOf(regularPrice))

                        }
                    }

                }
                isWeeklyPrizeSated = true
            }
        }
    }

//    private fun setProductData() {
//        CoroutineScope(Dispatchers.IO).launch {
//
//
//            ProductPurchaseHelper.getWeeklyProductInfo?.let { weeklyProductInfo ->
//                mWeeklyPlanProductInfo = weeklyProductInfo
//                setWeeklyProductData(productInfo = weeklyProductInfo)
//            }
//        }
//    }

    private fun setProductData() {
        CoroutineScope(Dispatchers.IO).launch {
            var subType: Int =
                mVasuSubscriptionRemoteConfigModel.weeklyScreenPlanType
            when (subType) {
                0-> {
                    ProductPurchaseHelper.getLifeTimeProductInfo?.let { weeklyProductInfo ->
                        mWeeklyPlanProductInfo = weeklyProductInfo
                        setWeeklyProductData(productInfo = weeklyProductInfo)
                    }
                }
                1 -> {
                    ProductPurchaseHelper.getYearlyProductInfo?.let { weeklyProductInfo ->
                        mWeeklyPlanProductInfo = weeklyProductInfo
                        setWeeklyProductData(productInfo = weeklyProductInfo)
                    }
                }

                2 -> {
                    ProductPurchaseHelper.getMonthlyProductInfo?.let { weeklyProductInfo ->
                        mWeeklyPlanProductInfo = weeklyProductInfo
                        setWeeklyProductData(productInfo = weeklyProductInfo)
                    }
                }

                3 -> {
                    ProductPurchaseHelper.getWeeklyProductInfo?.let { weeklyProductInfo ->
                        mWeeklyPlanProductInfo = weeklyProductInfo
                        setWeeklyProductData(productInfo = weeklyProductInfo)
                    }
                }

                else -> ProductPurchaseHelper.getYearlyProductInfo?.let { weeklyProductInfo ->
                    mWeeklyPlanProductInfo = weeklyProductInfo
                    setWeeklyProductData(productInfo = weeklyProductInfo)
                }
            }
        }
    }

    private fun setBillingListener(fWhere: String): Job {
        val job: Job = CoroutineScope(Dispatchers.IO).launch {
            logE(TAG, "$fWhere: Set Listener")
            ProductPurchaseHelper.setPurchaseListener(object :
                ProductPurchaseHelper.ProductPurchaseListener {
                override fun onBillingSetupFinished() {
                    if (!isWeeklyPrizeSated) {
                        ProductPurchaseHelper.initProductsKeys(fContext = mActivity) {
                            if (!isWeeklyPrizeSated) {
                                setProductData()
                            }
                        }
                    }
                }

                override fun onPurchasedSuccess() {
                    super.onPurchasedSuccess()

                    Log.e(TAG, "onPurchasedSuccess:  === $mActivity" )
                    CoroutineScope(Dispatchers.Main).launch {
                        mActivity.runOnUiThread {
                            val subType: Int = mVasuSubscriptionRemoteConfigModel.weeklyScreenPlanType
                            when (subType) {
                                0 -> {
                                    mWeeklyPlanProductInfo?.let { productInfo ->
                                        fireSubscriptionEvent(
                                            fEventType = SubscriptionEventType.LIFE_TIME_FREE_TRAIL_PURCHASE(
                                                paramBundle = getEventParamBundle(productInfo = productInfo)
                                            )
                                                .takeIf { productInfo.planOfferType == PlanOfferType.FREE_TRIAL }
                                                ?: SubscriptionEventType.LIFE_TIME_PURCHASE(
                                                    paramBundle = getEventParamBundle(
                                                        productInfo = productInfo
                                                    )
                                                )
                                        )
                                    }
                                }

                                1 -> {
                                    mWeeklyPlanProductInfo?.let { productInfo ->
                                        fireSubscriptionEvent(
                                            fEventType = SubscriptionEventType.YEARLY_FREE_TRAIL_SUBSCRIBE(
                                                paramBundle = getEventParamBundle(productInfo = productInfo)
                                            )
                                                .takeIf { productInfo.planOfferType == PlanOfferType.FREE_TRIAL }
                                                ?: SubscriptionEventType.YEARLY_SUBSCRIBE(
                                                    paramBundle = getEventParamBundle(
                                                        productInfo = productInfo
                                                    )
                                                )
                                        )
                                    }
                                }

                                2 -> {
                                    mWeeklyPlanProductInfo?.let { productInfo ->
                                        fireSubscriptionEvent(
                                            fEventType = SubscriptionEventType.MONTHLY_FREE_TRAIL_SUBSCRIBE(
                                                paramBundle = getEventParamBundle(productInfo = productInfo)
                                            )
                                                .takeIf { productInfo.planOfferType == PlanOfferType.FREE_TRIAL }
                                                ?: SubscriptionEventType.MONTHLY_SUBSCRIBE(
                                                    paramBundle = getEventParamBundle(
                                                        productInfo = productInfo
                                                    )
                                                )
                                        )
                                    }
                                }

                                3 -> {
                                    mWeeklyPlanProductInfo?.let { productInfo ->
                                        fireSubscriptionEvent(
                                            fEventType = SubscriptionEventType.WEEKLY_FREE_TRAIL_SUBSCRIBE(
                                                paramBundle = getEventParamBundle(productInfo = productInfo)
                                            )
                                                .takeIf { productInfo.planOfferType == PlanOfferType.FREE_TRIAL }
                                                ?: SubscriptionEventType.WEEKLY_SUBSCRIBE(
                                                    paramBundle = getEventParamBundle(
                                                        productInfo = productInfo
                                                    )
                                                )
                                        )
                                    }
                                }

                                else -> {
                                    mWeeklyPlanProductInfo?.let { productInfo ->
                                        fireSubscriptionEvent(
                                            fEventType = SubscriptionEventType.WEEKLY_FREE_TRAIL_SUBSCRIBE(
                                                paramBundle = getEventParamBundle(productInfo = productInfo)
                                            )
                                                .takeIf { productInfo.planOfferType == PlanOfferType.FREE_TRIAL }
                                                ?: SubscriptionEventType.WEEKLY_SUBSCRIBE(
                                                    paramBundle = getEventParamBundle(
                                                        productInfo = productInfo
                                                    )
                                                )
                                        )
                                    }
                                }
                            }

//                            mWeeklyPlanProductInfo?.let { productInfo ->
//                                fireSubscriptionEvent(
//                                    fEventType = SubscriptionEventType.WEEKLY_FREE_TRAIL_SUBSCRIBE(
//                                        paramBundle = getEventParamBundle(productInfo = productInfo)
//                                    )
//                                        .takeIf { productInfo.planOfferType == PlanOfferType.FREE_TRIAL }
//                                        ?: SubscriptionEventType.WEEKLY_SUBSCRIBE(
//                                            paramBundle = getEventParamBundle(
//                                                productInfo = productInfo
//                                            )
//                                        )
//                                )
//                            }


                            isUserPurchaseAnyPlan = true
                            mBinding.ivSubClose.performClick()
                        }
                    }
                }
            })
        }
        return job
    }

    override fun onResume() {
        super.onResume()
        mBinding.flStart.enable
        if (isOnPause) {
            isOnPause = false
            setBillingListener(fWhere = "onResume")
        }

    }

    private fun setCloseButton() {
        Timer().schedule(
            object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        Log.e("PremiumActivity", "initView: SplashDelay End")
                        mBinding.ivSubClose.visible
                        mBinding.ivSubClose.isClickable = true
                    }
                }
            },
            mVasuSubscriptionRemoteConfigModel.subscriptionCloseIconShowTime
        )
    }


}