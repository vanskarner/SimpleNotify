package com.vanskarner.simplenotify

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.net.Uri
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat.BubbleMetadata
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat

sealed class Data {
    /**
     * ID of the notification. If not specified, a value will be provided automatically
     * (1 to 150,000,000)
     */
    var id: Int? = null

    /**
     * String identifier for a notification
     */
    var tag: String? = null

    /**
     * A resource ID in the application's package of the drawable to use.
     */
    @DrawableRes
    var smallIcon: Int = R.drawable.notify_ic_notification_24

    /**
     * Large icon that is shown in the notification
     */
    var largeIcon: Bitmap? = null

    /**
     * Supply a [PendingIntent] to send when the notification is clicked.
     */
    var contentIntent: PendingIntent? = null

    /**
     * Setting this flag will make it so the notification is automatically canceled when the user
     * clicks it in the panel.
     */
    var autoCancel: Boolean = true

    /**
     * Specifies the time at which this notification should be canceled, if it is not already
     * canceled. No-op on versions prior to [android.os.Build.VERSION_CODES.O]
     */
    var timeoutAfter: Long? = null

    /**
     * Provides additional information displayed in the header area of the notification.
     * For the [NotifyConfig.asCall] notification applies up to version [android.os.Build.VERSION_CODES.R].
     * For [NotifyConfig.asDuoMessaging] and [NotifyConfig.asGroupMessaging] notification applies
     * up to version [android.os.Build.VERSION_CODES.P].
     */
    var subText: CharSequence? = null

    /**
     * Data structure used by the type of notification: [NotifyConfig.asBasic]
     */
    data class BasicData(
        /**
         * Set the title (first row) of the notification, in a standard notification.
         */
        var title: CharSequence? = null,
        /**
         * Set the text (second row) of the notification, in a standard notification.
         */
        var text: CharSequence? = null,
    ) : Data()

    /**
     * Data structure used by the type of notification: [NotifyConfig.asBigText]
     */
    data class BigTextData(
        /**
         * Set the title (first row) of the notification, in a standard notification.
         */
        var title: CharSequence? = null,
        /**
         * Set the text (second row) of the notification. Shown when the notification is collapsed.
         */
        var text: CharSequence? = null,
        /**
         * Provide the longer text to be displayed in the big form of the template
         * in place of the content text. Shown when the notification is expanded.
         */
        var bigText: CharSequence? = null,
    ) : Data() {
        init {
            smallIcon = R.drawable.notify_ic_text_24
        }
    }

    /**
     * Data structure used by the type of notification: [NotifyConfig.asInbox]
     */
    data class InboxData(
        /**
         * Set the title (first row) of the notification, in a standard notification.
         */
        var title: CharSequence? = null,
        /**
         * Set the text (second row) of the notification. Shown when the notification is collapsed.
         */
        var text: CharSequence? = null,
        /**
         * Add lines to the Inbox notification summary section. Shown when the notification is expanded.
         */
        var lines: ArrayList<String> = arrayListOf(),
    ) : Data() {
        init {
            smallIcon = R.drawable.notify_ic_email_24
        }
    }

    /**
     * Data structure used by the type of notification: [NotifyConfig.asBigPicture]
     */
    data class BigPictureData(
        /**
         * Set the title (first row) of the notification, in a standard notification.
         */
        var title: CharSequence? = null,
        /**
         * Set the text (second row) of the notification. Shown when the notification is collapsed.
         */
        var text: CharSequence? = null,
        /**
         * Set the first line of text after the detail section in the big form of the template.
         * Shown when the notification is expanded.
         */
        var summaryText: CharSequence? = null,
        /**
         * Provide the bitmap to be used as the payload for the BigPicture notification.
         * Shown when the notification is expanded.
         */
        var image: Bitmap? = null
    ) : Data() {
        init {
            smallIcon = R.drawable.notify_ic_image_24
        }
    }

    /**
     * Data structure used by the type of notification: [NotifyConfig.asDuoMessaging]
     */
    data class DuoMessageData(
        /**
         * The [Person] who represents oneself in the conversation. By default the name is `You`.
         */
        var you: Person = Person.Builder().setName("You").build(),
        /**
         * A [Person] whose [Person.getName] value is used as the display name for the sender.
         * By default the name is `Someone`.
         */
        var contact: Person = Person.Builder().setName("Someone").build(),
        /**
         * [NotifyMessaging] type messages between `you` and `contact`
         */
        var messages: ArrayList<NotifyMessaging> = arrayListOf(),
        /**
         * Specifies the use of `addHistoricMessage` for all messages except the last one.
         * By default this is false.
         * @see androidx.core.app.NotificationCompat.MessagingStyle.addHistoricMessage
         */
        var useHistoricMessage: Boolean = false,
        /**
         * Sets the [BubbleMetadata] that will be used to display app content in a floating window
         * over the existing foreground activity. Applies as of version [android.os.Build.VERSION_CODES.Q]
         *
         * @see androidx.core.app.NotificationCompat.Builder.setBubbleMetadata
         */
        var bubble: BubbleMetadata? = null,
        /**
         * Populates this notification with given [ShortcutInfoCompat]. The shortcuts are applied
         * from version [android.os.Build.VERSION_CODES.N]
         */
        var shortcut: ShortcutInfoCompat? = null,
        /**
         * Add the shortcut in case it does not exist using:
         * [androidx.core.content.pm.ShortcutManagerCompat.pushDynamicShortcut].
         * By default this is true.
         */
        var addShortcutIfNotExists: Boolean = true
    ) : Data() {
        init {
            smallIcon = R.drawable.notify_ic_message_24
        }
    }

    /**
     * Data structure used by the type of notification: [NotifyConfig.asGroupMessaging]
     */
    data class GroupMessageData(
        /**
         * The [Person] who represents oneself in the conversation. By default the name is `You`.
         */
        var you: Person = Person.Builder().setName("You").build(),
        /**
         * Sets the title to be displayed on this conversation.
         */
        var conversationTitle: CharSequence? = null,
        /**
         * [NotifyMessaging] type messages between all members of the conversation
         */
        var messages: ArrayList<NotifyMessaging> = arrayListOf(),
        /**
         * Specifies the use of `addHistoricMessage` for all messages except the last one.
         * By default this is false.
         * @see androidx.core.app.NotificationCompat.MessagingStyle.addHistoricMessage
         */
        var useHistoricMessage: Boolean = false,
        /**
         * Sets the [BubbleMetadata] that will be used to display app content
         * in a floating window over the existing foreground activity. Applies as of version
         * [android.os.Build.VERSION_CODES.Q]
         *
         * @see androidx.core.app.NotificationCompat.Builder.setBubbleMetadata
         */
        var bubble: BubbleMetadata? = null,
        /**
         * Populates this notification with given [ShortcutInfoCompat]. The shortcuts are applied
         * from version [android.os.Build.VERSION_CODES.N]
         */
        var shortcut: ShortcutInfoCompat? = null,
        /**
         * Add the shortcut in case it does not exist using:
         * [androidx.core.content.pm.ShortcutManagerCompat.pushDynamicShortcut].
         * By default this is true.
         */
        var addShortcutIfNotExists: Boolean = true
    ) : Data() {
        init {
            smallIcon = R.drawable.notify_ic_message_24
        }
    }

    /**
     * Data structure used by the type of notification: [NotifyConfig.asCall]
     */
    data class CallData(
        /**
         * Specifies the type of call. There are 3 types: `incoming`,`ongoing` and `screening`.
         * The `incoming` type requires the attributes [answer] and [declineOrHangup].
         * The `ongoing` type requires the [declineOrHangup] attribute.
         * The `screening` type requires the attributes [answer] and [declineOrHangup].
         */
        var type: String = "incoming",
        /**
         * Optional text to be displayed with the [verificationIcon] as caller verification status.
         * Shown starting with versions [android.os.Build.VERSION_CODES.S]
         */
        var verificationText: CharSequence? = null,
        /**
         * optional icon to be displayed with the [verificationText] as caller verification status.
         * Shown starting with versions [android.os.Build.VERSION_CODES.S]
         */
        var verificationIcon: Icon? = null,
        /**
         * [Person] representing who we are communicating with. If not specified, its default
         * name is `Someone`
         */
        var caller: Person? = null,
        /**
         * The intent used in types “incoming” and “screening” to be sent when the user clicks
         * on the response action.
         */
        var answer: PendingIntent? = null,
        /**
         * The intent used in all types. When the type is `incoming` it refers to decline and when
         * it refers to the other types it refers to hangUp.
         */
        var declineOrHangup: PendingIntent? = null
    ) : Data() {
        init {
            smallIcon = R.drawable.notify_ic_phone_24
        }

        companion object {
            internal fun defaultSecondCaller(context: Context): Person {
                return Person.Builder()
                    .setName("You")
                    .setIcon(IconCompat.createWithResource(context, R.drawable.notify_user_48))
                    .setImportant(true)
                    .build()
            }

            internal fun defaultCaller(context: Context): Person {
                return Person.Builder()
                    .setName("Someone")
                    .setIcon(IconCompat.createWithResource(context, R.drawable.notify_user_48))
                    .build()
            }
        }
    }

    /**
     * Data structure used by the type of notification: [NotifyConfig.asCustomDesign]
     */
    data class CustomDesignData(
        /**
         * Specifies whether to set the style [android.app.Notification.DecoratedCustomViewStyle]
         * which is the container that wraps the notification. Applicable for versions prior to
         * version [android.os.Build.VERSION_CODES.S].
         *
         * By default this is true.
         */
        var hasStyle: Boolean = true,
        /**
         * Provides a customized [RemoteViews] for the collapsed view of the notification. Starting
         * with version [android.os.Build.VERSION_CODES.S], style(container) is not applied by default.
         */
        var smallRemoteViews: () -> RemoteViews? = { null },
        /**
         * Provides a customized [RemoteViews] for the expanded view of the notification. Starting
         * with version [android.os.Build.VERSION_CODES.S], style(container) is applied by default.
         */
        var largeRemoteViews: () -> RemoteViews? = { null }
    ) : Data()

}

/**
 * Data structure that gathers several attributes that allows to change the predefined
 * characteristics of the notification types.
 */
data class ExtraData(
    /**
     * Set the relative priority for this notification.
     * Use `NotificationCompat.PRIORITY_*` to set it.
     */
    var priority: Int? = null,
    /**
     * Set the notification category.
     * Use `NotificationCompat.CATEGORY_*` to set it.
     */
    var category: String? = null,
    /**
     * Set the sound to play. Valid only for the previous to [android.os.Build.VERSION_CODES.O]
     */
    var sounds: Uri? = Uri.EMPTY,
    /**
     * Supply a [PendingIntent] to send when the notification is cleared by the user directly from
     * the notification panel. For example, this intent is sent when the user clicks the
     * "Clear all" button, or the individual "X" buttons on notifications.
     */
    var deleteIntent: PendingIntent? = null,
    /**
     * Set the notification visibility.
     * Use `NotificationCompat.VISIBILITY_*` to set it.
     */
    var visibility: Int? = null,
    /**
     * Set whether this is an ongoing notification.
     *
     * Ongoing notifications cannot be dismissed by the user, so your application or service
     * must take care of canceling them.
     *
     * They are typically used to indicate a background task that the user is actively engaged
     * with (e.g., playing music) or is pending in some way and therefore occupying the device
     * (e.g., a file download, sync operation, active network connection).
     */
    var ongoing: Boolean? = null,
    /**
     * Set the color for this notification.
     */
    @ColorInt
    var color: Int? = null,
    /**
     * Set the time that the event occurred. Notifications in the panel are sorted by this time.
     */
    var timestampWhen: Long? = null,
    /**
     * An intent to launch instead of posting the notification in the status bar. It should only
     * be used with extremely high priority notifications that demand the user's immediate
     * attention, such as an incoming phone call or an alarm clock that the user has explicitly
     * scheduled at a certain time. The boolean value refers to indicate whether this notification
     * will be sent even if other notifications are suppressed.
     */
    var fullScreenIntent: Pair<PendingIntent, Boolean>? = null,
    /**
     * Set this flag if you would only like the sound, vibrate and ticker to be played if the
     * notification is not already showing.
     */
    var onlyAlertOnce: Boolean? = null,
    /**
     * Controls whether the timestamp set with [timestampWhen] is displayed in the content view.
     * The default value is true.
     */
    var showWhen: Boolean? = null,
    /**
     * Show the [timestampWhen] field as a stopwatch. Instead of presenting when as a timestamp,
     * the notification will show an automatically updating display of the minutes and seconds
     * since when. Useful when showing an elapsed time (like an ongoing phone call).
     */
    var useChronometer: Boolean? = null,
    /**
     * Sets the number of items this notification represents. On the latest platforms, this may be
     * displayed as a badge count for launchers that support badges.
     */
    var badgeNumber: Int? = null,//from API 26
    /**
     * Sets which icon to display as a badge for this notification.
     * Use `NotificationCompat.BADGE_ICON_*` to set it.
     *
     * Note: This value might be ignored, for launchers that don't support badge icons.
     */
    var badgeIconType: Int? = null,//from API 26
    /**
     * Starting with Android 11, you can configure a shortcut identifier for conversations.
     * This behavior should be reserved for person-to-person conversations where there is a likely
     * social obligation for an individual to respond.
     */
    var shortCutId: String? = null,//from API 26
    /**
     * Determines whether the platform can generate contextual actions for a notification.
     * By default this is true.
     */
    var allowSystemGeneratedContextualActions: Boolean? = null,//from API 29
    /**
     * Set the remote input history. This should be set to the most recent inputs that have been
     * sent through a [RemoteInput] of this Notification and cleared once the it is no longer
     * relevant (e. g. for chat notifications once the other party has responded).
     *
     * Note: The reply text will only be shown on notifications that have least one action with a [RemoteInput]
     */
    var remoteInputHistory: List<CharSequence>? = null,
    /**
     * Set this notification to be part of a group of notifications sharing the same key.
     * Grouped notifications may display in a cluster or stack on devices which support such rendering
     */
    var groupKey: String? = null
)

sealed class ActionData {
    /**
     * Resource ID of a drawable that represents the action.
     */
    var icon: Int = 0

    /**
     * Text describing the action.
     */
    var title: CharSequence? = null

    /**
     * Data structure used for the simple action types.
     */
    data class BasicAction(
        /**
         * [PendingIntent] to be fired when the action is invoked.
         */
        var pending: PendingIntent? = null
    ) : ActionData()

    /**
     * Data structure used for the types of actions that are dynamic interactions.
     */
    data class ReplyAction(
        /**
         * the [PendingIntent] to fire when users trigger this action
         */
        var replyPending: PendingIntent? = null,
        /**
         * Add an input to be collected from the user when this action is sent.
         */
        var remote: RemoteInput? = null,
        /**
         * Set whether the platform should automatically generate possible responses to add to
         * [RemoteInput]. If the actions do not have a remote entry, this has no effect.
         */
        var allowGeneratedReplies: Boolean = false,//from API 29
    ) : ActionData()
}

/**
 * Data structure used for progress in notifications.
 */
data class ProgressData(
    /**
     * Current value of progress. It should be a value between 0 to 100.
     */
    var currentValue: Int = 0,
    /**
     * If true, show an indeterminate progress bar without a specific value.
     * By default this is false.
     */
    var indeterminate: Boolean = false,
    /**
     * Hide the progress bar. By default this is false.
     */
    var hide: Boolean = false
)

/**
 * Data structure for the notification stacking feature that has the same [ExtraData.groupKey].
 * Remember that in Android 7.0 (API level 24) and later versions, if your app sends notifications
 * and does not specify a group key or group summary, the system may automatically group them together.
 */
data class StackableData(
    /**
     * ID of the group notification. If not specified, a value will be provided automatically
     * (150,000,001 to 155,000,000)
     */
    var id: Int? = null,
    /**
     * A resource ID for the group notification.
     */
    @DrawableRes var smallIcon: Int = R.drawable.notify_ic_view_list_24,
    var title: CharSequence? = null,
    /**
     * Summary text for group notification
     */
    var summaryText: CharSequence = "Summary Group",
    /**
     * Specifies the minimum number of active notifications that have the same group key to group
     * them later. To this value will be added 1 which refers to the current individual notification.
     * By default this is 3.
     */
    var initialAmount: Int = 3
)

/**
 * Data structure used for notification messages: [NotifyConfig.asDuoMessaging]
 * and [NotifyConfig.asGroupMessaging]
 */
sealed class NotifyMessaging {
    internal var mimeData: Pair<String, Uri>? = null

    /**
     * Contains the messages of the `you` attribute in the types of [NotifyConfig.asDuoMessaging] and
     * [NotifyConfig.asGroupMessaging]
     */
    data class YourMsg(
        /**
         * Represent your message in the conversation.
         */
        val msg: CharSequence,
        /**
         * Time at which the message arrived in ms since Unix epoch
         */
        val timestamp: Long
    ) : NotifyMessaging()

    /**
     * Contains the messages of `contact` when type [NotifyConfig.asDuoMessaging] is used. When
     * the type [NotifyConfig.asGroupMessaging] is being used should [person] be specified
     */
    data class ContactMsg(
        /**
         * Represents the message with whom you are conversing
         */
        val msg: CharSequence,
        /**
         * Time at which the message arrived in ms since Unix epoch
         */
        val timestamp: Long,
        /**
         * Specifies the contact [Person] for group chat notifications.
         * By default the name is `Someone`.
         */
        val person: Person = Person.Builder().setName("Someone").build()
    ) : NotifyMessaging()

    /**
     * Sets a binary blob of data and an associated MIME type for a message.
     * Fulfills the same function as method:
     * [androidx.core.app.NotificationCompat.MessagingStyle.Message.setData].
     *
     * @param dataMimeType The MIME type of the content.
     * @param dataUri The uri containing the content whose type is given by the MIME type.
     */
    fun setData(dataMimeType: String, dataUri: Uri): NotifyMessaging {
        mimeData = Pair(dataMimeType, dataUri)
        return this
    }

}
