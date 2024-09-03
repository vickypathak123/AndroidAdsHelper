package com.example.app.ads.helper.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.utils.getStringRes
import com.example.app.ads.helper.base.utils.isOreoPlus
import com.example.app.ads.helper.purchase.SUBSCRIPTION_DATA_LANGUAGE_CODE
import com.example.app.ads.helper.purchase.product.AdsManager
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getFullBillingPeriod

class NotificationWorker(
    private val fContext: Context,
    fWorkerParams: WorkerParameters
) : Worker(fContext, fWorkerParams) {

    @Suppress("PropertyName")
    val TAG: String = "Akshay_Admob_${javaClass.simpleName}"

    override fun doWork(): Result {
        triggerNotification(fContext = fContext)
        return Result.success()
    }

    private fun triggerNotification(fContext: Context) {
        AdsManager(fContext).notificationData?.let { notificationData ->
            fContext.getSystemService(NotificationManager::class.java)?.let { notificationManager ->
                val channelId = notificationData.notificationChannelId

                if (isOreoPlus) {
                    NotificationChannel(channelId, notificationData.notificationChannelName, NotificationManager.IMPORTANCE_DEFAULT).let { channel ->
                        notificationManager.createNotificationChannel(channel)
                    }
                }

                @DrawableRes
                val notificationIcon: Int = notificationData.notificationIcon
                val notificationTitle: String = fContext.getStringRes(R.string.free_trial_ending_soon)
                SUBSCRIPTION_DATA_LANGUAGE_CODE = "en"
                val notificationBigText: String = fContext.getStringRes(R.string.you_will_be_charged_after_period, notificationData.actualFreeTrialPeriod.getFullBillingPeriod(context = fContext))

                val pendingIntent = PendingIntent.getActivity(
                    fContext,
                    10,
                    Intent(fContext, notificationData.intentClass).apply {
                        this.putExtra(KEY_SUBSCRIPTION_NOTIFICATION_INTENT, VALUE_SUBSCRIPTION_NOTIFICATION_INTENT)
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )

                NotificationCompat.Builder(fContext, channelId)
                    .setSmallIcon(notificationIcon)
                    .setContentTitle(notificationTitle)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(notificationBigText)
                    )
                    .setContentIntent(pendingIntent)
                    .setVibrate(longArrayOf(100, 100, 100, 100))
                    .setAutoCancel(true)
                    .build().also { notification ->
                        notificationManager.notify(notificationData.notificationId, notification)
                    }
            }
        }
    }
}