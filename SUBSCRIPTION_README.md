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