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
        val notificationPair = generateNotificationWithId()
        return notificationPair.second?.let {
            val currentNotification = Pair(notificationPair.first, it.build())
            val notificationList = notifyFeatures.getGroupStackable(
                context,
                stackableData,
                extra,
                notifyChannel
            ).toMutableList()
            var groupId = INVALID_NOTIFICATION_ID
            if (notificationList.isNotEmpty()) {
                groupId = notificationList.last().first
                val index = if (notificationList.size == 1) 0 else notificationList.size - 1
                notificationList.add(index, currentNotification)
            } else {
                notificationList.add(currentNotification)
            }
            with(NotificationManagerCompat.from(context)) {
                if (areNotificationsEnabled())
                    notificationList.forEach { pair -> notify(pair.first, pair.second) }
            }
            return Pair(currentNotification.first, groupId)
        } ?: Pair(INVALID_NOTIFICATION_ID, INVALID_NOTIFICATION_ID)
    }

    fun generateNotificationWithId(): Pair<Int, NotificationCompat.Builder?> {
        return data?.let {
            val notificationId = generateNotificationId(it)
            val notification = createNotification(it)
            Pair(notificationId, notification)
        } ?: Pair(INVALID_NOTIFICATION_ID, null)
    }

    private fun selectChannelId(): String {
        return when {
            data is Data.CallData && notifyChannel.checkChannelNotExists(context, channelId) ->
                notifyChannel.applyCallChannel(context)

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

    private fun applyActions(builder: NotificationCompat.Builder) {
        actions
            .takeLast(MAXIMUM_ACTIONS)
            .filterNotNull()
            .forEach { notifyFeatures.applyAction(it, builder) }
    }

}
