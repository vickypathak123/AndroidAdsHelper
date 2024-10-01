package com.example.app.ads.helper.retrofit.enqueue

import android.util.Log
import com.example.app.ads.helper.retrofit.builder.APIBuilder.forceUpdateClient
import com.example.app.ads.helper.retrofit.builder.APIBuilder.reviewClient
import com.example.app.ads.helper.retrofit.listener.APIResponseListener
import com.example.app.ads.helper.retrofit.model.ForceUpdateModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object APICallEnqueue {

    private val TAG: String = javaClass.simpleName

    fun forceUpdateApi(
        packageName: String,
        versionCode: String,
        fListener: APIResponseListener<ForceUpdateModel>,
    ) {
        if (packageName.isEmpty()) {
            throw RuntimeException("App Package Name is not set. Please set your App Package Name first for check force update.")
        } else if (versionCode.isEmpty()) {
            throw RuntimeException("App Version Name is not set. Please set your App Version Name first for check force update.")
        } else {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = CoroutineScope(Dispatchers.IO).async {
                        forceUpdateClient.forceUpdateApi(
                            packageName = packageName.toRequestBody("text/plain".toMediaType()),
                            versionCode = versionCode.toRequestBody("text/plain".toMediaType())
                        )
                    }.await()

                    CoroutineScope(Dispatchers.Main).launch {
                        Log.e(TAG, "forceUpdateApi: \npackageName::$packageName, \nversionCode::$versionCode, \nresponse::${response}")
                        fListener.onSuccess(response)
                    }
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    fListener.onError(e.message)
                }
            }
        }
    }

    fun subscriptionReviewApi(
        packageName: String,
        versionCode: String,
        languageKey: String,
        subscriptionReview: String,
        fListener: APIResponseListener<JSONObject>,
    ) {
        if (packageName.isEmpty()) {
            throw RuntimeException("App Package Name is not set. Please set your App Package Name first for submit subscription review.")
        } else if (versionCode.isEmpty()) {
            throw RuntimeException("App Version Name is not set. Please set your App Version Name first for submit subscription review.")
        } else {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = CoroutineScope(Dispatchers.IO).async {
                        reviewClient.subscriptionReviewApi(
                            packageName = packageName,
                            versionCode = versionCode,
                            languageKey = languageKey,
                            subscriptionReview = subscriptionReview,
                        )
                    }.await()

                    CoroutineScope(Dispatchers.Main).launch {
                        Log.e(TAG, "subscriptionReviewApi: \npackageName::$packageName, \nversionCode::$versionCode, \nlanguageKey::$languageKey, \nresponse::${response}")
                        fListener.onSuccess(response)
                    }
                }


            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    fListener.onError(e.message)
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
        fListener: APIResponseListener<JSONObject>,
    ) {
        if (packageName.isEmpty()) {
            throw RuntimeException("App Package Name is not set. Please set your App Package Name first for submit feedback.")
        } else if (versionCode.isEmpty()) {
            throw RuntimeException("App Version Name is not set. Please set your App Version Name first for submit feedback.")
        } else {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = CoroutineScope(Dispatchers.IO).async {
                        reviewClient.feedbackApi(
                            packageName = packageName,
                            versionCode = versionCode,
                            languageKey = languageKey,
                            review = review,
                            useOfApp = useOfApp,
                        )
                    }.await()

                    CoroutineScope(Dispatchers.Main).launch {
                        Log.e(TAG, "feedbackApi: \npackageName::$packageName, \nversionCode::$versionCode, \nlanguageKey::$languageKey, \nresponse::${response}")
                        fListener.onSuccess(response)
                    }
                }


            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    fListener.onError(e.message)
                }
            }
        }
    }
}