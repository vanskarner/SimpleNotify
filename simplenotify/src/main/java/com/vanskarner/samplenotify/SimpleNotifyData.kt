package com.vanskarner.samplenotify

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import com.vanskarner.simplenotify.R

sealed class Data {
    var id: Int? = null
    var smallIcon: Int = R.drawable.baseline_notifications_24
    var title: String? = null
    var largeIcon: Bitmap? = null
    var priority: Int = NotificationCompat.PRIORITY_DEFAULT
    var pending: PendingIntent? = null
    var autoCancel: Boolean = true
    var sound: Uri? = null

    internal open fun applyData(builder: Builder) {
        builder.setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setLargeIcon(largeIcon)
            .setContentIntent(pending)
            .setAutoCancel(autoCancel)
            .setPriority(priority)
            .setSound(sound)
    }

    data class BasicData(
        var text: String? = null
    ) : Data() {
        override fun applyData(builder: Builder) {
            super.applyData(builder)
            builder.setContentText(text)
        }
    }

    data class BigTextData(
        var bigText: String? = null,
        var collapsedText: String? = null,
        var summaryText: String? = null,
    ) : Data() {
        override fun applyData(builder: Builder) {
            super.applyData(builder)
            builder.setContentText(collapsedText)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(bigText)
                        .setSummaryText(summaryText)
                )
        }
    }
}

@Suppress("ArrayInDataClass")
data class ExtraData(
    var vibrationPattern: LongArray? = null,
    var lights: Triple<Int, Int, Int>? = null,
    var ongoing: Boolean = false,
    var visibility: Int? = null,
    var category: String? = null,
    var color: Int? = null,
    var timeoutAfter: Long? = null,
    var badgeIconType: Int? = null,//review
    var timestampWhen: Long? = null,
    var deleteIntent: PendingIntent? = null,
    var fullScreenIntent: Pair<PendingIntent, Boolean>? = null,
    var onlyAlertOnce: Boolean = false,
    var subText: String? = null,
    var showWhen: Boolean = false,
    var useChronometer: Boolean = false
)

data class ActionData(
    var icon: Int = R.drawable.baseline_notifications_24,
    var name: String? = null,
    var pending: PendingIntent? = null
)

data class ChannelData(
    var id: String = DEFAULT_ID,
    var name: String = "Default channel",
    var description: String = "Application default notification channel",
    var importance: Int = NotificationManager.IMPORTANCE_DEFAULT
) {
    companion object {
        private const val DEFAULT_ID = "SingleNotifyId"
        internal fun byDefault(context: Context) = ChannelData(
            id = DEFAULT_ID,
            name = context.getString(R.string.chanel_name),
            description = context.getString(R.string.chanel_description),
            importance = NotificationManager.IMPORTANCE_DEFAULT
        )
    }
}
