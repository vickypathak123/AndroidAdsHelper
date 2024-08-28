package com.example.app.ads.helper.purchase.sixbox.utils

import android.content.res.ColorStateList
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

data class ViewAllPlansScreenDataModel(
    var purchaseButtonTextIndex: Int,
    var listOfBoxItem: ArrayList<BoxItem>,
    var listOfRattingItem: ArrayList<RattingItem>,
    var yearPlanIconSelector: SelectorDrawableItem,
    var lifTimePlanIconSelector: SelectorDrawableItem,
    var monthPlanIconSelector: SelectorDrawableItem,
    var planSelector: SelectorDrawableItem,
    var planHeaderSelector: SelectorDrawableItem,
    var planBackgroundSelector: SelectorDrawableItem,
    var planHeaderTextColorSelector: SelectorColorItem,
    var planTitleTextColorSelector: SelectorColorItem,
    var planTrialPeriodTextColorSelector: SelectorColorItem,
    var planPriceTextColorSelector: SelectorColorItem,


    var headerColor: ColorStateList? = null,
    var subHeaderColor: ColorStateList? = null,
    var closeIconColor: ColorStateList? = null,
    var ratingColor: ColorStateList? = null,
    var ratingPlaceHolderColor: ColorStateList? = null,
    var ratingIndicatorColor: ColorStateList? = null,
    var unselectedItemDataColor: ColorStateList? = null,
    var selectedItemDataColor: ColorStateList? = null,
    var payNothingNowColor: ColorStateList? = null,
    var secureWithPlayStoreTextColor: ColorStateList? = null,
    var secureWithPlayStoreBackgroundColor: ColorStateList? = null,
    var itemBoxBackgroundColor: ColorStateList? = null,
    var selectedSkuBackgroundColor: ColorStateList? = null,
    var unselectedSkuBackgroundColor: ColorStateList? = null,
)

data class SelectorDrawableItem(
    @DrawableRes
    var selectedDrawableRes: Int = -1,
    @DrawableRes
    var defaultDrawableRes: Int = -1,
    @ColorRes
    var selectedColorRes: Int = -1,
    @ColorRes
    var defaultColorRes: Int = -1,
)

data class SelectorColorItem(
    @ColorRes
    var selectedColorRes: Int = -1,
    @ColorRes
    var defaultColorRes: Int = -1,
)