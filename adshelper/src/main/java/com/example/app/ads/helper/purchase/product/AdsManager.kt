@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.example.app.ads.helper.purchase.product

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.app.ads.helper.utils.getUIThread
import com.example.app.ads.helper.utils.isAppNotPurchased
import com.example.app.ads.helper.utils.isInternetAvailable
import com.example.app.ads.helper.utils.isOnlineApp
import com.example.app.ads.helper.utils.logE
import com.example.app.ads.helper.notification.KEY_SUBSCRIPTION_NOTIFICATION_INTENT
import com.example.app.ads.helper.notification.NotificationDataModel
import com.example.app.ads.helper.utils.updateAppPurchasedStatusRemoveAds
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * @author Akshay Harsoda
 * @since 21 Jul 2021
 * @updated 22 Jul 2024
 */

private const val KEY_LIFE_TIME_PLAN_PURCHASE = "key_life_time_plan_purchase"
private const val KEY_ANY_PLAN_SUBSCRIBE = "key_any_plan_subscribe"
private const val KEY_TEST_PURCHASE = "key_test_purchase"
private const val KEY_INITIAL_SUBSCRIPTION_OPEN_FLOW_INDEX = "key_initial_subscription_open_flow_index"
private const val KEY_NEED_TO_OPEN_REVIEW_DIALOG = "key_need_to_open_review_dialog"
private const val KEY_NEED_TO_OPEN_RATE_APP_DIALOG = "key_need_to_open_rate_app_dialog"

class AdsManager(private val context: Context) {
    @Suppress("PrivatePropertyName")
    private val TAG: String = javaClass.simpleName

    companion object {
        val isShowAds: MutableLiveData<Boolean> = MutableLiveData()
    }

    // SP to be save & retrieve
    private val sp: SharedPreferences = SharedPreferences(context)

    internal fun onProductPurchased() {
        logE(TAG, "onProductPurchased: ")
        sp.isLifeTimePlanPurchased = true
        updateAdsVisibility()
    }

    internal fun onProductExpired() {
        logE(TAG, "onProductExpired: ")
        sp.isLifeTimePlanPurchased = false
        updateAdsVisibility()
    }

    val isLifeTimePlanPurchased: Boolean get() = sp.isLifeTimePlanPurchased

    internal fun onProductSubscribed() {
        logE(TAG, "onProductSubscribed: ")
        sp.isAnyPlanSubscribed = true
        updateAdsVisibility()
    }

    internal fun onSubscribeExpired() {
        logE(TAG, "onSubscribeExpired: ")
        sp.isAnyPlanSubscribed = false
        updateAdsVisibility()
    }

    val isAnyPlanSubscribed: Boolean get() = sp.isAnyPlanSubscribed

    internal fun onProductTestPurchase() {
        logE(TAG, "onProductTestPurchase: ")
        sp.isTestPurchase = true
        updateAdsVisibility()
    }

    val isTestPurchase: Boolean get() = sp.isTestPurchase

    inline val isNeedToShowAds: Boolean
        get() {
            return isShowAds.value == true
        }

    internal fun updateAdsVisibility() {
        getUIThread {
            val newValue = (!(isLifeTimePlanPurchased.takeIf { it } ?: isAnyPlanSubscribed.takeIf { it } ?: isTestPurchase.takeIf { it } ?: false))
            if (!newValue) {
                updateAppPurchasedStatusRemoveAds()
            } else {
                isAppNotPurchased = true
            }
            val oldValue = isShowAds.value
            if (oldValue != newValue) {
                isShowAds.value = newValue
                isInternetAvailable.value = context.isOnlineApp
            }
        }
    }

    var notificationData: NotificationDataModel?
        get() = sp.notificationData
        set(fValue) {
            sp.notificationData = fValue
        }

    var initialSubscriptionOpenFlowIndex: Int
        get() = sp.initialSubscriptionOpenFlowIndex
        set(fValue) {
            sp.initialSubscriptionOpenFlowIndex = fValue
        }

    internal var isReviewDialogOpened: Boolean
        get() = sp.isReviewDialogOpened
//        get() = false
        set(fValue) {
            sp.isReviewDialogOpened = fValue
        }

    internal var isRateAppDialogOpened: Boolean
        get() = sp.isRateAppDialogOpened
        set(fValue) {
            sp.isRateAppDialogOpened = fValue
        }

    /**
     *   SharedPreferences helper class
     */
    private inner class SharedPreferences(private val context: Context) {
        private val myPreferences = "ads_pref"

        var isLifeTimePlanPurchased: Boolean
            get() = context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE).getBoolean(KEY_LIFE_TIME_PLAN_PURCHASE, false)
            set(value) {
                context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(KEY_LIFE_TIME_PLAN_PURCHASE, value)
                    .apply()
            }

        var isAnyPlanSubscribed: Boolean
            get() = context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE).getBoolean(KEY_ANY_PLAN_SUBSCRIBE, false)
            set(value) {
                context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(KEY_ANY_PLAN_SUBSCRIBE, value)
                    .apply()
            }

        var isTestPurchase: Boolean
            get() = context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE).getBoolean(KEY_TEST_PURCHASE, false)
            set(value) {
                context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(KEY_TEST_PURCHASE, value)
                    .apply()
            }

        var notificationData: NotificationDataModel?
            get() {
                val lJson: String = context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE).getString(KEY_SUBSCRIPTION_NOTIFICATION_INTENT, "") ?: ""
                if (lJson.isNotEmpty()) {
                    val lType = object : TypeToken<NotificationDataModel>() {}.type
                    return Gson().fromJson(lJson, lType)
                } else {
                    return null
                }
            } // get according your preference parameter type
            set(fValue) {
                context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_SUBSCRIPTION_NOTIFICATION_INTENT, Gson().toJson(fValue))
                    .apply()
            } // set according your preference parameter type

        var initialSubscriptionOpenFlowIndex: Int
            get() = context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE).getInt(KEY_INITIAL_SUBSCRIPTION_OPEN_FLOW_INDEX, 0)
            set(fValue) {
                context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
                    .edit()
                    .putInt(KEY_INITIAL_SUBSCRIPTION_OPEN_FLOW_INDEX, fValue)
                    .apply()
            }

        var isReviewDialogOpened: Boolean
            get() = context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE).getBoolean(KEY_NEED_TO_OPEN_REVIEW_DIALOG, false)
            set(fValue) {
                context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(KEY_NEED_TO_OPEN_REVIEW_DIALOG, fValue)
                    .apply()
            }

        var isRateAppDialogOpened: Boolean
            get() = context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE).getBoolean(KEY_NEED_TO_OPEN_RATE_APP_DIALOG, false)
            set(fValue) {
                context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(KEY_NEED_TO_OPEN_RATE_APP_DIALOG, fValue)
                    .apply()
            }
    }
}