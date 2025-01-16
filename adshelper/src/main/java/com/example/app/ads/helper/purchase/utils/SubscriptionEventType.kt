package com.example.app.ads.helper.purchase.utils

import android.annotation.SuppressLint
import android.os.Bundle
import com.example.app.ads.helper.purchase.product.PlanOfferType
import com.example.app.ads.helper.purchase.product.ProductInfo
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getSKU
import com.example.app.ads.helper.purchase.removeTrailingZeros
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * @author Akshay Harsoda
 * @since 20 Aug 2024
 */
//enum class SubscriptionEventType {
//    TIME_LINE_SCREEN_OPEN,
//    VIEW_ALL_PLANS_SCREEN_OPEN,
//    TIME_LINE_SCREEN_CLOSE,
//    VIEW_ALL_PLANS_SCREEN_CLOSE,
//    VIEW_MORE_PLANS_CLICK,
//    MONTHLY_SUBSCRIBE,
//    WEEKLY_SUBSCRIBE,
//    YEARLY_SUBSCRIBE,
//    LIFE_TIME_PURCHASE,
//    MONTHLY_FREE_TRAIL_SUBSCRIBE,
//    WEEKLY_FREE_TRAIL_SUBSCRIBE,
//    YEARLY_FREE_TRAIL_SUBSCRIBE,
//    LIFE_TIME_FREE_TRAIL_PURCHASE,
//}

@Suppress("ClassName")
sealed class SubscriptionEventType {
    data object TIME_LINE_SCREEN_OPEN : SubscriptionEventType()
    data object VIEW_ALL_PLANS_SCREEN_OPEN : SubscriptionEventType()
    data object TIME_LINE_SCREEN_CLOSE : SubscriptionEventType()
    data object VIEW_ALL_PLANS_SCREEN_CLOSE : SubscriptionEventType()
    data object VIEW_MORE_PLANS_CLICK : SubscriptionEventType()

    data class MONTHLY_SUBSCRIBE(val paramBundle: Bundle) : SubscriptionEventType()
    data class WEEKLY_SUBSCRIBE(val paramBundle: Bundle) : SubscriptionEventType()
    data class YEARLY_SUBSCRIBE(val paramBundle: Bundle) : SubscriptionEventType()
    data class LIFE_TIME_PURCHASE(val paramBundle: Bundle) : SubscriptionEventType()

    data class MONTHLY_FREE_TRAIL_SUBSCRIBE(val paramBundle: Bundle) : SubscriptionEventType()
    data class WEEKLY_FREE_TRAIL_SUBSCRIBE(val paramBundle: Bundle) : SubscriptionEventType()
    data class YEARLY_FREE_TRAIL_SUBSCRIBE(val paramBundle: Bundle) : SubscriptionEventType()
    data class LIFE_TIME_FREE_TRAIL_PURCHASE(val paramBundle: Bundle) : SubscriptionEventType()
}

@SuppressLint("SimpleDateFormat")
internal fun getEventParamBundle(
    productInfo: ProductInfo,
): Bundle {

    val fPlanSkuId = productInfo.id.getSKU
    val fPlanPrice = (productInfo.offerFormattedPrice.takeIf { productInfo.planOfferType == PlanOfferType.INTRO_OFFER } ?: productInfo.formattedPrice).removeTrailingZeros
    val fPlanPriceCurrencyCode = productInfo.priceCurrencyCode
    val fPlanPriceBillingPeriod = productInfo.offerActualBillingPeriod.takeIf { productInfo.planOfferType == PlanOfferType.INTRO_OFFER } ?: productInfo.actualBillingPeriod
    val fPlanPriceFreeTrialPeriod = productInfo.actualFreeTrialPeriod.takeIf { productInfo.planOfferType == PlanOfferType.FREE_TRIAL } ?: ""
    val fIsPlanPurchaseWithFreeTrial = (productInfo.planOfferType == PlanOfferType.FREE_TRIAL)

    val jsonObject = JSONObject()
    jsonObject.put("country_name", Locale.getDefault().displayCountry)
    if (fPlanSkuId.isNotEmpty()) {
        jsonObject.put("plan_sku_id", fPlanSkuId)
    }
    if (fPlanPrice.isNotEmpty()) {
        jsonObject.put("plan_price", fPlanPrice)
    }
    if (fPlanPriceCurrencyCode.isNotEmpty()) {
        jsonObject.put("plan_price_currency_code", fPlanPriceCurrencyCode)
    }
    if (fPlanPriceBillingPeriod.isNotEmpty()) {
        jsonObject.put("plan_price_billing_period", fPlanPriceBillingPeriod)
    }
    if (fIsPlanPurchaseWithFreeTrial) {
        if (fPlanPriceFreeTrialPeriod.isNotEmpty()) {
            jsonObject.put("plan_price_free_trial_period", fPlanPriceFreeTrialPeriod)
        }
        jsonObject.put("is_plan_purchase_with_free_trial", "true")
    }
    jsonObject.put("plan_purchase_date_time", SimpleDateFormat("'Date:-' dd MMM yyyy 'Time:-' hh:mm:ss a").format(System.currentTimeMillis()))

    val paramBundle = Bundle()
    jsonObject.keys().forEach {
        paramBundle.putString(it, jsonObject.getString(it))
    }
    return paramBundle
}