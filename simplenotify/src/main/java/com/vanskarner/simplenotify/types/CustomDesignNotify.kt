package com.vanskarner.simplenotify.types

import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.Notify

internal class CustomDesignNotify(context: Context, configData: ConfigData) :
    Notify, BaseNotify(
    context,
    configData.progressData,
    configData.extras,
    configData.stackableData,
    configData.channelId,
    configData.actions
) {
    private val data = configData.data as Data.CustomDesignData

    override fun show(): Pair<Int, Int> = notify(data)

    override fun generateBuilder(): NotificationCompat.Builder =
        createNotification(data, selectChannelId())

    override fun applyData(builder: NotificationCompat.Builder) {
        if (data.hasStyle) builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setCustomContentView(data.smallRemoteViews.invoke())
            .setCustomBigContentView(data.largeRemoteViews.invoke())
    }

    override fun enableProgress(): Boolean = true
}