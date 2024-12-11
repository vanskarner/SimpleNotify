package com.vanskarner.simplenotify.internal

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.vanskarner.simplenotify.ActionData
import com.vanskarner.simplenotify.ExtraData
import com.vanskarner.simplenotify.ProgressData
import com.vanskarner.simplenotify.StackableData

internal object NotifyFeatures {

    fun applyExtras(extras: ExtraData, builder: NotificationCompat.Builder) {
        if (extras.sounds == null || extras.sounds != Uri.EMPTY) builder.setSound(extras.sounds)
        extras.priority?.let { builder.setPriority(it) }
        extras.category?.let { builder.setCategory(it) }
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
                builder.addAction(actionData.icon, actionData.title, actionData.pending)
            }

            is ActionData.ReplyAction -> {
                val builderAction = NotificationCompat.Action.Builder(
                    actionData.icon,
                    actionData.title,
                    actionData.replyPending
                )
                    .addRemoteInput(actionData.remote)
                    .setAllowGeneratedReplies(actionData.allowGeneratedReplies)
                builder.addAction(builderAction.build())
            }
        }
    }

    fun applyProgress(progressData: ProgressData, builder: NotificationCompat.Builder) {
        builder.apply {
            setSound(null)
            if (progressData.hide) setProgress(0, 0, false)
            else setProgress(100, progressData.currentValue, progressData.indeterminate)
        }
    }

    fun getGroupStackable(
        context: Context,
        stackableData: StackableData?,
        extra: ExtraData
    ): List<Pair<Int, Notification>> {
        val stackable = stackableData ?: return emptyList()
        val groupKey = extra.groupKey ?: return emptyList()
        val manager =
            context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager
                ?: return emptyList()
        val notifications = manager.activeNotifications
            .filter { it.groupKey.contains(groupKey) }
            .sortedByDescending { it.postTime }
            .map { Pair(it.id, it.notification) }
        val initialAmount = if (stackable.initialAmount < 1) 1 else stackable.initialAmount
        val isValid = notifications.size + 1 >= initialAmount
        return if (isValid) notifications
        else emptyList()
    }

}