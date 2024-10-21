package com.vanskarner.samplenotify.internal

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.MessagingStyle.Message
import androidx.core.app.Person
import com.vanskarner.samplenotify.ActionData
import com.vanskarner.samplenotify.Data
import com.vanskarner.samplenotify.ExtraData
import com.vanskarner.samplenotify.NotifyMessaging
import com.vanskarner.samplenotify.ProgressData

internal object AssignContent {

    fun applyData(data: Data, builder: NotificationCompat.Builder) {
        val filteredBuilder = when (data) {
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
                    .setConversationTitle(data.conversationTitle)
                    .setGroupConversation(false)
                data.messages.forEach {
                    val message = when (it) {
                        is NotifyMessaging.ContactMsg -> {
                            Message(it.msg, it.timestamp, data.contact)
                        }

                        is NotifyMessaging.YourMsg -> {
                            val person: Person? = null
                            Message(it.msg, it.timestamp, person)
                        }
                    }
                    it.mimeData?.let { pair -> message.setData(pair.first, pair.second) }
                    style.addMessage(message)
                }
                builder.setStyle(style)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setShowWhen(true)
                    .addPerson(data.contact)
//                    .setWhen(chat.messages.last().timestamp)
//                    .setOnlyAlertOnce(true)
            }

            is Data.CustomDesignData -> {
                if (data.hasStyle) builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
                builder.setCustomContentView(data.smallRemoteViews.invoke())
                    .setCustomBigContentView(data.largeRemoteViews.invoke())
            }
        }
        data.timeoutAfter?.let { builder.setTimeoutAfter(it) }
        filteredBuilder.setSmallIcon(data.smallIcon)
            .setLargeIcon(data.largeIcon)
            .setContentIntent(data.contentIntent)
            .setAutoCancel(data.autoCancel)
            .setPriority(data.priority)
    }

    fun applyExtras(extras: ExtraData, builder: NotificationCompat.Builder) {
        extras.category?.let { builder.setCategory(it) }
        extras.subText?.let { builder.setSubText(it) }
        extras.deleteIntent?.let { builder.setDeleteIntent(it) }
        extras.visibility?.let { builder.setVisibility(it) }
        extras.ongoing?.let { builder.setOngoing(it) }
        extras.color?.let { builder.setColor(it) }
        extras.timestampWhen?.let { builder.setWhen(it) }
        extras.fullScreenIntent?.let { builder.setFullScreenIntent(it.first, it.second) }
        extras.onlyAlertOnce?.let { builder.setOnlyAlertOnce(it) }
        extras.showWhen?.let { builder.setShowWhen(it) }
        extras.useChronometer?.let { builder.setUsesChronometer(it) }
//        builder.setShortcutId(extras.badgeShortCutId)
        extras.badgeNumber?.let { builder.setNumber(it) }
        extras.badgeIconType?.let { builder.setBadgeIconType(it) }
        extras.allowSystemGeneratedContextualActions?.let {
            builder.setAllowSystemGeneratedContextualActions(it)
        }
        extras.remoteInputHistory?.let { builder.setRemoteInputHistory(it) }
    }

    fun applyAction(actionData: ActionData, builder: NotificationCompat.Builder) {
        when (actionData) {
            is ActionData.BasicAction -> {
                builder.addAction(actionData.icon, actionData.label, actionData.pending)
            }

            is ActionData.ReplyAction -> {
                val builderAction = NotificationCompat.Action.Builder(
                    actionData.icon,
                    actionData.label,
                    actionData.replyPending
                )
                    .addRemoteInput(actionData.remote)
                    .setAllowGeneratedReplies(actionData.allowGeneratedReplies)
                builder.addAction(builderAction.build())
            }
        }
    }

    fun applyProgress(progressData: ProgressData, builder: NotificationCompat.Builder) {
        if (progressData.hide) builder.setProgress(0, 0, false)
        else builder.setProgress(100, progressData.currentValue, progressData.indeterminate)
    }

}