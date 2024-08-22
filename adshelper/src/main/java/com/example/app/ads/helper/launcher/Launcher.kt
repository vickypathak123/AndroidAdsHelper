package com.example.app.ads.helper.launcher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import com.example.app.ads.helper.R
import com.example.app.ads.helper.launcher.tabs.CustomTabsHelper
import com.example.app.ads.helper.stopShowingOpenAdInternally

object Launcher {
    private fun openUri(context: Context, uri: String): Boolean = runCatching {
        stopShowingOpenAdInternally()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        context.startActivity(intent)
        true
    }.getOrNull() ?: false

    private fun openCustomTabs(context: Context, uri: String, @ColorInt toolbarColor: Int, isNightMode: Boolean): Boolean =
        openCustomTabs(context, Uri.parse(uri), toolbarColor, isNightMode)

    private fun openCustomTabs(context: Context, uri: Uri, @ColorInt toolbarColor: Int, isNightMode: Boolean): Boolean = runCatching {
        stopShowingOpenAdInternally()
        val scheme =
            if (isNightMode) CustomTabsIntent.COLOR_SCHEME_DARK
            else CustomTabsIntent.COLOR_SCHEME_LIGHT
        val params = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(toolbarColor)
            .build()
        val intent = CustomTabsIntent.Builder(CustomTabsHelper.session)
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .setColorScheme(scheme)
            .setDefaultColorSchemeParams(params)
            .build()
        intent.intent.setPackage(CustomTabsHelper.packageNameToBind)
        intent.launchUrl(context, uri)
        true
    }.getOrNull() ?: false

    @JvmStatic
    fun openGooglePlay(context: Context, packageName: String, @ColorInt toolbarColor: Int, isNightMode: Boolean = false): Boolean =
        openUri(context, "market://details?id=$packageName") ||
                openCustomTabs(context, "https://play.google.com/store/apps/details?id=$packageName", toolbarColor, isNightMode)

    @JvmStatic
    fun openGooglePlay(context: Context, packageName: String, @ColorInt toolbarColor: Int): Boolean =
        openGooglePlay(context = context, packageName = packageName, toolbarColor = toolbarColor, isNightMode = false)

    @JvmStatic
    fun openGooglePlay(context: Context, packageName: String, isNightMode: Boolean): Boolean =
        openGooglePlay(context = context, packageName = packageName, toolbarColor = ContextCompat.getColor(context, R.color.default_time_line_main_color), isNightMode = isNightMode)

    @JvmStatic
    fun openGooglePlay(context: Context, packageName: String): Boolean =
        openGooglePlay(context = context, packageName = packageName, toolbarColor = ContextCompat.getColor(context, R.color.default_time_line_main_color), isNightMode = false)

    @JvmStatic
    fun openPrivacyPolicy(context: Context, fLink: String, @ColorInt toolbarColor: Int, isNightMode: Boolean = false) =
        openCustomTabs(context, fLink, toolbarColor, isNightMode)

    @JvmStatic
    fun openPrivacyPolicy(context: Context, fLink: String, @ColorInt toolbarColor: Int) =
        openPrivacyPolicy(context = context, fLink = fLink, toolbarColor = toolbarColor, isNightMode = false)

    @JvmStatic
    fun openPrivacyPolicy(context: Context, fLink: String, isNightMode: Boolean) =
        openPrivacyPolicy(context = context, fLink = fLink, toolbarColor = ContextCompat.getColor(context, R.color.default_time_line_main_color), isNightMode = isNightMode)

    @JvmStatic
    fun openPrivacyPolicy(context: Context, fLink: String) =
        openPrivacyPolicy(context = context, fLink = fLink, toolbarColor = ContextCompat.getColor(context, R.color.default_time_line_main_color), isNightMode = false)

    @JvmStatic
    fun openAnyLink(context: Context, uri: String, @ColorInt toolbarColor: Int, isNightMode: Boolean = false) =
        openCustomTabs(context, uri, toolbarColor, isNightMode)

    @JvmStatic
    fun openAnyLink(context: Context, uri: String, @ColorInt toolbarColor: Int) =
        openAnyLink(context = context, uri = uri, toolbarColor = toolbarColor, isNightMode = false)

    @JvmStatic
    fun openAnyLink(context: Context, uri: String, isNightMode: Boolean) =
        openAnyLink(context = context, uri = uri, toolbarColor = ContextCompat.getColor(context, R.color.default_time_line_main_color), isNightMode = isNightMode)

    @JvmStatic
    fun openAnyLink(context: Context, uri: String) =
        openAnyLink(context = context, uri = uri, toolbarColor = ContextCompat.getColor(context, R.color.default_time_line_main_color), isNightMode = false)

    @JvmStatic
    fun shareThisApp(context: Activity, packageName: String, appName: String) {
        val lAppMsg = "Download this amazing " + appName + " app from play store, " +
                "Please search in play store or Click on the link given below to " +
                "download. "

        val lFinalMsg = String.format(lAppMsg, appName, appName)

        shareThisApp(
            context = context,
            packageName = packageName,
            appName = appName,
            appMessage = lFinalMsg
        )
    }

    @JvmStatic
    fun shareThisApp(context: Activity, packageName: String, appName: String, appMessage: String) {
        stopShowingOpenAdInternally()
        val lAppLink = "https://play.google.com/store/apps/details?id=${packageName}"

        ShareCompat.IntentBuilder(context)
            .setType("text/plain")
            .setSubject(appName)
            .setText("\n\n$appMessage\n\n$lAppLink")
            .startChooser()
    }

    @JvmStatic
    fun sharePlainText(context: Activity, appName: String, text: String) {
        stopShowingOpenAdInternally()

        ShareCompat.IntentBuilder(context)
            .setType("text/plain")
            .setSubject(appName)
            .setText(text)
            .startChooser()
    }
}
