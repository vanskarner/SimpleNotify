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
        when (data) {
            is Data.BasicData -> {
                builder.setContentTitle(data.title)
                    .setContentText(data.text)
            }

            is Data.BigTextData -> {
                val style = NotificationCompat.BigTextStyle()
                    .bigText(data.bigText)
                    .setSummaryText(data.summaryText)
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
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
            }

            is Data.GroupMessageData -> {
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
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
            }

            is Data.CallData -> {
                val secondCaller = Data.CallData.defaultSecondCaller(context)
                builder.setStyle(callTypeFilter(context, data))
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .addPerson(secondCaller)
                    .setVibrate(longArrayOf(0, 500, 1000, 500))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
            }

            is Data.CustomDesignData -> {
                if (data.hasStyle) builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
                builder.setCustomContentView(data.smallRemoteViews.invoke())
                    .setCustomBigContentView(data.largeRemoteViews.invoke())
            }
        }
    }

    private fun callTypeFilter(
        context: Context,
        data: Data.CallData
    ): NotificationCompat.CallStyle {
        val caller = data.caller ?: Data.CallData.defaultCaller(context)
        val declineOrHangup = data.declineOrHangup ?: Data.CallData.defaultDeclineOrHangup(context)
        return when (data.type.lowercase()) {
            "incoming" -> NotificationCompat.CallStyle.forIncomingCall(
                caller,
                declineOrHangup,
                data.answer ?: Data.CallData.defaultAnswer(context)
            )

            "ongoing" -> NotificationCompat.CallStyle.forOngoingCall(caller, declineOrHangup)

            "screening" -> NotificationCompat.CallStyle.forScreeningCall(
                caller,
                declineOrHangup,
                data.answer ?: Data.CallData.defaultAnswer(context)
            )

            else -> NotificationCompat.CallStyle.forIncomingCall(
                caller,
                declineOrHangup,
                data.answer ?: Data.CallData.defaultAnswer(context)
            )
        }
    }

}