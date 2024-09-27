package com.vanskarner.samplenotify.internal

import android.app.NotificationChannel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.vanskarner.samplenotify.ActionData
import com.vanskarner.samplenotify.ChannelData
import com.vanskarner.samplenotify.Data
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

            is Data.TextListData -> {
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
            .setSound(data.sound)
            .setCategory(data.category)
    }

    fun applyAction(actionData: ActionData, builder: NotificationCompat.Builder) {
        when (actionData) {
            is ActionData.BasicAction -> {
                builder.addAction(actionData.icon, actionData.label, actionData.pending)
            }

            is ActionData.ReplyAction -> {
                val remoteInput = RemoteInput.Builder(actionData.replyKey)
                    .setLabel(actionData.replyLabel)
                    .build()
                val replyAction = NotificationCompat.Action
                    .Builder(actionData.icon, actionData.label, actionData.replyPending)
                    .addRemoteInput(remoteInput)
                    .build()
                builder.addAction(replyAction)
            }
        }
    }

    fun applyProgress(progressData: ProgressData, builder: NotificationCompat.Builder) {
        when {
            progressData.enable && progressData.conditionToHide.invoke() ->
                builder.setProgress(0, 0, false)

            progressData.enable ->
                builder.setProgress(100, progressData.currentPercentage, progressData.indeterminate)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun applyNotificationChannel(channelData: ChannelData): NotificationChannel {
        val notificationChannel =
            NotificationChannel(channelData.id, channelData.name, channelData.importance)
                .apply {
                    description = channelData.summary
                }
        if (channelData.id == DEFAULT_PROGRESS_CHANNEL_ID) notificationChannel.setSound(null, null)
        return notificationChannel
    }

}