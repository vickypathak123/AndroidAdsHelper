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

### Exit Dialog UI

<img src="https://github.com/vickypathak123/AndroidAdsHelper/blob/main/screenshots/exit_dialog_with_ad.jpg" height="auto" width="300"/>    <img src="https://github.com/vickypathak123/AndroidAdsHelper/blob/main/screenshots/exit_dialog_without_ad.jpg" height="auto" width="300"/>

### Subscription Screen UI

<img src="https://github.com/vickypathak123/AndroidAdsHelper/blob/main/screenshots/timeline_subscription_screen.jpg" height="auto" width="200"/>    <img src="https://github.com/vickypathak123/AndroidAdsHelper/blob/main/screenshots/four_plan_subscription_screen.jpg" height="auto" width="200"/>    <img src="https://github.com/vickypathak123/AndroidAdsHelper/blob/main/screenshots/siz_box_subscription_screen.jpg" height="auto" width="200"/>
