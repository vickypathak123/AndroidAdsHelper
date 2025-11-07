package com.example.app.ads.helper.widget

import android.app.Activity
import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.example.app.ads.helper.R
import com.example.app.ads.helper.databinding.DialogRateAppBinding
import com.example.app.ads.helper.launcher.Launcher
import com.example.app.ads.helper.purchase.product.AdsManager
import com.example.app.ads.helper.utils.getLocalizedString
import com.example.app.ads.helper.utils.is_exit_dialog_opened
import com.example.app.ads.helper.utils.setIncludeFontPaddingFlag
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.Locale


/**
 * Class for [RateAppDialog]s styled as a bottom sheet.
 *
 * @param fActivity it refers to your [Activity] context.
 */
class RateAppDialog(
    private val fActivity: Activity,
) : Dialog(fActivity) {
    private val mBinding: DialogRateAppBinding =
        DialogRateAppBinding.inflate(fActivity.layoutInflater)


    private val TAG: String = javaClass.simpleName

    private var onClickAskMeLater: () -> Unit = {}
    private var onClickNegativeReview: () -> Unit = {}
    private var onClickPositiveReview: () -> Unit = {}
    private var onShow: () -> Unit = {}

    init {
        this.setContentView(mBinding.root)
        this.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            val params = it.attributes
            val screenWidth = fActivity.resources.displayMetrics.widthPixels
            val screenHeight = fActivity.resources.displayMetrics.heightPixels

            // Check if the device is in landscape mode
            if (screenWidth > screenHeight) { // Landscape mode
                params.width = (screenWidth * 43) / 100  // Set width to 80% of the screen width
            } else { // Portrait mode
                params.width =
                    WindowManager.LayoutParams.MATCH_PARENT  // Set width to full screen width
            }

            params.height =
                WindowManager.LayoutParams.WRAP_CONTENT // Adjust height based on content

            it.attributes = params
            it.setLayout(params.width, params.height)



//            it.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

            val lp = WindowManager.LayoutParams()
            lp.copyFrom(it.attributes)
            lp.gravity = Gravity.CENTER
            lp.windowAnimations = R.style.dialog_animation_bottom_to_top
            it.attributes = lp
        }
        this.setCancelable(false)
        this.setCanceledOnTouchOutside(false)

        setOnShowListener {
            is_exit_dialog_opened = true
            onShow.invoke()
//            updateBottomSheetWidth()
        }

        setOnDismissListener {
            is_exit_dialog_opened = false
        }

        setOnCancelListener {
            is_exit_dialog_opened = false
        }

        with(mBinding) {
            txtAskMeLater.setOnClickListener {
                onClickAskMeLater.invoke()
            }
            ivNegativeReview.setOnClickListener {
                onClickNegativeReview.invoke()
            }
            ivPositiveReview.setOnClickListener {
                onClickPositiveReview.invoke()
            }
        }
    }

    private fun updateBottomSheetWidth() {
        // Get the BottomSheet view
        val bottomSheet =
            findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

        bottomSheet?.let {
            // Get the behavior
            val behavior = BottomSheetBehavior.from(it)

            // Check for landscape orientation
            val configuration = context.resources.configuration
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // Set width to 80% of screen width
                val displayMetrics = context.resources.displayMetrics
                val width = (displayMetrics.widthPixels * 0.5).toInt()
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
            val peekHeight = context.resources.displayMetrics.heightPixels / 3
            behavior.peekHeight = peekHeight

            // Add callback to handle different states
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // Optional: Implement animations during sliding
                }
            })
        }
    }

    override fun show() {
        if (!fActivity.isFinishing && !isShowing) {
            Handler(Looper.getMainLooper()).postDelayed({
                fActivity.runOnUiThread {
                    super.show()
                }
            }, 500)
        }
    }

    /**
     * Show the exit dialog.
     *
     * @param fLanguageCode it's refers to your app language code.
     */
    fun show(
        fLanguageCode: String,
        onClickAskMeLater: () -> Unit = {},
        onClickNegativeReview: () -> Unit = {},
        onClickPositiveReview: () -> Unit = {},
        onShow: () -> Unit = {},
        onDismiss: () -> Unit = {},
    ) {
        if (!fActivity.isFinishing && !isShowing) {
            if (!AdsManager(context = fActivity).isRateAppDialogOpened) {


                setIncludeFontPaddingFlag(fView = mBinding.root, fLanguageCode = fLanguageCode)


                this.onClickAskMeLater = {
                    this.dismiss()
                    onClickAskMeLater.invoke()
                }

                this.onClickNegativeReview = {
                    AdsManager(context = fActivity).isRateAppDialogOpened = true
                    this.dismiss()
                    onClickNegativeReview.invoke()
                }

                this.onClickPositiveReview = {
                    AdsManager(context = fActivity).isRateAppDialogOpened = true
                    Launcher.openGooglePlay(
                        context = fActivity,
                        packageName = fActivity.packageName
                    )
                    this.dismiss()
                    onClickPositiveReview.invoke()
                }

                this.onShow = onShow

                val lLocale = Locale(fLanguageCode)

                with(mBinding) {
                    txtTitle.text = getLocalizedString<String>(
                        context = fActivity,
                        fLocale = lLocale,
                        resourceId = R.string.rate_dialog_title
                    )

                    txtSubTitle.text = getLocalizedString<String>(
                        context = fActivity,
                        fLocale = lLocale,
                        resourceId = R.string.rate_dialog_sub_title
                    )

                    txtAskMeLater.text = getLocalizedString<String>(
                        context = fActivity,
                        fLocale = lLocale,
                        resourceId = R.string.rate_dialog_ask_me_later
                    )
                }

                this.show()
            } else {
                onDismiss.invoke()
            }
        }
    }
}