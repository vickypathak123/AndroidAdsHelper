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
2. [Live Internet Connection Status](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/INTERNET_README.md)
3. [In-App Purchases and Subscription](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/SUBSCRIPTION_README.md)
4. [Safetynet (Google Play Integrity & Admob Consent Form)](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/SAFETYNET_README.md)
5. [Google Ads](https://github.com/vickypathak123/AndroidAdsHelper/blob/main/ADS_README.md)



--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--

# Check [AdMob Error Codes & Logs](https://support.google.com/admob/thread/3494603/admob-error-codes-logs?hl=en)




### ⭐️ If you liked it support me with your stars!

## Developed By

[Akshay Harsoda](https://github.com/AkshayHarsoda) - [akshayharsoda@gmail.com](https://mail.google.com/mail/u/0/?view=cm&fs=1&to=akshayharsoda@gmail.com&su=https://github.com/vickypathak123/Android-Ads-Helper&body=&bcc=akshayharsoda@gmail.com&tf=1)

  <a href="https://github.com/AkshayHarsoda" rel="nofollow">
  <img alt="Follow me on Github" 
       height="50" width="50" 
       src="https://github.com/vickypathak123/Android-Ads-Helper/blob/master/social/github.png" 
       style="max-width:100%;">
  </a>

  <a href="https://www.linkedin.com/in/akshay-harsoda-b66820116" rel="nofollow">
  <img alt="Follow me on LinkedIn" 
       height="50" width="50" 
       src="https://github.com/vickypathak123/Android-Ads-Helper/blob/master/social/linkedin.png" 
       style="max-width:100%;">
  </a>

  <a href="https://twitter.com/Akshayharsoda1" rel="nofollow">
  <img alt="Follow me on Twitter" 
       height="50" width="50"
       src="https://github.com/vickypathak123/Android-Ads-Helper/blob/master/social/twitter.png" 
       style="max-width:100%;">
  </a>

  <a href="https://www.facebook.com/akshay.harsoda" rel="nofollow">
  <img alt="Follow me on Facebook" 
       height="50" width="50" 
       src="https://github.com/vickypathak123/Android-Ads-Helper/blob/master/social/facebook.png" 
       style="max-width:100%;">
  </a>


