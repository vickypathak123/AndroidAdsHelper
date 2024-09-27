@file:Suppress("unused")

package com.example.app.ads.helper.revenuecat

import android.util.Log
import com.example.app.ads.helper.utils.getDebugModeStatus
import com.example.app.ads.helper.utils.getPurchaseHistoryLogStatus

/**
 * @author Akshay Harsoda
 * @since 16 Oct 2021
 * @updated 24 Jun 2024
 */

internal val isEnableDebugMode: Boolean get() = getDebugModeStatus()
internal val isPurchaseHistoryLogEnable: Boolean get() = getPurchaseHistoryLogStatus()

internal fun logD(tag: String, message: String) {
    if (isEnableDebugMode) {
        Log.d(tag, message)
    }
}

internal fun logI(tag: String, message: String) {
    if (isEnableDebugMode) {
        Log.i(tag, message)
    }
}

internal fun logE(tag: String, message: String) {
    if (isEnableDebugMode) {
        Log.e(tag, message)
    }
}

internal fun logW(tag: String, message: String) {
    if (isEnableDebugMode) {
        Log.w(tag, message)
    }
}