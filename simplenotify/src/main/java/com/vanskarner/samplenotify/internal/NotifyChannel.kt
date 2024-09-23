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
    private val defaultChannel: ChannelData by lazy { ChannelData.byDefault(context) }
    private val notificationManager: NotificationManager
            by lazy { context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager }

    fun getChannel(channelId: String): NotificationChannel? =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                notificationManager.getNotificationChannel(channelId)

            else -> null
        }

    fun applyChannel(channelId: String?): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val validChannelId = when {
                channelId == null || checkChannelNotExists(channelId) -> {
                    registerChannel(defaultChannel)
                    defaultChannel.id
                }

                else -> channelId
            }
            return NotificationCompat.Builder(context, validChannelId)
        }
        return NotificationCompat.Builder(context, defaultChannel.id)
    }

    fun registerChannel(data: ChannelData) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(data.id, data.name, data.importance)
                .apply { description = data.description }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun deleteChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationManager.deleteNotificationChannel(channelId)
    }

    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkChannelNotExists(channelId: String) =
        notificationManager.getNotificationChannel(channelId) == null

}