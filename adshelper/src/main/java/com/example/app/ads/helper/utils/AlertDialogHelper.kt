package com.example.app.ads.helper.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.MetricAffectingSpan
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog

/**
 * @author Akshay Harsoda
 * @since 25 Sep 2024
 */
object AlertDialogHelper {

//    private var mDialog: AlertDialog? = null

//    fun dismissDialog() {
//        mDialog?.dismiss()
//    }

    @JvmOverloads
    @JvmName("showAlertDialog")
    fun Context.showAlertDialog(
        fTitle: String? = null,
        fMessage: String? = null,
        fPositiveText: String? = null,
        fNegativeText: String? = null,
        fNeutralText: String? = null,
        @ColorInt fTitleColor: Int? = null,
        @ColorInt fMessageColor: Int? = null,
        @ColorInt fPositiveColor: Int? = null,
        @ColorInt fNegativeColor: Int? = null,
        @ColorInt fNeutralColor: Int? = null,
        @ColorInt fButtonColor: Int? = null,
        @ColorInt fAllTextColor: Int? = null,
        fTitleTypeface: Typeface? = null,
        fMessageTypeface: Typeface? = null,
        fPositiveTypeface: Typeface? = null,
        fNegativeTypeface: Typeface? = null,
        fNeutralTypeface: Typeface? = null,
        fButtonTypeface: Typeface? = null,
        fAllTextTypeface: Typeface? = null,
//        isDismissOnButtonClick: Boolean = true,
        fButtonClickListener: OnAlertButtonClickListener
    ) {
        var dialog: AlertDialog? = null

        AlertDialog.Builder(this).apply {
            this.setCancelable(false)

            this.setOnDismissListener {
                is_exit_dialog_opened = false
            }

            this.setOnCancelListener {
                is_exit_dialog_opened = false
            }

            fTitle?.let { title ->
                var ssBuilder = SpannableStringBuilder(title)

                fTitleColor?.let { color ->
                    ssBuilder = getColorSpannableString(ssBuilder, color)
                }

                fAllTextColor?.let { color ->
                    ssBuilder = getColorSpannableString(ssBuilder, color)
                }

                fTitleTypeface?.let { typeface ->
                    ssBuilder = getTypefaceSpannableString(ssBuilder, typeface)
                }

                fAllTextTypeface?.let { typeface ->
                    ssBuilder = getTypefaceSpannableString(ssBuilder, typeface)
                }

                this.setTitle(ssBuilder)
            }

            fMessage?.let { text ->

                var ssBuilder = SpannableStringBuilder(text)

                fMessageColor?.let { color ->
                    ssBuilder = getColorSpannableString(ssBuilder, color)
                }

                fAllTextColor?.let { color ->
                    ssBuilder = getColorSpannableString(ssBuilder, color)
                }

                fMessageTypeface?.let { typeface ->
                    ssBuilder = getTypefaceSpannableString(ssBuilder, typeface)
                }

                fAllTextTypeface?.let { typeface ->
                    ssBuilder = getTypefaceSpannableString(ssBuilder, typeface)
                }

                this.setMessage(ssBuilder)
            }

            fPositiveText?.let { text ->

                var ssBuilder = SpannableStringBuilder(text)

                fPositiveColor?.let { color ->
                    ssBuilder = getColorSpannableString(ssBuilder, color)
                }

                fButtonColor?.let { color ->
                    ssBuilder = getColorSpannableString(ssBuilder, color)
                }

                fPositiveTypeface?.let { typeface ->
                    ssBuilder = getTypefaceSpannableString(ssBuilder, typeface)
                }

                fButtonTypeface?.let { typeface ->
                    ssBuilder = getTypefaceSpannableString(ssBuilder, typeface)
                }

                fAllTextTypeface?.let { typeface ->
                    ssBuilder = getTypefaceSpannableString(ssBuilder, typeface)
                }

                this.setPositiveButton(ssBuilder) { _, _ ->
//                    if (isDismissOnButtonClick) {
                        dialog?.dismiss()
//                    }
                    fButtonClickListener.onPositiveButtonClick()
                }
            }

            fNegativeText?.let { text ->

                var ssBuilder = SpannableStringBuilder(text)

                fNegativeColor?.let { color ->
                    ssBuilder = getColorSpannableString(ssBuilder, color)
                }

                fButtonColor?.let { color ->
                    ssBuilder = getColorSpannableString(ssBuilder, color)
                }

                fNegativeTypeface?.let { typeface ->
                    ssBuilder = getTypefaceSpannableString(ssBuilder, typeface)
                }

                fButtonTypeface?.let { typeface ->
                    ssBuilder = getTypefaceSpannableString(ssBuilder, typeface)
                }

                fAllTextTypeface?.let { typeface ->
                    ssBuilder = getTypefaceSpannableString(ssBuilder, typeface)
                }

                this.setNegativeButton(ssBuilder) { _, _ ->
//                    if (isDismissOnButtonClick) {
                    dialog?.dismiss()
//                    }
                    fButtonClickListener.onNegativeButtonClick()
                }
            }

            fNeutralText?.let { text ->

                var ssBuilder = SpannableStringBuilder(text)

                fNeutralColor?.let { color ->
                    ssBuilder = getColorSpannableString(ssBuilder, color)
                }

                fButtonColor?.let { color ->
                    ssBuilder = getColorSpannableString(ssBuilder, color)
                }

                fNeutralTypeface?.let { typeface ->
                    ssBuilder = getTypefaceSpannableString(ssBuilder, typeface)
                }

                fButtonTypeface?.let { typeface ->
                    ssBuilder = getTypefaceSpannableString(ssBuilder, typeface)
                }

                fAllTextTypeface?.let { typeface ->
                    ssBuilder = getTypefaceSpannableString(ssBuilder, typeface)
                }

                this.setNeutralButton(ssBuilder) { _, _ ->
//                    if (isDismissOnButtonClick) {
                        dialog?.dismiss()
//                    }
                    fButtonClickListener.onNeutralButtonClick()
                }
            }

        }.also {
            dialog = it.create()

            dialog?.setOnShowListener {
                is_exit_dialog_opened = true
            }

            dialog?.show()
        }
    }

    private fun getColorSpannableString(
        fSource: SpannableStringBuilder,
        @ColorInt fColor: Int
    ): SpannableStringBuilder {
        val foregroundColorSpan = ForegroundColorSpan(fColor)
        fSource.setSpan(
            foregroundColorSpan,
            0,
            fSource.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return fSource
    }

    private fun getTypefaceSpannableString(
        fSource: SpannableStringBuilder,
        fTypeface: Typeface
    ): SpannableStringBuilder {
        val typefaceSpan = TypefaceSpan(fTypeface)
        fSource.setSpan(
            typefaceSpan,
            0,
            fSource.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return fSource
    }

    private class TypefaceSpan(private val typeface: Typeface) : MetricAffectingSpan() {

        override fun updateDrawState(tp: TextPaint) {
            tp.typeface = typeface
            tp.flags = tp.flags or Paint.SUBPIXEL_TEXT_FLAG
        }

        override fun updateMeasureState(p: TextPaint) {
            p.typeface = typeface
            p.flags = p.flags or Paint.SUBPIXEL_TEXT_FLAG
        }
    }

    interface OnAlertButtonClickListener {
        fun onPositiveButtonClick()
        fun onNegativeButtonClick() {}
        fun onNeutralButtonClick() {}
    }
}