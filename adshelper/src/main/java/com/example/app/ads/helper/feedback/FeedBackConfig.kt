@file:Suppress("unused")

package com.example.app.ads.helper.feedback

import android.app.Activity
import android.view.Gravity
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.annotation.LayoutRes
import com.example.app.ads.helper.R
import com.example.app.ads.helper.purchase.sixbox.utils.TextGravityFlags
import java.io.Serializable
import java.lang.ref.WeakReference

object FeedBackConfig {

    /**
     * initialization of subscription screen ui data
     */
    @JvmStatic
    fun with(fActivity: Activity, fAppVersionName: String): ActivityData {
        return ActivityData(fActivity = fActivity, fAppVersionName = fAppVersionName)
    }

    class ActivityData(private val fActivity: Activity, private val fAppVersionName: String) : Serializable {

        @Suppress("PropertyName")
        val TAG: String = "FeedBackConfig_${javaClass.simpleName}"

        private val mContextRef: WeakReference<Activity> = WeakReference(fActivity)
        private val mActivity: Activity get() = mContextRef.get() ?: fActivity

        private var mLanguageCode: String = "en"

        private var mFeedBackScreenData: FeedBackScreenData = FeedBackScreenData(fActivity = fActivity)


        /**
         * @param fCode it's refers to your app language code.
         */
        @JvmName("setAppLanguageCode")
        fun setAppLanguageCode(fCode: String) = this@ActivityData.apply {
            this.mLanguageCode = fCode
        }

        @JvmName("setFeedBackScreenData")
        fun setFeedBackScreenData(fFeedBackScreenData: FeedBackScreenData) = this@ActivityData.apply {
            this.mFeedBackScreenData = fFeedBackScreenData
        }

        @JvmName("setFeedBackScreenData")
        fun setFeedBackScreenData(action: (fFeedBackScreenData: FeedBackScreenData) -> Unit) = this@ActivityData.apply {
            action.invoke(this.mFeedBackScreenData)
        }


        /**
         * launch subscription screen
         *
         * @param onScreenFinish callback for screen finish. [@param isUserPurchaseAnyPlan true if user will purchase any plan]
         */
        fun launchScreen(
            onScreenFinish: () -> Unit,
        ) {
            FeedBackActivity.launchScreen(
                fActivity = mActivity,
                screenData = Triple(mLanguageCode, mActivity.packageName, fAppVersionName),
                screenDataModel = FeedBackScreenDataModel(
                    useLightStatusBar = mFeedBackScreenData.useLightStatusBar,
                    useLightNavigationBar = mFeedBackScreenData.useLightNavigationBar,
                    screenBackgroundColor = mFeedBackScreenData.screenBackgroundColor,
                    toolbarBackgroundColor = mFeedBackScreenData.toolbarBackgroundColor,
                    backIconViewResource = mFeedBackScreenData.backIconViewResource,
                    toolbarTextGravity = mFeedBackScreenData.toolbarTextGravity,
                    toolbarTextColor = mFeedBackScreenData.toolbarTextColor,
                    toolbarTextSize = mFeedBackScreenData.toolbarTextSize,
                    toolbarTextFontFamily = mFeedBackScreenData.toolbarTextFontFamily,
                    titleTextColor = mFeedBackScreenData.titleTextColor,
                    titleTextFontFamily = mFeedBackScreenData.titleTextFontFamily,
                    questionTextColor = mFeedBackScreenData.questionTextColor,
                    questionTextFontFamily = mFeedBackScreenData.questionTextFontFamily,
                    answerTextColor = mFeedBackScreenData.answerTextColor,
                    answerTextFontFamily = mFeedBackScreenData.answerTextFontFamily,
                    answerTextHintColor = mFeedBackScreenData.answerTextHintColor,
                    answerCountTextColor = mFeedBackScreenData.answerCountTextColor,
                    answerCountTextFontFamily = mFeedBackScreenData.answerCountTextFontFamily,
                    submitButtonTextColor = mFeedBackScreenData.submitButtonTextColor,
                    submitButtonTextFontFamily = mFeedBackScreenData.submitButtonTextFontFamily,
                    progressBarColor = mFeedBackScreenData.progressBarColor,
                    answerBackground = mFeedBackScreenData.answerBackground,
                    submitBackground = mFeedBackScreenData.submitBackground,
                ),
                onScreenFinish = {
                    onScreenFinish.invoke()
                }
            )
        }

        class FeedBackScreenData(private val fActivity: Activity) : Serializable {

            private var _useLightStatusBar: Boolean = true
            internal val useLightStatusBar: Boolean get() = _useLightStatusBar

            private var _useLightNavigationBar: Boolean = true
            internal val useLightNavigationBar: Boolean get() = _useLightNavigationBar

            @ColorRes
            private var _screenBackgroundColor: Int = android.R.color.white

            @get:ColorRes
            internal val screenBackgroundColor: Int get() = _screenBackgroundColor

            @ColorRes
            private var _toolbarBackgroundColor: Int = android.R.color.white

            @get:ColorRes
            internal val toolbarBackgroundColor: Int get() = _toolbarBackgroundColor

            @LayoutRes
            private var _backIconViewResource: Int = R.layout.item_toolbar_feedback

            @get:LayoutRes
            internal val backIconViewResource: Int get() = _backIconViewResource

            @TextGravityFlags
            private var _toolbarTextGravity: Int = Gravity.CENTER

            @get:TextGravityFlags
            internal val toolbarTextGravity: Int get() = _toolbarTextGravity

            @ColorRes
            private var _toolbarTextColor: Int = R.color.default_feedback_screen_toolbar_text_color

            @get:ColorRes
            internal val toolbarTextColor: Int get() = _toolbarTextColor

            @DimenRes
            private var _toolbarTextSize: Int = com.intuit.ssp.R.dimen._18ssp

            @get:DimenRes
            internal val toolbarTextSize: Int get() = _toolbarTextSize

            @FontRes
            private var _toolbarTextFontFamily: Int = R.font.ads_plus_jakarta_sans_semi_bold

            @get:FontRes
            internal val toolbarTextFontFamily: Int get() = _toolbarTextFontFamily

            @ColorRes
            private var _titleTextColor: Int = R.color.default_feedback_screen_title_text_color

            @get:ColorRes
            internal val titleTextColor: Int get() = _titleTextColor

            @FontRes
            private var _titleTextFontFamily: Int = R.font.ads_plus_jakarta_sans_medium

            @get:FontRes
            internal val titleTextFontFamily: Int get() = _titleTextFontFamily

            @ColorRes
            private var _questionTextColor: Int = R.color.default_feedback_screen_question_text_color

            @get:ColorRes
            internal val questionTextColor: Int get() = _questionTextColor

            @FontRes
            private var _questionTextFontFamily: Int = R.font.ads_plus_jakarta_sans_semi_bold

            @get:FontRes
            internal val questionTextFontFamily: Int get() = _questionTextFontFamily

            @ColorRes
            private var _answerTextColor: Int = R.color.default_feedback_screen_answer_text_color

            @get:ColorRes
            internal val answerTextColor: Int get() = _answerTextColor

            @FontRes
            private var _answerTextFontFamily: Int = R.font.ads_plus_jakarta_sans_medium

            @get:FontRes
            internal val answerTextFontFamily: Int get() = _answerTextFontFamily

            @ColorRes
            private var _answerTextHintColor: Int = R.color.default_feedback_screen_answer_text_hint_color

            @get:ColorRes
            internal val answerTextHintColor: Int get() = _answerTextHintColor

            @ColorRes
            private var _answerCountTextColor: Int = R.color.default_feedback_screen_answer_Count_text_color

            @get:ColorRes
            internal val answerCountTextColor: Int get() = _answerCountTextColor

            @FontRes
            private var _answerCountTextFontFamily: Int = R.font.ads_plus_jakarta_sans_medium

            @get:FontRes
            internal val answerCountTextFontFamily: Int get() = _answerCountTextFontFamily

            @ColorRes
            private var _submitButtonTextColor: Int = android.R.color.white

            @get:ColorRes
            internal val submitButtonTextColor: Int get() = _submitButtonTextColor

            @FontRes
            private var _submitButtonTextFontFamily: Int = R.font.ads_avenir_heavy

            @get:FontRes
            internal val submitButtonTextFontFamily: Int get() = _submitButtonTextFontFamily

            @ColorRes
            private var _progressBarColor: Int = R.color.default_view_more_plan_rating_indicator_color

            @get:ColorRes
            internal val progressBarColor: Int get() = _progressBarColor

            private var _answerBackground: FeedBackBackgroundItem = FeedBackBackgroundItem(backgroundColorRes = R.color.default_feedback_screen_answer_text_background_color)
            internal val answerBackground: FeedBackBackgroundItem get() = _answerBackground

            private var _submitBackground: FeedBackBackgroundItem = FeedBackBackgroundItem(backgroundDrawableRes = R.drawable.bg_feed_back_submit_button)
            internal val submitBackground: FeedBackBackgroundItem get() = _submitBackground


            @JvmName("useLightStatusBar")
            fun useLightStatusBar(isLight: Boolean) = this@FeedBackScreenData.apply {
                this._useLightStatusBar = isLight
                return this
            }

            @JvmName("useLightNavigationBar")
            fun useLightNavigationBar(isLight: Boolean) = this@FeedBackScreenData.apply {
                this._useLightNavigationBar = isLight
                return this
            }

            @JvmName("screenBackgroundColor")
            fun screenBackgroundColor(@ColorRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._screenBackgroundColor = resourceId
                return this
            }


            @JvmName("toolbarBackgroundColor")
            fun toolbarBackgroundColor(@ColorRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._toolbarBackgroundColor = resourceId
                return this
            }


            @JvmName("backIconViewResource")
            fun backIconViewResource(@LayoutRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._backIconViewResource = resourceId
                return this
            }

            @JvmName("toolbarTextGravity")
            fun toolbarTextGravity(@TextGravityFlags gravity: Int) = this@FeedBackScreenData.apply {
                this._toolbarTextGravity = gravity
                return this
            }

            @JvmName("toolbarTextColor")
            fun toolbarTextColor(@ColorRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._toolbarTextColor = resourceId
                return this
            }

            @JvmName("toolbarTextSize")
            fun toolbarTextSize(@DimenRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._toolbarTextSize = resourceId
                return this
            }

            @JvmName("toolbarTextFontFamily")
            fun toolbarTextFontFamily(@FontRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._toolbarTextFontFamily = resourceId
                return this
            }

            @JvmName("titleTextColor")
            fun titleTextColor(@ColorRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._titleTextColor = resourceId
                return this
            }

            @JvmName("titleTextFontFamily")
            fun titleTextFontFamily(@FontRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._titleTextFontFamily = resourceId
                return this
            }

            @JvmName("questionTextColor")
            fun questionTextColor(@ColorRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._questionTextColor = resourceId
                return this
            }

            @JvmName("questionTextFontFamily")
            fun questionTextFontFamily(@FontRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._questionTextFontFamily = resourceId
                return this
            }

            @JvmName("answerTextColor")
            fun answerTextColor(@ColorRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._answerTextColor = resourceId
                return this
            }

            @JvmName("answerTextFontFamily")
            fun answerTextFontFamily(@FontRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._answerTextFontFamily = resourceId
                return this
            }

            @JvmName("answerTextHintColor")
            fun answerTextHintColor(@ColorRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._answerTextHintColor = resourceId
                return this
            }

            @JvmName("answerCountTextColor")
            fun answerCountTextColor(@ColorRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._answerCountTextColor = resourceId
                return this
            }

            @JvmName("answerCountTextFontFamily")
            fun answerCountTextFontFamily(@FontRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._answerCountTextFontFamily = resourceId
                return this
            }

            @JvmName("submitButtonTextColor")
            fun submitButtonTextColor(@ColorRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._submitButtonTextColor = resourceId
                return this
            }

            @JvmName("submitButtonTextFontFamily")
            fun submitButtonTextFontFamily(@FontRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._submitButtonTextFontFamily = resourceId
                return this
            }

            @JvmName("progressBarColor")
            fun progressBarColor(@ColorRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._progressBarColor = resourceId
                return this
            }

            @JvmName("answerBackground")
            fun answerBackground(backgroundItem: FeedBackBackgroundItem) = this@FeedBackScreenData.apply {
                this._answerBackground = backgroundItem
                return this
            }

            @JvmName("submitBackground")
            fun submitBackground(backgroundItem: FeedBackBackgroundItem) = this@FeedBackScreenData.apply {
                this._submitBackground = backgroundItem
                return this
            }
        }
    }
}