package com.example.app.ads.helper.retrofit.enqueue

import android.util.Log
import com.example.app.ads.helper.retrofit.builder.APIBuilder.forceUpdateClient
import com.example.app.ads.helper.retrofit.builder.APIBuilder.reviewClient
import com.example.app.ads.helper.retrofit.listener.APIResponseListener
import com.example.app.ads.helper.retrofit.model.ForceUpdateModel
import com.example.app.ads.helper.utils.isOnline
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

object APICallEnqueue {

    private val TAG: String = javaClass.simpleName

    fun forceUpdateApi(
        packageName: String,
        versionCode: String,
        fListener: APIResponseListener<ForceUpdateModel>,
    ) {
        if (packageName.isEmpty()) {
            fListener.onError("")
            throw RuntimeException("App Package Name is not set. Please set your App Package Name first for check force update.")
        } else if (versionCode.isEmpty()) {
            fListener.onError("")
            throw RuntimeException("App Version Name is not set. Please set your App Version Name first for check force update.")
        } else {
            if (isOnline) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = CoroutineScope(Dispatchers.IO).async {
                            forceUpdateClient.forceUpdateApi(
                                packageName = packageName.toRequestBody("text/plain".toMediaType()),
                                versionCode = versionCode.toRequestBody("text/plain".toMediaType())
                            )
                        }.await()

                        withContext(Dispatchers.Main) {
                            Log.e(TAG, "forceUpdateApi: \npackageName::$packageName, \nversionCode::$versionCode, \nresponse::${response}")
                            fListener.onSuccess(response)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            fListener.onError(e.message)
                        }
                    }
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    fListener.onError("device is offline")
                }
            }
        }
    }

    fun subscriptionReviewApi(
        packageName: String,
        versionCode: String,
        languageKey: String,
        subscriptionReview: String,
        fListener: APIResponseListener<JsonObject>,
    ) {
        if (packageName.isEmpty()) {
            fListener.onError("")
            throw RuntimeException("App Package Name is not set. Please set your App Package Name first for submit subscription review.")
        } else if (versionCode.isEmpty()) {
            fListener.onError("")
            throw RuntimeException("App Version Name is not set. Please set your App Version Name first for submit subscription review.")
        } else {
            if (isOnline) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = CoroutineScope(Dispatchers.IO).async {
                            reviewClient.subscriptionReviewApi(
                                packageName = packageName,
                                versionCode = versionCode,
                                languageKey = languageKey,
                                subscriptionReview = subscriptionReview,
                            )
                        }.await()

                        withContext(Dispatchers.Main) {
                            Log.e(TAG, "subscriptionReviewApi: \npackageName::$packageName, \nversionCode::$versionCode, \nlanguageKey::$languageKey, \nresponse::${response}")
                            fListener.onSuccess(response)
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            fListener.onError(e.message)
                        }
                    }
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    fListener.onError("device is offline")
                }
            }
        }
    }

    fun feedbackApi(
        packageName: String,
        versionCode: String,
        languageKey: String,
        review: String,
        useOfApp: String,
        fListener: APIResponseListener<JsonObject>,
    ) {
        if (packageName.isEmpty()) {
            fListener.onError("")
            throw RuntimeException("App Package Name is not set. Please set your App Package Name first for submit feedback.")
        } else if (versionCode.isEmpty()) {
            fListener.onError("")
            throw RuntimeException("App Version Name is not set. Please set your App Version Name first for submit feedback.")
        } else {
            if (isOnline) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = CoroutineScope(Dispatchers.IO).async {
                            reviewClient.feedbackApi(
                                packageName = packageName,
                                versionCode = versionCode,
                                languageKey = languageKey,
                                review = review,
                                useOfApp = useOfApp,
                            )
                        }.await()

                        withContext(Dispatchers.Main) {
                            Log.e(TAG, "feedbackApi: \npackageName::$packageName, \nversionCode::$versionCode, \nlanguageKey::$languageKey, \nresponse::${response}")
                            fListener.onSuccess(response)
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            fListener.onError(e.message)
                        }
                    }
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    fListener.onError("device is offline")
                }
            }
        }
    }
}
