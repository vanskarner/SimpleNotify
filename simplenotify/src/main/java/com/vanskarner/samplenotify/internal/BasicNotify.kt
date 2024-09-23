package com.vanskarner.samplenotify.internal

import androidx.core.app.NotificationCompat
import com.vanskarner.samplenotify.Data

internal class BasicNotify(notifyData: NotifyData<Data.BasicData>) :
    Notify<Data.BasicData>(notifyData) {

    override fun applyData(builder: NotificationCompat.Builder) {
        builder.setSmallIcon(notifyData.data.smallIcon)
            .setContentTitle(notifyData.data.title)
            .setContentText(notifyData.data.text)
            .setLargeIcon(notifyData.data.largeIcon)
            .setContentIntent(notifyData.data.pending)
            .setAutoCancel(notifyData.data.autoCancel)
            .setPriority(notifyData.data.priority)
            .setSound(notifyData.data.sound)
    }

}