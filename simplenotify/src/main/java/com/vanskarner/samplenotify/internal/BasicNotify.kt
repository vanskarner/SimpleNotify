package com.vanskarner.samplenotify.internal

import com.vanskarner.samplenotify.Data

internal class BasicNotify(payLoad: NotifyData<Data.BasicData>) :
    Notify<Data.BasicData>(payLoad) {

    override fun applyData() {
        builder.setSmallIcon(payLoad.data.smallIcon)
            .setContentTitle(payLoad.data.title)
            .setContentText(payLoad.data.text)
            .setPriority(payLoad.data.importance)
            .setAutoCancel(true)
    }

}