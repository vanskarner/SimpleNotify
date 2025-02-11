package com.vanskarner.simplenotify.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Icon
import android.media.RingtoneManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import com.vanskarner.simplenotify.ActionData
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.ExtraData
import com.vanskarner.simplenotify.NotifyMessaging
import com.vanskarner.simplenotify.ProgressData
import com.vanskarner.simplenotify.R
import com.vanskarner.simplenotify.StackableData

class TestDataProvider {

    companion object {
        fun createChannel(manager: NotificationManager): String {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //Channels available from API 26
                val expectedChannel = NotificationChannel(
                    "testId",
                    "Test Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                manager.createNotificationChannel(expectedChannel)
                return expectedChannel.id
            }
            return ""
        }

        fun basicData(): Data.BasicData {
            val pendingIntent = pendingIntent()
            val basicData = Data.BasicData().apply {
                title = "Basic Style"
                text = "Text for Basic"
                subText = "SubText for Basic"
                smallIcon = R.drawable.notify_ic_notification_24
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                contentIntent = pendingIntent
                autoCancel = true
                timeoutAfter = 5000
            }
            return basicData
        }

        fun bigTextData(): Data.BigTextData {
            val pendingIntent = pendingIntent()
            val data = Data.BigTextData().apply {
                title = "BigText Style"
                bigText = "BigText"
                text = "Text for BigText"
                subText = "SubText for BigTextData"
                smallIcon = R.drawable.notify_ic_notification_24
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                contentIntent = pendingIntent
                autoCancel = true
                timeoutAfter = 5000
            }
            return data
        }

        fun inboxData(): Data.InboxData {
            val pendingIntent = pendingIntent()
            val data = Data.InboxData().apply {
                title = "Inbox Style"
                text = "Text for Inbox"
                subText = "SubText for InboxData"
                lines = arrayListOf("Any line 1", "Any line 2", "Any line 3")
                smallIcon = R.drawable.notify_ic_notification_24
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                contentIntent = pendingIntent
                autoCancel = true
                timeoutAfter = 5000
            }
            return data
        }

        fun bigPictureData(): Data.BigPictureData {
            val pendingIntent = pendingIntent()
            val data = Data.BigPictureData().apply {
                title = "BigPicture Style"
                text = "Text for BigPicture"
                subText = "SubText for BigPictureData"
                summaryText = "Summary for BigPicture"
                image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                smallIcon = R.drawable.notify_ic_notification_24
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                contentIntent = pendingIntent
                autoCancel = true
                timeoutAfter = 5000
            }
            return data
        }

        fun duoMessageData(
            context: Context,
            shortcutId: String
        ): Data.DuoMessageData {
            val pendingIntent = pendingIntent()
            val data = Data.DuoMessageData().apply {
                subText = "SubText for DuoMessageData"
                you = Person.Builder().setName("Albert").build()
                contact = Person.Builder().setName("Chris").setIcon(
                    IconCompat.createWithAdaptiveBitmap(
                        Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888)
                    )
                ).build()
                messages = arrayListOf(
                    NotifyMessaging.ContactMsg(
                        "Any Message 1",
                        System.currentTimeMillis() - (5 * 60 * 1000)
                    ),
                    NotifyMessaging.YourMsg(
                        "Any Message 2",
                        System.currentTimeMillis()
                    ).setData(
                        "image/jpeg",
                        "content://com.any.sample/photo/image.jpg".toUri()
                    )
                )
                bubble = NotificationCompat.BubbleMetadata.Builder(pendingIntent, contact.icon!!)
                    .setDesiredHeight(500)
                    .setAutoExpandBubble(true)
                    .setSuppressNotification(true)
                    .build()
                shortcut = ShortcutInfoCompat.Builder(context, shortcutId)
                    .setLongLived(true)
                    .setIntent(Intent().setAction(Intent.ACTION_VIEW))
                    .setShortLabel(contact.name!!)
                    .setIcon(contact.icon)
                    .setPerson(contact)
                    .build()
                addShortcutIfNotExists = false
                smallIcon = R.drawable.notify_ic_notification_24
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                contentIntent = pendingIntent
                autoCancel = true
                timeoutAfter = 5000
            }
            return data
        }

        fun groupMessageData(context: Context, shortcutId: String): Data.GroupMessageData {
            val pendingIntent = pendingIntent()
            val data = Data.GroupMessageData().apply {
                subText = "SubText for GroupMessageData"
                conversationTitle = "Any conversationTitle"
                you = Person.Builder().setName("You").build()
                messages = arrayListOf(
                    NotifyMessaging.ContactMsg(
                        "Any Message 1",
                        System.currentTimeMillis() - (5 * 60 * 1000),
                        Person.Builder().setName("Max").build()
                    ),
                    NotifyMessaging.ContactMsg(
                        "Any Message 2",
                        System.currentTimeMillis() - (3 * 60 * 1000),
                        Person.Builder().setName("Albert").build()
                    ),
                    NotifyMessaging.YourMsg(
                        "Any Message 3",
                        System.currentTimeMillis()
                    ).setData(
                        "image/jpeg",
                        "content://com.any.sample/photo/image.jpg".toUri()
                    )
                )
                val groupIcon = IconCompat.createWithAdaptiveBitmap(
                    Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888)
                )
                bubble = NotificationCompat.BubbleMetadata.Builder(pendingIntent, groupIcon)
                    .setDesiredHeight(500)
                    .setAutoExpandBubble(true)
                    .setSuppressNotification(true)
                    .build()
                shortcut = ShortcutInfoCompat.Builder(context, shortcutId)
                    .setLongLived(true)
                    .setIntent(Intent().setAction(Intent.ACTION_VIEW))
                    .setShortLabel(conversationTitle!!)
                    .setIcon(groupIcon)
                    .build()
                addShortcutIfNotExists = false
                smallIcon = R.drawable.notify_ic_notification_24
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                contentIntent = pendingIntent
                autoCancel = true
                timeoutAfter = 5000
            }
            return data
        }

        fun callData(context: Context): Data.CallData {
            val pendingIntent = pendingIntent()
            return Data.CallData().apply {
                subText = "SubText for CallData"
                type = "incoming"
                verificationText = "Verification Text"
                verificationIcon = Icon.createWithResource(context, R.drawable.notify_ic_phone_24)
                caller = Person.Builder().setName("Max").build()
                answer = pendingIntent()
                declineOrHangup = pendingIntent()
                smallIcon = R.drawable.notify_ic_notification_24
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                contentIntent = pendingIntent
                autoCancel = true
                timeoutAfter = 5000
            }
        }

        fun customDesignData(context: Context): Data.CustomDesignData {
            val data = Data.CustomDesignData().apply {
                subText = "SubText for CustomDesignData"
                hasStyle = false
                smallRemoteViews = {
                    val layoutId = com.vanskarner.simplenotify.test.R.layout.test_small_notification
                    val viewId = com.vanskarner.simplenotify.test.R.id.notification_title
                    val remoteViews =
                        RemoteViews(context.packageName, layoutId)
                    remoteViews.setTextViewText(viewId, "Small title")
                    remoteViews
                }
                largeRemoteViews = {
                    val layoutId = com.vanskarner.simplenotify.test.R.layout.test_large_notification
                    val viewId = com.vanskarner.simplenotify.test.R.id.notification_title
                    val remoteViews =
                        RemoteViews(context.packageName, layoutId)
                    remoteViews.setTextViewText(viewId, "Large title")
                    remoteViews
                }
                smallIcon = R.drawable.notify_ic_notification_24
                contentIntent = pendingIntent()
                autoCancel = true
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                timeoutAfter = 5000
            }
            return data
        }

        fun extraData() = ExtraData(
            priority = NotificationCompat.PRIORITY_HIGH,
            sounds = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
            category = NotificationCompat.CATEGORY_MESSAGE,
            visibility = NotificationCompat.VISIBILITY_PRIVATE,
            ongoing = true,
            color = Color.GRAY,
            timestampWhen = 1500,
            deleteIntent = pendingIntent(),
            fullScreenIntent = Pair(pendingIntent(), true),
            onlyAlertOnce = true,
            showWhen = true,
            useChronometer = true,
            badgeNumber = 15,
            badgeIconType = NotificationCompat.BADGE_ICON_SMALL,
            shortCutId = "anyShortCutId",
            allowSystemGeneratedContextualActions = true,
            remoteInputHistory = listOf("History 1", "History 2"),
            groupKey = "some_group_key"
        )

        fun basicAction(): ActionData.BasicAction {
            return ActionData.BasicAction().apply {
                icon = R.drawable.notify_ic_message_24
                title = "Action1"
                pending = pendingIntent()
            }
        }

        fun replyAction(): ActionData.ReplyAction {
            return ActionData.ReplyAction().apply {
                icon = R.drawable.notify_ic_email_24
                title = "ReplyAction1"
                replyPending = pendingIntent()
                remote = RemoteInput.Builder("ANY_KEY").build()
                allowGeneratedReplies = true
            }
        }

        fun progressData(): ProgressData {
            return ProgressData(
                currentValue = 50,
                indeterminate = true,
                hide = false
            )
        }

        fun stackableData(): StackableData {
            return StackableData().apply {
                smallIcon = R.drawable.notify_ic_view_list_24
                title = "My Group Summary"
                summaryText = "Any description"
                initialAmount = 3
            }
        }

        fun pendingIntent(): PendingIntent {
            val flags =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE
                else PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            return PendingIntent.getBroadcast(
                ApplicationProvider.getApplicationContext(),
                123,
                Intent(),
                flags
            )
        }

        fun basicNotification(context: Context, channelId: String): NotificationCompat.Builder {
            return NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notify_ic_notification_24)
                .setContentTitle("Any Title")
                .setContentText("Any Text")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }
    }

}