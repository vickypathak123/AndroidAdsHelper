package com.example.app.ads.helper.purchase.weekly.utill

import androidx.annotation.DrawableRes

data class WeeklyPlanScreenDataModel(
    @DrawableRes
    var backgroundDrawable: Int,
    var listOfPoints: ArrayList<WeeklyPlanUserItem>,
    var weeklyScreenCloseIconTime: Long
)