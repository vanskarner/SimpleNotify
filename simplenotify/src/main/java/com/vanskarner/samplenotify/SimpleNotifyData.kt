package com.vanskarner.samplenotify

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat.BubbleMetadata
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import com.vanskarner.simplenotify.R

sealed class Data {
    var id: Int? = null
    @DrawableRes var smallIcon: Int = R.drawable.baseline_notifications_24
    var largeIcon: Bitmap? = null
    var contentIntent: PendingIntent? = null
    var autoCancel: Boolean = true
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
        var useHistoricMessage: Boolean = false,
        var bubble: BubbleMetadata? = null,
        var shortcut: ShortcutInfoCompat? = null,
        var addShortcutIfNotExists: Boolean = true
    ) : Data()

    data class GroupMessageData(
        var you: Person = Person.Builder().setName("You").build(),
        var conversationTitle: String? = null,
        var messages: ArrayList<NotifyMessaging> = arrayListOf(),
        var useHistoricMessage: Boolean = false,
        var bubble: BubbleMetadata? = null,
        var shortcut: ShortcutInfoCompat? = null,
        var addShortcutIfNotExists: Boolean = true
    ) : Data()

    data class CallData(
        var type: String = "incoming",
        var caller: Person? = null,
        var answer: PendingIntent? = null,
        var declineOrHangup: PendingIntent? = null
    ) : Data() {
        companion object {
            fun defaultAnswer(context: Context): PendingIntent {
                return PendingIntent.getActivity(
                    context,
                    -333,
                    Intent(),
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

            fun defaultDeclineOrHangup(context: Context): PendingIntent {
                return PendingIntent.getActivity(
                    context,
                    -333,
                    Intent(),
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

            internal fun defaultSecondCaller(context: Context): Person {
                return Person.Builder()
                    .setName("You")
                    .setIcon(IconCompat.createWithResource(context,R.drawable.notify_user_48))
                    .setImportant(true)
                    .build()
            }

            internal fun defaultCaller(context: Context): Person {
                return Person.Builder()
                    .setName("Someone")
                    .setIcon(IconCompat.createWithResource(context,R.drawable.notify_user_48))
                    .build()
            }
        }
    }

    data class CustomDesignData(
        var hasStyle: Boolean = true,
        var smallRemoteViews: () -> RemoteViews? = { null },
        var largeRemoteViews: () -> RemoteViews? = { null }
    ) : Data()
}

@Suppress("ArrayInDataClass")
data class ExtraData(
    var priority: Int? = null,
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
    var shortCutId: String? = null,//from API 26
    var allowSystemGeneratedContextualActions: Boolean? = null,//from API 29
    var remoteInputHistory: Array<CharSequence>? = null,
    var groupKey: String? = null
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

data class StackableData(
    var id: Int? = null,
    @DrawableRes var smallIcon: Int? = R.drawable.baseline_notifications_24,
    var title: String? = null,
    var summaryText: String = "Summary Group",
    var initialAmount: Int = 3
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
