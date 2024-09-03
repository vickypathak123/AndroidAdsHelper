package com.example.app.ads.helper.purchase.timeline.utils

import android.content.res.ColorStateList
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.example.app.ads.helper.R

data class TimeLineScreenDataModel(
    @StringRes
    var listOfInstantAccessHint: ArrayList<Int> = ArrayList(),
    @RawRes
    var instantAccessLottieFileRawRes: Int = R.raw.lottie_subscription_unlock_today_bg,
    var isWithInstantAccessAnimation: Boolean = false,
    var isWithSliderAnimation: Boolean = false,

    var mainColor: ColorStateList? = null,
    var headerColor: ColorStateList? = null,
    var closeIconColor: ColorStateList? = null,
    var trackInactiveColor: ColorStateList? = null,
    var hintTextColor: ColorStateList? = null,
    var instantAccessHintTextColor: ColorStateList? = null,
    var secureWithPlayStoreTextColor: ColorStateList? = null,
    var secureWithPlayStoreBackgroundColor: ColorStateList? = null,
    var buttonContinueTextColor: ColorStateList? = null,

    )