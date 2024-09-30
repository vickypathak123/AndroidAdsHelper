package com.example.app.ads.helper.feedback

import android.view.Gravity
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.LayoutRes
import com.example.app.ads.helper.R
import com.example.app.ads.helper.purchase.sixbox.utils.TextGravityFlags

data class FeedBackScreenDataModel(
    var useLightStatusBar: Boolean = true,
    var useLightNavigationBar: Boolean = true,

    @ColorRes
    var screenBackgroundColor: Int = android.R.color.white,

    @ColorRes
    var toolbarBackgroundColor: Int = android.R.color.white,

    @LayoutRes
    var backIconViewResource: Int = R.layout.item_toolbar_feedback,

    @TextGravityFlags
    var toolbarTextGravity: Int = Gravity.CENTER,

    @ColorRes
    var toolbarTextColor: Int = R.color.default_feedback_screen_toolbar_text_color,

    @DimenRes
    var toolbarTextSize: Int = com.intuit.ssp.R.dimen._18ssp,

    @FontRes
    var toolbarTextFontFamily: Int = R.font.ads_plus_jakarta_sans_semi_bold,

    @ColorRes
    var titleTextColor: Int = R.color.default_feedback_screen_title_text_color,

    @FontRes
    var titleTextFontFamily: Int = R.font.ads_plus_jakarta_sans_medium,

    @ColorRes
    var questionTextColor: Int = R.color.default_feedback_screen_question_text_color,

    @FontRes
    var questionTextFontFamily: Int = R.font.ads_plus_jakarta_sans_semi_bold,

    @ColorRes
    var answerTextColor: Int = R.color.default_feedback_screen_answer_text_color,

    @FontRes
    var answerTextFontFamily: Int = R.font.ads_plus_jakarta_sans_medium,

    @ColorRes
    var answerTextHintColor: Int = R.color.default_feedback_screen_answer_text_hint_color,

    @ColorRes
    var answerCountTextColor: Int = R.color.default_feedback_screen_answer_Count_text_color,

    @FontRes
    var answerCountTextFontFamily: Int = R.font.ads_plus_jakarta_sans_medium,

    @ColorRes
    var submitButtonTextColor: Int = android.R.color.white,

    @FontRes
    var submitButtonTextFontFamily: Int = R.font.ads_avenir_heavy,

    @ColorRes
    var progressBarColor: Int = R.color.default_view_more_plan_rating_indicator_color,

    var answerBackground: FeedBackBackgroundItem = FeedBackBackgroundItem(backgroundColorRes = R.color.default_feedback_screen_answer_text_background_color),

    var submitBackground: FeedBackBackgroundItem = FeedBackBackgroundItem(backgroundDrawableRes = R.drawable.bg_feed_back_submit_button),
)

data class FeedBackBackgroundItem(
    @DrawableRes
    var backgroundDrawableRes: Int = -1,
    @ColorRes
    var backgroundColorRes: Int = -1,
    @DrawableRes
    var strokeDrawableRes: Int = -1,
    @ColorRes
    var strokeColorRes: Int = -1,
)


//sealed class FeedBackAnswerBackgroundItem {
//
//    // Case 1: Background Drawable & Stroke Drawable
//    data class BackgroundDrawableStrokeDrawable(
//        @DrawableRes val backgroundDrawableRes: Int,
//        @DrawableRes val strokeDrawableRes: Int
//    ) : FeedBackAnswerBackgroundItem()
//
//    // Case 2: Background Drawable & Stroke Color
//    data class BackgroundDrawableStrokeColor(
//        @DrawableRes val backgroundDrawableRes: Int,
//        @ColorRes val strokeColorRes: Int
//    ) : FeedBackAnswerBackgroundItem()
//
//    // Case 3: Background Color & Stroke Drawable
//    data class BackgroundColorStrokeDrawable(
//        @ColorRes val backgroundColorRes: Int,
//        @DrawableRes val strokeDrawableRes: Int
//    ) : FeedBackAnswerBackgroundItem()
//
//    // Case 4: Background Color & Stroke Color
//    data class BackgroundColorStrokeColor(
//        @ColorRes val backgroundColorRes: Int,
//        @ColorRes val strokeColorRes: Int
//    ) : FeedBackAnswerBackgroundItem()
//}

