package com.example.app.ads.helper.notification

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import com.example.app.ads.helper.R
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class NotificationDataModel(
//    val intentClass: Class<*>,
    val intentClass: String,
    @DrawableRes
    val notificationIcon: Int = R.drawable.outline_notification_important_24,
    val notificationId: Int = SUBSCRIPTION_NOTIFICATION_ID,
    val notificationChannelId: String = SUBSCRIPTION_NOTIFICATION_CHANNEL_ID,
    val notificationChannelName: String = SUBSCRIPTION_NOTIFICATION_CHANNEL_NAME,
    var actualFreeTrialPeriod: String = "",
) : Parcelable