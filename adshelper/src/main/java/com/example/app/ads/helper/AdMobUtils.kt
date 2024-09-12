@file:Suppress("unused")

package com.example.app.ads.helper

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.text.TextUtils
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import com.example.app.ads.helper.purchase.SUBSCRIPTION_DATA_LANGUAGE_CODE
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

/**
 * @author Akshay Harsoda
 * @since 16 Oct 2021
 * @updated 24 Jun 2024
 */

/**
 * Extension method for add AdMob Ads test devise id's
 *
 * Find This Log in your logcat for get your devise test id
 * I/Ads: Use RequestConfiguration.Builder.setTestDeviceIds("TEST_DEVICE_ID","TEST_DEVICE_ID")
 *
 * @param fDeviceId pass multiple your "TEST_DEVICE_ID"
 */
internal fun setTestDeviceIds(vararg fDeviceId: String) {

    val lTestDeviceIds: ArrayList<String> = ArrayList()
    lTestDeviceIds.add(AdRequest.DEVICE_ID_EMULATOR)
    lTestDeviceIds.addAll(fDeviceId)

    val lConfiguration = RequestConfiguration.Builder().setTestDeviceIds(lTestDeviceIds).build()

    MobileAds.setRequestConfiguration(lConfiguration)
}

internal fun ArrayList<*>.clearAll() {
    this.clear()
    this.removeAll(this.toSet())
}


internal fun setColorAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float = 0.3f, @ColorInt baseColor: Int = 0x4cffffff): Int {
    val value: Float = alpha
    val clamp = min(1f.toDouble(), max(0f.toDouble(), value.toDouble())).toFloat()
    val intAlpha = (clamp * 255f).toInt()
    return intAlpha shl 24 or (baseColor and 0x00FFFFFF)
}

internal val String.toCamelCase: String
    get() {
        val words: Array<String> = this.split(" ").toTypedArray()

        val builder = StringBuilder()
        for (i in words.indices) {
            var word: String = words[i]
            word = if (word.isEmpty()) word else Character.toUpperCase(word[0])
                .toString() + word.substring(1).lowercase()
            builder.append(word)
            if (i != (words.size - 1)) {
                builder.append(" ")
            }
        }
        return builder.toString()
    }

internal val isRTLDirectionFromLocale: Boolean get() = TextUtils.getLayoutDirectionFromLocale(dataLocale) == View.LAYOUT_DIRECTION_RTL

internal inline val dataLocale: Locale get() = Locale(SUBSCRIPTION_DATA_LANGUAGE_CODE.takeIf { it.isNotEmpty() } ?: "en")

internal inline fun <reified T> getLocalizedString(
    context: Context,
    fLocale: Locale = dataLocale,
    @StringRes resourceId: Int,
    vararg formatArgs: T = emptyArray()
): String {
    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(fLocale)
    val localizedContext = context.createConfigurationContext(configuration)
    return if (formatArgs.isNotEmpty()) {
        localizedContext.resources.getString(resourceId, *formatArgs)
    } else {
        localizedContext.resources.getString(resourceId)
    }
}

internal fun Activity.exitTheApp() {
    this.setResult(Activity.RESULT_CANCELED)
    this.finishAffinity()
    this.finishAfterTransition()
//    exitProcess(0)
}






