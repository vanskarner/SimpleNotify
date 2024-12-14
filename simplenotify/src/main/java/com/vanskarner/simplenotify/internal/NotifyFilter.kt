package com.vanskarner.simplenotify.internal

import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.MessagingStyle.Message
import androidx.core.app.Person
import androidx.core.content.pm.ShortcutManagerCompat
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.NotifyMessaging

object NotifyFilter {

    fun applyData(context: Context, data: Data, builder: NotificationCompat.Builder) {
        data.timeoutAfter?.let { builder.setTimeoutAfter(it) }
        builder.setSmallIcon(data.smallIcon)
            .setLargeIcon(data.largeIcon)
            .setContentIntent(data.contentIntent)
            .setAutoCancel(data.autoCancel)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSubText(data.subText)
        when (data) {
            is Data.BasicData -> {
                builder.setContentTitle(data.title)
                    .setContentText(data.text)
            }

            is Data.BigTextData -> {
                val style = NotificationCompat.BigTextStyle()
                    .bigText(data.bigText)
                builder.setContentTitle(data.title)
                    .setContentText(data.text)
                    .setStyle(style)
            }

            is Data.InboxData -> {
                val style = NotificationCompat.InboxStyle()
                data.lines.forEach { style.addLine(it) }
                builder.setContentTitle(data.title)
                    .setContentText(data.text)
                    .setStyle(style)
            }

            is Data.BigPictureData -> {
                val style = NotificationCompat.BigPictureStyle()
                    .setSummaryText(data.summaryText)
                    .bigPicture(data.image)
                builder.setContentTitle(data.title)
                    .setContentText(data.text)
                    .setStyle(style)
            }

            is Data.DuoMessageData -> {
                builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                val style = NotificationCompat.MessagingStyle(data.you)
                    .setGroupConversation(false)
                data.messages.forEachIndexed { index, item ->
                    val message = when (item) {
                        is NotifyMessaging.ContactMsg -> {
                            Message(item.msg, item.timestamp, data.contact)
                        }

                        is NotifyMessaging.YourMsg -> {
                            val person: Person? = null
                            Message(item.msg, item.timestamp, person)
                        }
                    }
                    item.mimeData?.let { pair -> message.setData(pair.first, pair.second) }
                    if (data.useHistoricMessage) {
                        if (index == data.messages.lastIndex) style.addMessage(message)
                        else style.addHistoricMessage(message)
                    } else {
                        style.addMessage(message)
                    }
                }
                data.bubble?.let { builder.setBubbleMetadata(it) }
                data.shortcut?.let { shortcut ->
                    if (data.addShortcutIfNotExists) {
                        val shortcutList = ShortcutManagerCompat.getDynamicShortcuts(context)
                        val shortcutFound = shortcutList.find { it.id == shortcut.id }
                        if (shortcutFound == null)
                            ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
                    }
                    builder.setShortcutInfo(shortcut)
                }
                builder.setStyle(style)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .addPerson(data.contact)
            }

            is Data.GroupMessageData -> {
                builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                val style = NotificationCompat.MessagingStyle(data.you)
                    .setConversationTitle(data.conversationTitle)
                    .setGroupConversation(true)
                data.messages.forEachIndexed { index, item ->
                    val message = when (item) {
                        is NotifyMessaging.ContactMsg -> {
                            Message(item.msg, item.timestamp, item.person)
                        }

                        is NotifyMessaging.YourMsg -> {
                            val person: Person? = null
                            Message(item.msg, item.timestamp, person)
                        }
                    }
                    item.mimeData?.let { pair -> message.setData(pair.first, pair.second) }
                    if (data.useHistoricMessage) {
                        if (index == data.messages.lastIndex) style.addMessage(message)
                        else style.addHistoricMessage(message)
                    } else {
                        style.addMessage(message)
                    }
                }
                data.bubble?.let { builder.setBubbleMetadata(it) }
                data.shortcut?.let { shortcut ->
                    if (data.addShortcutIfNotExists) {
                        val shortcutList = ShortcutManagerCompat.getDynamicShortcuts(context)
                        val shortcutFound = shortcutList.find { it.id == shortcut.id }
                        if (shortcutFound == null)
                            ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
                    }
                    builder.setShortcutInfo(shortcut)
                }
                builder.setStyle(style)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            }

            is Data.CallData -> {
                val caller = data.caller ?: Data.CallData.defaultCaller(context)
                val secondCaller = Data.CallData.defaultSecondCaller(context)
                val notificationSettings: (NotificationCompat.CallStyle) -> Unit = { style ->
                    style.setVerificationText(data.verificationText)
                        .setVerificationIcon(data.verificationIcon)
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setVibrate(longArrayOf(0, 500, 1000, 500))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                        .setStyle(style)
                        .addPerson(secondCaller)
                }
                val options = mapOf(
                    "incoming" to {
                        val answerIntent = data.answer
                        val declineIntent = data.declineOrHangup
                        if (answerIntent != null && declineIntent != null) {
                            val style = NotificationCompat.CallStyle
                                .forIncomingCall(caller, declineIntent, answerIntent)
                            notificationSettings(style)
                        }
                    }, "ongoing" to {
                        data.declineOrHangup?.let {
                            val style = NotificationCompat.CallStyle.forOngoingCall(caller, it)
                            notificationSettings(style)
                        }
                    }, "screening" to {
                        val hangUpIntent = data.declineOrHangup
                        val answerIntent = data.answer
                        if (hangUpIntent != null && answerIntent != null) {
                            val style = NotificationCompat.CallStyle
                                .forScreeningCall(caller, hangUpIntent, answerIntent)
                            notificationSettings(style)
                        }
                    })
                options[data.type.lowercase()]?.invoke()
            }

            is Data.CustomDesignData -> {
                if (data.hasStyle) builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
                builder.setCustomContentView(data.smallRemoteViews.invoke())
                    .setCustomBigContentView(data.largeRemoteViews.invoke())
            }
        }
    }

}