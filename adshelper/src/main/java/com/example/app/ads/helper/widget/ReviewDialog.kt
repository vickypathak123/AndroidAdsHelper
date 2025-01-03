package com.example.app.ads.helper.widget

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import com.example.app.ads.helper.R
import com.example.app.ads.helper.databinding.DialogReviewBinding
import com.example.app.ads.helper.purchase.product.AdsManager
import com.example.app.ads.helper.retrofit.enqueue.APICallEnqueue.subscriptionReviewApi
import com.example.app.ads.helper.retrofit.listener.APIResponseListener
import com.example.app.ads.helper.utils.getLocalizedString
import com.example.app.ads.helper.utils.isOnline
import com.example.app.ads.helper.utils.is_exit_dialog_opened
import com.example.app.ads.helper.utils.logD
import com.example.app.ads.helper.utils.logE
import com.example.app.ads.helper.utils.logI
import com.example.app.ads.helper.utils.setIncludeFontPaddingFlag
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import java.util.Locale


/**
 * Class for [ReviewDialog]s styled as a bottom sheet.
 *
 * @param fActivity it refers to your [Activity] context.
 */
internal class ReviewDialog(
    private val fActivity: Activity,
) : BottomSheetDialog(fActivity, R.style.theme_exit_dialog) {
    private val mBinding: DialogReviewBinding = DialogReviewBinding.inflate(fActivity.layoutInflater)


    private val TAG: String = javaClass.simpleName

    private var onClickOption: (String) -> Unit = {}

    init {
        this.setContentView(mBinding.root)
        this.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

            val lp = WindowManager.LayoutParams()
            lp.copyFrom(it.attributes)
            lp.gravity = Gravity.BOTTOM
            lp.windowAnimations = R.style.dialog_animation_bottom_to_top
            it.attributes = lp
        }
        this.setCancelable(false)
        this.setCanceledOnTouchOutside(false)

        setOnShowListener {
            is_exit_dialog_opened = true
        }

        setOnDismissListener {
            is_exit_dialog_opened = false
        }

        setOnCancelListener {
            is_exit_dialog_opened = false
        }

//        Don't know what it is
//        I need to try it first
//        Too expensive
//        I don't pay for apps

        with(mBinding) {
            txtOption1.setOnClickListener {
                onClickOption.invoke("Don't know what it is")
            }
            txtOption2.setOnClickListener {
                onClickOption.invoke("Too expensive")
            }
            txtOption3.setOnClickListener {
                onClickOption.invoke("I don't pay for apps")
            }
            txtOption4.setOnClickListener {
                onClickOption.invoke("I need to try it first")
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
        fPackageName: String,
        fVersionName: String,
        fLanguageCode: String,
        onDismiss: () -> Unit
    ) {
        if (!fActivity.isFinishing && !isShowing) {
            if (!AdsManager(context = fActivity).isReviewDialogOpened) {
                AdsManager(context = fActivity).isReviewDialogOpened = true

                setIncludeFontPaddingFlag(fView = mBinding.root, fLanguageCode = fLanguageCode)

                logI(TAG, "show: \nfPackageName::-> $fPackageName, \nfVersionName::-> $fVersionName, \nfLanguageCode::-> $fLanguageCode")

                onClickOption = { review ->
                    this.dismiss()

                    logD(TAG, "show: review::-> $review")

                    if (isOnline) {
                        subscriptionReviewApi(
                            packageName = fPackageName,
                            versionCode = fVersionName,
                            languageKey = fLanguageCode,
                            subscriptionReview = review,
                            fListener = object : APIResponseListener<JsonObject> {
                                override fun onSuccess(fResponse: JsonObject) {
                                    logI(TAG, "onSuccess: $fResponse")
                                }

                                override fun onError(fErrorMessage: String?) {
                                    super.onError(fErrorMessage)
                                    logE(TAG, "onError: $fErrorMessage")
                                }
                            }
                        )
                    }


                    onDismiss.invoke()
                }

                val lLocale = Locale(fLanguageCode)

                with(mBinding) {
                    txtTitle.text = getLocalizedString<String>(
                        context = fActivity,
                        fLocale = lLocale,
                        resourceId = R.string.review_dialog_title
                    )

                    txtSubTitle.text = getLocalizedString<String>(
                        context = fActivity,
                        fLocale = lLocale,
                        resourceId = R.string.review_dialog_sub_title
                    )

                    txtOption1.text = getLocalizedString<String>(
                        context = fActivity,
                        fLocale = lLocale,
                        resourceId = R.string.review_option_1
                    )

                    txtOption2.text = getLocalizedString<String>(
                        context = fActivity,
                        fLocale = lLocale,
                        resourceId = R.string.review_option_2
                    )

                    txtOption3.text = getLocalizedString<String>(
                        context = fActivity,
                        fLocale = lLocale,
                        resourceId = R.string.review_option_3
                    )

                    txtOption4.text = getLocalizedString<String>(
                        context = fActivity,
                        fLocale = lLocale,
                        resourceId = R.string.review_option_4
                    )
                }

                this.show()
            } else {
                onDismiss.invoke()
            }
        }
    }
}