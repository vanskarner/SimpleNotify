package com.vanskarner.samplenotify.internal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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

    private val notifyFilter = NotifyFilter
    private val notifyFeatures = NotifyFeatures
    private val notifyChannel = NotifyChannel

    fun show(): Pair<Int, Int> {
        val notificationPair = generateNotificationWithId()
        val currentNotification = Pair(notificationPair.first, notificationPair.second.build())
        val notificationList = notifyFeatures
            .getGroupStackable(context, stackableData, extra, notifyChannel).toMutableList()
        var groupId = INVALID_NOTIFICATION_ID
        if (notificationList.isNotEmpty()) {
            groupId = notificationList.last().first
            val index = if (notificationList.size == 1) 0 else notificationList.size - 1
            notificationList.add(index, currentNotification)
        } else notificationList.add(currentNotification)
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notificationList.forEach { pair -> notify(pair.first, pair.second) }
        }
        return Pair(currentNotification.first, groupId)
    }

    fun generateNotificationWithId(): Pair<Int, NotificationCompat.Builder> {
        val notificationId = generateNotificationId()
        val notification = createNotification()
        return Pair(notificationId, notification)
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

    private fun createNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, selectChannelId()).apply {
            notifyFilter.applyData(context, data, this)
            notifyFeatures.applyExtras(extra, this)
            applyActions(this)
            progressData?.let { progress -> notifyFeatures.applyProgress(progress, this) }
        }
    }

    private fun generateNotificationId(): Int {
        return if (progressData != null && data.id == null) DEFAULT_PROGRESS_NOTIFICATION_ID
        else data.id ?: Random.nextInt(RANGE_NOTIFICATION.first, RANGE_NOTIFICATION.second)
    }

    private fun applyActions(builder: NotificationCompat.Builder) {
        actions
            .takeLast(MAXIMUM_ACTIONS)
            .filterNotNull()
            .forEach { notifyFeatures.applyAction(it, builder) }
    }

}
