package com.example.app.ads.helper.feedback

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.text.InputFilter
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.BaseActivity
import com.example.app.ads.helper.base.BaseBindingActivity
import com.example.app.ads.helper.base.utils.gone
import com.example.app.ads.helper.base.utils.inflateLayout
import com.example.app.ads.helper.base.utils.isTiramisuPlus
import com.example.app.ads.helper.base.utils.makeText
import com.example.app.ads.helper.base.utils.visible
import com.example.app.ads.helper.databinding.ActivityFeedBackBinding
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getFullBillingPeriod
import com.example.app.ads.helper.purchase.sixbox.utils.TextGravityFlags
import com.example.app.ads.helper.retrofit.enqueue.APICallEnqueue.feedbackApi
import com.example.app.ads.helper.retrofit.listener.APIResponseListener
import com.example.app.ads.helper.utils.getLocalizedString
import com.example.app.ads.helper.utils.isOnline
import com.example.app.ads.helper.utils.logE
import com.example.app.ads.helper.utils.logI
import org.json.JSONObject
import java.util.Locale

internal class FeedBackActivity : BaseBindingActivity<ActivityFeedBackBinding>() {

    private val mAppLanguage: String get() = screenData.first
    private val mAppPackageName: String get() = screenData.second
    private val mAppVersionName: String get() = screenData.third

    @get:LayoutRes
    private val mBackIconViewResource: Int get() = screenDataModel.backIconViewResource

    @TextGravityFlags
    private val screenTitleTextGravity: Int get() = screenDataModel.screenTitleTextGravity

    private val maxLength: Int = 500

    //    private val Int.suggestionCount: String get() = "${this}/${maxLength}" getLocalizedString(resourceId = R.string.price_slash_period)
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

    override fun setBinding(): ActivityFeedBackBinding {
        return ActivityFeedBackBinding.inflate(layoutInflater)
    }

    override fun getActivityContext(): BaseActivity {
        return this@FeedBackActivity
    }

    override fun initView() {
        super.initView()
        with(mBinding) {

            ivHeaderBack.apply {
                removeAllViews()
                addView(this.inflateLayout(mBackIconViewResource))
            }

            headerRightIconView.apply {
                removeAllViews()
                addView(this.inflateLayout(mBackIconViewResource))
            }

            txtHeaderTitle.apply {
                this.text = getLocalizedString(resourceId = R.string.feedback_title)

                this.gravity = when (screenTitleTextGravity) {
                    Gravity.CENTER -> Gravity.CENTER
                    Gravity.START -> Gravity.CENTER_VERTICAL or Gravity.START
                    else -> Gravity.CENTER_VERTICAL or Gravity.END
                }
            }

            txtTitle.text = getLocalizedString(resourceId = R.string.feedback_sub_title)
            txtWhatIsYourProfession.text = getLocalizedString(resourceId = R.string.feedback_what_is_your_profession)
            txtYourSuggestions.text = getLocalizedString(resourceId = R.string.feedback_write_your_suggestions)
            etProfession.hint = getLocalizedString(resourceId = R.string.feedback_write_here)
            etSuggestions.hint = getLocalizedString(resourceId = R.string.feedback_write_here)
            txtSuggestionsCount.text = 0.suggestionCount
            txtBtnSubmit.text = getLocalizedString(resourceId = R.string.feedback_submit)
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
            (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(this.windowToken, 0)
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
                            fListener = object : APIResponseListener<JSONObject> {
                                override fun onSuccess(fResponse: JSONObject) {
                                    logI(TAG, "onSuccess: $fResponse")
                                    makeText(text = getLocalizedString(resourceId = R.string.feedback_response_success))
                                    customOnBackPressed()
                                }

                                override fun onError(fErrorMessage: String?) {
                                    super.onError(fErrorMessage)
                                    logE(TAG, "onError: $fErrorMessage")
                                    clProgress.gone
                                    makeText(text = getLocalizedString(resourceId = R.string.feedback_response_error))
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