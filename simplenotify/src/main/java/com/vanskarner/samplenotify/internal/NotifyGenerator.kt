package com.vanskarner.samplenotify.internal

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vanskarner.samplenotify.ActionData
import com.vanskarner.samplenotify.ChannelData
import com.vanskarner.samplenotify.Data
import com.vanskarner.samplenotify.ProgressData
import kotlin.random.Random

internal class NotifyGenerator(
    private val context: Context,
    private val data: Data,
    private val progressData: ProgressData,
    private val channelData: ChannelData,
    private val actions: Array<ActionData?>,
) {

    private val assignContent = AssignContent
    private val notifyChannel = NotifyChannel

    fun show(): Int {
        val selectedChannel = selectChannel()
        notifyChannel.applyChannel(context, selectedChannel)
        val notifyBuilder = NotificationCompat.Builder(context, selectedChannel.id)
        assignContent.applyData(data, notifyBuilder)
        applyActions(notifyBuilder)
        assignContent.applyProgress(progressData, notifyBuilder)
        val notificationId = generateId()
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(notificationId, notifyBuilder.build())
        }
        return notificationId
    }

    private fun selectChannel(): ChannelData {
        return if (progressData.enable && channelData.id != DEFAULT_CHANNEL_ID)
            ChannelData.forProgress(context)
        else channelData
    }

    private fun generateId(): Int {
        return if (progressData.enable && data.id == null) DEFAULT_PROGRESS_NOTIFICATION_ID
        else data.id ?: Random.nextInt(0, Int.MAX_VALUE)
    }

    private fun applyActions(builder: NotificationCompat.Builder) {
        actions
            .takeLast(MAXIMUM_ACTIONS)
            .filterNotNull()
            .forEach { assignContent.applyAction(it, builder) }
    }

}