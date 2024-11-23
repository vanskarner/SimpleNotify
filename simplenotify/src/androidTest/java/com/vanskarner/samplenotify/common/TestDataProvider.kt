package com.vanskarner.samplenotify.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import com.vanskarner.samplenotify.ActionData
import com.vanskarner.samplenotify.Data
import com.vanskarner.samplenotify.ExtraData
import com.vanskarner.samplenotify.NotifyMessaging
import com.vanskarner.samplenotify.ProgressData
import com.vanskarner.simplenotify.test.R

class TestDataProvider {

    companion object {
        fun basicData(): Data.BasicData {
            val pendingIntent = pendingIntent()
            val basicData = Data.BasicData().apply {
                title = "Any title"
                text = "Any text"
                smallIcon = R.drawable.test_ic_notification_24
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                priority = NotificationCompat.PRIORITY_DEFAULT
                contentIntent = pendingIntent
                autoCancel = true
                timeoutAfter = 5000
            }
            return basicData
        }

        fun bigTextData(): Data.BigTextData {
            val pendingIntent = pendingIntent()
            val data = Data.BigTextData().apply {
                title = "Any title"
                bigText = "Any text"
                text = "Any collapsedText"
                summaryText = "Any summary"
                smallIcon = R.drawable.test_ic_notification_24
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                priority = NotificationCompat.PRIORITY_DEFAULT
                contentIntent = pendingIntent
                autoCancel = true
                timeoutAfter = 5000
            }
            return data
        }

        fun inboxData(): Data.InboxData {
            val pendingIntent = pendingIntent()
            val data = Data.InboxData().apply {
                title = "Any title"
                text = "Any text"
                lines = arrayListOf("Any line 1", "Any line 2", "Any line 3")
                text = "Any summary"
                smallIcon = R.drawable.test_ic_notification_24
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                priority = NotificationCompat.PRIORITY_HIGH
                contentIntent = pendingIntent
                autoCancel = true
                timeoutAfter = 5000
            }
            return data
        }

        fun bigPictureData(): Data.BigPictureData {
            val pendingIntent = pendingIntent()
            val data = Data.BigPictureData().apply {
                title = "Any title"
                text = "Any collapsedText"
                summaryText = "Any text"
                image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                smallIcon = R.drawable.test_ic_notification_24
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                priority = NotificationCompat.PRIORITY_HIGH
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
                smallIcon = R.drawable.test_ic_notification_24
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                priority = NotificationCompat.PRIORITY_HIGH
                contentIntent = pendingIntent
                autoCancel = true
                timeoutAfter = 5000
            }
            return data
        }

        fun groupMessageData(context: Context, shortcutId: String): Data.GroupMessageData {
            val pendingIntent = pendingIntent()
            val data = Data.GroupMessageData().apply {
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
//                        .setPerson(contact)
                    .build()
                addShortcutIfNotExists = false
                smallIcon = R.drawable.test_ic_notification_24
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                priority = NotificationCompat.PRIORITY_HIGH
                contentIntent = pendingIntent
                autoCancel = true
                timeoutAfter = 5000
            }
            return data
        }

        fun callData(): Data.CallData {
            val pendingIntent = pendingIntent()
            return Data.CallData().apply {
                type = "incoming"
                caller = Person.Builder().setName("Max").build()
                answer = pendingIntent()
                declineOrHangup = pendingIntent()
                smallIcon = R.drawable.test_ic_notification_24
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                priority = NotificationCompat.PRIORITY_DEFAULT
                contentIntent = pendingIntent
                autoCancel = true
                timeoutAfter = 5000
            }
        }

        fun customDesignData(context: Context): Data.CustomDesignData {
            val data = Data.CustomDesignData().apply {
                hasStyle = false
                smallRemoteViews = {
                    val remoteViews =
                        RemoteViews(context.packageName, R.layout.test_small_notification)
                    remoteViews.setTextViewText(R.id.notification_title, "Small title")
                    remoteViews
                }
                largeRemoteViews = {
                    val remoteViews =
                        RemoteViews(context.packageName, R.layout.test_large_notification)
                    remoteViews.setTextViewText(R.id.notification_title, "Large title")
                    remoteViews
                }
                smallIcon = R.drawable.test_ic_notification_24
                contentIntent = pendingIntent()
                autoCancel = true
                priority = NotificationCompat.PRIORITY_HIGH
                largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                timeoutAfter = 5000
            }
            return data
        }

        fun extraData() = ExtraData(
            category = NotificationCompat.CATEGORY_MESSAGE,
            visibility = NotificationCompat.VISIBILITY_PRIVATE,
//        vibrationPattern = longArrayOf(0, 500, 1000, 500),
//        lights = Triple(Color.GREEN, 1000, 1000),
            ongoing = true,
            color = Color.GRAY,
//        timeoutAfter = 1000,
//        badgeIconType = 12,
            timestampWhen = 1500,
            deleteIntent = pendingIntent(),
            fullScreenIntent = Pair(pendingIntent(), true),
            onlyAlertOnce = true,
            subText = "Any SubText",
            showWhen = true,
            useChronometer = true,
            badgeNumber = 15,
            badgeIconType = NotificationCompat.BADGE_ICON_SMALL,
            shortCutId = "anyShortCutId",
            allowSystemGeneratedContextualActions = true,
            remoteInputHistory = arrayOf("History 1", "History 2"),
            groupKey = "some_group_key"
        )

        fun basicAction(): ActionData.BasicAction {
            return ActionData.BasicAction().apply {
                icon = R.drawable.test_ic_mail_24
                label = "Any Label"
                pending = pendingIntent()
            }
        }

        fun replyAction(): ActionData.ReplyAction {
            return ActionData.ReplyAction().apply {
                icon = R.drawable.test_ic_mail_24
                label = "Any Label"
                replyPending = pendingIntent()
                remote = RemoteInput.Builder("any_key").build()
                allowGeneratedReplies = true
            }
        }

        fun progressData(isHide: Boolean): ProgressData {
            return if (isHide) {
                ProgressData(
                    currentValue = 50,
                    indeterminate = true,
                    hide = true
                )

            } else {
                ProgressData(
                    currentValue = 50,
                    indeterminate = true,
                    hide = false
                )
            }
        }

        fun pendingIntent(): PendingIntent {
            return PendingIntent.getBroadcast(
                ApplicationProvider.getApplicationContext(),
                123,
                Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        fun notificationChannel(): NotificationChannel {
            return NotificationChannel(
                "testId",
                "Test Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        }

        fun basicNotification(context: Context, channelId: String): NotificationCompat.Builder {
            return NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.test_ic_notification_24)
                .setContentTitle("Any Title")
                .setContentText("Any Text")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }
    }

}