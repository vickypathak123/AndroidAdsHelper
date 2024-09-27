package com.example.app.ads.helper.retrofit.listener

import androidx.annotation.UiThread

interface APIResponseListener<T> {
    @UiThread
    fun onSuccess(fResponse : T)
    @UiThread
    fun onError(fErrorMessage: String?) {}
}
