package com.vanskarner.samplenotify.internal

import androidx.core.app.NotificationCompat
import com.vanskarner.samplenotify.Data

internal class BigTextNotify(notifyData: NotifyData<Data.BigTextData>) :
    Notify<Data.BigTextData>(notifyData) {

    override fun applyData(builder: NotificationCompat.Builder) {
        builder.setSmallIcon(notifyData.data.smallIcon)
            .setContentTitle(notifyData.data.title)
            .setContentText(notifyData.data.collapsedText)
            .setLargeIcon(notifyData.data.largeIcon)
            .setContentIntent(notifyData.data.pending)
            .setAutoCancel(notifyData.data.autoCancel)
            .setPriority(notifyData.data.importance)
            .setSound(notifyData.data.sound)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notifyData.data.bigText)
                    .setSummaryText(notifyData.data.summaryText)
            )
    }

}