package com.example.app.ads.helper.purchase.fourplan.utils

data class FourPlanScreenDataModel(
    var purchaseButtonTextIndex: Int = 0,
    var listOfBoxItem: ArrayList<FourPlanUserItem>,
    var listOfRattingItem: ArrayList<FourPlanRattingItem>,
    var lifeTimePlanDiscountPercentage: Int = 80,
)