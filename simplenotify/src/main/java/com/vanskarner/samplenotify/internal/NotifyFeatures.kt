package com.vanskarner.samplenotify.internal

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import com.vanskarner.samplenotify.ActionData
import com.vanskarner.samplenotify.ExtraData
import com.vanskarner.samplenotify.ProgressData
import com.vanskarner.samplenotify.StackableData
import kotlin.random.Random

internal object NotifyFeatures {

    fun applyExtras(extras: ExtraData, builder: NotificationCompat.Builder) {
        extras.priority?.let { builder.setPriority(it) }
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
        extras.shortCutId?.let { builder.setShortcutId(it) }
        extras.badgeNumber?.let { builder.setNumber(it) }
        extras.badgeIconType?.let { builder.setBadgeIconType(it) }
        extras.allowSystemGeneratedContextualActions?.let {
            builder.setAllowSystemGeneratedContextualActions(it)
        }
        extras.remoteInputHistory?.let { builder.setRemoteInputHistory(it) }
        extras.groupKey?.let { builder.setGroup(it) }
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

    fun getGroupStackable(
        context: Context,
        stackableData: StackableData?,
        extra: ExtraData,
        notifyChannel: NotifyChannel
    ): List<Pair<Int, Notification>> {
        val stackable = stackableData ?: return mutableListOf()
        val groupKey = extra.groupKey ?: return mutableListOf()
        val manager =
            context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager
                ?: return mutableListOf()
        val notifications = manager.activeNotifications
            .filter { it.groupKey.contains(groupKey) }
            .sortedByDescending { it.postTime }
            .map { Pair(it.id, it.notification) }
        val initialAmount = if (stackable.initialAmount > 1) 1 else stackable.initialAmount
        val isValid = notifications.size + 1 >= initialAmount
        return if (isValid) {
            val groupId = stackableData.id ?: Random.nextInt(
                RANGE_GROUP_NOTIFICATIONS.first,
                RANGE_GROUP_NOTIFICATIONS.second
            )
            val groupNotification = NotificationCompat
                .Builder(context, notifyChannel.applyDefaultChannel(context))
                .setStyle(NotificationCompat.InboxStyle().setSummaryText(stackable.summaryText))
                .setGroup(groupKey)
                .setGroupSummary(true)
            stackable.title?.let { title -> groupNotification.setContentTitle(title) }
            stackable.smallIcon?.let { icon -> groupNotification.setSmallIcon(icon) }
            return notifications.toMutableList().apply {
                add(Pair(groupId, groupNotification.build()))
            }
        } else mutableListOf()
    }

}