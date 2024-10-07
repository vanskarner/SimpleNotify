package com.vanskarner.samplenotify

import androidx.core.app.NotificationCompat.MessagingStyle.Message
import android.app.PendingIntent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import com.vanskarner.simplenotify.R

sealed class Data {
    var id: Int? = null
    var smallIcon: Int = R.drawable.baseline_notifications_24
    var title: String? = null
    var largeIcon: Bitmap? = null
    var priority: Int = NotificationCompat.PRIORITY_DEFAULT
    var pending: PendingIntent? = null
    var autoCancel: Boolean = true
//    var timeoutAfter: Long? = null
//    var badgeIconType: Int? = null

    data class BasicData(
        var text: String? = null
    ) : Data()

    data class BigTextData(
        var bigText: String? = null,
        var collapsedText: String? = null,
        var summaryText: String? = null,
    ) : Data()

    data class InboxData(
        var summaryText: String? = null,
        var lines: ArrayList<String> = arrayListOf(),
    ) : Data()

    data class BigPictureData(
        var collapsedText: String? = null,
        var summaryText: String? = null,
        var image: Bitmap? = null
    ) : Data()

    data class MessageData(
        var conversationTitle: String? = null,
        var user: Person = Person.Builder().build(),
        var messages: ArrayList<Message> = arrayListOf()
    ) : Data()
}

data class ExtraData(
    var category: String? = null,
    var visibility: Int? = null,
    var ongoing: Boolean? = null,
    var color: Int? = null,
    var timestampWhen: Long? = null,
    var deleteIntent: PendingIntent? = null,
    var fullScreenIntent: Pair<PendingIntent, Boolean>? = null,
    var onlyAlertOnce: Boolean? = null,
    var subText: String? = null,
    var showWhen: Boolean? = null,
    var useChronometer: Boolean? = null
)

sealed class ActionData {
    data class BasicAction(
        var icon: Int = 0,
        var label: String? = null,
        var pending: PendingIntent? = null
    ) : ActionData()

    data class ReplyAction(
        var icon: Int = 0,
        var label: String? = null,
        var replyPending: PendingIntent? = null,
        var remote: RemoteInput? = null
    ) : ActionData()
}

data class ProgressData(
    var currentValue: Int = 0,
    var indeterminate: Boolean = false,
    var hide: Boolean = false
)
