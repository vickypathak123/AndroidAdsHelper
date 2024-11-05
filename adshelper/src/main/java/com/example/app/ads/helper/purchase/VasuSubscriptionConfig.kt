@file:Suppress("unused")

package com.example.app.ads.helper.purchase

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.utils.getColorRes
import com.example.app.ads.helper.base.utils.getColorStateRes
import com.example.app.ads.helper.notification.SUBSCRIPTION_NOTIFICATION_CHANNEL_ID
import com.example.app.ads.helper.notification.SUBSCRIPTION_NOTIFICATION_CHANNEL_NAME
import com.example.app.ads.helper.notification.SUBSCRIPTION_NOTIFICATION_ID
import com.example.app.ads.helper.purchase.fourplan.activity.FourPlanActivity
import com.example.app.ads.helper.purchase.fourplan.utils.FourPlanRattingItem
import com.example.app.ads.helper.purchase.fourplan.utils.FourPlanScreenDataModel
import com.example.app.ads.helper.purchase.fourplan.utils.FourPlanUserItem
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.checkIsAllPriceLoaded
import com.example.app.ads.helper.purchase.sixbox.activity.ViewAllPlansActivity
import com.example.app.ads.helper.purchase.sixbox.utils.BoxItem
import com.example.app.ads.helper.purchase.sixbox.utils.RattingItem
import com.example.app.ads.helper.purchase.sixbox.utils.SelectorColorItem
import com.example.app.ads.helper.purchase.sixbox.utils.SelectorDrawableItem
import com.example.app.ads.helper.purchase.sixbox.utils.ViewAllPlansScreenDataModel
import com.example.app.ads.helper.purchase.timeline.activity.TimeLineActivity
import com.example.app.ads.helper.purchase.timeline.utils.TimeLineScreenDataModel
import com.example.app.ads.helper.purchase.utils.MorePlanScreenType
import com.example.app.ads.helper.purchase.utils.SubscriptionEventType
import com.example.app.ads.helper.remoteconfig.mVasuSubscriptionRemoteConfigModel
import com.example.app.ads.helper.utils.clearAll
import com.example.app.ads.helper.utils.logE
import java.io.Serializable

object VasuSubscriptionConfig {

    /**
     * initialization of subscription screen ui data
     */
    @JvmStatic
    fun with(fActivity: Activity, fAppPackageName: String, fAppVersionName: String): ActivityData {
        return ActivityData(fActivity = fActivity,  fAppPackageName =  fAppPackageName, fAppVersionName = fAppVersionName)
    }

    class ActivityData(private val fActivity: Activity, private val  fAppPackageName: String, private val fAppVersionName: String) : Serializable {

        @Suppress("PropertyName")
        val TAG: String = "AdMob_${javaClass.simpleName}"

//        private val mContextRef: WeakReference<Activity> = WeakReference(fActivity)
//        private val mActivity: Activity get() = mContextRef.get() ?: fActivity

        private var mTimeLineScreenData: TimeLineScreenData = TimeLineScreenData(fActivity = fActivity)
        private var mViewAllPlansScreenData: ViewAllPlansScreenData = ViewAllPlansScreenData(fActivity = fActivity)
        private var mFourPlanScreenData: FourPlanScreenData = FourPlanScreenData(fActivity = fActivity)

        private var mNotificationData: NotificationData? = null

        private var mLanguageCode: String = "en"

        private var mTermsOfUse: String = ""
        private var mPrivacyPolicy: String = ""

        private var isEnableTestPurchase: Boolean = false


        /**
         * @param fCode it's refers to your app language code.
         */
        @JvmName("setAppLanguageCode")
        fun setAppLanguageCode(fCode: String) = this@ActivityData.apply {
            this.mLanguageCode = fCode
        }

        @JvmName("setTimeLineScreenData")
        fun setTimeLineScreenData(fTimeLineScreenData: TimeLineScreenData) = this@ActivityData.apply {
            this.mTimeLineScreenData = fTimeLineScreenData
        }

        @JvmName("setTimeLineScreenData")
        fun setTimeLineScreenData(action: (fTimeLineScreenData: TimeLineScreenData) -> Unit) = this@ActivityData.apply {
            action.invoke(this.mTimeLineScreenData)
        }

        @JvmName("setViewAllPlansScreenData")
        fun setViewAllPlansScreenData(fViewAllPlansScreenData: ViewAllPlansScreenData) = this@ActivityData.apply {
            this.mViewAllPlansScreenData = fViewAllPlansScreenData
        }

        @JvmName("setViewAllPlansScreenData")
        fun setViewAllPlansScreenData(action: (fViewAllPlansScreenData: ViewAllPlansScreenData) -> Unit) = this@ActivityData.apply {
            action.invoke(this.mViewAllPlansScreenData)
        }

        @JvmName("setFourPlanScreenData")
        fun setFourPlanScreenData(fFourPlanScreenData: FourPlanScreenData) = this@ActivityData.apply {
            this.mFourPlanScreenData = fFourPlanScreenData
        }

        @JvmName("setFourPlanScreenData")
        fun setFourPlanScreenData(action: (fFourPlanScreenData: FourPlanScreenData) -> Unit) = this@ActivityData.apply {
            action.invoke(this.mFourPlanScreenData)
        }

        /**
         * @param fLink it's refers to your app terms of use.
         */
        @JvmName("setTermsOfUse")
        fun setTermsOfUse(fLink: String) = this@ActivityData.apply {
            this.mTermsOfUse = fLink
        }

        /**
         * @param fLink it's refers to your app privacy policy.
         */
        @JvmName("setPrivacyPolicy")
        fun setPrivacyPolicy(fLink: String) = this@ActivityData.apply {
            this.mPrivacyPolicy = fLink
        }

        /**
         * enable test purchase [by Default value = false].
         *
         * call if you want to test your purchase code.
         */
        @JvmName("enableTestPurchase")
        fun enableTestPurchase(fIsEnable: Boolean) = this@ActivityData.apply {
            this.isEnableTestPurchase = fIsEnable
        }

        @JvmName("setNotificationData")
        fun setNotificationData(fNotificationData: NotificationData) = this@ActivityData.apply {
            this.mNotificationData = fNotificationData
        }

        /**
         * launch subscription screen
         *
         * @param fPlanScreenType it's refers to your app plan screen type.
         * @param isFromSplash it's refers to you need to launch from splash screen or not.
         * @param directShowMorePlanScreen it's refers to you need to launch directly more plan screen or not.
         * @param onSubscriptionEvent callback for subscription event.
         * @param onScreenFinish callback for screen finish. [@param isUserPurchaseAnyPlan true if user will purchase any plan]
         */
        fun launchScreen(
            fPlanScreenType: MorePlanScreenType = MorePlanScreenType.fromName(value = mVasuSubscriptionRemoteConfigModel.morePlanScreenType.takeIf { it.isNotEmpty() } ?: "four_plan_screen"),
            isFromSplash: Boolean = false,
            directShowMorePlanScreen: Boolean = false,
            onSubscriptionEvent: (eventType: SubscriptionEventType) -> Unit,
            onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit,
            onOpeningError: () -> Unit,
        ) {

            mNotificationData?.let { notificationData ->
                IS_ENABLE_TEST_PURCHASE = this.isEnableTestPurchase

                SUBSCRIPTION_DATA_LANGUAGE_CODE = mLanguageCode
                SUBSCRIPTION_TERMS_OF_USE = mTermsOfUse
                SUBSCRIPTION_PRIVACY_POLICY = mPrivacyPolicy
                triggerSubscriptionEvent = onSubscriptionEvent

                IS_FROM_SPLASH = isFromSplash
                SHOW_CLOSE_AD_FOR_TIME_LINE_SCREEN = mVasuSubscriptionRemoteConfigModel.initialSubscriptionTimeLineCloseAd
                SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN_OPEN_AFTER_SPLASH = mVasuSubscriptionRemoteConfigModel.initialSubscriptionMorePlanCloseAd
                SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN = mVasuSubscriptionRemoteConfigModel.inAppSubscriptionAdClose

                logE(TAG, "launchScreen: checkIsAllPriceLoaded::-> $checkIsAllPriceLoaded")
                if (checkIsAllPriceLoaded) {

                    val lScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit = { isUserPurchaseAnyPlan ->
                        IS_FROM_SPLASH = false
                        SHOW_CLOSE_AD_FOR_TIME_LINE_SCREEN = false
                        SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN_OPEN_AFTER_SPLASH = false
                        SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN = false
                        onScreenFinish.invoke(isUserPurchaseAnyPlan)
                    }

                    val reviewDialogData = Pair(fAppPackageName, fAppVersionName)

                    if (!directShowMorePlanScreen && ProductPurchaseHelper.isNeedToLaunchTimeLineScreen) {
                        launchTimeLineScreen(
                            fNotificationData = notificationData,
                            reviewDialogData = reviewDialogData,
                            onViewAllPlans = {
                                fireSubscriptionEvent(fEventType = SubscriptionEventType.VIEW_MORE_PLANS_CLICK)
                                launchMorePlanScreen(
                                    fType = fPlanScreenType,
                                    isFromTimeLine = true,
                                    reviewDialogData = reviewDialogData,
                                    onScreenFinish = { isUserPurchaseAnyPlan ->
                                        if (isUserPurchaseAnyPlan) {
                                            TimeLineActivity.onPurchaseFromMorePlanScreen.invoke()
                                        }
                                    }
                                )
                            },
                            onScreenFinish = lScreenFinish,
                        )
                    } else {
                        launchMorePlanScreen(
                            fType = fPlanScreenType,
                            reviewDialogData = reviewDialogData,
                            isFromTimeLine = false,
                            onScreenFinish = lScreenFinish
                        )
                    }
                } else {
                    onOpeningError.invoke()
                }
            } ?: kotlin.run {
                throw RuntimeException("Set Subscription Notification Data First")
            }

        }

        private fun launchTimeLineScreen(
            fNotificationData: NotificationData,
            reviewDialogData: Pair<String, String>,
            onViewAllPlans: () -> Unit,
            onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit
        ) {
            TimeLineActivity.launchScreen(
                fActivity = fActivity,
                screenDataModel = TimeLineScreenDataModel(
                    listOfInstantAccessHint = mTimeLineScreenData.listOfInstantAccessHint,
                    instantAccessLottieFileRawRes = mTimeLineScreenData.instantAccessLottieFileRawRes,
                    isWithInstantAccessAnimation = mTimeLineScreenData.isWithInstantAccessAnimation,
                    isWithSliderAnimation = mTimeLineScreenData.isWithSliderAnimation,
                    mainColor = mTimeLineScreenData.mainColor,
                    headerColor = mTimeLineScreenData.headerColor,
                    closeIconColor = mTimeLineScreenData.closeIconColor,
                    trackInactiveColor = mTimeLineScreenData.trackInactiveColor,
                    hintTextColor = mTimeLineScreenData.hintTextColor,
                    instantAccessHintTextColor = mTimeLineScreenData.instantAccessHintTextColor,
                    secureWithPlayStoreTextColor = mTimeLineScreenData.secureWithPlayStoreTextColor,
                    secureWithPlayStoreBackgroundColor = mTimeLineScreenData.secureWithPlayStoreBackgroundColor,
                    buttonContinueTextColor = mTimeLineScreenData.buttonContinueTextColor,
                ),
                notificationData = fNotificationData,
                onViewAllPlans = onViewAllPlans,
                onScreenFinish = onScreenFinish,
                reviewDialogData = reviewDialogData,
            )
        }

        private fun launchMorePlanScreen(
            fType: MorePlanScreenType,
            isFromTimeLine: Boolean,
            reviewDialogData: Pair<String, String>,
            onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit = {}
        ) {
            when (fType) {
                MorePlanScreenType.SIX_BOX_SCREEN -> {
                    launchViewAllPlansScreen(
                        isFromTimeLine = isFromTimeLine,
                        onScreenFinish = onScreenFinish,
                        reviewDialogData = reviewDialogData
                    )
                }

                MorePlanScreenType.FOUR_PLAN_SCREEN -> {
                    launchFourPlanScreen(
                        isFromTimeLine = isFromTimeLine,
                        onScreenFinish = onScreenFinish,
                        reviewDialogData = reviewDialogData,
                    )
                }
            }
        }

        private fun launchViewAllPlansScreen(
            isFromTimeLine: Boolean,
            reviewDialogData: Pair<String, String>,
            onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit = {}
        ) {
            ViewAllPlansActivity.launchScreen(
                fActivity = fActivity,
                isFromTimeLine = isFromTimeLine,
                screenDataModel = ViewAllPlansScreenDataModel(
//                    purchaseButtonTextIndex = mViewAllPlansScreenData.purchaseButtonTextIndex,
                    listOfBoxItem = mViewAllPlansScreenData.listOfBoxItem,
                    listOfRattingItem = mViewAllPlansScreenData.listOfRattingItem,
                    yearPlanIconSelector = mViewAllPlansScreenData.yearPlanIconSelector,
                    lifTimePlanIconSelector = mViewAllPlansScreenData.lifTimePlanIconSelector,
                    monthPlanIconSelector = mViewAllPlansScreenData.monthPlanIconSelector,
                    planSelector = mViewAllPlansScreenData.planSelector,
                    planHeaderSelector = mViewAllPlansScreenData.planHeaderSelector,
                    planBackgroundSelector = mViewAllPlansScreenData.planBackgroundSelector,
                    planHeaderTextColorSelector = mViewAllPlansScreenData.planHeaderTextColorSelector,
                    planTitleTextColorSelector = mViewAllPlansScreenData.planTitleTextColorSelector,
                    planTrialPeriodTextColorSelector = mViewAllPlansScreenData.planTrialPeriodTextColorSelector,
                    planPriceTextColorSelector = mViewAllPlansScreenData.planPriceTextColorSelector,


                    headerColor = mViewAllPlansScreenData.headerColor,
                    subHeaderColor = mViewAllPlansScreenData.subHeaderColor,
                    closeIconColor = mViewAllPlansScreenData.closeIconColor,
                    ratingColor = mViewAllPlansScreenData.ratingColor,
                    ratingPlaceHolderColor = mViewAllPlansScreenData.ratingPlaceHolderColor,
                    ratingIndicatorColor = mViewAllPlansScreenData.ratingIndicatorColor,
                    unselectedItemDataColor = mViewAllPlansScreenData.unselectedItemDataColor,
                    selectedItemDataColor = mViewAllPlansScreenData.selectedItemDataColor,
                    payNothingNowColor = mViewAllPlansScreenData.payNothingNowColor,
                    secureWithPlayStoreTextColor = mViewAllPlansScreenData.secureWithPlayStoreTextColor,
                    secureWithPlayStoreBackgroundColor = mViewAllPlansScreenData.secureWithPlayStoreBackgroundColor,
                    itemBoxBackgroundColor = mViewAllPlansScreenData.itemBoxBackgroundColor,
                    selectedSkuBackgroundColor = mViewAllPlansScreenData.selectedSkuBackgroundColor,
                    unselectedSkuBackgroundColor = mViewAllPlansScreenData.unselectedSkuBackgroundColor,
                ),
                onScreenFinish = onScreenFinish,
                reviewDialogData = reviewDialogData,
            )
        }

        private fun launchFourPlanScreen(
            isFromTimeLine: Boolean,
            reviewDialogData: Pair<String, String>,
            onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit = {}
        ) {
            FourPlanActivity.launchScreen(
                fActivity = fActivity,
                isFromTimeLine = isFromTimeLine,
                screenDataModel = FourPlanScreenDataModel(
//                    purchaseButtonTextIndex = mFourPlanScreenData.purchaseButtonTextIndex,
                    listOfBoxItem = mFourPlanScreenData.listOfBoxItem,
                    listOfRattingItem = mFourPlanScreenData.listOfRattingItem,
//                    lifeTimePlanDiscountPercentage = mFourPlanScreenData.lifeTimePlanDiscountPercentage,
                ),
                onScreenFinish = onScreenFinish,
                reviewDialogData = reviewDialogData,
            )
        }
    }

    class TimeLineScreenData(private val fActivity: Activity) : Serializable {

        @StringRes
        private var _listOfInstantAccessHint: ArrayList<Int> = ArrayList()

        @get:StringRes
        internal val listOfInstantAccessHint: ArrayList<Int> get() = _listOfInstantAccessHint

        @RawRes
        private var _instantAccessLottieFileRawRes: Int = R.raw.lottie_subscription_unlock_today_bg

        @get:RawRes
        internal val instantAccessLottieFileRawRes: Int get() = _instantAccessLottieFileRawRes

        private var _isWithInstantAccessAnimation: Boolean = false
        internal val isWithInstantAccessAnimation: Boolean get() = _isWithInstantAccessAnimation

        private var _isWithSliderAnimation: Boolean = false
        internal val isWithSliderAnimation: Boolean get() = _isWithSliderAnimation

        private var _mainColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_time_line_main_color))
        internal val mainColor: ColorStateList get() = _mainColor

        private var _headerColor: ColorStateList? = null
        internal val headerColor: ColorStateList get() = _headerColor ?: mainColor

        private var _closeIconColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_time_line_close_icon_color))
        internal val closeIconColor: ColorStateList get() = _closeIconColor

        private var _trackInactiveColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_time_line_track_inactive_color))
        internal val trackInactiveColor: ColorStateList get() = _trackInactiveColor

        private var _hintTextColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_time_line_hint_text_color))
        internal val hintTextColor: ColorStateList get() = _hintTextColor

        private var _instantAccessHintTextColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_time_line_instant_access_hint_text_color))
        internal val instantAccessHintTextColor: ColorStateList get() = _instantAccessHintTextColor

        private var _secureWithPlayStoreTextColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_secure_with_play_store_text_color))
        internal val secureWithPlayStoreTextColor: ColorStateList get() = _secureWithPlayStoreTextColor

        private var _secureWithPlayStoreBackgroundColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_secure_with_play_store_background_color))
        internal val secureWithPlayStoreBackgroundColor: ColorStateList get() = _secureWithPlayStoreBackgroundColor

        private var _buttonContinueTextColor: ColorStateList = ColorStateList.valueOf(Color.WHITE)
        internal val buttonContinueTextColor: ColorStateList get() = _buttonContinueTextColor

        @JvmName("setInstantAccessHint")
        fun setInstantAccessHint(@StringRes vararg listOfInstantAccessHint: Int) = this@TimeLineScreenData.apply {
            this._listOfInstantAccessHint.clearAll()
            this._listOfInstantAccessHint.addAll(listOfInstantAccessHint.toMutableList())
            return this
        }

        @JvmName("setInstantAccessLottieFile")
        fun setInstantAccessLottieFile(@RawRes fLottieFile: Int) = this@TimeLineScreenData.apply {
            this._instantAccessLottieFileRawRes = fLottieFile
            return this
        }

        @JvmName("setWithInstantAccessAnimation")
        fun setWithInstantAccessAnimation(isAnimated: Boolean) = this@TimeLineScreenData.apply {
            this._isWithInstantAccessAnimation = isAnimated
            return this
        }

        @JvmName("setWithSliderAnimation")
        fun setWithSliderAnimation(isAnimated: Boolean) = this@TimeLineScreenData.apply {
            this._isWithSliderAnimation = isAnimated
            return this
        }

        //<editor-fold desc="Main Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("mainColor")
        fun mainColor(fColors: ColorStateList?): TimeLineScreenData {
            fColors?.let { this._mainColor = it }
            return this
        }

        @JvmName("mainColor")
        fun mainColor(@ColorInt fColorInt: Int): TimeLineScreenData {
            return mainColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("mainColorResources")
        fun mainColorResources(@ColorRes id: Int): TimeLineScreenData {
            return mainColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("mainColorStateListResources")
        fun mainColorStateListResources(@ColorRes id: Int): TimeLineScreenData {
            fActivity.getColorStateRes(id)?.let { mainColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Header Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("headerColor")
        fun headerColor(fColors: ColorStateList?): TimeLineScreenData {
            fColors?.let { this._headerColor = it }
            return this
        }

        @JvmName("headerColor")
        fun headerColor(@ColorInt fColorInt: Int): TimeLineScreenData {
            return headerColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("headerColorResources")
        fun headerColorResources(@ColorRes id: Int): TimeLineScreenData {
            return headerColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("headerColorStateListResources")
        fun headerColorStateListResources(@ColorRes id: Int): TimeLineScreenData {
            fActivity.getColorStateRes(id)?.let { headerColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Close Icon Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("closeIconColor")
        fun closeIconColor(fColors: ColorStateList?) = this@TimeLineScreenData.apply {
            fColors?.let { this._closeIconColor = it }
        }

        @JvmName("closeIconColor")
        fun closeIconColor(@ColorInt fColorInt: Int) = this@TimeLineScreenData.apply {
            closeIconColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("closeIconColorResources")
        fun closeIconColorResources(@ColorRes id: Int) = this@TimeLineScreenData.apply {
            closeIconColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("closeIconColorStateListResources")
        fun closeIconColorStateListResources(@ColorRes id: Int) = this@TimeLineScreenData.apply {
            fActivity.getColorStateRes(id)?.let { closeIconColor(fColors = it) }
        }
        //</editor-fold>

        //<editor-fold desc="Track Inactive Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("trackInactiveColor")
        fun trackInactiveColor(fColors: ColorStateList?): TimeLineScreenData {
            fColors?.let { this._trackInactiveColor = it }
            return this
        }

        @JvmName("trackInactiveColor")
        fun trackInactiveColor(@ColorInt fColorInt: Int): TimeLineScreenData {
            return trackInactiveColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("trackInactiveColorResources")
        fun trackInactiveColorResources(@ColorRes id: Int): TimeLineScreenData {
            return trackInactiveColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("trackInactiveColorStateListResources")
        fun trackInactiveColorStateListResources(@ColorRes id: Int): TimeLineScreenData {
            fActivity.getColorStateRes(id)?.let { trackInactiveColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Hint Text Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("hintTextColor")
        fun hintTextColor(fColors: ColorStateList?): TimeLineScreenData {
            fColors?.let { this._hintTextColor = it }
            return this
        }

        @JvmName("hintTextColor")
        fun hintTextColor(@ColorInt fColorInt: Int): TimeLineScreenData {
            return hintTextColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("hintTextColorResources")
        fun hintTextColorResources(@ColorRes id: Int): TimeLineScreenData {
            return hintTextColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("hintTextColorStateListResources")
        fun hintTextColorStateListResources(@ColorRes id: Int): TimeLineScreenData {
            fActivity.getColorStateRes(id)?.let { hintTextColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Instant Access Hint Text Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("instantAccessHintTextColor")
        fun instantAccessHintTextColor(fColors: ColorStateList?): TimeLineScreenData {
            fColors?.let { this._instantAccessHintTextColor = it }
            return this
        }

        @JvmName("instantAccessHintTextColor")
        fun instantAccessHintTextColor(@ColorInt fColorInt: Int): TimeLineScreenData {
            return instantAccessHintTextColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("instantAccessHintTextColorResources")
        fun instantAccessHintTextColorResources(@ColorRes id: Int): TimeLineScreenData {
            return instantAccessHintTextColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("instantAccessHintTextColorStateListResources")
        fun instantAccessHintTextColorStateListResources(@ColorRes id: Int): TimeLineScreenData {
            fActivity.getColorStateRes(id)?.let { instantAccessHintTextColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Secure With Play Store Text Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("secureWithPlayStoreTextColor")
        fun secureWithPlayStoreTextColor(fColors: ColorStateList?) = this@TimeLineScreenData.apply {
            fColors?.let { this._secureWithPlayStoreTextColor = it }
        }

        @JvmName("secureWithPlayStoreTextColor")
        fun secureWithPlayStoreTextColor(@ColorInt fColorInt: Int) = this@TimeLineScreenData.apply {
            secureWithPlayStoreTextColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("secureWithPlayStoreTextColorResources")
        fun secureWithPlayStoreTextColorResources(@ColorRes id: Int) = this@TimeLineScreenData.apply {
            secureWithPlayStoreTextColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("secureWithPlayStoreTextColorStateListResources")
        fun secureWithPlayStoreTextColorStateListResources(@ColorRes id: Int) = this@TimeLineScreenData.apply {
            fActivity.getColorStateRes(id)?.let { secureWithPlayStoreTextColor(fColors = it) }
        }
        //</editor-fold>

        //<editor-fold desc="Secure With Play Store Background Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("secureWithPlayStoreBackgroundColor")
        fun secureWithPlayStoreBackgroundColor(fColors: ColorStateList?) = this@TimeLineScreenData.apply {
            fColors?.let { this._secureWithPlayStoreBackgroundColor = it }
        }

        @JvmName("secureWithPlayStoreBackgroundColor")
        fun secureWithPlayStoreBackgroundColor(@ColorInt fColorInt: Int) = this@TimeLineScreenData.apply {
            secureWithPlayStoreBackgroundColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("secureWithPlayStoreBackgroundColorResources")
        fun secureWithPlayStoreBackgroundColorResources(@ColorRes id: Int) = this@TimeLineScreenData.apply {
            secureWithPlayStoreBackgroundColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("secureWithPlayStoreBackgroundColorStateListResources")
        fun secureWithPlayStoreBackgroundColorStateListResources(@ColorRes id: Int) = this@TimeLineScreenData.apply {
            fActivity.getColorStateRes(id)?.let { secureWithPlayStoreBackgroundColor(fColors = it) }
        }
        //</editor-fold>

        //<editor-fold desc="Button Continue Text Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("buttonContinueTextColor")
        fun buttonContinueTextColor(fColors: ColorStateList?): TimeLineScreenData {
            fColors?.let { this._buttonContinueTextColor = it }
            return this
        }

        @JvmName("buttonContinueTextColor")
        fun buttonContinueTextColor(@ColorInt fColorInt: Int): TimeLineScreenData {
            return buttonContinueTextColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("buttonContinueTextColorResources")
        fun buttonContinueTextColorResources(@ColorRes id: Int): TimeLineScreenData {
            return buttonContinueTextColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("buttonContinueTextColorStateListResources")
        fun buttonContinueTextColorStateListResources(@ColorRes id: Int): TimeLineScreenData {
            fActivity.getColorStateRes(id)?.let { buttonContinueTextColor(fColors = it) }
            return this
        }
        //</editor-fold>

    }

    class ViewAllPlansScreenData(private val fActivity: Activity) : Serializable {
//        private var _purchaseButtonTextIndex: Int = 0
//        internal val purchaseButtonTextIndex: Int get() = _purchaseButtonTextIndex

        private var _listOfBoxItem: ArrayList<BoxItem> = ArrayList()
        internal val listOfBoxItem: ArrayList<BoxItem> get() = _listOfBoxItem

        private var _listOfRattingItem: ArrayList<RattingItem> = ArrayList()
        internal val listOfRattingItem: ArrayList<RattingItem> get() = _listOfRattingItem

        private var _yearPlanIconSelector: SelectorDrawableItem = SelectorDrawableItem()
        internal val yearPlanIconSelector: SelectorDrawableItem get() = _yearPlanIconSelector

        private var _lifTimePlanIconSelector: SelectorDrawableItem = SelectorDrawableItem()
        internal val lifTimePlanIconSelector: SelectorDrawableItem get() = _lifTimePlanIconSelector

        private var _monthPlanIconSelector: SelectorDrawableItem = SelectorDrawableItem()
        internal val monthPlanIconSelector: SelectorDrawableItem get() = _monthPlanIconSelector

        private var _planSelector: SelectorDrawableItem = SelectorDrawableItem()
        internal val planSelector: SelectorDrawableItem get() = _planSelector

        private var _planHeaderSelector: SelectorDrawableItem = SelectorDrawableItem()
        internal val planHeaderSelector: SelectorDrawableItem get() = _planHeaderSelector

        private var _planBackgroundSelector: SelectorDrawableItem = SelectorDrawableItem()
        internal val planBackgroundSelector: SelectorDrawableItem get() = _planBackgroundSelector

        private var _planHeaderTextColorSelector: SelectorColorItem = SelectorColorItem()
        internal val planHeaderTextColorSelector: SelectorColorItem get() = _planHeaderTextColorSelector

        private var _planTitleTextColorSelector: SelectorColorItem = SelectorColorItem()
        internal val planTitleTextColorSelector: SelectorColorItem get() = _planTitleTextColorSelector

        private var _planTrialPeriodTextColorSelector: SelectorColorItem = SelectorColorItem()
        internal val planTrialPeriodTextColorSelector: SelectorColorItem get() = _planTrialPeriodTextColorSelector

        private var _planPriceTextColorSelector: SelectorColorItem = SelectorColorItem()
        internal val planPriceTextColorSelector: SelectorColorItem get() = _planPriceTextColorSelector


        private var _headerColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_view_more_plan_header_color))
        internal val headerColor: ColorStateList get() = _headerColor

        private var _subHeaderColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_view_more_plan_sub_header_color))
        internal val subHeaderColor: ColorStateList get() = _subHeaderColor

        private var _closeIconColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_view_more_plan_close_icon_color))
        internal val closeIconColor: ColorStateList get() = _closeIconColor

        private var _ratingColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_view_more_plan_rating_color))
        internal val ratingColor: ColorStateList get() = _ratingColor

        private var _ratingPlaceHolderColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_view_more_plan_rating_place_holder_color))
        internal val ratingPlaceHolderColor: ColorStateList get() = _ratingPlaceHolderColor

        private var _ratingIndicatorColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_view_more_plan_rating_indicator_color))
        internal val ratingIndicatorColor: ColorStateList get() = _ratingIndicatorColor

        private var _unselectedItemDataColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_view_more_plan_unselected_item_data_color))
        internal val unselectedItemDataColor: ColorStateList get() = _unselectedItemDataColor

        private var _selectedItemDataColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_view_more_plan_selected_item_data_color))
        internal val selectedItemDataColor: ColorStateList get() = _selectedItemDataColor

        private var _payNothingNowColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_view_more_plan_pay_nothing_now_color))
        internal val payNothingNowColor: ColorStateList get() = _payNothingNowColor

        private var _secureWithPlayStoreTextColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_view_more_plan_secure_with_play_store_text_color))
        internal val secureWithPlayStoreTextColor: ColorStateList get() = _secureWithPlayStoreTextColor

        private var _secureWithPlayStoreBackgroundColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_secure_with_play_store_background_color))
        internal val secureWithPlayStoreBackgroundColor: ColorStateList get() = _secureWithPlayStoreBackgroundColor

        private var _itemBoxBackgroundColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_view_more_plan_item_box_background_color))
        internal val itemBoxBackgroundColor: ColorStateList get() = _itemBoxBackgroundColor

        private var _selectedSkuBackgroundColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_view_more_plan_selected_sku_background_color))
        internal val selectedSkuBackgroundColor: ColorStateList get() = _selectedSkuBackgroundColor

        private var _unselectedSkuBackgroundColor: ColorStateList = ColorStateList.valueOf(fActivity.getColorRes(R.color.default_view_more_plan_unselected_sku_background_color))
        internal val unselectedSkuBackgroundColor: ColorStateList get() = _unselectedSkuBackgroundColor


        //<editor-fold desc="Remove this fold After Color Work Done">
//        @JvmName("setPurchaseButtonTextIndex")
//        fun setPurchaseButtonTextIndex(index: Int) = this@ViewAllPlansScreenData.apply {
//            this._purchaseButtonTextIndex = index
//            return this
//        }

        @JvmName("setBoxItems")
        fun setBoxItems(vararg listOfBoxItem: BoxItem) = this@ViewAllPlansScreenData.apply {
            this._listOfBoxItem.clearAll()
            this._listOfBoxItem.addAll(listOfBoxItem)
            return this
        }

        @JvmName("setRattingItems")
        fun setRattingItems(vararg listOfRattingItem: RattingItem) = this@ViewAllPlansScreenData.apply {
            this._listOfRattingItem.clearAll()
            this._listOfRattingItem.addAll(listOfRattingItem)
            return this
        }

        @JvmName("setYearPlanIconSelector")
        fun setYearPlanIconSelector(fSelectorItem: SelectorDrawableItem) = this@ViewAllPlansScreenData.apply {
            this._yearPlanIconSelector = fSelectorItem
            return this
        }

        @JvmName("setLifTimePlanIconSelector")
        fun setLifTimePlanIconSelector(fSelectorItem: SelectorDrawableItem) = this@ViewAllPlansScreenData.apply {
            this._lifTimePlanIconSelector = fSelectorItem
            return this
        }

        @JvmName("setMonthPlanIconSelector")
        fun setMonthPlanIconSelector(fSelectorItem: SelectorDrawableItem) = this@ViewAllPlansScreenData.apply {
            this._monthPlanIconSelector = fSelectorItem
            return this
        }

        @JvmName("setPlanSelector")
        fun setPlanSelector(fSelectorItem: SelectorDrawableItem) = this@ViewAllPlansScreenData.apply {
            this._planSelector = fSelectorItem
            return this
        }

        @JvmName("setPlanHeaderSelector")
        fun setPlanHeaderSelector(fSelectorItem: SelectorDrawableItem) = this@ViewAllPlansScreenData.apply {
            this._planHeaderSelector = fSelectorItem
            return this
        }

        @JvmName("setPlanBackgroundSelector")
        fun setPlanBackgroundSelector(fSelectorItem: SelectorDrawableItem) = this@ViewAllPlansScreenData.apply {
            this._planBackgroundSelector = fSelectorItem
            return this
        }

        @JvmName("setPlanHeaderTextColorSelector")
        fun setPlanHeaderTextColorSelector(fSelectorItem: SelectorColorItem) = this@ViewAllPlansScreenData.apply {
            this._planHeaderTextColorSelector = fSelectorItem
            return this
        }

        @JvmName("setPlanTitleTextColorSelector")
        fun setPlanTitleTextColorSelector(fSelectorItem: SelectorColorItem) = this@ViewAllPlansScreenData.apply {
            this._planTitleTextColorSelector = fSelectorItem
            return this
        }

        @JvmName("setPlanTrialPeriodTextColorSelector")
        fun setPlanTrialPeriodTextColorSelector(fSelectorItem: SelectorColorItem) = this@ViewAllPlansScreenData.apply {
            this._planTrialPeriodTextColorSelector = fSelectorItem
            return this
        }

        @JvmName("setPlanPriceTextColorSelector")
        fun setPlanPriceTextColorSelector(fSelectorItem: SelectorColorItem) = this@ViewAllPlansScreenData.apply {
            this._planPriceTextColorSelector = fSelectorItem
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Header Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("headerColor")
        fun headerColor(fColors: ColorStateList?) = this@ViewAllPlansScreenData.apply {
            fColors?.let { this._headerColor = it }
            return this
        }

        @JvmName("headerColor")
        fun headerColor(@ColorInt fColorInt: Int) = this@ViewAllPlansScreenData.apply {
            headerColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("headerColorResources")
        fun headerColorResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            headerColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("headerColorStateListResources")
        fun headerColorStateListResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            fActivity.getColorStateRes(id)?.let { headerColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Sub Header Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("subHeaderColor")
        fun subHeaderColor(fColors: ColorStateList?) = this@ViewAllPlansScreenData.apply {
            fColors?.let { this._subHeaderColor = it }
            return this
        }

        @JvmName("subHeaderColor")
        fun subHeaderColor(@ColorInt fColorInt: Int) = this@ViewAllPlansScreenData.apply {
            subHeaderColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("subHeaderColorResources")
        fun subHeaderColorResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            subHeaderColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("subHeaderColorStateListResources")
        fun subHeaderColorStateListResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            fActivity.getColorStateRes(id)?.let { subHeaderColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Close Icon Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("closeIconColor")
        fun closeIconColor(fColors: ColorStateList?) = this@ViewAllPlansScreenData.apply {
            fColors?.let { this._closeIconColor = it }
            return this
        }

        @JvmName("closeIconColor")
        fun closeIconColor(@ColorInt fColorInt: Int) = this@ViewAllPlansScreenData.apply {
            closeIconColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("closeIconColorResources")
        fun closeIconColorResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            closeIconColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("closeIconColorStateListResources")
        fun closeIconColorStateListResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            fActivity.getColorStateRes(id)?.let { closeIconColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Rating Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("ratingColor")
        fun ratingColor(fColors: ColorStateList?) = this@ViewAllPlansScreenData.apply {
            fColors?.let { this._ratingColor = it }
            return this
        }

        @JvmName("ratingColor")
        fun ratingColor(@ColorInt fColorInt: Int) = this@ViewAllPlansScreenData.apply {
            ratingColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("ratingColorResources")
        fun ratingColorResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            ratingColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("ratingColorStateListResources")
        fun ratingColorStateListResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            fActivity.getColorStateRes(id)?.let { ratingColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Rating Place Holder Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("ratingPlaceHolderColor")
        fun ratingPlaceHolderColor(fColors: ColorStateList?) = this@ViewAllPlansScreenData.apply {
            fColors?.let { this._ratingPlaceHolderColor = it }
            return this
        }

        @JvmName("ratingPlaceHolderColor")
        fun ratingPlaceHolderColor(@ColorInt fColorInt: Int) = this@ViewAllPlansScreenData.apply {
            ratingPlaceHolderColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("ratingPlaceHolderColorResources")
        fun ratingPlaceHolderColorResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            ratingPlaceHolderColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("ratingPlaceHolderColorStateListResources")
        fun ratingPlaceHolderColorStateListResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            fActivity.getColorStateRes(id)?.let { ratingPlaceHolderColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Rating Indicator Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("ratingIndicatorColor")
        fun ratingIndicatorColor(fColors: ColorStateList?) = this@ViewAllPlansScreenData.apply {
            fColors?.let { this._ratingIndicatorColor = it }
            return this
        }

        @JvmName("ratingIndicatorColor")
        fun ratingIndicatorColor(@ColorInt fColorInt: Int) = this@ViewAllPlansScreenData.apply {
            ratingIndicatorColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("ratingIndicatorColorResources")
        fun ratingIndicatorColorResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            ratingIndicatorColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("ratingIndicatorColorStateListResources")
        fun ratingIndicatorColorStateListResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            fActivity.getColorStateRes(id)?.let { ratingIndicatorColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Unselected Item Data Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("unselectedItemDataColor")
        fun unselectedItemDataColor(fColors: ColorStateList?) = this@ViewAllPlansScreenData.apply {
            fColors?.let { this._unselectedItemDataColor = it }
            return this
        }

        @JvmName("unselectedItemDataColor")
        fun unselectedItemDataColor(@ColorInt fColorInt: Int) = this@ViewAllPlansScreenData.apply {
            unselectedItemDataColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("unselectedItemDataColorResources")
        fun unselectedItemDataColorResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            unselectedItemDataColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("unselectedItemDataColorStateListResources")
        fun unselectedItemDataColorStateListResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            fActivity.getColorStateRes(id)?.let { unselectedItemDataColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Selected Item Data Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("selectedItemDataColor")
        fun selectedItemDataColor(fColors: ColorStateList?) = this@ViewAllPlansScreenData.apply {
            fColors?.let { this._selectedItemDataColor = it }
            return this
        }

        @JvmName("selectedItemDataColor")
        fun selectedItemDataColor(@ColorInt fColorInt: Int) = this@ViewAllPlansScreenData.apply {
            selectedItemDataColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("selectedItemDataColorResources")
        fun selectedItemDataColorResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            selectedItemDataColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("selectedItemDataColorStateListResources")
        fun selectedItemDataColorStateListResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            fActivity.getColorStateRes(id)?.let { selectedItemDataColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Pay Nothing Now Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("payNothingNowColor")
        fun payNothingNowColor(fColors: ColorStateList?) = this@ViewAllPlansScreenData.apply {
            fColors?.let { this._payNothingNowColor = it }
            return this
        }

        @JvmName("payNothingNowColor")
        fun payNothingNowColor(@ColorInt fColorInt: Int) = this@ViewAllPlansScreenData.apply {
            payNothingNowColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("payNothingNowColorResources")
        fun payNothingNowColorResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            payNothingNowColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("payNothingNowColorStateListResources")
        fun payNothingNowColorStateListResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            fActivity.getColorStateRes(id)?.let { payNothingNowColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Secure With Play Store Text Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("secureWithPlayStoreTextColor")
        fun secureWithPlayStoreTextColor(fColors: ColorStateList?) = this@ViewAllPlansScreenData.apply {
            fColors?.let { this._secureWithPlayStoreTextColor = it }
            return this
        }

        @JvmName("secureWithPlayStoreTextColor")
        fun secureWithPlayStoreTextColor(@ColorInt fColorInt: Int) = this@ViewAllPlansScreenData.apply {
            secureWithPlayStoreTextColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("secureWithPlayStoreTextColorResources")
        fun secureWithPlayStoreTextColorResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            secureWithPlayStoreTextColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("secureWithPlayStoreTextColorStateListResources")
        fun secureWithPlayStoreTextColorStateListResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            fActivity.getColorStateRes(id)?.let { secureWithPlayStoreTextColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Secure With Play Store Background Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("secureWithPlayStoreBackgroundColor")
        fun secureWithPlayStoreBackgroundColor(fColors: ColorStateList?) = this@ViewAllPlansScreenData.apply {
            fColors?.let { this._secureWithPlayStoreBackgroundColor = it }
            return this
        }

        @JvmName("secureWithPlayStoreBackgroundColor")
        fun secureWithPlayStoreBackgroundColor(@ColorInt fColorInt: Int) = this@ViewAllPlansScreenData.apply {
            secureWithPlayStoreBackgroundColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("secureWithPlayStoreBackgroundColorResources")
        fun secureWithPlayStoreBackgroundColorResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            secureWithPlayStoreBackgroundColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("secureWithPlayStoreBackgroundColorStateListResources")
        fun secureWithPlayStoreBackgroundColorStateListResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            fActivity.getColorStateRes(id)?.let { secureWithPlayStoreBackgroundColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Item Box Background Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("itemBoxBackgroundColor")
        fun itemBoxBackgroundColor(fColors: ColorStateList?) = this@ViewAllPlansScreenData.apply {
            fColors?.let { this._itemBoxBackgroundColor = it }
            return this
        }

        @JvmName("itemBoxBackgroundColor")
        fun itemBoxBackgroundColor(@ColorInt fColorInt: Int) = this@ViewAllPlansScreenData.apply {
            itemBoxBackgroundColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("itemBoxBackgroundColorResources")
        fun itemBoxBackgroundColorResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            itemBoxBackgroundColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("itemBoxBackgroundColorStateListResources")
        fun itemBoxBackgroundColorStateListResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            fActivity.getColorStateRes(id)?.let { itemBoxBackgroundColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Selected Sku Background Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("selectedSkuBackgroundColor")
        fun selectedSkuBackgroundColor(fColors: ColorStateList?) = this@ViewAllPlansScreenData.apply {
            fColors?.let { this._selectedSkuBackgroundColor = it }
            return this
        }

        @JvmName("selectedSkuBackgroundColor")
        fun selectedSkuBackgroundColor(@ColorInt fColorInt: Int) = this@ViewAllPlansScreenData.apply {
            selectedSkuBackgroundColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("selectedSkuBackgroundColorResources")
        fun selectedSkuBackgroundColorResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            selectedSkuBackgroundColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("selectedSkuBackgroundColorStateListResources")
        fun selectedSkuBackgroundColorStateListResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            fActivity.getColorStateRes(id)?.let { selectedSkuBackgroundColor(fColors = it) }
            return this
        }
        //</editor-fold>

        //<editor-fold desc="Unselected Sku Background Color Using ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]">
        @JvmName("unselectedSkuBackgroundColor")
        fun unselectedSkuBackgroundColor(fColors: ColorStateList?) = this@ViewAllPlansScreenData.apply {
            fColors?.let { this._unselectedSkuBackgroundColor = it }
            return this
        }

        @JvmName("unselectedSkuBackgroundColor")
        fun unselectedSkuBackgroundColor(@ColorInt fColorInt: Int) = this@ViewAllPlansScreenData.apply {
            unselectedSkuBackgroundColor(fColors = ColorStateList.valueOf(fColorInt))
        }

        @JvmName("unselectedSkuBackgroundColorResources")
        fun unselectedSkuBackgroundColorResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            unselectedSkuBackgroundColor(fColorInt = fActivity.getColorRes(id))
        }

        @JvmName("unselectedSkuBackgroundColorStateListResources")
        fun unselectedSkuBackgroundColorStateListResources(@ColorRes id: Int) = this@ViewAllPlansScreenData.apply {
            fActivity.getColorStateRes(id)?.let { unselectedSkuBackgroundColor(fColors = it) }
            return this
        }
        //</editor-fold>
    }

    class FourPlanScreenData(private val fActivity: Activity) : Serializable {
//        private var _purchaseButtonTextIndex: Int = 0
//        internal val purchaseButtonTextIndex: Int get() = _purchaseButtonTextIndex

        private var _listOfBoxItem: ArrayList<FourPlanUserItem> = ArrayList()
        internal val listOfBoxItem: ArrayList<FourPlanUserItem> get() = _listOfBoxItem

        private var _listOfRattingItem: ArrayList<FourPlanRattingItem> = ArrayList()
        internal val listOfRattingItem: ArrayList<FourPlanRattingItem> get() = _listOfRattingItem

//        private var _lifeTimePlanDiscountPercentage: Int = 0
//        internal val lifeTimePlanDiscountPercentage: Int get() = _lifeTimePlanDiscountPercentage

//        @JvmName("setPurchaseButtonTextIndex")
//        fun setPurchaseButtonTextIndex(index: Int) = this@FourPlanScreenData.apply {
//            this._purchaseButtonTextIndex = index
//            return this
//        }

        @JvmName("setBoxItems")
        fun setBoxItems(vararg listOfBoxItem: FourPlanUserItem) = this@FourPlanScreenData.apply {
            this._listOfBoxItem.clearAll()
            this._listOfBoxItem.addAll(listOfBoxItem)
            return this
        }

        @JvmName("setRattingItems")
        fun setRattingItems(vararg listOfRattingItem: FourPlanRattingItem) = this@FourPlanScreenData.apply {
            this._listOfRattingItem.clearAll()
            this._listOfRattingItem.addAll(listOfRattingItem)
            return this
        }

//        @JvmName("setLifeTimePlanDiscountPercentage")
//        fun setLifeTimePlanDiscountPercentage(discountPercentage: Int) = this@FourPlanScreenData.apply {
//            this._lifeTimePlanDiscountPercentage = discountPercentage
//            return this
//        }
    }

    /**
     * initialization of Notification Data
     *
     * @param intentClass it's refers to your app intent class.
     */
    class NotificationData(val intentClass: Class<*>) : Serializable {
        @DrawableRes
        private var _notificationIcon: Int = R.drawable.outline_notification_important_24

        @get:DrawableRes
        internal val notificationIcon: Int get() = _notificationIcon

        private var _notificationId: Int = SUBSCRIPTION_NOTIFICATION_ID
        internal val notificationId: Int get() = _notificationId

        private var _notificationChannelId: String = SUBSCRIPTION_NOTIFICATION_CHANNEL_ID
        internal val notificationChannelId: String get() = _notificationChannelId

        private var _notificationChannelName: String = SUBSCRIPTION_NOTIFICATION_CHANNEL_NAME
        internal val notificationChannelName: String get() = _notificationChannelName


        /**
         * @param id it's refers to your app notification icon.
         */
        @JvmName("setNotificationIcon")
        fun setNotificationIcon(@DrawableRes id: Int) = this@NotificationData.apply {
            this._notificationIcon = id
            return this
        }

        /**
         * @param id it's refers to your app notification id, [by Default value = 275]
         */
        @JvmName("setNotificationId")
        fun setNotificationId(id: Int) = this@NotificationData.apply {
            this._notificationId = id
            return this
        }

        /**
         * @param channelId it's refers to your app notification channel id, [by Default value = "subscription_notification_channel_id"]
         */
        @JvmName("setNotificationChannelId")
        fun setNotificationChannelId(channelId: String) = this@NotificationData.apply {
            this._notificationChannelId = channelId
            return this
        }


        /**
         * @param channelName it's refers to your app notification channel name, [by Default value = "Free Trial Expire"]
         */
        @JvmName("setNotificationChannelName")
        fun setNotificationChannelName(channelName: String) = this@NotificationData.apply {
            this._notificationChannelName = channelName
            return this
        }
    }
}