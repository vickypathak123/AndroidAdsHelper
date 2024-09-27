package com.example.app.ads.helper.remoteconfig

import android.content.Context
import com.example.app.ads.helper.utils.logD
import com.example.app.ads.helper.utils.logE
import com.example.app.ads.helper.utils.logI
import com.google.gson.Gson
import org.json.JSONObject
import java.io.IOException

private const val TAG: String = "RemoteConfigJSON"

var mVasuSubscriptionRemoteConfigModel: VasuSubscriptionRemoteConfigModel = VasuSubscriptionRemoteConfigModel()

/**
 * initialization of your remote config json
 *
 * @param jsonString it refers to your original remote config JSON.
 */
fun Context.initSubscriptionRemoteConfig(jsonString: String = "") {

    val finalJsonString: String = jsonString.takeIf { it.isNotEmpty() } ?: getJsonFromRaw(context = this) ?: ""

    logD(TAG, "initSubscriptionRemoteConfig: finalJsonString::-> $finalJsonString")

    if (finalJsonString.isNotEmpty()) {
        val jsonObject = JSONObject(finalJsonString)

        if (jsonObject.has("vasu_subscription_config")) {
            val vasuSubscriptionConfig = jsonObject.getJSONObject("vasu_subscription_config")
            mVasuSubscriptionRemoteConfigModel = Gson().fromJson(vasuSubscriptionConfig.toString(), VasuSubscriptionRemoteConfigModel::class.java)
        }
    }

    logI(TAG, "initSubscriptionRemoteConfig: $mVasuSubscriptionRemoteConfigModel")
}

private fun getJsonFromRaw(context: Context): String? {
    return try {
        val inputStream = context.resources.openRawResource(com.example.app.ads.helper.R.raw.vasu_subscription_remote_config)
        inputStream.bufferedReader().use { it.readText() }
    } catch (ex: IOException) {
        logE(TAG, "getJsonFromRaw: IOException: ${ex.message}")
        null
    }
}