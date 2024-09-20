package com.vanskarner.samplenotify.internal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import com.vanskarner.samplenotify.ChannelData

import com.vanskarner.simplenotify.R

object NotifyChannel {
    private const val DEFAULT_CHANNEL_ID = "defaultId"

    fun getChannel(context: Context, data: ChannelData?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = data?.id ?: DEFAULT_CHANNEL_ID
            val notificationManager = createNotificationManager(context)
            if (checkChannelNotExists(channelId, notificationManager)) {
                val notificationChannel = createChannel(data, context)
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(data: ChannelData?, context: Context): NotificationChannel {
        return data?.let {
            NotificationChannel(it.id, it.name, it.importance)
                .apply { description = it.description }
        } ?: NotificationChannel(
            DEFAULT_CHANNEL_ID,
            getString(context, R.string.chanel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = getString(context, R.string.chanel_text) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkChannelNotExists(channelId: String, manager: NotificationManager): Boolean {
        return manager.getNotificationChannel(channelId) == null
    }

    private fun createNotificationManager(context: Context) =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    private fun getString(context: Context, resId: Int) = context.getString(resId)

}