@file:Suppress("unused")

package com.example.app.ads.helper.base.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.view.updateLayoutParams
import kotlin.math.roundToInt

//<editor-fold desc="For View Data">
/**
 * Show the view  (visibility = View.VISIBLE)
 */
internal inline val View.visible: View
    get() {
        if (visibility != View.VISIBLE) {
            visibility = View.VISIBLE
        }
        return this
    }

/**
 * Hide the view. (visibility = View.INVISIBLE)
 */
internal inline val View.invisible: View
    get() {
        if (visibility != View.INVISIBLE) {
            visibility = View.INVISIBLE
        }
        return this
    }

/**
 * Remove the view (visibility = View.GONE)
 */
internal inline val View.gone: View
    get() {
        if (visibility != View.GONE) {
            visibility = View.GONE
        }
        return this
    }

/**
 * Remove the view (View.setEnable(true))
 */
internal inline val View.enable: View
    get() {
        isEnabled = true
        return this
    }

/**
 * Remove the view (View.setEnable(false))
 */
internal inline val View.disable: View
    get() {
        isEnabled = false
        return this
    }

/**
 * Extension method to get LayoutInflater
 */
internal inline val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)

/**
 * Remove all margin of the view
 */
internal inline val View.removeMargin: View
    get() {
        val v: View = this
        v.updateLayoutParams<MarginLayoutParams> {
            this.leftMargin = 0
            this.topMargin = 0
            this.rightMargin = 0
            this.bottomMargin = 0
        }
        return v
    }
//</editor-fold>

//<editor-fold desc="For get Display Data">
/**
 * Extension method to get theme for Context.
 */
internal inline val Context.isDarkTheme: Boolean get() = resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

/**
 * Extension method to find a device width in pixels
 */
internal inline val Context.displayWidth: Int get() = resources.displayMetrics.widthPixels

/**
 * Extension method to find a device height in pixels
 */
internal inline val Context.displayHeight: Int get() = resources.displayMetrics.heightPixels

/**
 * Extension method to find a device density
 */
internal inline val Context.displayDensity: Float get() = resources.displayMetrics.density

/**
 * Extension method to find a device density in DPI
 */
internal inline val Context.displayDensityDpi: Int get() = resources.displayMetrics.densityDpi

/**
 * Extension method to find a device DisplayMetrics
 */
internal inline val Context.displayMetrics: DisplayMetrics get() = resources.displayMetrics
//</editor-fold>

//<editor-fold desc="For Text Entity">
internal inline val String.toEditable: Editable get() = Editable.Factory.getInstance().newEditable(this)

internal inline val String.removeMultipleSpace: String get() = this.trim().replace("\\s+".toRegex(), " ")

internal inline val String.getFromHtml: Spanned
    get() {
        return if (isNougatPlus) {
            Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(this)
        }
    }

internal inline val String.withUnderLine: Spanned
    get() {
        return SpannableString(this).apply {
            this.setSpan(UnderlineSpan(), 0, this.length, 0)
        }
    }
//</editor-fold>

internal inline val Double.roundToHalf: Double get() = ((this * 2).roundToInt() / 2.0)

internal inline val Context.isValidContextForGlide: Boolean get() = !(this is Activity && this.isFinishing)

internal inline val String.isDigitOnly: Boolean get() = this.matches("-?[0-9]+?".toRegex())

@get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
internal inline val isNougatPlus get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

@get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
internal inline val isOreoPlus get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

@get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
internal inline val isPiePlus get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

@get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
internal inline val isRPlus get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

@get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
internal inline val isTiramisuPlus get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

