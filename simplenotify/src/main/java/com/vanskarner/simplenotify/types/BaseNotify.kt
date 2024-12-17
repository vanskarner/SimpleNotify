package com.vanskarner.simplenotify.types

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vanskarner.simplenotify.ActionData
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.ExtraData
import com.vanskarner.simplenotify.ProgressData
import com.vanskarner.simplenotify.R
import com.vanskarner.simplenotify.StackableData
import com.vanskarner.simplenotify.internal.DEFAULT_PROGRESS_NOTIFICATION_ID
import com.vanskarner.simplenotify.internal.INVALID_NOTIFICATION_ID
import com.vanskarner.simplenotify.internal.MAXIMUM_ACTIONS
import com.vanskarner.simplenotify.internal.NotifyChannel
import com.vanskarner.simplenotify.internal.RANGE_GROUP_NOTIFICATION
import com.vanskarner.simplenotify.internal.RANGE_NOTIFICATION
import kotlin.random.Random

internal abstract class BaseNotify(
    private val context: Context,
    private val progressData: ProgressData?,
    private val extras: ExtraData,
    private val stackableData: StackableData?,
    private val channelId: String?,
    private val actions: List<ActionData?>,
) {
    val notifyChannel = NotifyChannel

    fun notify(data: Data): Pair<Int, Int> {
        val notifyManager = NotificationManagerCompat.from(context)
        if (notifyManager.areNotificationsEnabled()) {
            val currentNotificationId = generateNotificationId(data)
            val currentNotification = createNotification(data, selectChannelId()).build()
            val groupStackable = getGroupStackable()
            val groupKey = extras.groupKey
            if (groupStackable.isEmpty() || groupKey == null || stackableData == null) {
                notifyManager.notify(data.tag, currentNotificationId, currentNotification)
                return Pair(currentNotificationId, INVALID_NOTIFICATION_ID)
            }
            groupStackable.forEach { (id, notification) -> notifyManager.notify(id, notification) }
            notifyManager.notify(data.tag, currentNotificationId, currentNotification)
            val groupNotificationId = generateGroupNotificationId()
            notifyManager.notify(groupNotificationId, createGroupNotification(groupKey).build())
            return Pair(currentNotificationId, groupNotificationId)
        }
        return invalidNotificationResult()
    }

    fun createNotification(data: Data, channelId: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId).apply {
            applyCommonData(data, this)
            applyData(this)
            applyExtras(this)
            applyActions(this)
            applyProgress(this)
        }
    }

    fun invalidNotificationResult() = Pair(INVALID_NOTIFICATION_ID, INVALID_NOTIFICATION_ID)

    open fun selectChannelId(): String {
        return when {
            progressData == null && notifyChannel.checkChannelNotExists(context, channelId) ->
                notifyChannel.applyDefaultChannel(context)

            progressData != null && notifyChannel.checkChannelNotExists(context, channelId) ->
                notifyChannel.applyProgressChannel(context)

            else -> channelId ?: notifyChannel.applyDefaultChannel(context)
        }
    }

    abstract fun applyData(builder: NotificationCompat.Builder)

    abstract fun enableProgress(): Boolean

    private fun generateGroupNotificationId(): Int {
        return when {
            stackableData != null -> stackableData.id ?: Random.nextInt(
                RANGE_GROUP_NOTIFICATION.first,
                RANGE_GROUP_NOTIFICATION.second
            )

            else -> INVALID_NOTIFICATION_ID
        }
    }

    private fun generateNotificationId(dataType: Data): Int {
        return when {
            progressData != null && dataType.id == null -> DEFAULT_PROGRESS_NOTIFICATION_ID
            else -> dataType.id ?: Random.nextInt(
                RANGE_NOTIFICATION.first,
                RANGE_NOTIFICATION.second
            )
        }
    }

    private fun applyCommonData(data: Data, builder: NotificationCompat.Builder) {
        data.timeoutAfter?.let { builder.setTimeoutAfter(it) }
        builder.setSmallIcon(data.smallIcon)
            .setLargeIcon(data.largeIcon)
            .setContentIntent(data.contentIntent)
            .setAutoCancel(data.autoCancel)
            .setSubText(data.subText)
    }

    private fun applyExtras(builder: NotificationCompat.Builder) {
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
        extras.remoteInputHistory?.let { builder.setRemoteInputHistory(it.toTypedArray()) }
        extras.groupKey?.let { builder.setGroup(it) }
    }

    private fun applyActions(builder: NotificationCompat.Builder) {
        actions
            .takeLast(MAXIMUM_ACTIONS)
            .filterNotNull()
            .forEach { applyAction(it, builder) }
    }

    private fun applyProgress(builder: NotificationCompat.Builder) {
        progressData?.let { progress ->
            builder.setSound(null)
            if (progress.hide) builder.setProgress(0, 0, false)
            else builder.setProgress(100, progress.currentValue, progress.indeterminate)
        }
    }

    private fun createGroupNotification(groupKey: String): NotificationCompat.Builder {
        return NotificationCompat
            .Builder(context, notifyChannel.applyDefaultChannel(context))
            .setSmallIcon(stackableData?.smallIcon ?: R.drawable.notify_ic_view_list_24)
            .setStyle(NotificationCompat.InboxStyle().setSummaryText(stackableData?.summaryText))
            .setGroup(groupKey)
            .setGroupSummary(true)
            .setContentTitle(stackableData?.title)
    }

    private fun getGroupStackable(): List<Pair<Int, Notification>> {
        val stackable = stackableData ?: return emptyList()
        val groupKey = extras.groupKey ?: return emptyList()
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

    private fun applyAction(actionData: ActionData, builder: NotificationCompat.Builder) {
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

}