package com.vanskarner.simplenotify.internal.types

import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.internal.ConfigData
import com.vanskarner.simplenotify.internal.Notify

internal class BigPictureNotify(context: Context, configData: ConfigData) :
    Notify, BaseNotify(
    context,
    configData.progressData,
    configData.extras,
    configData.stackableData,
    configData.channelId,
    configData.actions
) {
    private val data = configData.data as Data.BigPictureData

    override fun show(): Pair<Int, Int> = notify(data)

    override fun generateBuilder(): NotificationCompat.Builder =
        createNotification(data, selectChannelId())

    override fun applyData(builder: NotificationCompat.Builder) {
        val style = NotificationCompat.BigPictureStyle()
            .setSummaryText(data.summaryText)
            .bigPicture(data.image)
        builder
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(data.title)
            .setContentText(data.text)
            .setStyle(style)
    }

    override fun enableProgress(): Boolean = true

}