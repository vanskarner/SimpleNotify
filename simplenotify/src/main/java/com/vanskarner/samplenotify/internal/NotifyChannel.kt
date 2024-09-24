package com.vanskarner.samplenotify.internal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.vanskarner.samplenotify.ChannelData

internal class NotifyChannel(private val context: Context) {
    private val notificationManager: NotificationManager
            by lazy { context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager }

    fun getChannel(channelId: String): NotificationChannel? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                notificationManager.getNotificationChannel(channelId)

            else -> null
        }
    }

    fun applyChannel(data: ChannelData): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && checkChannelNotExists(data.id))
            registerChannel(data)
        return NotificationCompat.Builder(context, data.id)
    }

    fun deleteChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationManager.deleteNotificationChannel(channelId)
    }

    fun cancelNotification(id: Int) = notificationManager.cancel(id)

    private fun registerChannel(data: ChannelData) {
        val channel = NotificationChannel(data.id, data.name, data.importance)
            .apply { description = data.description }
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkChannelNotExists(channelId: String) =
        notificationManager.getNotificationChannel(channelId) == null

}