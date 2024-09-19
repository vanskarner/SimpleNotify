package com.vanskarner.samplenotify.internal

import androidx.core.app.NotificationCompat
import com.vanskarner.samplenotify.Data

internal class BasicNotify(payLoad: NotifyData<Data.BasicData>) :
    Notify<Data.BasicData>(payLoad) {

    override fun applyData(builder: NotificationCompat.Builder) {
        builder.setSmallIcon(notifyData.data.smallIcon)
            .setContentTitle(notifyData.data.title)
            .setContentText(notifyData.data.text)
            .setContentIntent(notifyData.data.pending)
            .setAutoCancel(notifyData.data.autoCancel)
            .setPriority(notifyData.data.importance)
            .setSound(notifyData.data.sound)
    }

}