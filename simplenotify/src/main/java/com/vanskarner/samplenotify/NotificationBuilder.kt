package com.vanskarner.samplenotify

import androidx.core.app.NotificationCompat

internal fun buildSimpleNotify(notificationConfig: NotificationConfig): NotificationCompat.Builder {
    val smallIcon = notificationConfig.basicData.smallIcon
    val title = notificationConfig.basicData.title
    val text = notificationConfig.basicData.text
    /*val intent = Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent =
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)*/

    return NotificationCompat.Builder(notificationConfig.context, notificationConfig.channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
}