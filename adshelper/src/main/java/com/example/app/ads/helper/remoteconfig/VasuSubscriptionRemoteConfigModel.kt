package com.example.app.ads.helper.remoteconfig


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.Expose

@Keep
@Parcelize
data class VasuSubscriptionRemoteConfigModel(
    @SerializedName("initial_subscription_open_flow")
    @Expose
    val initialSubscriptionOpenFlow: List<Int> = listOf(1,2,3),
    @SerializedName("purchase_button_text_index")
    @Expose
    val purchaseButtonTextIndex: Int = 0,
    @SerializedName("initial_subscription_time_line_close_ad")
    @Expose
    val initialSubscriptionTimeLineCloseAd: Boolean = true,
    @SerializedName("initial_subscription_more_plan_close_ad")
    @Expose
    val initialSubscriptionMorePlanCloseAd: Boolean = true,
    @SerializedName("in_app_subscription_ad_close")
    @Expose
    val inAppSubscriptionAdClose: Boolean = true,
    @SerializedName("more_plan_screen_type")
    @Expose
    val morePlanScreenType: String = "four_plan_screen",
    @SerializedName("life_time_plan_discount_percentage")
    @Expose
    val lifeTimePlanDiscountPercentage: Int = 90,
    @SerializedName("ratting_bar_slider_timing")
    @Expose
    val rattingBarSliderTiming: Long = 5000,
) : Parcelable {

    override fun toString(): String {
        return StringBuilder().apply {
            append("vasu_subscription_config : {")
            append("\ninitial_subscription_open_flow : '${initialSubscriptionOpenFlow}', ")
            append("\npurchase_button_text_index : '${purchaseButtonTextIndex}', ")
            append("\ninitial_subscription_time_line_close_ad : '${initialSubscriptionTimeLineCloseAd}', ")
            append("\ninitial_subscription_more_plan_close_ad : '${initialSubscriptionMorePlanCloseAd}', ")
            append("\nin_app_subscription_ad_close : '${inAppSubscriptionAdClose}', ")
            append("\nmore_plan_screen_type : '${morePlanScreenType}', ")
            append("\nlife_time_plan_discount_percentage : '${lifeTimePlanDiscountPercentage}'")
            append("\nratting_bar_slider_timing : '${rattingBarSliderTiming}'")
            append("}")
        }.toString()
    }
}