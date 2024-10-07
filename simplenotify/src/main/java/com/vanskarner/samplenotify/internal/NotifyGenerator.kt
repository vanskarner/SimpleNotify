package com.vanskarner.samplenotify.internal

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vanskarner.samplenotify.ActionData
import com.vanskarner.samplenotify.Data
import com.vanskarner.samplenotify.ExtraData
import com.vanskarner.samplenotify.ProgressData
import kotlin.random.Random

internal class NotifyGenerator(
    private val context: Context,
    private val data: Data,
    private val extra: ExtraData,
    private val progressData: ProgressData?,
    private val channelId: String?,
    private val actions: Array<ActionData?>,
) {

    private val assignContent = AssignContent
    private val notifyChannel = NotifyChannel

    fun show(): Int {
        val notifyBuilder = NotificationCompat.Builder(context, selectChannelId())
        assignContent.applyData(data, notifyBuilder)
        assignContent.applyExtras(extra, notifyBuilder)
        applyActions(notifyBuilder)
        progressData?.let { assignContent.applyProgress(it, notifyBuilder) }
        val notificationId = generateNotificationId()
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

    fun selectChannelId(): String {
        return when {
            hasNoProgress() && channelNotExists() -> notifyChannel.applyDefaultChannel(context)
            hasProgress() && channelNotExists() -> notifyChannel.applyProgressChannel(context)
            else -> channelId ?: notifyChannel.applyDefaultChannel(context)
        }
    }

    private fun generateNotificationId(): Int {
        return if (hasProgress() && data.id == null) DEFAULT_PROGRESS_NOTIFICATION_ID
        else data.id ?: Random.nextInt(0, Int.MAX_VALUE)
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

}