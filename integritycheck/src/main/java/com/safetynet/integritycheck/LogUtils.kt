@file:Suppress("unused")

package com.safetynet.integritycheck

import android.util.Log

/**
 * @author Akshay Harsoda
 * @since 16 Oct 2021
 * @updated 24 Jun 2024
 */

internal var isEnableDebugMode: Boolean = false

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