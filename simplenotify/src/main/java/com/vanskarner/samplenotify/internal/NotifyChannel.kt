package com.vanskarner.samplenotify.internal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.vanskarner.simplenotify.R

internal object NotifyChannel {

    fun applyDefaultChannel(context: Context): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && checkChannelNotExists(context, DEFAULT_CHANNEL_ID)
        ) {
            val manager = getManager(context)
            val defaultChannel = defaultChannel(context)
            manager.createNotificationChannel(defaultChannel)
        }
        return DEFAULT_CHANNEL_ID
    }

    fun applyProgressChannel(context: Context): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && checkChannelNotExists(context, DEFAULT_PROGRESS_CHANNEL_ID)
        ) {
            val manager = getManager(context)
            val progressChannel = progressChannel(context)
            manager.createNotificationChannel(progressChannel)
        }
        return DEFAULT_PROGRESS_CHANNEL_ID
    }

    fun applyCallChannel(context: Context): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && checkChannelNotExists(context, DEFAULT_CALL_CHANNEL_ID)
        ) {
            val manager = getManager(context)
            val callChannel = callChannel(context)
            manager.createNotificationChannel(callChannel)
        }
        return DEFAULT_CALL_CHANNEL_ID
    }

    fun checkChannelNotExists(context: Context, channelId: String?): Boolean {
        if (channelId.isNullOrEmpty()) return true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getManager(context)
            return manager.getNotificationChannel(channelId) == null
        }
        return true
    }

    fun cancelNotification(context: Context, id: Int) = getManager(context).cancel(id)

    fun cancelAllNotification(context: Context) = getManager(context).cancelAll()

    private fun getManager(context: Context): NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    @RequiresApi(Build.VERSION_CODES.O)
    private fun defaultChannel(context: Context): NotificationChannel {
        val channel = NotificationChannel(
            DEFAULT_CHANNEL_ID,
            context.getString(R.string.chanel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = context.getString(R.string.chanel_description)
        channel.setSound(
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
            AudioAttributes.Builder().build()
        )
        return channel
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun progressChannel(context: Context): NotificationChannel {
        val channel = NotificationChannel(
            DEFAULT_PROGRESS_CHANNEL_ID,
            context.getString(R.string.progress_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = context.getString(R.string.progress_channel_description)
        channel.setSound(null, null)
        return channel
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun callChannel(context: Context): NotificationChannel {
        val channel = NotificationChannel(
            DEFAULT_CALL_CHANNEL_ID,
            context.getString(R.string.call_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.call_channel_description)
            setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build()
            )
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 1000, 500, 1000)
        }
        return channel
    }

}