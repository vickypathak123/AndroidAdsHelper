@file:Suppress("unused")

package com.example.app.ads.helper

import android.util.Log

/**
 * @author Akshay Harsoda
 * @since 16 Oct 2021
 * @updated 24 Jun 2024
 */

internal var isEnableDebugMode: Boolean = false
internal var isPurchaseHistoryLogEnable: Boolean = false

fun logD(tag: String, message: String) {
    if (isEnableDebugMode) {
        Log.d(tag, message)
    }
}

fun logI(tag: String, message: String) {
    if (isEnableDebugMode) {
        Log.i(tag, message)
    }
}

fun logE(tag: String, message: String) {
    if (isEnableDebugMode) {
        Log.e(tag, message)
    }
}

fun logW(tag: String, message: String) {
    if (isEnableDebugMode) {
        Log.w(tag, message)
    }
}