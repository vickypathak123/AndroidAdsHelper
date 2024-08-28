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

    private var _isRunning: Boolean = false
    var isRunning: Boolean
        get() = _isRunning
        set(value) {
            _isRunning = value
        }

    override fun onTick(millisUntilFinished: Long) {
        val lCountDownTime = (millisUntilFinished / 1000)
        val formattedSeconds = String.format(Locale.getDefault(), "%02d", lCountDownTime)
        logE(TAG, "onTick: Formatted Time Number is $formattedSeconds")
        _isRunning = true
        onTick.invoke(lCountDownTime)
    }

    override fun onFinish() {
        _isRunning = false
        onFinish.invoke()
    }

    fun cancelTimer() {
        _isRunning = false
        this.cancel()
    }
}