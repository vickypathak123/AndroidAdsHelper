package com.example.ads.helper.new_.demo.newdemo.utils


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class AppRemoteConfigModel(
    @SerializedName("googleAds")
    @Expose
    val googleAds: GoogleAdsModel = GoogleAdsModel(),
    @SerializedName("vasu_subscription_config")
    @Expose
    val vasuSubscriptionConfig: VasuSubscriptionConfigModel = VasuSubscriptionConfigModel(),
    @SerializedName("playIntegrity")
    @Expose
    val playIntegrity: PlayIntegrityModel = PlayIntegrityModel(),
) : Parcelable {
    override fun toString(): String {
        return StringBuilder().apply {
            append("{\n")
            append("\t${googleAds},\n")
            append("\t${vasuSubscriptionConfig},\n")
            append("\t${playIntegrity}\n")
            append("}")
        }.toString()
    }
}


@Keep
@Parcelize
data class GoogleAdsModel(
    @SerializedName("is_need_to_show_banner_ad")
    @Expose
    val isNeedToShowBannerAd: Boolean = true,
    @SerializedName("is_need_to_show_interstitial_ad")
    @Expose
    val isNeedToShowInterstitialAd: Boolean = true,
    @SerializedName("is_need_to_show_native_ad")
    @Expose
    val isNeedToShowNativeAd: Boolean = true,
    @SerializedName("is_need_to_show_app_open_ad")
    @Expose
    val isNeedToShowAppOpenAd: Boolean = true,
    @SerializedName("is_need_to_show_rewarded_interstitial_ad")
    @Expose
    val isNeedToShowRewardedInterstitialAd: Boolean = true,
    @SerializedName("is_need_to_show_rewarded_video_ad")
    @Expose
    val isNeedToShowRewardedVideoAd: Boolean = true
) : Parcelable {
    override fun toString(): String {
        return StringBuilder().apply {
            append("\"googleAds\": {\n")
            append("\t\t\"is_need_to_show_banner_ad\": ${isNeedToShowBannerAd},\n")
            append("\t\t\"is_need_to_show_interstitial_ad\": ${isNeedToShowInterstitialAd},\n")
            append("\t\t\"is_need_to_show_native_ad\": ${isNeedToShowNativeAd},\n")
            append("\t\t\"is_need_to_show_app_open_ad\": ${isNeedToShowAppOpenAd},\n")
            append("\t\t\"is_need_to_show_rewarded_interstitial_ad\": ${isNeedToShowRewardedInterstitialAd},\n")
            append("\t\t\"is_need_to_show_rewarded_video_ad\": ${isNeedToShowRewardedVideoAd}\n")
            append("\t}")
        }.toString()
    }
}

@Keep
@Parcelize
data class VasuSubscriptionConfigModel(
    @SerializedName("initial_subscription_open_flow")
    @Expose
    val initialSubscriptionOpenFlow: ArrayList<Int> = arrayListOf(1, 2, 3),
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
    @SerializedName("_comment")
    @Expose
    val comment: String = "\"more_plan_screen_type\": \"six_box_screen / four_plan_screen\"",
    @SerializedName("more_plan_screen_type")
    @Expose
    val morePlanScreenType: String = "four_plan_screen",
    @SerializedName("life_time_plan_discount_percentage")
    @Expose
    val lifeTimePlanDiscountPercentage: Int = 90,

    @SerializedName("ratting_bar_slider_timing")
    @Expose
    val rattingBarSliderTiming: Int = 5000,

    @SerializedName("subscription_close_icon_show_time")
    @Expose
    val subscriptionCloseIconShowTime: Long = 3000,

    @SerializedName("is_show_review_dialog")
    @Expose
    val isShowReviewDialog: Boolean = false
) : Parcelable {
    override fun toString(): String {
        return StringBuilder().apply {
            append("\"vasu_subscription_config\": {\n")
            append("\t\t\"initial_subscription_open_flow\": ${initialSubscriptionOpenFlow},\n")
            append("\t\t\"purchase_button_text_index\": ${purchaseButtonTextIndex},\n")
            append("\t\t\"initial_subscription_time_line_close_ad\": ${initialSubscriptionTimeLineCloseAd},\n")
            append("\t\t\"initial_subscription_more_plan_close_ad\": ${initialSubscriptionMorePlanCloseAd},\n")
            append("\t\t\"in_app_subscription_ad_close\": ${inAppSubscriptionAdClose},\n")
            append("\t\t\"more_plan_screen_type\": ${morePlanScreenType},\n")
            append("\t\t\"life_time_plan_discount_percentage\": ${lifeTimePlanDiscountPercentage},\n")
            append("\t\t\"ratting_bar_slider_timing\": ${rattingBarSliderTiming},\n")
            append("\t\t\"subscription_close_icon_show_time\": ${subscriptionCloseIconShowTime},\n")
            append("\t\t\"is_show_review_dialog\": ${isShowReviewDialog}\n")
            append("\t}")
        }.toString()
    }
}

@Keep
@Parcelize
data class PlayIntegrityModel(
    @SerializedName("errorHide")
    @Expose
    val errorHide: Boolean = false,
    @SerializedName("verdictsResponseCodes")
    @Expose
    val verdictsResponseCodes: ArrayList<String> = arrayListOf()
) : Parcelable {
    override fun toString(): String {
        return StringBuilder().apply {
            append("\"playIntegrity\": {\n")
            append("\t\t\"errorHide\": ${errorHide},\n")
            append("\t\t\"verdictsResponseCodes\": ${verdictsResponseCodes}\n")
            append("\t}")
        }.toString()
    }
}