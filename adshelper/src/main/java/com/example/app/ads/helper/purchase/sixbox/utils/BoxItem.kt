package com.example.app.ads.helper.purchase.sixbox.utils

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class BoxItem(
    @ColorInt
    var backgroundColor: Int = 0,
    @ColorInt
    var foregroundColor: Int = 0,
    @StringRes
    var itemName: Int,
    @DrawableRes
    var itemIcon: Int,
)