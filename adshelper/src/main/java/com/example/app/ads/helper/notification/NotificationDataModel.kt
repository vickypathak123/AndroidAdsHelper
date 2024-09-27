package com.example.app.ads.helper.notification

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import com.example.app.ads.helper.R
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class NotificationDataModel(
    @SerializedName("intent_class")
    @Expose
    val intentClass: String,
    @SerializedName("notification_icon")
    @Expose
    @DrawableRes
    val notificationIcon: Int = R.drawable.outline_notification_important_24,
    @SerializedName("notification_id")
    @Expose
    val notificationId: Int = SUBSCRIPTION_NOTIFICATION_ID,
    @SerializedName("notification_channel_id")
    @Expose
    val notificationChannelId: String = SUBSCRIPTION_NOTIFICATION_CHANNEL_ID,
    @SerializedName("notification_channel_name")
    @Expose
    val notificationChannelName: String = SUBSCRIPTION_NOTIFICATION_CHANNEL_NAME,
    @SerializedName("actual_free_trial_period")
    @Expose
    var actualFreeTrialPeriod: String = "",
) : Parcelable