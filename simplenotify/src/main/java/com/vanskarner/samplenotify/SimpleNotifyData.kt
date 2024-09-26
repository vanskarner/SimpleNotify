package com.vanskarner.samplenotify

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.vanskarner.samplenotify.internal.DEFAULT_CHANNEL_ID
import com.vanskarner.samplenotify.internal.DEFAULT_PROGRESS_CHANNEL_ID
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
    var category: String? = null

    data class BasicData(
        var text: String? = null
    ) : Data()

    data class BigTextData(
        var bigText: String? = null,
        var collapsedText: String? = null,
        var summaryText: String? = null,
    ) : Data()

    data class TextListData(
        var summaryText: String? = null,
        var lines: ArrayList<String> = arrayListOf(),
    ) : Data()
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

sealed class ActionData {
    data class BasicAction(
        var icon: Int = R.drawable.baseline_notifications_24,
        var label: String? = null,
        var pending: PendingIntent? = null
    ) : ActionData()

    data class ReplyAction(
        var icon: Int = R.drawable.baseline_notifications_24,
        var label: String? = null,
        var replyPending: PendingIntent? = null,
        var replyLabel: String? = null,
        var replyKey: String = "default"
    ) : ActionData()
}

internal data class ProgressData(
    var currentPercentage: Int,
    var indeterminate: Boolean,
    var conditionToHide: (() -> Boolean),
    var enable: Boolean
) {
    companion object {
        internal fun byDefault(): ProgressData {
            return ProgressData(
                currentPercentage = 0,
                indeterminate = false,
                conditionToHide = { false },
                enable = false,
            )
        }
    }
}

data class ChannelData(
    var id: String,
    var name: String,
    var summary: String,
    var importance: Int
) {
    companion object {
        internal fun byDefault(context: Context) = ChannelData(
            id = DEFAULT_CHANNEL_ID,
            name = context.getString(R.string.chanel_name),
            summary = context.getString(R.string.chanel_description),
            importance = NotificationManager.IMPORTANCE_DEFAULT
        )

        internal fun forProgress(context: Context) = ChannelData(
            id = DEFAULT_PROGRESS_CHANNEL_ID,
            name = context.getString(R.string.progress_channel_name),
            summary = context.getString(R.string.progress_channel_description),
            importance = NotificationManager.IMPORTANCE_DEFAULT
        )
    }

}
