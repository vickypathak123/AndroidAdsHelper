package com.example.ads.helper.new_.demo.newdemo.utils

import android.content.Context
import android.util.Log
import com.example.ads.helper.new_.demo.base.shared_prefs.getString
import com.example.ads.helper.new_.demo.base.shared_prefs.save
import com.example.app.ads.helper.remoteconfig.initSubscriptionRemoteConfig
import com.google.gson.Gson

private const val TAG: String = "AppRemoteConfigUtils"

const val isRunAppWithTestingRemoteConfig: Boolean = false
const val isEnableTestPurchase: Boolean = true

const val TEST_REMOTE_CONFIG_KEY: String = "YOUR_APP_NAME_TEST"
const val LIVE_REMOTE_CONFIG_KEY: String = "YOUR_APP_NAME"
private val REMOTE_CONFIG_KEY: String get() = TEST_REMOTE_CONFIG_KEY.takeIf { isRunAppWithTestingRemoteConfig } ?: LIVE_REMOTE_CONFIG_KEY

private var newRemoteConfigModel: AppRemoteConfigModel? = null
private val Context.savedRemoteConfigModel: AppRemoteConfigModel get() = Gson().fromJson(this.getString(fKey = REMOTE_CONFIG_KEY, fDefaultValue = AppRemoteConfigModel().toString()), AppRemoteConfigModel::class.java) ?: AppRemoteConfigModel()
val Context.remoteConfigModel: AppRemoteConfigModel get() = newRemoteConfigModel ?: savedRemoteConfigModel

/**
 * only for testing purpose
 */
fun Context.setRemoteConfigJson(fDataModel: AppRemoteConfigModel, action: () -> Unit) {
    this.saveRemoteConfig(jsonString = Gson().toJson(fDataModel), action = action)
}


fun Context.setOfflineRemoteConfig(action: () -> Unit) {
    val jsonString: String = this.getString(fKey = REMOTE_CONFIG_KEY, fDefaultValue = AppRemoteConfigModel().toString())
    initRemoteConfig(jsonString = jsonString, action = action)
}

private fun Context.saveRemoteConfig(jsonString: String, action: () -> Unit) {
    this.save(fKey = REMOTE_CONFIG_KEY, fValue = jsonString)
    initRemoteConfig(jsonString = jsonString, action = action)
}

private fun Context.initRemoteConfig(jsonString: String, action: () -> Unit) {
    newRemoteConfigModel = Gson().fromJson(jsonString, AppRemoteConfigModel::class.java)
    Log.i(TAG, "initRemoteConfig: \n$remoteConfigModel")
    this.initSubscriptionRemoteConfig(jsonString = this.remoteConfigModel.toString())
    action.invoke()
}