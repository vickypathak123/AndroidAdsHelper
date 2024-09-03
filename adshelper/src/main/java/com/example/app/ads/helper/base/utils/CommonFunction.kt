@file:Suppress("unused")

package com.example.app.ads.helper.base.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import kotlin.math.roundToInt

//<editor-fold desc="For Get All Type of Resources">
/**
 * Extension method to Get String resource for Context.
 */
internal fun Context.getStringRes(@StringRes id: Int) = resources.getString(id)

internal fun Context.getStringRes(@StringRes id: Int, vararg formatArgs: String) = resources.getString(id, *formatArgs)

internal fun <T> Context.getStringRes(@StringRes id: Int, vararg formatArgs: T) = resources.getString(id, *formatArgs)

/**
 * Extension method to Get Color resource for Context.
 */
internal fun Context.getColorRes(@ColorRes id: Int) = ContextCompat.getColor(this, id)

internal fun Context.getColorStateRes(@ColorRes id: Int) = ContextCompat.getColorStateList(this, id)

/**
 * Extension method to Get Drawable for resource for Context.
 */
internal fun Context.getDrawableRes(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

internal fun Context.getFontRes(@FontRes id: Int) = ResourcesCompat.getFont(this, id)

internal fun Context.getDimensionRes(@DimenRes id: Int) = resources.getDimension(id)

internal fun Context.sdpToPx(@DimenRes id: Int) = resources.getDimensionPixelSize(id)

internal fun Context.dpToPx(dp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

internal fun Context.getPixelValueForDP(dp: Int): Int = this.dpToPx(dp = dp.toFloat()).toInt()
//</editor-fold>

internal fun TextView.setTextSizeDimension(@DimenRes id: Int) {
    this.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.context.getDimensionRes(id))
}

internal fun ViewGroup.inflateLayout(@LayoutRes resource: Int, attachToRoot: Boolean = false): View = this.context.inflater.inflate(resource, this, attachToRoot)

/**
 * Hide the view if condition is true
 * True: Hide the view
 * False: Show the view
 * @param beInvisible
 */
internal fun View.beInvisibleIf(beInvisible: Boolean) = if (beInvisible) invisible else visible

/**
 * Show the view if condition is true
 * True: Show the view
 * False: Remove the view
 * @param beVisible
 */
internal fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) visible else gone

/**
 * Remove the view if condition is true
 * True: Remove the view
 * False: Show the view
 * @param beGone
 */
internal fun View.beGoneIf(beGone: Boolean) = beVisibleIf(!beGone)

internal fun ViewGroup.setSelection() {

    for (view in this.children) {
        if (view is ViewGroup) {
            view.setSelection()
        } else {
            when (view) {
                is CheckBox -> {
                    view.isSelected = true
                }

                is RadioButton -> {
                    view.isSelected = true
                }

                is Button -> {
                    view.isSelected = true
                }

                is TextView -> {
                    view.isSelected = true
                }
            }
        }
    }
}

internal fun Context.makeText(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

/**
 * add margin of the view
 *
 * @param fMargin set All side same margin of view
 * @param fLeftMargin set only Left side same margin of view
 * @param fTopMargin set only Top side same margin of view
 * @param fRightMargin set only Right side same margin of view
 * @param fBottomMargin set only Bottom side same margin of view
 */
internal fun View.addMargin(
    fMargin: Float? = null,
    fLeftMargin: Float? = null,
    fTopMargin: Float? = null,
    fRightMargin: Float? = null,
    fBottomMargin: Float? = null
): View {
    val v: View = this
    v.updateLayoutParams<MarginLayoutParams> {
        fMargin?.let {
            val lMargin: Int = v.context.dpToPx(dp = fMargin).roundToInt()
            this.leftMargin = lMargin
            this.topMargin = lMargin
            this.rightMargin = lMargin
            this.bottomMargin = lMargin
        } ?: fLeftMargin?.let {
            val lMargin: Int = v.context.dpToPx(dp = fLeftMargin).roundToInt()
            this.leftMargin = lMargin
        } ?: fTopMargin?.let {
            val lMargin: Int = v.context.dpToPx(dp = fTopMargin).roundToInt()
            this.topMargin = lMargin
        } ?: fRightMargin?.let {
            val lMargin: Int = v.context.dpToPx(dp = fRightMargin).roundToInt()
            this.rightMargin = lMargin
        } ?: fBottomMargin?.let {
            val lMargin: Int = v.context.dpToPx(dp = fBottomMargin).roundToInt()
            this.bottomMargin = lMargin
        }
    }

    return v
}



