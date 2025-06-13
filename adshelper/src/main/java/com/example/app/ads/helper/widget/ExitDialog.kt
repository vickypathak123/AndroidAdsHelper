package com.example.app.ads.helper.widget

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.utils.beVisibleIf
import com.example.app.ads.helper.base.utils.getColorRes
import com.example.app.ads.helper.databinding.DialogExitBinding
import com.example.app.ads.helper.purchase.product.AdsManager
import com.example.app.ads.helper.utils.exitTheApp
import com.example.app.ads.helper.utils.getLocalizedString
import com.example.app.ads.helper.utils.is_exit_dialog_opened
import com.example.app.ads.helper.utils.setIncludeFontPaddingFlag
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Locale


/**
 * Class for [ExitDialog]s styled as a bottom sheet.
 *
 * @param fActivity it refers to your [ComponentActivity] context.
 * @param isForTesting [by Default value = false] it's refers to UI change like test exit Ad's & exit Icon.
 * @param backgroundColor it's refers to main background color of dialog.
 * @param iconColor it's refers to exit icon color of dialog.
 * @param iconLineColor it's refers to exit icon background line color of dialog.
 * @param titleTextColor it's refers to title text color of dialog.
 * @param subTitleTextColor it's refers to sub title text color of dialog.
 * @param buttonTextColor it's refers to positive & negative button text color of dialog.
 * @param buttonBackgroundColor it's refers to positive & negative button background color of dialog.
 * @param buttonStrokeColor it's refers to positive & negative button background stroke color of dialog.
 */
class ExitDialog(
    private val fActivity: ComponentActivity,
    private val isForTesting: Boolean = false,
    @ColorRes
    private val backgroundColor: Int,
    @ColorRes
    private val iconColor: Int,
    @ColorRes
    private val iconLineColor: Int,
    @ColorRes
    private val titleTextColor: Int,
    @ColorRes
    private val subTitleTextColor: Int,
    @ColorRes
    private val positiveButtonTextColor: Int,
    @ColorRes
    private val positiveButtonBackgroundColor: Int,
    @ColorRes
    private val positiveButtonStrokeColor: Int,
    @ColorRes
    private val negativeButtonTextColor: Int,
    @ColorRes
    private val negativeButtonBackgroundColor: Int,
    @ColorRes
    private val negativeButtonStrokeColor: Int,
    private val isForceExitApp: Boolean
) : BottomSheetDialog(fActivity, R.style.Transparent) {
    private val mBinding: DialogExitBinding = DialogExitBinding.inflate(fActivity.layoutInflater)

    private var isTestNeedToShowAds = false

    @Suppress("PropertyName")
    val TAG: String = javaClass.simpleName

    init {
        this.setContentView(mBinding.root)
        window?.let {
//            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            it.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        }
        this.setCancelable(false)
        this.setCanceledOnTouchOutside(false)

        mBinding.root.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            mBinding.clExitIconContainer.beVisibleIf(!mBinding.nativeAdView.isVisible)
        }

        val orientationEventListener = object : OrientationEventListener(context) {
            private var lastOrientation = context.resources.configuration.orientation

            override fun onOrientationChanged(orientation: Int) {
                val currentOrientation = context.resources.configuration.orientation

                // Check if orientation has actually changed
                if (currentOrientation != lastOrientation) {
                    Log.e(TAG, "onOrientationChanged: ", )
                    lastOrientation = currentOrientation

                    // Update bottom sheet width based on new orientation
                    updateBottomSheetWidth()
                }
            }
        }

        // Enable the orientation listener
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable()
        }

        setOnShowListener {
            is_exit_dialog_opened = true
            updateBottomSheetWidth()
        }


        setOnDismissListener {
            is_exit_dialog_opened = false
        }

        setOnCancelListener {
            is_exit_dialog_opened = false
        }

        with(mBinding) {
//            clExitMain.setBackgroundColor(fActivity.getColorRes(backgroundColor))
            clExitMain.backgroundTintList = ColorStateList.valueOf(fActivity.getColorRes(backgroundColor))
//            materialCardViewRoot.setCardBackgroundColor(fActivity.getColorRes(backgroundColor))

            backgroundViewExitIcon.setBackgroundColor(fActivity.getColorRes(backgroundColor))
            ivExitIconMain.setColorFilter(fActivity.getColorRes(iconColor), android.graphics.PorterDuff.Mode.SRC_IN)
            ivExitIconLine.setColorFilter(fActivity.getColorRes(iconLineColor), android.graphics.PorterDuff.Mode.SRC_IN)

            txtTitle.setTextColor(fActivity.getColorRes(titleTextColor))
            txtSubTitle.setTextColor(fActivity.getColorRes(subTitleTextColor))

            btnNegative.apply {
                isSelected = true
                setTextColor(fActivity.getColorRes(negativeButtonTextColor))
                setBackgroundColor(fActivity.getColorRes(negativeButtonBackgroundColor))
                strokeColor = ColorStateList.valueOf(fActivity.getColorRes(negativeButtonStrokeColor))

                setOnClickListener {
                    this@ExitDialog.dismiss()
                }
            }

            btnPositive.apply {
                isSelected = true
                setTextColor(fActivity.getColorRes(positiveButtonTextColor))
                setBackgroundColor(fActivity.getColorRes(positiveButtonBackgroundColor))
                strokeColor = ColorStateList.valueOf(fActivity.getColorRes(positiveButtonStrokeColor))

                setOnClickListener {
                    this@ExitDialog.dismiss()
                    fActivity.exitTheApp(isForceExitApp)
                }
            }
            scrollView.post {

                scrollView.isSmoothScrollingEnabled = true
                scrollView.fullScroll(View.FOCUS_DOWN)

            }

        }
    }



    private fun updateBottomSheetWidth() {
        // Get the BottomSheet view
        val bottomSheet = findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

        bottomSheet?.let {
            // Get the behavior
            val behavior = BottomSheetBehavior.from(it)

            // Check for landscape orientation
            val configuration = context.resources.configuration
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // Set width to 80% of screen width
                val displayMetrics = context.resources.displayMetrics
                val width = (displayMetrics.widthPixels * 0.6).toInt()
                it.layoutParams.width = width

                // Center the bottom sheet
                (it.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                    val margin = (displayMetrics.widthPixels - width) / 2
                    marginStart = margin
                    marginEnd = margin
                }

                // Request layout to apply changes
                it.requestLayout()
            }

            // Configure behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            // Calculate appropriate peek height
            val peekHeight = context.resources.displayMetrics.heightPixels /*/ 3*/
            behavior.peekHeight = peekHeight


        }
    }

    override fun show() {
        if (!fActivity.isFinishing && !isShowing) {
            testExitDialogUI()
            super.show()
        }
    }

    /**
     * Show the exit dialog.
     *
     * @param fLanguageCode it's refers to your app language code.
     * @param subTitleId it's refers to your exit dialog sub title string resources id.
     */
    fun show(
        fLanguageCode: String,
        @StringRes subTitleId: Int
    ) {
        if (!fActivity.isFinishing && !isShowing) {

            setIncludeFontPaddingFlag(fView = mBinding.root, fLanguageCode = fLanguageCode)

            mBinding.nativeAdView.beVisibleIf(AdsManager(fActivity).isNeedToShowAds)

            with(mBinding) {
                txtTitle.text = getLocalizedString<String>(
                    context = fActivity,
                    fLocale = Locale(fLanguageCode),
                    resourceId = R.string.exit_dialog_title
                )

                txtSubTitle.text = getLocalizedString<String>(
                    context = fActivity,
                    fLocale = Locale(fLanguageCode),
                    resourceId = subTitleId
                )

                btnNegative.text = getLocalizedString<String>(
                    context = fActivity,
                    fLocale = Locale(fLanguageCode),
                    resourceId = R.string.exit_dialog_negative_button_text
                )

                btnPositive.text = getLocalizedString<String>(
                    context = fActivity,
                    fLocale = Locale(fLanguageCode),
                    resourceId = R.string.exit_dialog_positive_button_text
                )
            }

            this.show()
        }
    }

    private fun testExitDialogUI() {
        if (isForTesting) {
            isTestNeedToShowAds = !isTestNeedToShowAds
            mBinding.nativeAdView.beVisibleIf(isTestNeedToShowAds)
        }
    }
}