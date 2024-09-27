package com.example.app.ads.helper.widget

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import com.example.app.ads.helper.R
import com.example.app.ads.helper.databinding.DialogRateAppBinding
import com.example.app.ads.helper.launcher.Launcher
import com.example.app.ads.helper.purchase.product.AdsManager
import com.example.app.ads.helper.utils.getLocalizedString
import com.example.app.ads.helper.utils.is_exit_dialog_opened
import java.util.Locale


/**
 * Class for [RateAppDialog]s styled as a bottom sheet.
 *
 * @param fActivity it refers to your [Activity] context.
 */
class RateAppDialog(
    private val fActivity: Activity,
) : Dialog(fActivity) {
    private val mBinding: DialogRateAppBinding = DialogRateAppBinding.inflate(fActivity.layoutInflater)


    private val TAG: String = javaClass.simpleName

    private var onClickAskMeLater: () -> Unit = {}
    private var onClickNegativeReview: () -> Unit = {}
    private var onClickPositiveReview: () -> Unit = {}
    private var onShow: () -> Unit = {}

    init {
        this.setContentView(mBinding.root)
        this.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

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
                    Launcher.openGooglePlay(context = fActivity, packageName = fActivity.packageName)
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