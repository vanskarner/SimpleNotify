package com.vanskarner.samplenotify.internal

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.service.notification.StatusBarNotification
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vanskarner.samplenotify.ActionData
import com.vanskarner.samplenotify.Data
import com.vanskarner.samplenotify.ExtraData
import com.vanskarner.samplenotify.ProgressData
import com.vanskarner.samplenotify.StackableData
import kotlin.random.Random

internal class NotifyGenerator(
    private val context: Context,
    private val data: Data,
    private val extra: ExtraData,
    private val progressData: ProgressData?,
    private val stackableData: StackableData?,
    private val channelId: String?,
    private val actions: Array<ActionData?>,
) {

    private val assignContent = AssignContent
    private val notifyChannel = NotifyChannel

    fun show(): Pair<Int, Int> {
        val notificationId = generateNotificationId()
        val notification = createNotification()
        val notificationsToPublish = mutableListOf<Pair<Int, Notification>>()
        notificationsToPublish.add(Pair(notificationId, notification.build()))
        val checkNotificationGroup = checkStackable()
        var groupId = INVALID_NOTIFICATION_ID
        if (checkNotificationGroup.first) {
            notificationsToPublish.clear()
            val notifications = checkNotificationGroup.second
            val groupNotification = checkNotificationGroup.third
            notifications
                .forEach { item -> notificationsToPublish.add(Pair(item.id, item.notification)) }
            notificationsToPublish.add(Pair(notificationId, notification.build()))
            groupNotification?.let {
                groupId = generateGroupNotificationId()
                notificationsToPublish.add(Pair(groupId, it))
            }
        }
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notificationsToPublish.forEach { pair -> notify(pair.first, pair.second) }
        }
        return Pair(notificationId, groupId)
    }

    fun selectChannelId(): String {
        return when {
            hasNoProgress() && channelNotExists() -> notifyChannel.applyDefaultChannel(context)
            hasProgress() && channelNotExists() -> notifyChannel.applyProgressChannel(context)
            else -> channelId ?: notifyChannel.applyDefaultChannel(context)
        }
    }

    private fun createNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, selectChannelId()).apply {
            assignContent.applyData(context, data, this)
            assignContent.applyExtras(extra, this)
            applyActions(this)
            progressData?.let { progress -> assignContent.applyProgress(progress, this) }
        }
    }

    private fun generateNotificationId(): Int {
        return if (hasProgress() && data.id == null) DEFAULT_PROGRESS_NOTIFICATION_ID
        else data.id ?: Random.nextInt(RANGE_NOTIFICATIONS.first, RANGE_NOTIFICATIONS.second)
    }

    private fun generateGroupNotificationId(): Int {
        return stackableData?.id ?: Random.nextInt(
            RANGE_GROUP_NOTIFICATIONS.first,
            RANGE_GROUP_NOTIFICATIONS.second
        )
    }

    private fun channelNotExists(): Boolean {
        if (channelId != null) {
            return notifyChannel.checkChannelNotExists(context, channelId)
        }
        return true
    }

    private fun hasProgress() = progressData != null

    private fun hasNoProgress() = progressData == null

    private fun applyActions(builder: NotificationCompat.Builder) {
        actions
            .takeLast(MAXIMUM_ACTIONS)
            .filterNotNull()
            .forEach { assignContent.applyAction(it, builder) }
    }

    private fun checkStackable(): Triple<Boolean, List<StatusBarNotification>, Notification?> {
        val defaultResult = Triple(false, emptyList<StatusBarNotification>(), null)
        val stackable = stackableData ?: return defaultResult
        val groupKey = extra.groupKey ?: return defaultResult
        val manager =
            context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager
                ?: return defaultResult
        val notifications = manager.activeNotifications
            .filter { it.groupKey.contains(groupKey) }
            .sortedByDescending { it.postTime }
        val isValid = notifications.size + 1 >= stackable.initialAmount
        if (isValid) {
            val style = NotificationCompat.InboxStyle().setSummaryText(stackable.summaryText)
            val groupBuilder = NotificationCompat
                .Builder(context, selectChannelId())
                .setStyle(style)
                .setGroup(groupKey)
                .setGroupSummary(true)
            stackable.title?.let { title -> groupBuilder.setContentTitle(title) }
            stackable.smallIcon?.let { icon -> groupBuilder.setSmallIcon(icon) }
            return Triple(true, notifications, groupBuilder.build())
        }
        return defaultResult
    }

}
