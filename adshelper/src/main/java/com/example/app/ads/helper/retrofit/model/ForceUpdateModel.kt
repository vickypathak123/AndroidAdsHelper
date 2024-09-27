package com.example.app.ads.helper.retrofit.model


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ForceUpdateModel(
    @SerializedName("status")
    @Expose
    val status: Boolean = false,
    @SerializedName("response_code")
    @Expose
    val responseCode: Int = 0,
    @SerializedName("response_message")
    @Expose
    val responseMessage: String = "",
    @SerializedName("is_need_to_update")
    @Expose
    val isNeedToUpdate: Boolean = false,
) : Parcelable