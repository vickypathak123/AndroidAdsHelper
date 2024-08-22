@file:Suppress("unused")

package com.example.app.ads.helper.purchase.utils

import android.os.CountDownTimer
import com.example.app.ads.helper.logE
import java.util.Locale

class AdTimer(
    millisInFuture: Long,
    countDownInterval: Long,
    private val onTick: (countDownTime: Long) -> Unit = {},
    private val onFinish: () -> Unit = {},
) : CountDownTimer(millisInFuture, countDownInterval) {

    @Suppress("PrivatePropertyName")
    private val TAG: String = javaClass.simpleName

    var isRunning: Boolean = false

    override fun onTick(millisUntilFinished: Long) {
        val lCountDownTime = (millisUntilFinished / 1000)
        val formattedSeconds = String.format(Locale.getDefault(), "%02d", lCountDownTime)
        logE(TAG, "onTick: Formatted Time Number is $formattedSeconds")
        isRunning = true
        onTick.invoke(lCountDownTime)
    }

    override fun onFinish() {
        isRunning = false
        onFinish.invoke()
    }

    fun cancelTimer() {
        isRunning = false
        this.cancel()
    }
}