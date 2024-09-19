package com.vanskarner.samplenotify.internal

import androidx.core.app.NotificationCompat
import com.vanskarner.samplenotify.Data

internal class BigTextNotify(payLoad: NotifyData<Data.BigTextData>) :
    Notify<Data.BigTextData>(payLoad) {

    override fun applyData() {
        builder.setSmallIcon(payLoad.data.smallIcon)
            .setContentTitle(payLoad.data.title)
            .setContentText(payLoad.data.text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(payLoad.data.largeText)
            )
            .setAutoCancel(true)
    }


}