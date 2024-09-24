package com.vanskarner.samplenotify.internal

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.random.Random

internal object NotifyGenerator {
    const val MAXIMUM_ACTIONS = 3

    fun show(notifyData: NotifyData): Int {
        val channelId = NotifyChannel(notifyData.context).applyChannel(notifyData.channelData)
        val notifyBuilder = NotificationCompat.Builder(notifyData.context, channelId)
        val notificationId = notifyData.data.id ?: Random.nextInt(0, Int.MAX_VALUE)
        notifyData.data.applyData(notifyBuilder)
        applyActions(notifyData, notifyBuilder)
        with(NotificationManagerCompat.from(notifyData.context)) {
            if (ActivityCompat.checkSelfPermission(
                    notifyData.context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(notificationId, notifyBuilder.build())
        }
        return notificationId
    }

    private fun applyActions(notifyData: NotifyData, builder: NotificationCompat.Builder) =
        notifyData.actions
            .takeLast(MAXIMUM_ACTIONS)
            .filterNotNull()
            .forEach { action -> builder.addAction(action.icon, action.name, action.pending) }

}