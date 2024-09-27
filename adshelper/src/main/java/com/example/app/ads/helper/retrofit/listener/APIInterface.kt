package com.example.app.ads.helper.retrofit.listener

import com.example.app.ads.helper.retrofit.model.ForceUpdateModel
import com.example.app.ads.helper.retrofit.utils.FORCE_UPDATE_API
import com.example.app.ads.helper.retrofit.utils.FORCE_UPDATE_PARAM_PACKAGE_NAME
import com.example.app.ads.helper.retrofit.utils.FORCE_UPDATE_PARAM_VERSION_CODE
import com.example.app.ads.helper.retrofit.utils.REVIEW_API
import com.example.app.ads.helper.retrofit.utils.REVIEW_PARAM_LANGUAGE_KEY
import com.example.app.ads.helper.retrofit.utils.REVIEW_PARAM_PACKAGE_NAME
import com.example.app.ads.helper.retrofit.utils.REVIEW_PARAM_REVIEW
import com.example.app.ads.helper.retrofit.utils.REVIEW_PARAM_SUBSCRIPTION_REVIEW
import com.example.app.ads.helper.retrofit.utils.REVIEW_PARAM_USE_OF_APP
import com.example.app.ads.helper.retrofit.utils.REVIEW_PARAM_VERSION_CODE
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface APIInterface {

    @Multipart
    @POST(FORCE_UPDATE_API)
    suspend fun forceUpdateApi(
        @Part(FORCE_UPDATE_PARAM_PACKAGE_NAME) packageName: RequestBody,
        @Part(FORCE_UPDATE_PARAM_VERSION_CODE) versionCode: RequestBody,
    ): ForceUpdateModel

    @FormUrlEncoded
    @POST(REVIEW_API)
    suspend fun subscriptionReviewApi(
        @Field(REVIEW_PARAM_PACKAGE_NAME) packageName: String,
        @Field(REVIEW_PARAM_VERSION_CODE) versionCode: String,
        @Field(REVIEW_PARAM_LANGUAGE_KEY) languageKey: String,
        @Field(REVIEW_PARAM_SUBSCRIPTION_REVIEW) subscriptionReview: String,
    ): JSONObject

    @FormUrlEncoded
    @POST(REVIEW_API)
    suspend fun feedbackApi(
        @Field(REVIEW_PARAM_PACKAGE_NAME) packageName: String,
        @Field(REVIEW_PARAM_VERSION_CODE) versionCode: String,
        @Field(REVIEW_PARAM_LANGUAGE_KEY) languageKey: String,
        @Field(REVIEW_PARAM_REVIEW) review: String,
        @Field(REVIEW_PARAM_USE_OF_APP) useOfApp: String,
    ): JSONObject
}
