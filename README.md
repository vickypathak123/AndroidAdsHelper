# Android Ads Helper With Subscription (with/without Revenue-Cat)

Android Ads code that is required in every app of Vasundhara Infotech [Vasundhara Infotech LLP](https://vasundharainfotechllp.com)

[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)
[![Version](https://jitpack.io/v/vickypathak123/AndroidAdsHelper.svg)](https://jitpack.io/#vickypathak123/AndroidAdsHelper)

## Using `build.gradle`

###### Step 1. Add the JitPack repository to your project's `build.gradle`

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

###### Step 2. Add the dependency to your module's `build.gradle`
#### Note: Add only 1 of these 2 dependency, either the adshelper or the revenueCat

```groovy
android {
    defaultConfig {
        multiDexEnabled true
//        add below line if you face any issue with localization of Subscription Screen
        resConfigs "en", "af-rZA", "ar-rAE", "da-rDK", "es-rES", "fil", "fr-rFR", "gu-rIN", "ha", "hi-rIN", "it-rIT", "ja", "ko", "pt-rBR", "ru-rRU", "th", "ur-rIN", "zh-rCN"
    }
}

dependencies {
    implementation 'com.android.support:multidex:1.0.3'
    implementation 'com.github.vickypathak123.AndroidAdsHelper:adshelper:latest_build_version'
    implementation 'com.github.vickypathak123.AndroidAdsHelper:revenueCat:latest_build_version'
}
```

# Check [AdMob Error Codes & Logs](https://support.google.com/admob/thread/3494603/admob-error-codes-logs?hl=en)