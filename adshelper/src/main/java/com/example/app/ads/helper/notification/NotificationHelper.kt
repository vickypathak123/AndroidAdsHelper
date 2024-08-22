package com.example.app.ads.helper.notification

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.app.ads.helper.base.utils.isOreoPlus
import java.time.Duration

internal const val SUBSCRIPTION_NOTIFICATION_ID: Int = 275
internal const val SUBSCRIPTION_NOTIFICATION_CHANNEL_ID: String = "subscription_notification_channel_id"
internal const val SUBSCRIPTION_NOTIFICATION_CHANNEL_NAME: String = "Free Trial Expire"
internal const val KEY_SUBSCRIPTION_NOTIFICATION_INTENT: String = "key_subscription_notification_intent"
internal const val VALUE_SUBSCRIPTION_NOTIFICATION_INTENT: String = "from_subscription_free_trial_expire_notification"
internal const val SUBSCRIPTION_NOTIFICATION_WORK_TAG: String = "subscription_notification_work_tag"

fun Context.scheduleNotification(intervalMillis: Long) {
    OneTimeWorkRequest.Builder(NotificationWorker::class.java).let {
        if (isOreoPlus) {
            it.setInitialDelay(Duration.ofMillis(intervalMillis))
        } else {
            it.setInitialDelay(intervalMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
        }
        it.addTag(SUBSCRIPTION_NOTIFICATION_WORK_TAG)
        it.build().also { notificationWork ->
            WorkManager.getInstance(this).enqueue(notificationWork)
        }
    }
}