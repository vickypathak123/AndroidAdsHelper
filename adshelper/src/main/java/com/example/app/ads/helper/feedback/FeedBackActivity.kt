package com.example.app.ads.helper.feedback

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.text.InputFilter
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doOnTextChanged
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.BaseActivity
import com.example.app.ads.helper.base.BaseBindingActivity
import com.example.app.ads.helper.base.utils.getColorRes
import com.example.app.ads.helper.base.utils.getDrawableRes
import com.example.app.ads.helper.base.utils.getFontRes
import com.example.app.ads.helper.base.utils.gone
import com.example.app.ads.helper.base.utils.inflateLayout
import com.example.app.ads.helper.base.utils.isTiramisuPlus
import com.example.app.ads.helper.base.utils.makeText
import com.example.app.ads.helper.base.utils.setTextSizeDimension
import com.example.app.ads.helper.base.utils.visible
import com.example.app.ads.helper.databinding.ActivityFeedBackBinding
import com.example.app.ads.helper.purchase.sixbox.utils.TextGravityFlags
import com.example.app.ads.helper.retrofit.enqueue.APICallEnqueue.feedbackApi
import com.example.app.ads.helper.retrofit.listener.APIResponseListener
import com.example.app.ads.helper.utils.getLocalizedString
import com.example.app.ads.helper.utils.isOnline
import com.example.app.ads.helper.utils.logE
import com.example.app.ads.helper.utils.logI
import com.google.gson.JsonObject
import java.util.Locale


internal class FeedBackActivity : BaseBindingActivity<ActivityFeedBackBinding>() {

    private val mAppLanguage: String get() = screenData.first
    private val mAppPackageName: String get() = screenData.second
    private val mAppVersionName: String get() = screenData.third



    private val useLightStatusBar: Boolean get() = screenDataModel.useLightStatusBar

    private val useLightNavigationBar: Boolean get() = screenDataModel.useLightNavigationBar

    @get:ColorRes
    private val screenBackgroundColor: Int get() = screenDataModel.screenBackgroundColor

    @get:ColorRes
    private val toolbarBackgroundColor: Int get() = screenDataModel.toolbarBackgroundColor

    @get:LayoutRes
    private val backIconViewResource: Int get() = screenDataModel.backIconViewResource

    @TextGravityFlags
    private val toolbarTextGravity: Int get() = screenDataModel.toolbarTextGravity

    @get:ColorRes
    private val toolbarTextColor: Int get() = screenDataModel.toolbarTextColor

    @get:DimenRes
    private val toolbarTextSize: Int get() = screenDataModel.toolbarTextSize

    @get:FontRes
    private val toolbarTextFontFamily: Int get() = screenDataModel.toolbarTextFontFamily

    @get:ColorRes
    private val titleTextColor: Int get() = screenDataModel.titleTextColor

    @get:FontRes
    private val titleTextFontFamily: Int get() = screenDataModel.titleTextFontFamily

    @get:ColorRes
    private val questionTextColor: Int get() = screenDataModel.questionTextColor

    @get:FontRes
    private val questionTextFontFamily: Int get() = screenDataModel.questionTextFontFamily

    @get:ColorRes
    private val answerTextColor: Int get() = screenDataModel.answerTextColor

    @get:FontRes
    private val answerTextFontFamily: Int get() = screenDataModel.answerTextFontFamily

    @get:ColorRes
    private val answerTextHintColor: Int get() = screenDataModel.answerTextHintColor

    @get:ColorRes
    private val answerCountTextColor: Int get() = screenDataModel.answerCountTextColor

    @get:FontRes
    private val answerCountTextFontFamily: Int get() = screenDataModel.answerCountTextFontFamily

    @get:ColorRes
    private val submitButtonTextColor: Int get() = screenDataModel.submitButtonTextColor

    @get:FontRes
    private val submitButtonTextFontFamily: Int get() = screenDataModel.submitButtonTextFontFamily

    @get:ColorRes
    private val progressBarColor: Int get() = screenDataModel.progressBarColor

    private val answerBackground: FeedBackBackgroundItem get() = screenDataModel.answerBackground

    private val submitBackground: FeedBackBackgroundItem get() = screenDataModel.submitBackground


    private val maxLength: Int = 500

    private val Int.suggestionCount: String
        get() = getLocalizedString(
            resourceId = R.string.price_slash_period,
            formatArgs = arrayOf("$this", "$maxLength")
        )

    companion object {
        private var onScreenFinish: () -> Unit = {}
        private var screenData: Triple<String, String, String> = Triple("en", "", "")
        private var screenDataModel: FeedBackScreenDataModel = FeedBackScreenDataModel()

        internal fun launchScreen(
            fActivity: Activity,
            screenData: Triple<String, String, String>,
            screenDataModel: FeedBackScreenDataModel,
            onScreenFinish: () -> Unit,
        ) {
            Companion.onScreenFinish = onScreenFinish
            Companion.screenData = screenData
            Companion.screenDataModel = screenDataModel

            val lIntent = Intent(fActivity, FeedBackActivity::class.java)

            @AnimatorRes @AnimRes val lEnterAnimId: Int = android.R.anim.fade_in
            @AnimatorRes @AnimRes val lExitAnimId: Int = android.R.anim.fade_out

            fActivity.runOnUiThread {
                if (isTiramisuPlus) {
                    val options = ActivityOptions.makeCustomAnimation(fActivity, lEnterAnimId, lExitAnimId)
                    fActivity.startActivity(lIntent, options.toBundle())
                } else {
                    fActivity.startActivity(lIntent)
                    @Suppress("DEPRECATION")
                    fActivity.overridePendingTransition(lEnterAnimId, lExitAnimId)
                }
            }
        }
    }

    override fun getScreenLanguageCode(): String {
        return mAppLanguage
    }

    override fun setBinding(): ActivityFeedBackBinding {
        return ActivityFeedBackBinding.inflate(layoutInflater)
    }

    override fun getActivityContext(): BaseActivity {
        return this@FeedBackActivity
    }

    override fun setParamBeforeLayoutInit() {
        super.setParamBeforeLayoutInit()
        window?.let { lWindow ->
            lWindow.decorView.let { lDecorView ->
                WindowInsetsControllerCompat(lWindow, lDecorView).apply {
                    this.isAppearanceLightStatusBars = useLightStatusBar
                    this.isAppearanceLightNavigationBars = useLightNavigationBar

                    lWindow.statusBarColor = mActivity.getColorRes(toolbarBackgroundColor)
                    lWindow.navigationBarColor = mActivity.getColorRes(screenBackgroundColor)
                }
            }
        }
    }

    override fun initView() {
        super.initView()

        with(mBinding) {

            root.setBackgroundColor(mActivity.getColorRes(screenBackgroundColor))
            clHeader.setBackgroundColor(mActivity.getColorRes(toolbarBackgroundColor))

            strokeProfession.apply {
                when {
                    (answerBackground.strokeDrawableRes != -1) -> {
                        this.setBackgroundDrawable(mActivity.getDrawableRes(answerBackground.strokeDrawableRes))
                    }

                    (answerBackground.strokeColorRes != -1) -> {
                        this.setBackgroundColor(mActivity.getColorRes(answerBackground.strokeColorRes))
                    }

                    (answerBackground.backgroundDrawableRes != -1) -> {
                        this.setBackgroundDrawable(mActivity.getDrawableRes(answerBackground.backgroundDrawableRes))
                    }

                    (answerBackground.backgroundColorRes != -1) -> {
                        this.setBackgroundColor(mActivity.getColorRes(answerBackground.backgroundColorRes))
                    }
                }
            }

            strokeSuggestions.apply {
                when {
                    (answerBackground.strokeDrawableRes != -1) -> {
                        this.setBackgroundDrawable(mActivity.getDrawableRes(answerBackground.strokeDrawableRes))
                    }

                    (answerBackground.strokeColorRes != -1) -> {
                        this.setBackgroundColor(mActivity.getColorRes(answerBackground.strokeColorRes))
                    }

                    (answerBackground.backgroundDrawableRes != -1) -> {
                        this.setBackgroundDrawable(mActivity.getDrawableRes(answerBackground.backgroundDrawableRes))
                    }

                    (answerBackground.backgroundColorRes != -1) -> {
                        this.setBackgroundColor(mActivity.getColorRes(answerBackground.backgroundColorRes))
                    }
                }
            }

            backgroundProfession.apply {
                when {
                    (answerBackground.backgroundDrawableRes != -1) -> {
                        this.setBackgroundDrawable(mActivity.getDrawableRes(answerBackground.backgroundDrawableRes))
                    }

                    (answerBackground.backgroundColorRes != -1) -> {
                        this.setBackgroundColor(mActivity.getColorRes(answerBackground.backgroundColorRes))
                    }
                }
            }

            backgroundSuggestions.apply {
                when {
                    (answerBackground.backgroundDrawableRes != -1) -> {
                        this.setBackgroundDrawable(mActivity.getDrawableRes(answerBackground.backgroundDrawableRes))
                    }

                    (answerBackground.backgroundColorRes != -1) -> {
                        this.setBackgroundColor(mActivity.getColorRes(answerBackground.backgroundColorRes))
                    }
                }
            }

            ivBackground.apply {
                when {
                    (submitBackground.backgroundDrawableRes != -1) -> {
                        this.setBackgroundDrawable(mActivity.getDrawableRes(submitBackground.backgroundDrawableRes))
                    }

                    (submitBackground.backgroundColorRes != -1) -> {
                        this.setBackgroundColor(mActivity.getColorRes(submitBackground.backgroundColorRes))
                    }

                    (submitBackground.strokeDrawableRes != -1) -> {
                        this.setBackgroundDrawable(mActivity.getDrawableRes(submitBackground.strokeDrawableRes))
                    }

                    (submitBackground.strokeColorRes != -1) -> {
                        this.setBackgroundColor(mActivity.getColorRes(submitBackground.strokeColorRes))
                    }
                }
            }

            ivHeaderBack.apply {
                removeAllViews()
                addView(this.inflateLayout(backIconViewResource))
            }

            headerRightIconView.apply {
                removeAllViews()
                addView(this.inflateLayout(backIconViewResource))
            }

            txtHeaderTitle.apply {
                this.text = getLocalizedString(resourceId = R.string.feedback_title)
                this.setTextColor(mActivity.getColorRes(toolbarTextColor))
                this.setTextSizeDimension(toolbarTextSize)
                this.typeface = mActivity.getFontRes(toolbarTextFontFamily)

                this.gravity = when (toolbarTextGravity) {
                    Gravity.CENTER -> Gravity.CENTER
                    Gravity.START -> Gravity.CENTER_VERTICAL or Gravity.START
                    else -> Gravity.CENTER_VERTICAL or Gravity.END
                }
            }

            txtTitle.apply {
                this.text = getLocalizedString(resourceId = R.string.feedback_sub_title)
                this.setTextColor(mActivity.getColorRes(titleTextColor))
                this.typeface = mActivity.getFontRes(titleTextFontFamily)
            }
            txtWhatIsYourProfession.apply {
                this.text = getLocalizedString(resourceId = R.string.feedback_what_is_your_profession)
                this.setTextColor(mActivity.getColorRes(questionTextColor))
                this.typeface = mActivity.getFontRes(questionTextFontFamily)
            }
            txtYourSuggestions.apply {
                this.text = getLocalizedString(resourceId = R.string.feedback_write_your_suggestions)
                this.setTextColor(mActivity.getColorRes(questionTextColor))
                this.typeface = mActivity.getFontRes(questionTextFontFamily)
            }
            etProfession.apply {
                this.hint = getLocalizedString(resourceId = R.string.feedback_write_here)
                this.setTextColor(mActivity.getColorRes(answerTextColor))
                this.setHintTextColor(mActivity.getColorRes(answerTextHintColor))
                this.typeface = mActivity.getFontRes(answerTextFontFamily)
            }
            etSuggestions.apply {
                this.hint = getLocalizedString(resourceId = R.string.feedback_write_here_suggestion)
                this.setTextColor(mActivity.getColorRes(answerTextColor))
                this.setHintTextColor(mActivity.getColorRes(answerTextHintColor))
                this.typeface = mActivity.getFontRes(answerTextFontFamily)
            }
            txtSuggestionsCount.apply {
                this.text = 0.suggestionCount
                this.setTextColor(mActivity.getColorRes(answerCountTextColor))
                this.typeface = mActivity.getFontRes(answerCountTextFontFamily)
            }
            txtBtnSubmit.apply {
                this.text = getLocalizedString(resourceId = R.string.feedback_submit)
                this.setTextColor(mActivity.getColorRes(submitButtonTextColor))
                this.typeface = mActivity.getFontRes(submitButtonTextFontFamily)
            }

            pbLoading.indeterminateTintList = ColorStateList.valueOf(mActivity.getColorRes(progressBarColor))
            clProgress.gone
        }
    }

    override fun initViewListener() {
        super.initViewListener()
        with(mBinding) {
            val filterArray = arrayOf(InputFilter.LengthFilter(maxLength))
            etSuggestions.filters = filterArray
            etSuggestions.doOnTextChanged { text, _, _, _ ->
                txtSuggestionsCount.text = (text.toString().length).suggestionCount
            }

            setClickListener(ivHeaderBack, btnSubmit, clProgress)
        }
    }

    private inline val View.hideKeyBord: Unit
        get() {
            this.clearFocus()
            this.context.getSystemService(InputMethodManager::class.java)?.hideSoftInputFromWindow(this.windowToken, 0)
        }

    override fun onClick(v: View) {
        super.onClick(v)
        with(mBinding) {
            when (v) {
                ivHeaderBack -> customOnBackPressed()
                btnSubmit -> {
                    val lProfession = etProfession.text.toString().trim()
                    val lSuggestions = etSuggestions.text.toString().trim()

                    if (!isOnline) {
                        makeText(text = getLocalizedString(resourceId = R.string.internet_offline))
                    } else if (lProfession.isEmpty()) {
                        makeText(text = getLocalizedString(resourceId = R.string.write_your_profession))
                    } else if (lSuggestions.isEmpty()) {
                        makeText(text = getLocalizedString(resourceId = R.string.write_your_suggestions))
                    } else {
                        etProfession.hideKeyBord
                        clProgress.visible
                        feedbackApi(
                            packageName = mAppPackageName,
                            versionCode = mAppVersionName,
                            languageKey = mAppLanguage,
                            review = lProfession,
                            useOfApp = lSuggestions,
                            fListener = object : APIResponseListener<JsonObject> {
                                override fun onSuccess(fResponse: JsonObject) {
                                    logI(TAG, "onSuccess: $fResponse")
                                    makeText(text = getLocalizedString(resourceId = R.string.feedback_response_success))
                                    customOnBackPressed()
                                }

                                override fun onError(fErrorMessage: String?) {
                                    super.onError(fErrorMessage)
                                    logE(TAG, "onError: $fErrorMessage")
                                    clProgress.gone
                                    makeText(text = getLocalizedString(resourceId = R.string.feedback_response_error))
                                    customOnBackPressed()
                                }
                            }
                        )
                    }
                }

                else -> {}
            }
        }
    }

    private fun getLocalizedString(
        @StringRes resourceId: Int,
        vararg formatArgs: String = emptyArray()
    ): String {
        return getLocalizedString<String>(
            context = mActivity,
            fLocale = Locale(mAppLanguage),
            resourceId = resourceId,
            formatArgs = formatArgs
        )
    }

    override fun onScreenFinishing() {
        super.onScreenFinishing()
        onScreenFinish.invoke()
    }
}