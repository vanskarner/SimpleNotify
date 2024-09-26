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
    private val progressData: ProgressData?,
    private val channelData: ChannelData,
    private val actions: Array<ActionData?>,
) {
    companion object {
        const val MAXIMUM_ACTIONS = 3
    }

    private val assignContent = AssignContent

    fun show(): Int {
        val channelId = if (progressData != null)
            NotifyChannel(context).applyChannel(ChannelData.forProgress(context))
        else NotifyChannel(context).applyChannel(channelData)
        val notifyBuilder = NotificationCompat.Builder(context, channelId)
        val notificationId = data.id ?: Random.nextInt(0, Int.MAX_VALUE)
        assignContent.applyData(data, notifyBuilder)
        applyActions(notifyBuilder)
        if (progressData != null) assignContent.applyProgress(progressData, notifyBuilder)
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

    private fun applyActions(builder: NotificationCompat.Builder) {
        actions
            .takeLast(MAXIMUM_ACTIONS)
            .filterNotNull()
            .forEach { assignContent.applyAction(it, builder) }
    }

}