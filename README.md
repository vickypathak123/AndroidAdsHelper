# Vasundhara Android Common Code

Common Code For Android that is required in every app of [Vasundhara Infotech LLP](https://vasundharainfotechllp.com)

## Installation

The library is hosted on [jitpack](https://jitpack.io/), add this to your **build.gradle**:

##### Step 1. Add the JitPack repository to your project's `build.gradle`

```gradle
pluginManagement {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}

dependencyResolutionManagement {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

##### Step 2. Add the dependency to your module's `build.gradle`
Note: Add only 1 of these 2 dependency, either the adshelper or the revenueCat.

[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)
[![Version](https://jitpack.io/v/vickypathak123/AndroidAdsHelper.svg)](https://jitpack.io/#vickypathak123/AndroidAdsHelper)

```gradle
android {
    defaultConfig {
        ...
        multiDexEnabled true
        
//        add below line if you face any issue with localization of Subscription Screen
        resConfigs "en", "af-rZA", "ar-rAE", "da-rDK", "es-rES", "fil", "fr-rFR", "gu-rIN", "ha", "hi-rIN", "it-rIT", "ja", "ko", "pt-rBR", "ru-rRU", "th", "ur-rIN", "zh-rCN"
    }
    
//        add below line if you face "A failure occurred while executing com.android.build.gradle.internal.tasks.MergeJavaResWorkAction"    
    packagingOptions {
        resources {
            excludes += ['META-INF/DEPENDENCIES', 'META-INF/INDEX.LIST']
        }
    }
}

dependencies {
    implementation 'com.android.support:multidex:1.0.3'
    
//        add below line if you don't want to work with adshelper or revenueCat
    implementation 'com.github.vickypathak123.AndroidAdsHelper:integritycheck:latest_build_version'
    
//        add below line if you want to work with Google Billing    
    implementation 'com.github.vickypathak123.AndroidAdsHelper:adshelper:latest_build_version'
    
//        add below line if you want to work with RevenueCat Billing    
    implementation 'com.github.vickypathak123.AndroidAdsHelper:revenueCat:latest_build_version'
}
```

## Following points are covered in this code:

1. [Google Ads](https://github.com/vickypathak123/AndroidAdsHelper/tree/main/adshelper)
2. [Safetynet - Google Play Integrity](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/integritycheck/src/main/java/com/safetynet/integritycheck/integrity/AppProtector.kt#L120)
3. [Safetynet - Admob Consent Form](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/integritycheck/src/main/java/com/safetynet/integritycheck/integrity/GoogleMobileAdsConsentManager.kt#L16)
4. [Subscription Using Google Billing](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/adshelper/src/main/java/com/example/app/ads/helper/purchase/product/ProductPurchaseHelper.kt#L111)
5. [Subscription Using RevenueCat](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/revenueCat/src/main/java/com/example/app/ads/helper/revenuecat/RevenueCatPurchaseHelper.kt#L28)
6. [Initial App Opening Flow After Splash](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/adshelper/src/main/java/com/example/app/ads/helper/VasuSplashConfig.kt#L25)
7. [Initialization Of Subscription Related Remote Config JSON](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/adshelper/src/main/java/com/example/app/ads/helper/remoteconfig/VasuSubscriptionConfig.kt#L9)
8. [Opening Flow of Subscription Screen](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/adshelper/src/main/java/com/example/app/ads/helper/purchase/VasuSubscriptionConfig.kt#L123)
9. [Exit Dialog](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/adshelper/src/main/java/com/example/app/ads/helper/widget/ExitDialog.kt)
10. [Live Internet Connection Status](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/adshelper/src/main/java/com/example/app/ads/helper/InternetUtils.kt#L62)
11. Some Launcher functions like [Open Google Play Store](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/adshelper/src/main/java/com/example/app/ads/helper/launcher/Launcher.kt#L48), [Open Privacy Policy Link](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/adshelper/src/main/java/com/example/app/ads/helper/launcher/Launcher.kt#L65), [Open Any Other Link](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/adshelper/src/main/java/com/example/app/ads/helper/launcher/Launcher.kt#L81), and [Share This App](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/adshelper/src/main/java/com/example/app/ads/helper/launcher/Launcher.kt#L97), all links are open using the Google Custom

## Usage
1. [Exit Dialog](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/EXIT_DIALOG_README.md)



--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--

# Check [AdMob Error Codes & Logs](https://support.google.com/admob/thread/3494603/admob-error-codes-logs?hl=en)

### Remote Config Json For Subscription
Note: in all apps, subscription-related JSON must look like this, no need to remove old remote config data just add this new one and use it.

```json
{
  "vasu_subscription_config": {
    "initial_subscription_open_flow": [1, 1, 1],
    "purchase_button_text_index": 0,
    "initial_subscription_time_line_close_ad": true,
    "initial_subscription_more_plan_close_ad": false,
    "in_app_subscription_ad_close": true,
    "more_plan_screen_type": "six_box_screen / four_plan_screen",
    "life_time_plan_discount_percentage": 80
  }
}
```

## Subscription Screen Reference

### Subscription Screen UI

<img src="https://github.com/vickypathak123/AndroidAdsHelper/blob/main/screenshots/timeline_subscription_screen.jpg" height="auto" width="200" alt="Time Line Screen UI"/>    <img src="https://github.com/vickypathak123/AndroidAdsHelper/blob/main/screenshots/four_plan_subscription_screen.jpg" height="auto" width="200" alt="Four Plan Screen UI"/>    <img src="https://github.com/vickypathak123/AndroidAdsHelper/blob/main/screenshots/siz_box_subscription_screen.jpg" height="auto" width="200" alt="Six Box Screen UI"/>
