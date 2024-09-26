package com.vanskarner.samplenotify.internal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import com.vanskarner.samplenotify.ChannelData

internal object NotifyChannel {

    fun getChannel(context: Context, channelId: String): NotificationChannel? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                getManager(context).getNotificationChannel(channelId)

            else -> null
        }
    }

    fun applyChannel(context: Context, data: ChannelData): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && checkChannelNotExists(
                context,
                data.id
            )
        )
            registerChannel(context, data)
        return data.id
    }

    fun deleteChannel(context: Context, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            getManager(context).deleteNotificationChannel(channelId)
    }

    fun cancelNotification(context: Context, id: Int) = getManager(context).cancel(id)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerChannel(context: Context, data: ChannelData) {
        val channel = AssignContent.applyNotificationChannel(data)
        getManager(context).createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkChannelNotExists(context: Context, channelId: String) =
        getManager(context).getNotificationChannel(channelId) == null

    private fun getManager(context: Context): NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

}