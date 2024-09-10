# In-App Purchases and Subscription Usage

All type of Subscription Screen UI, Opening flow, and Product Details using Google Billing or RevenueCat Billing.

## UI

<img src="https://github.com/vickypathak123/AndroidAdsHelper/blob/main/screenshots/timeline_subscription_screen.jpg" height="auto" width="250" alt="Time Line Screen UI"/>    <img src="https://github.com/vickypathak123/AndroidAdsHelper/blob/main/screenshots/siz_box_subscription_screen.jpg" height="auto" width="250" alt="Six Box Screen UI"/>    <img src="https://github.com/vickypathak123/AndroidAdsHelper/blob/main/screenshots/four_plan_subscription_screen.jpg" height="auto" width="250" alt="Four Plan Screen UI"/>

## Prerequisites

- Make your app remote config JSON, for example:

```json
{
  "vasu_subscription_config": {
    "initial_subscription_open_flow": [1, 2, 3],
    "purchase_button_text_index": 0,
    "initial_subscription_time_line_close_ad": true,
    "initial_subscription_more_plan_close_ad": true,
    "in_app_subscription_ad_close": true,
    "more_plan_screen_type": "six_box_screen / four_plan_screen",
    "life_time_plan_discount_percentage": 80
  }
}
```

- #### Note: in all apps, subscription-related JSON must look like this, no need to remove old remote config data just add this new one and use it.

- #### Details of Remote config parameters
    - "initial_subscription_open_flow" :- it's a `IntArray` for what type of screen is open after splash.
        1. `Index == 1` : Opens the Subscription Screen. The developer will manage the next action after opening the Subscription Screen.
        2. `Index == 2` : Shows the App Open Ad. When it closes, the next action is performed.
        3. `Index == 3` : Shows the Interstitial Ad. When it closes, the next action is performed.
        4. `Index == else` : Directly the next action is performed.

    - "purchase_button_text_index" :- this index will indicate what type of text will show on purchase button of screen.
        1. `Index == 1` : "START MY FREE TRIAL".
        2. `Index == 2` : "Try `free trial period` for `currency symbol`0" ["Try 3 days for $0"].
        3. `Index == else` : "CONTINUE".

    - "initial_subscription_time_line_close_ad" :- show ads when time line screen close after splash.
        1. `true` : show Interstitial Ad.
        2. `false` : do not show Interstitial Ad.

    - "initial_subscription_more_plan_close_ad" :- show ads when more plan screen close after splash.
        1. `true` : show Interstitial Ad.
        2. `false` : do not show Interstitial Ad.

    - "in_app_subscription_ad_close" :- show ads when more plan screen close when it's not opening from splash.
        1. `true` : show Interstitial Ad.
        2. `false` : do not show Interstitial Ad.

    - "more_plan_screen_type" :- more plan screen name.
        1. `six_box_screen` : open six box type of screen.
        2. `four_plan_screen` : open four plan type of screen.

    - "life_time_plan_discount_percentage" :- It indicates the % discount you give to your customer.

## Usage

- Add initialization of your remote config json, for example:

```kotlin
class AppApplication : AppOpenApplication() {

    override fun onCreate() {
        super.onCreate()
            ...
        /**
         * initialization of your remote config json
         *
         * @param jsonString it refers to your original remote config JSON.
         */
        initSubscriptionRemoteConfig(jsonString = "Your original remote config JSON")
    }
    
}
```

- Add below code to your `SplashActivity`, for example:

```kotlin
class SplashActivity : AppCompatActivity() {

    override fun onCreate() {
        super.onCreate()
            ...
        /**
         * initialization of your remote config json
         *
         * @param jsonString it refers to your original remote config JSON.
         */
        CoroutineScope(Dispatchers.IO).launch {
        
            /**
             * initialization of Product Purchase Listener.
             */
            ProductPurchaseHelper.setPurchaseListener(object : ProductPurchaseHelper.ProductPurchaseListener {
            
                override fun onBillingSetupFinished() {
                
                    val action: () -> Unit = {
                        CoroutineScope(Dispatchers.Main).launch {
                            mActivity.runOnUiThread {
                                // TODO: start splash timer
                            }
                        }
                    }

                    /**
                     * use below line if you want to use RevenueCat Billing.
                     *
                     * initialization of Product SKU data using RevenueCat Billing.
                     *
                     * @param fContext it's refers to your application, activity or fragment context.
                     * @param onInitializationComplete Callback for data will be initialized.
                     */
                    initRevenueCatProductList(fContext = mActivity, onInitializationComplete = action)
                    
                    /**
                     * use below line if you want to use Google Billing.
                     *
                     * initialization of Product SKU data using Google Billing.
                     *
                     * @param fContext it's refers to your application, activity or fragment context.
                     * @param onInitializationComplete Callback for data will be initialized.
                     */
                    ProductPurchaseHelper.initProductsKeys(fContext = mActivity, onInitializationComplete = action)
                    
                }
            })

            /**
             * initialization of Google Billing Client.
             *
             * @param fContext it's refers to your application, activity or fragment context.
             */
            ProductPurchaseHelper.initBillingClient(fContext = mActivity)
        }
    }

    fun launchNextScreen() {
        
        /**
         * Manages the subscription flow and ad displays based on the provided index.
         *
         * - `Index == 1` : Opens the Subscription Screen. The developer will manage the next action after opening the Subscription Screen.
         * - `Index == 2` : Shows the App Open Ad. When it closes, the next action is performed.
         * - `Index == 3` : Shows the Interstitial Ad. When it closes, the next action is performed.
         * - `Index == else` : Performs the next action without showing ads or the Subscription Screen.
         *
         * @param fActivity Reference to the current Activity.
         * @param onOpenSubscriptionScreen Callback for opening the subscription screen and performing the next action.
         * @param onNextAction Callback for performing the next action directly.
         */
        VasuSplashConfig.showSplashFlow(
            fActivity = mActivity,
            onOpenSubscriptionScreen = {
                // TODO: Callback for opening the subscription screen and performing the next action.
            },
            onNextAction = {
                // TODO: Callback for performing the next action directly.
            }
        )
    }
    
}
```

- Add below code for opening Subscription Screen, for example:

```kotlin
    /**
     * initialization of subscription screen ui data
     */
    VasuSubscriptionConfig.with(fActivity = this@YourActivity)
        /**
         * enable test purchase [by Default value = false].
         * call if you want to test your purchase code.
         */
        .enableTestPurchase(fIsEnable = true)
        /**
         * @param fCode it's refers to your app language code.
         */
        .setAppLanguageCode(fCode = "en")
        /**
         * @param fLink it's refers to your app privacy policy.
         */
        .setPrivacyPolicy(fLink = "")
        /**
         * @param fLink it's refers to your app terms of use.
         */
        .setTermsOfUse(fLink = "")
        /**
         * @param fNotificationData it's refers to your app notification data.
         */
        .setNotificationData(fNotificationData = notificationData)
        /**
         * initialization of time line screen ui data
         */
        .setTimeLineScreenData { fTimeLineScreenData ->
            with(fTimeLineScreenData) {
                /**
                 * set instant access hint, pass string resources ids.
                 */
                setInstantAccessHint()
                /**
                 * set instant access animation lottie file, pass raw resources id.
                 */
                setInstantAccessLottieFile(fLottieFile =)
                /**
                 * pass true if you want to animate instant access hint icon.
                 */
                setWithInstantAccessAnimation(isAnimated =)
                /**
                 * pass true if you want to animate time line slider.
                 */
                setWithSliderAnimation(isAnimated =)
                /**
                 * set main color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                mainColor() or mainColorResources() or mainColorStateListResources()
                /**
                 * set header color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                headerColor() or headerColorResources() or headerColorStateListResources()
                /**
                 * set Close Icon color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                closeIconColor() or closeIconColorResources() or closeIconColorStateListResources()
                /**
                 * set Track Inactive color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                trackInactiveColor() or trackInactiveColorResources() or trackInactiveColorStateListResources()
                /**
                 * set Hint Text color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                hintTextColor() or hintTextColorResources() or hintTextColorStateListResources()
                /**
                 * set Instant Access Hint Text color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                instantAccessHintTextColor() or instantAccessHintTextColorResources() or instantAccessHintTextColorStateListResources()
                /**
                 * set Secure With Play Store Text color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                secureWithPlayStoreTextColor() or secureWithPlayStoreTextColorResources() or secureWithPlayStoreTextColorStateListResources()
                /**
                 * set Secure With Play Store Background color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                secureWithPlayStoreBackgroundColor() or secureWithPlayStoreBackgroundColorResources() or secureWithPlayStoreBackgroundColorStateListResources()
                /**
                 * set Button Continue Text Color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                buttonContinueTextColor() or buttonContinueTextColorResources() or buttonContinueTextColorStateListResources()
            }
        }
        /**
         * initialization of six box screen ui data
         */
        .setViewAllPlansScreenData { fViewAllPlansScreenData ->
            with(fViewAllPlansScreenData) {
                /**
                 * set item box UI data.
                 */
                setBoxItems(BoxItem())
                /**
                 * set Ratting item UI data.
                 */
                setRattingItems(RattingItem())
                /**
                 * change yearly plan icon selector.
                 */
                setYearPlanIconSelector(SelectorDrawableItem())
                /**
                 * change lifetime plan icon selector.
                 */
                setLifTimePlanIconSelector(SelectorDrawableItem())
                /**
                 * change monthly plan icon selector.
                 */
                setMonthPlanIconSelector(SelectorDrawableItem())
                /**
                 * change plan box item selector.
                 */
                setPlanSelector(SelectorDrawableItem())
                /**
                 * change plan box header item selector.
                 */
                setPlanHeaderSelector(SelectorDrawableItem())
                /**
                 * change plan box item background selector.
                 */
                setPlanBackgroundSelector(SelectorDrawableItem())
                /**
                 * change plan box header text color selector.
                 */
                setPlanHeaderTextColorSelector(SelectorColorItem())
                /**
                 * change plan box title text color selector.
                 */
                setPlanTitleTextColorSelector(SelectorColorItem())
                /**
                 * change plan box Trial Period text color selector.
                 */
                setPlanTrialPeriodTextColorSelector(SelectorColorItem())
                /**
                 * change plan box Price text color selector.
                 */
                setPlanPriceTextColorSelector(SelectorColorItem())
                /**
                 * set Header Color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                headerColor() or headerColorResources() or headerColorStateListResources()
                /**
                 * set Sub Header Color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                subHeaderColor() or subHeaderColorResources() or subHeaderColorStateListResources()
                /**
                 * set Close Icon color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                closeIconColor() or closeIconColorResources() or closeIconColorStateListResources()
                /**
                 * set Rating color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                ratingColor() or ratingColorResources() or ratingColorStateListResources()
                /**
                 * set Rating Place Holder color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                ratingPlaceHolderColor() or ratingPlaceHolderColorResources() or ratingPlaceHolderColorStateListResources()
                /**
                 * set Rating Indicator color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                ratingIndicatorColor() or ratingIndicatorColorResources() or ratingIndicatorColorStateListResources()
                /**
                 * set Unselected Item Data color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                unselectedItemDataColor() or unselectedItemDataColorResources() or unselectedItemDataColorStateListResources()
                /**
                 * set Selected Item Data color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                selectedItemDataColor() or selectedItemDataColorResources() or selectedItemDataColorStateListResources()
                /**
                 * set Pay Nothing Now color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                payNothingNowColor() or payNothingNowColorResources() or payNothingNowColorStateListResources()
                /**
                 * set Secure With Play Store Text color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                secureWithPlayStoreTextColor() or secureWithPlayStoreTextColorResources() or secureWithPlayStoreTextColorStateListResources()
                /**
                 * set Secure With Play Store Background color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                secureWithPlayStoreBackgroundColor() or secureWithPlayStoreBackgroundColorResources() or secureWithPlayStoreBackgroundColorStateListResources()
                /**
                 * set Item Box Background Color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                itemBoxBackgroundColor() or itemBoxBackgroundColorResources() or itemBoxBackgroundColorStateListResources()
                /**
                 * set Selected Sku Background Color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                selectedSkuBackgroundColor() or selectedSkuBackgroundColorResources() or selectedSkuBackgroundColorStateListResources()
                /**
                 * set Unselected Sku Background Color
                 * pass ColorStateList, ColorInt & ColorRes ID [ColorResources Or ColorStateListResources]
                 */
                unselectedSkuBackgroundColor() or unselectedSkuBackgroundColorResources() or unselectedSkuBackgroundColorStateListResources()
            }
        }
        /**
         * initialization of four plan screen ui data
         */
        .setFourPlanScreenData { fFourPlanScreenData ->
            with(fFourPlanScreenData) {
                /**
                 * set item box UI data.
                 */
                setBoxItems(FourPlanUserItem())
                /**
                 * set Ratting item UI data.
                 */
                setRattingItems(FourPlanRattingItem())
            }
        }
        /**
         * launch subscription screen
         *
         * do not pass any data on this param if you want to use remote config data
         * @param fPlanScreenType it's refers to your app plan screen type.
         * @param isFromSplash it's refers to you need to launch from splash screen or not.
         * @param directShowMorePlanScreen it's refers to you need to launch directly more plan screen or not.
         * @param onSubscriptionEvent callback for subscription event.
         * @param onScreenFinish callback for screen finish.
         * @param isUserPurchaseAnyPlan true if user will purchase any plan
         */
        .launchScreen(
            fPlanScreenType = MorePlanScreenType.FOUR_PLAN_SCREEN,
            isFromSplash = false,
            directShowMorePlanScreen = false,
            onSubscriptionEvent = {},
            onScreenFinish = { isUserPurchaseAnyPlan -> }
        )
```

- Add below code for initializing Notification Data, for example:

```kotlin
    /**
     * initialization of Notification Data
     *
     * @param intentClass it's refers to your app intent class.
     */
    val notificationData = VasuSubscriptionConfig.NotificationData(intentClass = StartupActivity::class.java)
    
        /**
         * @param id it's refers to your app notification icon drawable resources id.
         */
        .setNotificationIcon(id = com.example.app.ads.helper.R.drawable.outline_notification_important_24)
        
        /**
         * add below line if you want to change notification id.
         * @param id it's refers to your app notification id, [by Default value = 275]
         */
        .setNotificationId(id = 100)
        
        /**
         * add below line if you want to change notification channel id.
         * @param channelId it's refers to your app notification channel id, [by Default value = "subscription_notification_channel_id"]
         */
        .setNotificationChannelId(channelId = "channelId")

        /**
         * add below line if you want to change notification channel id.
         * @param channelName it's refers to your app notification channel name, [by Default value = "Free Trial Expire"]
         */
        .setNotificationChannelName(channelName = "channelName")
```
