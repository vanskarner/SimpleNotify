package com.vanskarner.samplenotify

import androidx.core.app.NotificationCompat

internal class NotifyBuilder (config: NotifyConfig) {
    private val channelId = config.channelId
    private val context = config.context
    private val basicData = config.basicData
    private val builder = NotificationCompat.Builder(context,channelId)

    fun basic(): NotificationCompat.Builder {
        return builder
            .setSmallIcon(basicData.smallIcon)
            .setContentTitle(basicData.title)
            .setContentText(basicData.text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(basicData.click)
            .setAutoCancel(true)
    }

}
