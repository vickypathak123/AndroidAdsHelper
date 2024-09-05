@file:Suppress("unused")

package com.safetynet.integritycheck.integrity

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.widget.Toast
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.StandardIntegrityManager
import com.google.android.play.core.integrity.model.IntegrityDialogResponseCode.DIALOG_CANCELLED
import com.google.android.play.core.integrity.model.IntegrityDialogResponseCode.DIALOG_FAILED
import com.google.android.play.core.integrity.model.IntegrityDialogResponseCode.DIALOG_SUCCESSFUL
import com.google.android.play.core.integrity.model.IntegrityDialogResponseCode.DIALOG_UNAVAILABLE
import com.google.android.play.core.integrity.model.IntegrityDialogTypeCode.GET_LICENSED
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.playintegrity.v1.PlayIntegrity
import com.google.api.services.playintegrity.v1.PlayIntegrityRequestInitializer
import com.google.api.services.playintegrity.v1.model.DecodeIntegrityTokenRequest
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.safetynet.integritycheck.isOnlineApp
import com.safetynet.integritycheck.logE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess

/**
 * @author Akshay Harsoda
 * @since 05 Sep 2024
 */
object AppProtector {

    private val TAG: String = javaClass.simpleName

//    private var setAdsID: SetAdsID? = null

    @JvmStatic
    fun with(fContext: Activity): CheckIntegrity {
        return CheckIntegrity(fContext = fContext)
    }

    class CheckIntegrity(fContext: Activity) : Serializable {
        private val mContextRef: WeakReference<Activity> = WeakReference(fContext)
        private val mContext: Activity get() = mContextRef.get()!!

        private var appName: String = ""
        private var appPackageName: String = ""
        private var cloudProjectNumber: Long = 0
        private var playIntegrityRemoteConfigJson: String = "{" +
                "\"errorHide\": true," +
                "\"verdictsResponseCodes\": [" +
//                "\"UNRECOGNIZED_VERSION\"," +
//                "\"UNEVALUATED\"" +
                "]" +
                "}"
        private var testDeviceId: String = ""
        private var isEnableDebugMode: Boolean = false
        private var isBlockCheckIntegrity: Boolean = false
        private val isMobileAdsInitializeCalled = AtomicBoolean(false)

        @JvmName("appName")
        fun appName(appName: String) = this@CheckIntegrity.apply {
            this.appName = appName
        }

        @JvmName("packageName")
        fun packageName(packageName: String) = this@CheckIntegrity.apply {
            this.appPackageName = packageName
        }

        /**
         * Helper method to set Cloud project number which you can find from google-services.json file
         */
        @JvmName("cloudProjectNumber")
        fun cloudProjectNumber(cloudProjectNumber: Long) = this@CheckIntegrity.apply {
            this.cloudProjectNumber = cloudProjectNumber
        }

        /**
         * Helper method to set Json which you can get from Remote config
         */
        @JvmName("playIntegrityRemoteConfigJson")
        fun playIntegrityRemoteConfigJson(remoteConfigJson: String) = this@CheckIntegrity.apply {
            this.playIntegrityRemoteConfigJson = remoteConfigJson
        }

        /**
         * Helper method to set deviceId which you can get from logs
         * it's required field to set debug testing for google-consent
         * Check your logcat output for the hashed device ID e.g.
         * "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345")" to use
         */
        @JvmName("deviceId")
        fun setTestDeviceId(deviceId: String) = this@CheckIntegrity.apply {
            this.testDeviceId = deviceId
        }

        @JvmName("enableDebugMode")
        fun enableDebugMode(fIsEnable: Boolean) = this@CheckIntegrity.apply {
            this.isEnableDebugMode = fIsEnable
        }

        @JvmName("needToBlockInterstitialAd")
        fun needToBlockCheckIntegrity(fIsBlock: Boolean) = this@CheckIntegrity.apply {
            this.isBlockCheckIntegrity = fIsBlock
        }

        @JvmName("checkIntegrity")
        fun checkIntegrity(checkPlayIntegrityStatus: () -> Unit) {
            com.safetynet.integritycheck.isEnableDebugMode = this.isEnableDebugMode
            if (this.isBlockCheckIntegrity) {
                checkPlayIntegrityStatus.invoke()
            } else {
                try {
                    if (mContext.isOnlineApp) {
                        requestIntegrityToken(
                            context = mContext,
                            packageName = appPackageName,
                            cloudProjectNumber = cloudProjectNumber,
                            playIntegrityRemoteConfigJson = playIntegrityRemoteConfigJson,
                            callback = { type ->
                                when (type) {
                                    LicenseType.NOT_SAFE -> logE(TAG, "checkIntegrity: LICENSE NOT_SAFE")

                                    LicenseType.SAFE,
                                    LicenseType.ERROR,
                                    LicenseType.OLD_PLAY_STORE -> {
                                        val googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(mContext)

                                        googleMobileAdsConsentManager.gatherConsent(
                                            activity = mContext,
                                            deviceId = testDeviceId,
                                            isDebug = isEnableDebugMode,
                                            onConsentGatheringCompleteListener = { consentError ->
                                                if (consentError != null) {
                                                    // Consent not obtained in current session.
                                                    logE(TAG, "checkIntegrity: consentError errorCode::-> ${consentError.errorCode}, message::-> ${consentError.message}")
                                                }


                                                logE(TAG, "checkIntegrity: consentResult errorCode::-> ${consentError?.errorCode}, message::-> ${consentError?.message}")

                                                if (googleMobileAdsConsentManager.canRequestAds) {
                                                    if (isMobileAdsInitializeCalled.getAndSet(true)) {
                                                        logE(TAG, "checkIntegrity: consentError errorCode::-> ${consentError?.errorCode}, message::-> ${consentError?.message}")
                                                    } else {
                                                        checkPlayIntegrityStatus.invoke()
                                                    }
                                                } else {
                                                    checkPlayIntegrityStatus.invoke()
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    } else {
                        logE(TAG, "checkIntegrity: Internet is not available")
                        checkPlayIntegrityStatus.invoke()
                    }
                } catch (e: Exception) {
                    logE(TAG, "checkIntegrity: catch: ${e.message}")
                    checkPlayIntegrityStatus.invoke()
                }
            }
        }

        enum class LicenseType {
            SAFE, NOT_SAFE, ERROR, OLD_PLAY_STORE
        }

        private fun requestIntegrityToken(
            context: Activity,
            packageName: String,
            cloudProjectNumber: Long,
            playIntegrityRemoteConfigJson: String,
            callback: (type: LicenseType) -> Unit
        ) {
            try {

                var showError = true
                var verdictCodeList = arrayListOf<String>()

                if (playIntegrityRemoteConfigJson.isNotEmpty()) {
                    val obj = JSONObject(playIntegrityRemoteConfigJson)
                    if (obj.has("errorHide")) {
                        showError = obj.getBoolean("errorHide")
                    }
                    if (obj.has("verdictsResponseCodes")) {
                        verdictCodeList = getVerdictCodeList(data = obj.getJSONArray("verdictsResponseCodes"))
                    }
                }

                logE(TAG, "requestIntegrityToken: showError::-> $showError, verdictCodeList::-> $verdictCodeList")
                logE(TAG, "requestIntegrityToken: Start Integrity")

                IntegrityManagerFactory.createStandard(context)
                    .prepareIntegrityToken(
                        StandardIntegrityManager.PrepareIntegrityTokenRequest.builder()
                            .setCloudProjectNumber(cloudProjectNumber)
                            .build()
                    )
                    .addOnSuccessListener { tokenProvider ->
                        logE(TAG, "IntegrityManagerFactory: addOnSuccessListener: integrityTokenProvider::-> $tokenProvider")

                        val requestHash = generateNonce()

                        val credentials = GoogleCredentials.fromStream(
                            requireNotNull(context.javaClass.classLoader?.getResourceAsStream("credentials.json"))
                        )

                        tokenProvider.request(
                            StandardIntegrityManager.StandardIntegrityTokenRequest.builder()
                                .setRequestHash(
                                    requestHash
                                )
                                .build()
                        )
                            .addOnSuccessListener { response: StandardIntegrityManager.StandardIntegrityToken ->
                                logE(TAG, "tokenProvider: addOnSuccessListener: Token::-> ${response.token()}")

                                val requestInitializer: HttpRequestInitializer = HttpCredentialsAdapter(credentials)
                                val httpTransport = NetHttpTransport()
                                val jsonFactory = JacksonFactory()
                                val initializer = PlayIntegrityRequestInitializer()

                                val play = PlayIntegrity.Builder(httpTransport, jsonFactory, requestInitializer)
                                    .setApplicationName(packageName)
                                    .setGoogleClientRequestInitializer(initializer)
                                    .build()

                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val content = DecodeIntegrityTokenRequest().setIntegrityToken(response.token())
                                        val apiResponse = play.v1().decodeIntegrityToken(packageName, content).execute()
                                        logE(TAG, "tokenProvider: addOnSuccessListener: apiResponse::-> $apiResponse")

                                        showToast(context = context, showError = showError, msg = "apiResponse: $apiResponse")

                                        val appLicensingVerdict = apiResponse.tokenPayloadExternal.accountDetails.appLicensingVerdict
                                        val appRecognitionVerdict = apiResponse.tokenPayloadExternal.appIntegrity.appRecognitionVerdict
                                        val deviceRecognitionVerdict = apiResponse.tokenPayloadExternal.deviceIntegrity.deviceRecognitionVerdict
                                        logE(
                                            TAG,
                                            "tokenProvider: addOnSuccessListener: " +
                                                    "\nappLicensingVerdict::-> $appLicensingVerdict" +
                                                    "\nappRecognitionVerdict::-> $appRecognitionVerdict" +
                                                    "\ndeviceRecognitionVerdict::-> $deviceRecognitionVerdict" +
                                                    ""
                                        )

                                        if (appLicensingVerdict == "LICENSED"
                                            && appRecognitionVerdict == "PLAY_RECOGNIZED"
                                            && deviceRecognitionVerdict[0] == "MEETS_DEVICE_INTEGRITY"
                                        ) {
                                            callback.invoke(LicenseType.SAFE)
                                        } else {
                                            val isSafeLast = verdictCodeList.isNotEmpty()
                                                    && (appLicensingVerdict == "LICENSED" || verdictCodeList.contains(appLicensingVerdict))
                                                    && (appRecognitionVerdict == "PLAY_RECOGNIZED" || verdictCodeList.contains(appRecognitionVerdict))
                                                    && (deviceRecognitionVerdict[0] == "MEETS_DEVICE_INTEGRITY" || verdictCodeList.contains(deviceRecognitionVerdict[0]))

                                            if (isSafeLast) {
                                                callback.invoke(LicenseType.SAFE)
                                            } else {
                                                response.showDialog(context, GET_LICENSED)
                                                    .addOnCanceledListener {
                                                        logE(TAG, "showDialog: addOnCanceledListener: Dialog closed")
                                                    }
                                                    .addOnCompleteListener {
                                                        when (it.result) {
                                                            DIALOG_CANCELLED -> {
                                                                logE(TAG, "showDialog: addOnCompleteListener: DIALOG_CANCELLED")
                                                                context.setResult(Activity.RESULT_CANCELED)
                                                                context.finishAffinity()
                                                                context.finishAfterTransition()
                                                                exitProcess(0)
                                                            }

                                                            DIALOG_FAILED -> logE(TAG, "showDialog: addOnCompleteListener: DIALOG_FAILED")
                                                            DIALOG_SUCCESSFUL -> {
                                                                logE(TAG, "showDialog: addOnCompleteListener: DIALOG_SUCCESSFUL")
                                                                requestIntegrityToken(
                                                                    context = context,
                                                                    packageName = packageName,
                                                                    cloudProjectNumber = cloudProjectNumber,
                                                                    playIntegrityRemoteConfigJson = playIntegrityRemoteConfigJson,
                                                                    callback = callback,
                                                                )
                                                            }

                                                            DIALOG_UNAVAILABLE -> logE(TAG, "showDialog: addOnCompleteListener: DIALOG_UNAVAILABLE")
                                                        }
                                                    }
                                                    .addOnFailureListener {
                                                        logE(TAG, "showDialog: addOnFailureListener: dialog Exception::-> $it")
                                                    }
                                            }
                                        }

                                    } catch (e: Exception) {
                                        logE(TAG, "tokenProvider: addOnSuccessListener: catch: ${e.message}")
                                        e.printStackTrace()
                                        callback.invoke(LicenseType.SAFE)
                                    }
                                }
                            }
                            .addOnFailureListener { exception ->
                                logE(TAG, "requestIntegrityToken: addOnFailureListener: tokenProvider::-> $exception")
                                callback.invoke(LicenseType.SAFE)
                            }
                    }
                    .addOnFailureListener { exception ->
                        logE(TAG, "tokenProvider: addOnFailureListener: $exception")
                        callback.invoke(LicenseType.SAFE)
                    }


            } catch (e: Exception) {
                logE(TAG, "requestIntegrityToken: catch: ${e.message}")
                callback.invoke(LicenseType.SAFE)
            }
        }

        private fun getVerdictCodeList(data: JSONArray): ArrayList<String> {
            val list = arrayListOf<String>()
            for (i in 0 until data.length()) {
                list.add(data.getString(i))
            }
            return list
        }

        private fun generateNonce(): String {
            val allowed = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            val token = (1..50).map { allowed.random() }.joinToString("")
                .toByteArray(Charsets.UTF_8)
                .let { Base64.encodeToString(it, Base64.URL_SAFE) }
            logE(TAG, "generateNonce: Token::-> $token")
            return token
        }

        private fun showToast(context: Context, showError: Boolean, msg: String) {
            CoroutineScope(Dispatchers.Main).launch {
                Handler(Looper.getMainLooper()).post {
                    if (showError) {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}