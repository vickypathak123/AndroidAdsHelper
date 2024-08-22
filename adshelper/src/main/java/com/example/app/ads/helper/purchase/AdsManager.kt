@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.example.app.ads.helper.purchase

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.app.ads.helper.getUIThread
import com.example.app.ads.helper.notification.KEY_SUBSCRIPTION_NOTIFICATION_INTENT
import com.example.app.ads.helper.notification.NotificationDataModel
import com.example.app.ads.helper.updateAppPurchasedStatusRemoveAds
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

class AdsManager(context: Context) {
    @Suppress("PrivatePropertyName")
    private val TAG: String = javaClass.simpleName

    companion object {
        val isShowAds: MutableLiveData<Boolean> = MutableLiveData()
    }

    // SP to be save & retrieve
    private val sp: SharedPreferences = SharedPreferences(context)

    fun onProductPurchased() {
        Log.e(TAG, "onProductPurchased")
        sp.isLifeTimePlanPurchased = true
        updateAdsVisibility()
    }

    fun onProductExpired() {
        Log.e(TAG, "onProductExpired")
        sp.isLifeTimePlanPurchased = false
        updateAdsVisibility()
    }

    val isLifeTimePlanPurchased: Boolean get() = sp.isLifeTimePlanPurchased

    fun onProductSubscribed() {
        Log.e(TAG, "onProductSubscribed")
        sp.isAnyPlanSubscribed = true
        updateAdsVisibility()
    }

    fun onSubscribeExpired() {
        Log.e(TAG, "onSubscribeExpired")
        sp.isAnyPlanSubscribed = false
        updateAdsVisibility()
    }

    val isAnyPlanSubscribed: Boolean get() = sp.isAnyPlanSubscribed

    fun onProductTestPurchase() {
        Log.e(TAG, "onProductTestPurchase")
        sp.isTestPurchase = true
        updateAdsVisibility()
    }

    val isTestPurchase: Boolean get() = sp.isTestPurchase

    inline val isNeedToShowAds: Boolean
        get() {
            return isShowAds.value == true
        }

    private fun updateAdsVisibility() {
        getUIThread {
            val newValue = (!(isLifeTimePlanPurchased.takeIf { it } ?: isAnyPlanSubscribed.takeIf { it } ?: isTestPurchase.takeIf { it } ?: false))
            if (!newValue) {
                updateAppPurchasedStatusRemoveAds()
            }
            val oldValue = isShowAds.value
            if (oldValue != newValue) {
                isShowAds.value = newValue
            }
        }
    }

    var notificationData: NotificationDataModel?
        get() = sp.notificationData
        set(fValue) {
            sp.notificationData = fValue
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
    }
}