package com.example.app.ads.helper.widget

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.utils.beVisibleIf
import com.example.app.ads.helper.base.utils.getColorRes
import com.example.app.ads.helper.databinding.DialogExitBinding
import com.example.app.ads.helper.exitTheApp
import com.example.app.ads.helper.getLocalizedString
import com.example.app.ads.helper.purchase.product.AdsManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Locale

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
    private val buttonTextColor: Int,
    @ColorRes
    private val buttonBackgroundColor: Int,
    @ColorRes
    private val buttonStrokeColor: Int,
) : BottomSheetDialog(fActivity, R.style.theme_exit_dialog) {
    private val mBinding: DialogExitBinding = DialogExitBinding.inflate(fActivity.layoutInflater)

    private var isTestNeedToShowAds = false

    init {
        this.setContentView(mBinding.root)
        window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        }
        this.setCancelable(false)
        this.setCanceledOnTouchOutside(false)

        if (!isForTesting) {
            AdsManager.isShowAds.observe(fActivity) { isNeedToShowAd ->
                mBinding.nativeAdView.beVisibleIf(isNeedToShowAd)
                mBinding.clExitIconContainer.beVisibleIf(!isNeedToShowAd)
            }
        }

        with(mBinding) {
            clExitMain.setBackgroundColor(fActivity.getColorRes(backgroundColor))
            backgroundViewExitIcon.setBackgroundColor(fActivity.getColorRes(backgroundColor))
            ivExitIconMain.setColorFilter(fActivity.getColorRes(iconColor), android.graphics.PorterDuff.Mode.SRC_IN)
            ivExitIconLine.setColorFilter(fActivity.getColorRes(iconLineColor), android.graphics.PorterDuff.Mode.SRC_IN)

            txtTitle.setTextColor(fActivity.getColorRes(titleTextColor))
            txtSubTitle.setTextColor(fActivity.getColorRes(subTitleTextColor))

            btnNegative.apply {
                setTextColor(fActivity.getColorRes(buttonTextColor))
                setBackgroundColor(fActivity.getColorRes(buttonBackgroundColor))
                strokeColor = ColorStateList.valueOf(fActivity.getColorRes(buttonStrokeColor))

                setOnClickListener {
                    this@ExitDialog.dismiss()
                }
            }

            btnPositive.apply {
                setTextColor(fActivity.getColorRes(buttonTextColor))
                setBackgroundColor(fActivity.getColorRes(buttonBackgroundColor))
                strokeColor = ColorStateList.valueOf(fActivity.getColorRes(buttonStrokeColor))

                setOnClickListener {
                    this@ExitDialog.dismiss()
                    fActivity.exitTheApp()
                }
            }
        }
    }

    override fun show() {
        if (!fActivity.isFinishing && !isShowing) {
            testExitDialogUI()
            super.show()
        }
    }

    fun show(
        fLanguageCode: String,
        @StringRes subTitleId: Int
    ) {
        if (!fActivity.isFinishing && !isShowing) {

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
            mBinding.clExitIconContainer.beVisibleIf(!isTestNeedToShowAds)
        }
    }
}