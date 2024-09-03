package com.example.app.ads.helper.purchase.fourplan.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class FourPlanUserItem(
    @DrawableRes
    var backgroundDrawable: Int,
    @DrawableRes
    var itemIcon: Int,
    @StringRes
    var itemName: Int,
)
