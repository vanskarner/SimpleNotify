package com.vanskarner.samplenotify.internal

import android.app.NotificationChannel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.vanskarner.samplenotify.ActionData
import com.vanskarner.samplenotify.ChannelData
import com.vanskarner.samplenotify.Data
import com.vanskarner.samplenotify.ExtraData
import com.vanskarner.samplenotify.ProgressData

internal object AssignContent {

    fun applyData(data: Data, builder: NotificationCompat.Builder) {
        val filteredBuilder = when (data) {
            is Data.BasicData -> {
                builder.setContentText(data.text)
            }

            is Data.BigTextData -> {
                builder.setContentText(data.collapsedText)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(data.bigText)
                            .setSummaryText(data.summaryText)
                    )
            }

            is Data.InboxData -> {
                builder.setContentText(data.summaryText)
                val style = NotificationCompat.InboxStyle()
                data.lines.forEach { style.addLine(it) }
                builder.setStyle(style)
            }

            is Data.BigPictureData -> {
                builder.setContentText(data.collapsedText)
                    .setStyle(
                        NotificationCompat.BigPictureStyle()
                            .setSummaryText(data.summaryText)
                            .bigPicture(data.image)
                    )
            }

            is Data.MessageData -> {
                val style = NotificationCompat.MessagingStyle(data.user)
                    .setConversationTitle(data.conversationTitle)
                data.messages.forEach { style.addMessage(it.text, it.timestamp, it.person) }
                builder.setStyle(style)
            }
        }
        filteredBuilder.setSmallIcon(data.smallIcon)
            .setContentTitle(data.title)
            .setLargeIcon(data.largeIcon)
            .setContentIntent(data.pending)
            .setAutoCancel(data.autoCancel)
            .setPriority(data.priority)
    }

    fun applyExtras(extras: ExtraData, builder: NotificationCompat.Builder) {
        builder.setCategory(extras.category)
        builder.setSubText(extras.subText)
        builder.setDeleteIntent(extras.deleteIntent)
        extras.visibility?.let { builder.setVisibility(it) }
        extras.ongoing?.let { builder.setOngoing(it) }
        extras.color?.let { builder.setColor(it) }
        extras.timestampWhen?.let { builder.setWhen(it) }
        extras.fullScreenIntent?.let { builder.setFullScreenIntent(it.first, it.second) }
        extras.onlyAlertOnce?.let { builder.setOnlyAlertOnce(it) }
        extras.showWhen?.let { builder.setShowWhen(it) }
        extras.useChronometer?.let { builder.setUsesChronometer(it) }
    }

    fun applyAction(actionData: ActionData, builder: NotificationCompat.Builder) {
        when (actionData) {
            is ActionData.BasicAction -> {
                builder.addAction(actionData.icon, actionData.label, actionData.pending)
            }

            is ActionData.ReplyAction -> {
                val replyAction = NotificationCompat.Action
                    .Builder(actionData.icon, actionData.label, actionData.replyPending)
                    .addRemoteInput(actionData.remote)
                    .build()
                builder.addAction(replyAction)
            }
        }
    }

    fun applyProgress(progressData: ProgressData, builder: NotificationCompat.Builder) {
        if (progressData.hide) builder.setProgress(0, 0, false)
        else builder.setProgress(100, progressData.currentValue, progressData.indeterminate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun applyNotificationChannel(channelData: ChannelData): NotificationChannel {
        val notificationChannel =
            NotificationChannel(channelData.id, channelData.name, channelData.importance)
                .apply {
                    description = channelData.summary
                }
        notificationChannel.setSound(channelData.sound, channelData.audioAttributes)
        return notificationChannel
    }

}