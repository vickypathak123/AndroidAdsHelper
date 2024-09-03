package com.example.app.ads.helper.purchase.fourplan.utils

import android.view.Gravity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.app.ads.helper.purchase.sixbox.utils.TextGravityFlags


data class FourPlanRattingItem(
    val ratingCount: Float = 0.0f,
    @StringRes
    val ratingHeader: Int = 0,
    @StringRes
    val ratingSubHeader: Int = 0,

    val satisfiedCustomerCount: Int = 0,
    @DrawableRes
    val satisfiedCustomerDrawable: Int = 0,

    @StringRes
    var reviewTitle: Int = 0,
    @StringRes
    var reviewSubTitle: Int = 0,
    @StringRes
    var reviewGivenBy: Int = 0,
    @TextGravityFlags
    var reviewGivenByTextGravity: Int = Gravity.END,

    var itemType: FourPlanRattingItemType
)

enum class FourPlanRattingItemType {
    APP_RATING,
    SATISFIED_CUSTOMER,
    REVIEW
}