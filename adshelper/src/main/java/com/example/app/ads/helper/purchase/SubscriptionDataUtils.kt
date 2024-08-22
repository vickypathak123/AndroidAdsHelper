package com.example.app.ads.helper.purchase

import com.example.app.ads.helper.purchase.utils.SubscriptionEventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal var SUBSCRIPTION_DATA_LANGUAGE_CODE: String = "en"
internal var SUBSCRIPTION_TERMS_OF_USE: String = ""
internal var SUBSCRIPTION_PRIVACY_POLICY: String = ""

internal var IS_ENABLE_TEST_PURCHASE: Boolean = false
internal var IS_FROM_SPLASH: Boolean = false
internal var SHOW_CLOSE_AD_FOR_TIME_LINE_SCREEN: Boolean = false
internal var SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN_OPEN_AFTER_SPLASH: Boolean = false
internal var SHOW_CLOSE_AD_FOR_VIEW_ALL_PLAN_SCREEN: Boolean = false

internal var triggerSubscriptionEvent: (eventType: SubscriptionEventType) -> Unit = {}

internal fun fireSubscriptionEvent(fEventType: SubscriptionEventType) {
    CoroutineScope(Dispatchers.IO).launch {
        triggerSubscriptionEvent.invoke(fEventType)
    }
}