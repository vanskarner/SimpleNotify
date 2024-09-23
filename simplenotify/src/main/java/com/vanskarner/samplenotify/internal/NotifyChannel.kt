package com.vanskarner.samplenotify.internal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.vanskarner.samplenotify.ChannelData

import com.vanskarner.simplenotify.R

internal class NotifyChannel(private val context: Context) {
    companion object {
        private const val DEFAULT_CHANNEL_ID = "defaultId"
    }

    private val notificationManager: NotificationManager
            by lazy { context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager }

    fun getChannel(context: Context, channelId: String): NotificationChannel? {
        var channel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = notificationManager.getNotificationChannel(channelId)
        return channel
    }

    fun applyChannel(channelId: String?): NotificationCompat.Builder {
        var validChannelId = DEFAULT_CHANNEL_ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when {
                channelId == null -> registerDefaultChannel()
                checkChannelNotExists(channelId) -> registerDefaultChannel()
                else -> validChannelId = channelId
            }
        }
        return NotificationCompat.Builder(context, validChannelId)
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
    fun registerDefaultChannel() {
        if (checkChannelNotExists(DEFAULT_CHANNEL_ID)) {
            val channel = NotificationChannel(
                DEFAULT_CHANNEL_ID,
                getString(R.string.chanel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = getString(R.string.chanel_description) }
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkChannelExists(channelId: String) =
        notificationManager.getNotificationChannel(channelId) != null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkChannelNotExists(channelId: String) =
        notificationManager.getNotificationChannel(channelId) == null

    private fun getString(resId: Int) = context.getString(resId)

}