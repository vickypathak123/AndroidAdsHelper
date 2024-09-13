# Google Ads Usage

All types of Ads load and show details using Google Ads.

## Prerequisites

- Add the below code for set Ads Data as an example:

    ```kotlin
        class AppApplication : AppOpenApplication() {
        
            override fun onCreate() {
                super.onCreate()
                
                /**
                 * Initialization of ads data.
                 * All set & enable methods are optional.
                 * You can set multiple IDs in one method.
                 */
                VasuAdsConfig.with(this)
                    /**
                     * Enable logCat (Default value = false).
                     * Call this if you want to show ads-related logs in your logCat.
                     */
                    .enableDebugMode(fIsEnable = false)
                    
                    /**
                     * Enable logCat for Purchase History (Default value = false).
                     * Call this if you want to show Purchase History-related logs in your logCat.
                     */
                    .enablePurchaseHistoryLog(fIsEnable = false)
                    
                    /**
                     * Enable App Open Ad in your project (Default value = false).
                     * Call this if you want to show the App Open Ad in your project.
                     */
                    .enableOpenAd(fIsEnable = false)
                    
                    /**
                     * Block Interstitial Ad in your project (Default value = false).
                     * Call this if you want to test the FullScreenNative Ad in your project.
                     */
                    .needToBlockInterstitialAd(fIsBlock = false)
                    
                    /**
                     * Show ads with test ads ID in your project (Default value = false).
                     * Call this if you want to show ads with test ads IDs in your project.
                     */
                    .needToTakeAllTestAdID(fIsTakeAll = false)
                    
                    /**
                     * Using the methods below, you can set your live ads IDs in your project.
                     * All methods are optional.
                     * You can set multiple IDs in one method.
                     */
                    .setAdmobAppId(fAdmobAppId = "LIVE_APP_ID")
                    .setAdmobSplashBannerAdId("LIVE_SPLASH_BANNER_ID")
                    .setAdmobBannerAdId("LIVE_BANNER_ID")
                    .setAdmobInterstitialAdId("LIVE_INTERSTITIAL_ID")
                    .setAdmobNativeAdvancedAdId("LIVE_NATIVE_ADVANCED_ID")
                    .setAdmobOpenAdId("LIVE_APP_OPEN_ID")
                    .setAdmobRewardInterstitialAdId("LIVE_REWARD_INTERSTITIAL_ID")
                    .setAdmobRewardVideoAdId("LIVE_REWARD_VIDEO_ID")
                    
                    /**
                     * Using the methods below, you can set your product key SKU in your project.
                     * You can set multiple SKUs in one method.
                     * Call this if your app does not use the RevenueCat SDK.
                     */
                    .setLifeTimeProductKey("LIFE_TIME_PRODUCT_PURCHASE_SKU_KEY")
                    .setSubscriptionKey("PRODUCT_SUBSCRIBE_SKU_KEY")
                    
                    /**
                     * Use the method below to initialize your ads data.
                     */
                    .initialize()
                    
                /**
                 * Initializing the mobile ads SDK.
                 * 
                 * Helper method to set device IDs, which you can get from logs.
                 * Check your logcat output for the test device ID, e.g.,
                 * I/Ads: Use RequestConfiguration.Builder.setTestDeviceIds("TEST_DEVICE_ID","TEST_DEVICE_ID")
                 * 
                 * @param fDeviceId Pass multiple "TEST_DEVICE_ID" values.
                 */
                initMobileAds("747DD141C5DB53A9F7E3E452845C08FF")
            }
            
            fun updateRemoteConfigFlag() {
                /**
                 * Initialization of ads remote config data.
                 * All methods are optional.
                 */
                VasuAdsConfig.with(this)
                    /**
                     * Enable Banner Ad in your project (Default value = true).
                     */
                    .enableBannerAdFromRemoteConfig(fIsEnable = true)
                    
                    /**
                     * Enable Interstitial Ad in your project (Default value = true).
                     */
                    .enableInterstitialAdFromRemoteConfig(fIsEnable = true)
                    
                    /**
                     * Enable Native Advanced Ad in your project (Default value = true).
                     */
                    .enableNativeAdFromRemoteConfig(fIsEnable = true)
                    
                    /**
                     * Enable App Open Ad in your project (Default value = true).
                     */
                    .enableAppOpenAdFromRemoteConfig(fIsEnable = true)
                    
                    /**
                     * Enable Rewarded Interstitial Ad in your project (Default value = true).
                     */
                    .enableRewardedInterstitialAdFromRemoteConfig(fIsEnable = true)
                    
                    /**
                     * Enable Rewarded Video Ad in your project (Default value = true).
                     */
                    .enableRewardedVideoAdFromRemoteConfig(fIsEnable = true)
                    
                    /**
                     * Use the method below to initialize only ads-related remote config data.
                     */
                    .initializeRemoteConfig()
            }
            
        }
    ```

## Usage

#### 1. Banner Ad

- Add the below code for the Banner Ad as an example:

    ```xml
    <androidx.constraintlayout.widget.ConstraintLayout 
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
            ............
            <com.example.app.ads.helper.banner.BannerAdView
                android:id="@+id/banner_ad_view"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                app:banner_ad_size="banner"
                app:banner_ad_type="normal"
                app:banner_placeholder_type="shimmer"
                app:layout_constraintBottom_toBottomOf="parent"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent" />
            
    </androidx.constraintlayout.widget.ConstraintLayout>
    ```

- Customizing Banner Ads using XML attributes:
    * There is no need to add these attributes to your `attrs.xml` resource file manually, as the library provides them. Here’s an example:
    ```xml
        <resources>
            
            <declare-styleable name="BannerAdView">
                <attr name="banner_ad_size" />
                <attr name="banner_ad_type" />
                <attr name="banner_placeholder_type" />
                <attr name="banner_placeholder_text_color" format="color" />
                <attr name="banner_custom_placeholder" format="reference" />
                <attr name="banner_auto_load" format="boolean" />
            </declare-styleable>
            
            <attr name="banner_placeholder_type" format="enum">
                <enum name="none" value="0" />
                <enum name="shimmer" value="1" />
                <enum name="text" value="2" />
                <enum name="custom" value="3" />
            </attr>
            
            <attr name="banner_ad_type" format="enum">
                <enum name="normal" value="0" />
                <enum name="splash" value="1" />
                <enum name="collapsible_bottom" value="2" />
                <enum name="collapsible_top" value="3" />
            </attr>
            
            <attr name="banner_ad_size" format="enum">
                <enum name="banner" value="0" />
                <enum name="large_banner" value="1" />
                <enum name="medium_rectangle" value="2" />
                <enum name="full_banner" value="3" />
                <enum name="leaderboard" value="4" />
                <enum name="adaptive_banner" value="5" />
            </attr>
            
        </resources>
    ```

    * Details of the above custom attributes:
        * `banner_ad_size`: This attribute allows you to customize your ad size.
            * There are 6 banner ad sizes: [`banner`, `large_banner`, `medium_rectangle`, `full_banner`, `leaderboard`, `adaptive_banner`].
        * `banner_ad_type`: This attribute allows you to customize your ad type.
            * There are 4 types of banner ads: [`normal`, `splash`, `collapsible_bottom`, `collapsible_top`].
            * The `normal` type works with all kinds of `banner_ad_size`.
            * `splash`, `collapsible_bottom`, and `collapsible_top` types work only when `banner_ad_size` is set to `adaptive_banner`.
            * When using the `splash` type, ensure your banner ad view is in the splash activity.
        * `banner_placeholder_type`: This attribute allows you to customize your ad loading view.
            * There are 4 types of placeholder views: [`none`, `shimmer`, `text`, `custom`].
            * `none` - No loading view is shown.
            * `shimmer` - Displays a shimmer view as the loading view.
            * `text` - Displays a text view as the loading view.
                * You can change the text color using the `banner_placeholder_text_color` attribute.
            * `custom` - Displays a custom view as the loading view.
                * You can set your custom view using the `banner_custom_placeholder` attribute.
        * `banner_auto_load`: This attribute allows you to handle load requests manually.
            * The default value is `true`, meaning you cannot handle the loading request manually.
                * The loading request is triggered when your view is attached to the display.

#### 2. Interstitial Ad

- Add the below code for Interstitial Ad as an example:

    ```kotlin
        class YourActivity : AppCompatActivity() {
            fun showAd() {
                /**
                 * Call this method when you need to show an Interstitial Ad.
                 * This method will also call our offline native ad [InterstitialNativeAdActivity] when the Interstitial Ad fails,
                 * and it will provide a callback in the same way.
                 *
                 * Usage of this method:
                 * activity.showInterstitialAd { [your code that runs after the ad is shown or if the ad fails to show] }
                 * Call this method with the [Activity] instance.
                 *
                 * @param fIsShowFullScreenNativeAd Pass `false` if you don't need a native ad when interstitial ads are not loaded.
                 * @param onAdClosed This is the callback for when the ad is closed; it is also called if the ad was not shown to the user.
                 * @param isAdShowing `true` when the Interstitial Ad is showing to the user.
                 * @param isShowFullScreenAd `true` when the Full Screen Native Ad is showing to the user.
                 */
                mActivity.showInterstitialAd(fIsShowFullScreenNativeAd = true) { isAdShowing, isShowFullScreenAd ->
                    // TODO: do your next action 
                }
            }
        }
    ```

#### 3. Native Ad

- Add the below code for the Native Ad as an example:

    ```xml
        <androidx.constraintlayout.widget.ConstraintLayout 
            xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:app="http://schemas.android.com/apk/res-auto"
            android:layout_width="match_parent"
            android:layout_height="match_parent">
                ............
                <com.example.app.ads.helper.nativead.NativeAdView
                    android:id="@+id/banner_ad_view"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    app:native_ad_type="big"
                    app:native_placeholder_type="shimmer"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />
                    
        </androidx.constraintlayout.widget.ConstraintLayout>
    ```

- Customizing Native Ads using XML attributes:
    * There is no need to manually add these attributes to your `attrs.xml` resource file, as the library provides them. Here’s an example:
    ```xml
        <resources>
          
            <declare-styleable name="NativeAdView">
                <attr name="native_ad_type" />
                <attr name="native_placeholder_type" />
                <attr name="native_placeholder_text_color" format="color" />
                <attr name="native_custom_placeholder" format="reference" />
                <attr name="native_custom_ad_view" format="reference" />
                <attr name="native_auto_load" format="boolean" />
                <attr name="native_show_placeholder" format="boolean" />
                <attr name="native_new_ad_request" format="boolean" />
                <attr name="cardBackgroundColor" format="color" />
                <attr name="cardCornerRadius" format="dimension" />
                <attr name="cardElevation" format="dimension" />
                <attr name="cardUseCompatPadding" format="boolean" />
                <attr name="cardPreventCornerOverlap" format="boolean" />
                <attr name="contentPadding" format="dimension" />
                <attr name="contentPaddingLeft" format="dimension" />
                <attr name="contentPaddingRight" format="dimension" />
                <attr name="contentPaddingTop" format="dimension" />
                <attr name="contentPaddingBottom" format="dimension" />
                <attr name="strokeColor" format="color" />
                <attr name="strokeWidth" format="dimension" />
            </declare-styleable>
            
            <attr name="native_ad_type" format="enum">
                <enum name="big" value="0" />
                <enum name="medium" value="1" />
                <enum name="interstitial_native" value="2" />
                <enum name="custom" value="3" />
            </attr>
            
            <attr name="native_placeholder_type" format="enum">
                <enum name="none" value="0" />
                <enum name="shimmer" value="1" />
                <enum name="text" value="2" />
                <enum name="custom" value="3" />
            </attr>
            
        </resources>
    ```

    * Details of the above custom attributes:
        * `native_ad_type`: This attribute allows you to customize your ad size.
            * There are 4 native ad sizes: [`big`, `medium`, `interstitial_native`, `custom`].
            * Avoid using the `interstitial_native` type.
            * `custom`: Displays a custom view as the ad view.
                * You can set your custom view using the `native_custom_ad_view` attribute.
        * `native_placeholder_type`: This attribute allows you to customize your ad loading view.
            * There are 4 types of placeholder views: [`none`, `shimmer`, `text`, `custom`].
            * `none`: No loading view is shown.
            * `shimmer`: Displays a shimmer view as the loading view.
            * `text`: Displays a text view as the loading view.
                * You can change the text color using the `native_placeholder_text_color` attribute.
            * `custom`: Displays a custom view as the loading view.
                * You can set your custom view using the `native_custom_placeholder` attribute.
        * `native_auto_load`: This attribute allows you to handle load requests manually.
            * The default value is `true`, meaning you cannot handle the loading request manually.
                * The loading request is triggered when your view is attached to the display.
        * `native_show_placeholder`: This attribute allows you to view the placeholder and main ad view in edit mode.
        * You can customize the background card properties using the following attributes:
            * `cardBackgroundColor`
            * `cardCornerRadius`
            * `cardElevation`
            * `cardUseCompatPadding`
            * `cardPreventCornerOverlap`
            * `contentPadding`
            * `contentPaddingLeft`
            * `contentPaddingRight`
            * `contentPaddingTop`
            * `contentPaddingBottom`
        * You can change the background card stroke color and width using the following attributes:
            * `strokeColor`
            * `strokeWidth`

#### 4. App Open Ad

- Add the below code for the App Open Ad as an example:

    ```kotlin
        class AppApplication : AppOpenApplication() {
        
            /**
             * Override this function in your application class to show the App Open Ad.
             *
             * @param fCurrentActivity Refers to your current activity.
             * @return `true` if you want to show the App Open Ad,
             * @return `false` if you don't want to show the App Open Ad.
             */
            override fun onResumeApp(fCurrentActivity: Activity): Boolean {
                return true
            }
        }
    ```

#### 5. Reward Interstitial Ad

- Add the below code for the Reward Interstitial Ad as an example:

    ```kotlin
        class YourActivity : AppCompatActivity() {
            fun loadAd() {
                /**
                 * Call this method when you need to load your Reward Interstitial Ad.
                 * You need to call this method only once in any activity or fragment.
                 *
                 * Usage of this Method:
                 * loadAd(
                 *      fContext = reference to your activity or fragment context,
                 *      onStartToLoadAd = {[show progress when starting to load the Reward Interstitial Ad]},
                 *      onAdLoaded = {[hide progress after successfully loading the Reward Interstitial Ad]},
                 * )
                 *
                 * @param fContext This is a reference to your activity or fragment context.
                 * @param onStartToLoadAd Called when the Reward Interstitial Ad starts loading.
                 * @param onAdLoaded Called when the Reward Interstitial Ad has successfully loaded.
                 */
                RewardedInterstitialAdHelper.loadAd(
                    fContext = mActivity,
                    onStartToLoadAd = {
                        // TODO: Manage your view when the ad starts loading
                    },
                    onAdLoaded = {
                        // TODO: Manage your view after the ad has loaded successfully
                    }
                )
            }
            
            fun showAd() {
                /**
                 * Call this method when you need to show your Reward Interstitial Ad.
                 * You need to call this method with your Activity Context.
                 *
                 * Usage of this Method:
                 * activity.showRewardedInterstitialAd(
                 *      onUserEarnedReward = { isUserEarnedReward -> [Default value = false, it's true when the user successfully earns a reward]},
                 * )
                 * Call this method with the [Activity] instance.
                 *
                 * @param onUserEarnedReward Called when the user earns a reward. @see [AdMobAdsListener.onUserEarnedReward]
                 */
                mActivity.showRewardedInterstitialAd { isUserEarnedReward ->
                    // Manage your view or app state based on whether the user earned the reward
                    if (isUserEarnedReward) {
                        // The user earned the reward
                        // TODO: Reward the user (e.g., give them points, unlock features, etc.)
                    } else {
                        // The user did not earn the reward (e.g., they closed the ad early)
                        // TODO: Manage the scenario where the user did not earn the reward
                    }
                }
            }
        }
    ```

#### 6. Reward Video Ad

- Add the below code for the Reward Video Ad as an example:

    ```kotlin
        class YourActivity : AppCompatActivity() {
            fun loadAd() {
                /**
                 * Call this method when you need to load your Reward Video Ad.
                 * You need to call this method only once in any activity or fragment.
                 *
                 * Usage of this Method:
                 * loadAd(
                 *      fContext = reference to your activity or fragment context,
                 *      onStartToLoadAd = {[show progress when starting to load the Reward Video Ad]},
                 *      onAdLoaded = {[hide progress after successfully loading the Reward Video Ad]},
                 * )
                 *
                 * @param fContext This is a reference to your activity or fragment context.
                 * @param onStartToLoadAd Called when the Reward Video Ad starts loading.
                 * @param onAdLoaded Called when the Reward Video Ad has successfully loaded.
                 */
                RewardedVideoAdHelper.loadAd(
                    fContext = mActivity,
                    onStartToLoadAd = {
                        // TODO: Manage your view when the ad starts loading
                    },
                    onAdLoaded = {
                        // TODO: Manage your view after the ad has loaded successfully
                    }
                )
            }
            
            fun showAd() {
                /**
                 * Call this method when you need to show your Reward Video Ad.
                 * You need to call this method with your Activity Context.
                 *
                 * Usage of this Method:
                 * activity.showRewardedVideoAd(
                 *      onUserEarnedReward = { isUserEarnedReward -> [Default value = false, it's true when the user successfully earns a reward]},
                 * )
                 * Call this method with the [Activity] instance.
                 *
                 * @param onUserEarnedReward Called when the user earns a reward. @see [AdMobAdsListener.onUserEarnedReward]
                 */
                mActivity.showRewardedVideoAd { isUserEarnedReward ->
                    // Manage your view or app state based on whether the user earned the reward
                    if (isUserEarnedReward) {
                        // The user earned the reward
                        // TODO: Reward the user (e.g., give them points, unlock features, etc.)
                    } else {
                        // The user did not earn the reward (e.g., they closed the ad early)
                        // TODO: Manage the scenario where the user did not earn the reward
                    }
                }
            }
        }
    ```
