package com.vanskarner.samplenotify

import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import com.vanskarner.simplenotify.R

sealed class Data {
    var smallIcon: Int = R.drawable.baseline_notifications_24
    var title: String? = null
    var text: String? = null

    data class BasicData(
        var importance: Int = NotificationCompat.PRIORITY_DEFAULT
    ) : Data()

    data class BigTextData(
        var largeText: String? = null,
    ) : Data()
}

data class ActionData(
    var icon: Int = R.drawable.baseline_notifications_24,
    var name: String? = null,
    var pending: PendingIntent? = null
)

data class ChannelData(
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    var importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
)
