package com.vanskarner.simplenotify.internal

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vanskarner.simplenotify.ActionData
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.ExtraData
import com.vanskarner.simplenotify.ProgressData
import com.vanskarner.simplenotify.StackableData
import kotlin.random.Random

internal class NotifyGenerator(
    private val context: Context,
    private val data: Data?,
    private val extra: ExtraData,
    private val progressData: ProgressData?,
    private val stackableData: StackableData?,
    private val channelId: String?,
    private val actions: Array<ActionData?>,
) {

    private val notifyFilter = NotifyFilter
    private val notifyFeatures = NotifyFeatures
    private val notifyChannel = NotifyChannel

    fun show(): Pair<Int, Int> {
        val invalidResponse = Pair(INVALID_NOTIFICATION_ID, INVALID_NOTIFICATION_ID)
        val myData = data ?: return invalidResponse
        val notificationManager = NotificationManagerCompat.from(context)
        if (notificationManager.areNotificationsEnabled()) {
            val currentNotificationId = generateNotificationId(myData)
            val currentNotification = createNotification(myData).build()
            val groupStackable = notifyFeatures.getGroupStackable(context, stackableData, extra)
            val groupKey = extra.groupKey
            val stackable = stackableData
            if (groupStackable.isNotEmpty() && groupKey != null && stackable != null) {
                groupStackable.forEach { notificationManager.notify(it.first, it.second) }
                notificationManager.notify(myData.tag, currentNotificationId, currentNotification)
                val groupNotificationId = generateGroupNotificationId()
                val groupNotification = createGroupNotification(groupKey, stackable)
                notificationManager.notify(groupNotificationId, groupNotification.build())
                return Pair(currentNotificationId, groupNotificationId)
            } else {
                notificationManager.notify(myData.tag, currentNotificationId, currentNotification)
                return Pair(currentNotificationId, INVALID_NOTIFICATION_ID)
            }
        }
        return invalidResponse
    }

    fun generateBuilder(): NotificationCompat.Builder {
        val myData = data ?: Data.BasicData()
        return createNotification(myData)
    }

    private fun selectChannelId(): String {
        return when {
            data is Data.CallData && notifyChannel.checkChannelNotExists(context, channelId) ->
                notifyChannel.applyCallChannel(context)

            data is Data.DuoMessageData && notifyChannel.checkChannelNotExists(
                context,
                channelId
            ) -> notifyChannel.applyMessagingChannel(context)

            data is Data.GroupMessageData && notifyChannel.checkChannelNotExists(
                context,
                channelId
            ) -> notifyChannel.applyMessagingChannel(context)

            progressData == null && notifyChannel.checkChannelNotExists(context, channelId) ->
                notifyChannel.applyDefaultChannel(context)

            progressData != null && notifyChannel.checkChannelNotExists(context, channelId) ->
                notifyChannel.applyProgressChannel(context)

            else -> channelId ?: notifyChannel.applyDefaultChannel(context)
        }
    }

    private fun createNotification(dataType: Data): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, selectChannelId()).apply {
            notifyFilter.applyData(context, dataType, this)
            notifyFeatures.applyExtras(extra, this)
            applyActions(this)
            progressData?.let { progress -> notifyFeatures.applyProgress(progress, this) }
        }
    }

    private fun generateNotificationId(dataType: Data): Int {
        return if (progressData != null && dataType.id == null) DEFAULT_PROGRESS_NOTIFICATION_ID
        else dataType.id ?: Random.nextInt(RANGE_NOTIFICATION.first, RANGE_NOTIFICATION.second)
    }

    private fun generateGroupNotificationId(): Int {
        return when {
            stackableData != null -> stackableData.id ?: Random.nextInt(
                RANGE_GROUP_NOTIFICATION.first,
                RANGE_GROUP_NOTIFICATION.second
            )

            else -> INVALID_NOTIFICATION_ID
        }
    }

    private fun applyActions(builder: NotificationCompat.Builder) {
        actions
            .takeLast(MAXIMUM_ACTIONS)
            .filterNotNull()
            .forEach { notifyFeatures.applyAction(it, builder) }
    }

    private fun createGroupNotification(
        groupKey: String,
        stackable: StackableData
    ): NotificationCompat.Builder {
        val style = NotificationCompat.InboxStyle().setSummaryText(stackableData?.summaryText)
        return NotificationCompat
            .Builder(context, notifyChannel.applyDefaultChannel(context))
            .setSmallIcon(stackable.smallIcon)
            .setStyle(style)
            .setGroup(groupKey)
            .setGroupSummary(true)
            .setContentTitle(stackable.title)
    }

}
