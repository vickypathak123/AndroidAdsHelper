package com.example.ads.helper.new_.demo.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AkshayTestingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        AppProtector.with(this@AkshayTestingActivity)
//            .appName(appName = "YOUR APP NAME")
//            .packageName(packageName = "YOUR APP PACKAGE NAME")
//            .cloudProjectNumber(cloudProjectNumber = YOUR CLOUD PROJECT NUMBER)
//            .playIntegrityRemoteConfigJson(remoteConfigJson = "PLAY INTEGRITY REMOTE CONFIG JSON")
//
//            /**
//             * Helper method to set deviceId which you can get from logs
//             * it's required field to set debug testing for google-consent
//             * Check your logcat output for the hashed device ID e.g.
//             * "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345")" to use
//             */
//
//            .setTestDeviceId(deviceId = "YOUR TEST DEVICE ID")
//
//            /**
//             * enable debug mode [by Default value = false]
//             * for check in test device and check all type of logs in Logcat
//             */
//            .enableDebugMode(fIsEnable = false)
//
//            /**
//             * for testing purpose
//             * need to block check integrity [by Default value = false]
//             * it pass true then not check play Integrity.
//             */
//            .needToBlockCheckIntegrity(fIsBlock = false)
//
//            /**
//             * check integrity
//             * callback when devise passed all Safetynet params
//             */
//            .checkIntegrity {
//                // TODO: perform your next action
//            }

//        /**
//         * initialization of Notification Data
//         *
//         * @param intentClass it's refers to your app intent class.
//         */
//        val notificationData = VasuSubscriptionConfig.NotificationData(intentClass = StartupActivity::class.java)
//            /**
//             * @param id it's refers to your app notification icon drawable resources id.
//             */
//            .setNotificationIcon(id = com.example.app.ads.helper.R.drawable.outline_notification_important_24)
//            /**
//             * add below line if you want to change notification id.
//             * @param id it's refers to your app notification id, [by Default value = 275]
//             */
//            .setNotificationId(id = 100)
//            /**
//             * add below line if you want to change notification channel id.
//             * @param channelId it's refers to your app notification channel id, [by Default value = "subscription_notification_channel_id"]
//             */
//            .setNotificationChannelId(channelId = "channelId")
//
//            /**
//             * add below line if you want to change notification channel id.
//             * @param channelName it's refers to your app notification channel name, [by Default value = "Free Trial Expire"]
//             */
//            .setNotificationChannelName(channelName = "channelName")
//
//
//        /**
//         * initialization of subscription screen ui data
//         */
//        VasuSubscriptionConfig.with(fActivity = this@AkshayTestingActivity)
//            /**
//             * enable test purchase [by Default value = false].
//             * call if you want to test your purchase code.
//             */
//            .enableTestPurchase(fIsEnable = true)
//            /**
//             * @param fCode it's refers to your app language code.
//             */
//            .setAppLanguageCode(fCode = "en")
//            /**
//             * @param fLink it's refers to your app privacy policy.
//             */
//            .setPrivacyPolicy(fLink = "")
//            /**
//             * @param fLink it's refers to your app terms of use.
//             */
//            .setTermsOfUse(fLink = "")
//            /**
//             * @param fNotificationData it's refers to your app notification data.
//             */
//            .setNotificationData(fNotificationData = notificationData)
//            /**
//             * initialization of time line screen ui data
//             */
//            .setTimeLineScreenData { fTimeLineScreenData ->
//                with(fTimeLineScreenData) {
//                    /**
//                     * set instant access hint, pass string resources ids.
//                     */
//                    setInstantAccessHint()
//                    /**
//                     * set instant access animation lottie file, pass raw resources id.
//                     */
//                    setInstantAccessLottieFile(fLottieFile =)
//                    /**
//                     * pass true if you want to animate instant access hint icon.
//                     */
//                    setWithInstantAccessAnimation(isAnimated =)
//                    /**
//                     * pass true if you want to animate time line slider.
//                     */
//                    setWithSliderAnimation(isAnimated =)
//                    /**
//                     * set main color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    mainColor() or mainColorResources() or mainColorStateListResources()
//                    /**
//                     * set header color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    headerColor() or headerColorResources() or headerColorStateListResources()
//                    /**
//                     * set Close Icon color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    closeIconColor() or closeIconColorResources() or closeIconColorStateListResources()
//                    /**
//                     * set Track Inactive color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    trackInactiveColor() or trackInactiveColorResources() or trackInactiveColorStateListResources()
//                    /**
//                     * set Hint Text color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    hintTextColor() or hintTextColorResources() or hintTextColorStateListResources()
//                    /**
//                     * set Instant Access Hint Text color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    instantAccessHintTextColor() or instantAccessHintTextColorResources() or instantAccessHintTextColorStateListResources()
//                    /**
//                     * set Secure With Play Store Text color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    secureWithPlayStoreTextColor() or secureWithPlayStoreTextColorResources() or secureWithPlayStoreTextColorStateListResources()
//                    /**
//                     * set Secure With Play Store Background color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    secureWithPlayStoreBackgroundColor() or secureWithPlayStoreBackgroundColorResources() or secureWithPlayStoreBackgroundColorStateListResources()
//                    /**
//                     * set Button Continue Text Color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    buttonContinueTextColor() or buttonContinueTextColorResources() or buttonContinueTextColorStateListResources()
//                }
//            }
//            /**
//             * initialization of six box screen ui data
//             */
//            .setViewAllPlansScreenData { fViewAllPlansScreenData ->
//                with(fViewAllPlansScreenData) {
//                    /**
//                     * set item box UI data.
//                     */
//                    setBoxItems(BoxItem())
//                    /**
//                     * set Ratting item UI data.
//                     */
//                    setRattingItems(RattingItem())
//                    /**
//                     * change yearly plan icon selector.
//                     */
//                    setYearPlanIconSelector(SelectorDrawableItem())
//                    /**
//                     * change lifetime plan icon selector.
//                     */
//                    setLifTimePlanIconSelector(SelectorDrawableItem())
//                    /**
//                     * change monthly plan icon selector.
//                     */
//                    setMonthPlanIconSelector(SelectorDrawableItem())
//                    /**
//                     * change plan box item selector.
//                     */
//                    setPlanSelector(SelectorDrawableItem())
//                    /**
//                     * change plan box header item selector.
//                     */
//                    setPlanHeaderSelector(SelectorDrawableItem())
//                    /**
//                     * change plan box item background selector.
//                     */
//                    setPlanBackgroundSelector(SelectorDrawableItem())
//                    /**
//                     * change plan box header text color selector.
//                     */
//                    setPlanHeaderTextColorSelector(SelectorColorItem())
//                    /**
//                     * change plan box title text color selector.
//                     */
//                    setPlanTitleTextColorSelector(SelectorColorItem())
//                    /**
//                     * change plan box Trial Period text color selector.
//                     */
//                    setPlanTrialPeriodTextColorSelector(SelectorColorItem())
//                    /**
//                     * change plan box Price text color selector.
//                     */
//                    setPlanPriceTextColorSelector(SelectorColorItem())
//                    /**
//                     * set Header Color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    headerColor() or headerColorResources() or headerColorStateListResources()
//                    /**
//                     * set Sub Header Color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    subHeaderColor() or subHeaderColorResources() or subHeaderColorStateListResources()
//                    /**
//                     * set Close Icon color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    closeIconColor() or closeIconColorResources() or closeIconColorStateListResources()
//                    /**
//                     * set Rating color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    ratingColor() or ratingColorResources() or ratingColorStateListResources()
//                    /**
//                     * set Rating Place Holder color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    ratingPlaceHolderColor() or ratingPlaceHolderColorResources() or ratingPlaceHolderColorStateListResources()
//                    /**
//                     * set Rating Indicator color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    ratingIndicatorColor() or ratingIndicatorColorResources() or ratingIndicatorColorStateListResources()
//                    /**
//                     * set Unselected Item Data color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    unselectedItemDataColor() or unselectedItemDataColorResources() or unselectedItemDataColorStateListResources()
//                    /**
//                     * set Selected Item Data color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    selectedItemDataColor() or selectedItemDataColorResources() or selectedItemDataColorStateListResources()
//                    /**
//                     * set Pay Nothing Now color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    payNothingNowColor() or payNothingNowColorResources() or payNothingNowColorStateListResources()
//                    /**
//                     * set Secure With Play Store Text color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    secureWithPlayStoreTextColor() or secureWithPlayStoreTextColorResources() or secureWithPlayStoreTextColorStateListResources()
//                    /**
//                     * set Secure With Play Store Background color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    secureWithPlayStoreBackgroundColor() or secureWithPlayStoreBackgroundColorResources() or secureWithPlayStoreBackgroundColorStateListResources()
//                    /**
//                     * set Item Box Background Color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    itemBoxBackgroundColor() or itemBoxBackgroundColorResources() or itemBoxBackgroundColorStateListResources()
//                    /**
//                     * set Selected Sku Background Color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    selectedSkuBackgroundColor() or selectedSkuBackgroundColorResources() or selectedSkuBackgroundColorStateListResources()
//                    /**
//                     * set Unselected Sku Background Color
//                     * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
//                     */
//                    unselectedSkuBackgroundColor() or unselectedSkuBackgroundColorResources() or unselectedSkuBackgroundColorStateListResources()
//                }
//            }
//            /**
//             * initialization of four plan screen ui data
//             */
//            .setFourPlanScreenData { fFourPlanScreenData ->
//                with(fFourPlanScreenData) {
//                    /**
//                     * set item box UI data.
//                     */
//                    setBoxItems(FourPlanUserItem())
//                    /**
//                     * set Ratting item UI data.
//                     */
//                    setRattingItems(FourPlanRattingItem())
//                }
//            }
//            /**
//             * launch subscription screen
//             *
//             * @param fPlanScreenType it's refers to your app plan screen type.
//             * @param isFromSplash it's refers to you need to launch from splash screen or not.
//             * @param directShowMorePlanScreen it's refers to you need to launch directly more plan screen or not.
//             * @param onSubscriptionEvent callback for subscription event.
//             * @param onScreenFinish callback for screen finish.
//             * @param isUserPurchaseAnyPlan true if user will purchase any plan
//             */
//            .launchScreen(
//                fPlanScreenType = MorePlanScreenType.FOUR_PLAN_SCREEN,
//                isFromSplash = false,
//                directShowMorePlanScreen = false,
//                onSubscriptionEvent = {},
//                onScreenFinish = { isUserPurchaseAnyPlan -> }
//            )
    }
}