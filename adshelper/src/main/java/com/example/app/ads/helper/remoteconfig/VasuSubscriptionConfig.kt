package com.example.app.ads.helper.remoteconfig

import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject

var mVasuSubscriptionConfigModel: VasuSubscriptionConfigModel = VasuSubscriptionConfigModel()

fun initSubscriptionRemoteConfig(jsonString: String) {

    val jsonObject = JSONObject(jsonString)

    if (jsonObject.has("vasu_subscription_config")) {
        val vasuSubscriptionConfig = jsonObject.getJSONObject("vasu_subscription_config")
        mVasuSubscriptionConfigModel = Gson().fromJson(vasuSubscriptionConfig.toString(), VasuSubscriptionConfigModel::class.java)
    }

    Log.i("RemoteConfig", "initSubscriptionRemoteConfig: $mVasuSubscriptionConfigModel")
}

val testJson: String = "{\n" +
        "  \"subscription\": {\n" +
        "    \"screen_count\": \"7\",\n" +
        "    \"initial_Sub_Discount_Popup_Open\": [\n" +
        "      1\n" +
        "    ],\n" +
        "    \"subscription_Continue_Btn_Text\": 1\n" +
        "  },\n" +
        "  \"googleAds\": {\n" +
        "    \"isNeedToShowInterstitialAds\": true,\n" +
        "    \"isNeedToShowRewardAds\": true,\n" +
        "    \"isNeedToShowNativeAds\": true,\n" +
        "    \"isNeedToShowBannerAds\": true,\n" +
        "    \"isNeedToShowOpenAds\": true\n" +
        "  },\n" +
        "  \"initial_Open_Flow\": [\n" +
        "    1,\n" +
        "    1\n" +
        "  ],\n" +
        "  \"ApiConfig\": {\n" +
        "    \"reels_limit\": 25\n" +
        "  },\n" +
        "  \"RatingConfig\": {\n" +
        "    \"home_Open\": 5,\n" +
        "    \"share_Voice_Open\": 3,\n" +
        "    \"share_Voice_HomeClick_Open\": 3,\n" +
        "    \"prank_Back\": 3,\n" +
        "    \"textToVoice_Back\": 3,\n" +
        "    \"message_Download_Click\": 3\n" +
        "  },\n" +
        "  \"HomeScreenFreeTrial\": {\n" +
        "    \"isShowPopUp\": true,\n" +
        "    \"freeTrialPlan\": \"Month\"\n" +
        "  },\n" +
        "  \"AdShowConfig\": {\n" +
        "    \"subscription_Close\": true,\n" +
        "    \"initial_subscription_Close\": true,\n" +
        "    \"voice_text_my_work_open\": 0,\n" +
        "    \"save_audio\": 0,\n" +
        "    \"setting_back\": 0,\n" +
        "    \"subsciption_Tired_Ad_Open\": 3,\n" +
        "    \"mywork_audio_video_open\": 0,\n" +
        "    \"prank_Cat_Open\": 5,\n" +
        "    \"prank_SoundPlay_Back\": 3,\n" +
        "    \"reels_Open\": 3,\n" +
        "    \"reels_Back\": 2,\n" +
        "    \"reels_TrySound\": 3,\n" +
        "    \"reels_Scroll\": 5\n" +
        "  },\n" +
        "  \"playIntegrity\": {\n" +
        "    \"errorHide\": \"false\",\n" +
        "    \"verdictsResponseCodes\": [\n" +
        "      \"UNRECOGNIZED_VERSION\",\n" +
        "      \"UNEVALUATED\"\n" +
        "    ]\n" +
        "  },\n" +
        "  \"AIVoiceLimitConfig\": {\n" +
        "    \"noOfMin_Text\": 10,\n" +
        "    \"noOfMax_Text_WithOutPurchase\": 150,\n" +
        "    \"noOfMax_Text_WithPurchase\": 1000,\n" +
        "    \"isText_SpecialCharAllow\": true,\n" +
        "    \"noOfMin_SecondsAudio\": 3,\n" +
        "    \"noOfMax_SecondsAudio\": 60,\n" +
        "    \"noOfMin_SecondsVideo\": 3,\n" +
        "    \"noOfMax_SecondsVideo\": 60,\n" +
        "    \"minAudio_SizeinMB\": 0,\n" +
        "    \"maxAudio_SizeinMB\": 50\n" +
        "  },\n" +
        "  \"vasu_subscription_config\": {\n" +
        "    \"initial_subscription_open_flow\": [1],\n" +
        "    \"purchase_button_text_index\": 1,\n" +
        "    \"initial_subscription_time_line_close_ad\": true,\n" +
        "    \"initial_subscription_more_plan_close_ad\": true,\n" +
        "    \"in_app_subscription_ad_close\": true,\n" +
//        "    \"more_plan_screen_type\": \"four_plan_screen\",\n" +
//        "    \"more_plan_screen_type\": \"six_box_screen\",\n" +
        "    \"life_time_plan_discount_percentage\": 50\n" +
        "  }" +
        "}"