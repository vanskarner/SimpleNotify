package com.vanskarner.samplenotify

import android.app.PendingIntent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import com.vanskarner.simplenotify.R

sealed class Data {
    var id: Int? = null
    var smallIcon: Int = R.drawable.baseline_notifications_24
    var largeIcon: Bitmap? = null
    var contentIntent: PendingIntent? = null
    var autoCancel: Boolean = true
    var priority: Int = NotificationCompat.PRIORITY_DEFAULT
    var timeoutAfter: Long? = null

    data class BasicData(
        var title: String? = null,
        var text: String? = null,
    ) : Data()

    data class BigTextData(
        var title: String? = null,
        var text: String? = null,
        var bigText: String? = null,
        var summaryText: String? = null,
    ) : Data()

    data class InboxData(
        var title: String? = null,
        var text: String? = null,
        var lines: ArrayList<String> = arrayListOf(),
    ) : Data()

    data class BigPictureData(
        var title: String? = null,
        var text: String? = null,
        var summaryText: String? = null,
        var image: Bitmap? = null
    ) : Data()

    data class DuoMessageData(
        var you: Person = Person.Builder().setName("You").build(),
        var contact: Person = Person.Builder().setName("Someone").build(),
        var messages: ArrayList<NotifyMessaging> = arrayListOf(),
        var useHistoricMessage: Boolean = false
    ) : Data()

    data class GroupMessageData(
        var you: Person = Person.Builder().setName("You").build(),
        var conversationTitle: String? = null,
        var messages: ArrayList<NotifyMessaging> = arrayListOf(),
        var useHistoricMessage: Boolean = false
    ) : Data()

    data class CustomDesignData(
        var hasStyle: Boolean = true,
        var smallRemoteViews: () -> RemoteViews? = { null },
        var largeRemoteViews: () -> RemoteViews? = { null }
    ) : Data()
}

@Suppress("ArrayInDataClass")
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
    var useChronometer: Boolean? = null,
    var badgeNumber: Int? = null,//from API 26
    var badgeIconType: Int? = null,//from API 26
    var badgeShortCutId: String? = null,//from API 26
    var allowSystemGeneratedContextualActions: Boolean? = null,//from API 29
    var remoteInputHistory: Array<CharSequence>? = null
)

sealed class ActionData {
    var icon: Int = 0
    var label: String? = null

    data class BasicAction(
        var pending: PendingIntent? = null
    ) : ActionData()

    data class ReplyAction(
        var replyPending: PendingIntent? = null,
        var remote: RemoteInput? = null,
        var allowGeneratedReplies: Boolean = false,//from API 29
    ) : ActionData()
}

data class ProgressData(
    var currentValue: Int = 0,
    var indeterminate: Boolean = false,
    var hide: Boolean = false
)

sealed class NotifyMessaging {
    internal var mimeData: Pair<String, Uri>? = null

    data class YourMsg(
        val msg: String,
        val timestamp: Long
    ) : NotifyMessaging()

    data class ContactMsg(
        val msg: String,
        val timestamp: Long,
        val person: Person = Person.Builder().setName("Someone").build()
    ) : NotifyMessaging()

    fun setData(dataMimeType: String, dataUri: Uri): NotifyMessaging {
        mimeData = Pair(dataMimeType, dataUri)
        return this
    }

}
